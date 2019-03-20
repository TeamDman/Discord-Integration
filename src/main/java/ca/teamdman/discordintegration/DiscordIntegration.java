package ca.teamdman.discordintegration;

import discord4j.core.DiscordClient;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.reflect.Field;

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
			RichPresence.enable();
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

}
