package ca.teamdman.discordintegration;

import com.google.common.eventbus.Subscribe;
import discord4j.core.DiscordClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
@Mod.EventBusSubscriber
@Mod(modid= DiscordIntegration.MODID, name = "Discord Integration", version = "@VERSION@", acceptableRemoteVersions = "*")
public class DiscordIntegration {
	static final String MODID  = "discordintegration";
	static final Logger logger = LogManager.getLogger(MODID);
	static DiscordClient client;

	public DiscordIntegration() {
		try {
			Field f = Loader.class.getDeclaredField("canonicalConfigDir");
			f.setAccessible(true);
			Config.init(new File(((File) f.get(Loader.instance())),MODID+".cfg"));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
			RichPresence.enable(RichPresence.State.LOADING);
	}

	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {
//		MinecraftForge.EVENT_BUS.register(proxy);
		System.out.println(event.getModConfigurationDirectory());
	}

//	@Mod.EventHandler
//	public void onFMLServerStarted(FMLServerStartedEvent e) {
//		proxy.onServerStart(e);
//	}

	@Mod.EventHandler
	public static void onLoadComplete(FMLLoadCompleteEvent event) {
		RichPresence.setState(RichPresence.State.ENABLED);
		RichPresence.update(presence -> {
			presence.details = "At the main menu";
			presence.state = Loader.instance().getActiveModList().size() + " mods loaded";
		});
	}

	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			RichPresence.setState(RichPresence.State.ENABLED);
			RichPresence.update(presence -> {
				presence.details = "In game";
				presence.state = localizeDimension(event.player.getEntityWorld().provider.getDimensionType().getName());
			});
		}
	}

	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			RichPresence.update(presence -> {
				presence.details = "At the main menu";
				presence.state = Loader.instance().getActiveModList().size() + " mods loaded";
			});
		}
	}

	@SubscribeEvent
	public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			RichPresence.update(presence -> {
				presence.details = "In game";
				presence.state = localizeDimension(event.player.getEntityWorld().provider.getDimensionType().getName());
			});
		}
	}

	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
			RichPresence.update(presence -> {
				presence.details = "In game";
				presence.state = localizeDimension(event.player.getEntityWorld().provider.getDimensionType().getName());
			});
		}
	}


	private static String localizeDimension(String dim) {
		return Arrays.stream( dim.replaceAll("_"," ").split("\\s+")).map(s -> s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase()).collect(Collectors.joining(" "));
	}
}
