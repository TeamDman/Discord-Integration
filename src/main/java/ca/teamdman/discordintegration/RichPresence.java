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

	public static void enable() {
		System.out.println("Creating Discord Rich Presence");
		String               applicationId = Config.client.appid;
		DiscordEventHandlers handlers      = new DiscordEventHandlers();
		handlers.ready = (user) -> System.out.println("Ready!");
		rpc.Discord_Initialize(applicationId, handlers, true, null);

		presence.startTimestamp = System.currentTimeMillis() / 1000;
		presence.details = "Launching";
		presence.largeImageKey = "cog_i";
		presence.smallImageKey = "ftb";
		rpc.Discord_UpdatePresence(presence);

		ProgressManager.ProgressBar barbar = null;
		try {
			Field f = Loader.class.getDeclaredField("progressBar");
			f.setAccessible(true);
			barbar = (ProgressManager.ProgressBar) f.get(Loader.instance());
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		final ProgressManager.ProgressBar bar = barbar;
		new ScheduledThreadPoolExecutor(1).scheduleAtFixedRate(() -> {
			if (bar != null) {
				update(presence -> {
					presence.details = "Launching [" + bar.getStep() + "/" +  bar.getSteps() + "]";
					presence.state = bar.getMessage();
				});
			}
			rpc.Discord_RunCallbacks();
		}, 0, 2, TimeUnit.SECONDS);

		System.out.println("Discord Rich Presence thread started");
	}

	public static void update(Consumer<DiscordRichPresence> consumer) {
		consumer.andThen(rpc::Discord_UpdatePresence).accept(presence);
	}

	public static void disable() {
		if (rpc != null) {
			rpc.Discord_ClearPresence();
			rpc.Discord_Shutdown();
		}
	}
}
