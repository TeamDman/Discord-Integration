package ca.teamdman.discordintegration;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

import java.io.File;

@Mod(modid = DiscordIntegration.MODID, name = "Discord", version = "@VERSION@", acceptableRemoteVersions = "*", clientSideOnly = true)
public class DiscordIntegration {
	static final String MODID = "discord";

	public DiscordIntegration() {
		Config.init(new File(Loader.instance().getConfigDir(), MODID + ".cfg"));
		RichPresence.enable();
		RichPresence.setState(RichPresence.State.LOADING);
	}

	@Mod.EventHandler
	public static void onLoadComplete(FMLLoadCompleteEvent event) {
		RichPresence.setState(RichPresence.State.ENABLED);
		RichPresence.update(presence -> {
			presence.details = "Main Menu";
			presence.state = Loader.instance().getActiveModList().size() + " mods loaded";
		});
	}
}
