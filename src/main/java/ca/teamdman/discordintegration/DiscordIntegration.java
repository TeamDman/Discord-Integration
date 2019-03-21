package ca.teamdman.discordintegration;

import discord4j.core.DiscordClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;

@Mod.EventBusSubscriber
@Mod(modid = DiscordIntegration.MODID, name = "Discord", version = "@VERSION@", acceptableRemoteVersions = "*")
public class DiscordIntegration {
	static final String        MODID  = "discord";
	static final Logger        logger = LogManager.getLogger(MODID);
	static       DiscordClient client;

	public DiscordIntegration() {
		try {
			Field f = Loader.class.getDeclaredField("canonicalConfigDir");
			f.setAccessible(true);
			Config.init(new File(((File) f.get(Loader.instance())), MODID + ".cfg"));
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		if (FMLCommonHandler.instance().getSide() == Side.CLIENT)
			RichPresence.enable(RichPresence.State.LOADING);
	}

	@Mod.EventHandler
	public static void onLoadComplete(FMLLoadCompleteEvent event) {
		RichPresence.setState(RichPresence.State.ENABLED);
		RichPresence.update(presence -> {
			presence.details = "Main Menu";
			presence.state = Loader.instance().getActiveModList().size() + " mods loaded";
		});
	}

	//	@Mod.EventHandler
	//	public void onFMLServerStarted(FMLServerStartedEvent e) {
	//		proxy.onServerStart(e);
	//	}



}
