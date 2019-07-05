package ca.teamdman.discordintegration;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ProgressManager;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class RichPresence {
	private static final DiscordRichPresence presence = new DiscordRichPresence();
	private static final DiscordRPC          rpc      = DiscordRPC.INSTANCE;
	private static       State               state    = State.DISABLED;

	static {
		ProgressManager.ProgressBar barbar = null;
		try {
			Field f = Loader.class.getDeclaredField("progressBar");
			f.setAccessible(true);
			barbar = (ProgressManager.ProgressBar) f.get(Loader.instance());
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		final ProgressManager.ProgressBar bar = barbar;
		ScheduledThreadPoolExecutor executor =  new ScheduledThreadPoolExecutor(1);
		executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
		executor.scheduleAtFixedRate(() -> {
			if (state == State.LOADING && bar != null) {
				update(presence -> {
					presence.details = "Launching [" + bar.getStep() + "/" + bar.getSteps() + "]";
					presence.state = bar.getMessage();
				});
			}
			if (state != State.DISABLED)
				rpc.Discord_RunCallbacks();
		}, 0, 2, TimeUnit.SECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread("Discord Rich Presence Shutdown"){
			@Override
			public void run() {
				disable();
				executor.shutdown();
			}
		});
	}

	public static void enable() {
		System.out.println("Creating Discord Rich Presence");
		String               applicationId = Config.client.appid;
		DiscordEventHandlers handlers      = new DiscordEventHandlers();
		handlers.ready = (user) -> System.out.println("Ready!");
		rpc.Discord_Initialize(applicationId, handlers, true, null);

		presence.startTimestamp = System.currentTimeMillis() / 1000;
		presence.largeImageKey = Config.client.largeImageKey;
		presence.smallImageKey = Config.client.smallImageKey;
		presence.smallImageText = Config.client.smallImageText;
		rpc.Discord_UpdatePresence(presence);
		state = State.ENABLED;
		System.out.println("Discord Rich Presence thread started");
	}

	public static void setState(State state) {
		if (state == State.DISABLED)
			return;
		RichPresence.state = state;
	}

	public static void update(Consumer<DiscordRichPresence> consumer) {
		if (state == State.DISABLED)
			return;
		try {
			consumer.accept(presence);
		} catch (Exception ignored) {}
		rpc.Discord_UpdatePresence(presence);
	}

	public static void disable() {
		rpc.Discord_ClearPresence();
		rpc.Discord_Shutdown();
		RichPresence.state = State.DISABLED;
	}

	public enum State {
		DISABLED,
		ENABLED,
		LOADING
	}
}
