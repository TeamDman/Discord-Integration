package ca.teamdman.discordintegration;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
	public static Client client = new Client();
	private static Configuration config;

	public static void init(File file) {
		config = new Configuration(file);
		try {
			config.load();
			syncConfig();
		} catch (Exception e) {
			System.out.println("There was an error loading the configuration file");
			e.printStackTrace();
		} finally {
			config.save();
		}
	}

	private static void syncConfig() {
		client.appid = config.get("Client", "App ID", "558133001419096084", "Client ID of the Discord Rich Presence as seen in the developer portal").getString();
		client.largeImageKey = config.get("Client", "Large Image Key", "hollow").getString();
		client.smallImageKey = config.get("Client", "Small Image Key", "diamond").getString();
		client.smallImageText = config.get("Client", "Small Image Text", "").getString();
	}

	public static class Client {
		public String appid          = "";
		public String largeImageKey  = "";
		public String smallImageKey  = "";
		public String smallImageText = "";
	}
}
