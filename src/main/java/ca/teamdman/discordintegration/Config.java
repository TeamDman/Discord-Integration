package ca.teamdman.discordintegration;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
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

	public static Client client = new Client();
	public static Server server = new Server();

	public static class Client {
		public String appid = "";
		public String largeImageKey = "";
		public String smallImageKey = "";
		public String smallImageText = "";
	}

	public static class Server {
		public boolean enabled = false;
		public String channel_id = "";
		public String token      = "";
		public String topic      = "Welcome! Players online: {1} of {2}.";
	}

	private static void syncConfig() {
		client.appid = config.get("Client", "App ID", "000000000000000000", "Client ID of the Discord Rich Presence as seen in the developer portal").getString();
		client.largeImageKey = config.get("Client", "Large Image Key" , "").getString();
		client.smallImageKey = config.get("Client", "Small Image Key" , "").getString();
		client.smallImageText = config.get("Client", "Small Image Text" , "").getString();
		server.enabled = config.get("Server","Enable Chat Bridge", false, "Whether or not messages will be connected to a Discord channel").getBoolean();
		server.channel_id = config.get("Server", "Channel ID", "000000000000000000", "Channel ID for the chat bridge").getString();
		server.token = config.get("Server", "Bot Token", "XXXXXXXXXXXXXXXXXXXXXXXX.XXXXXX.XXXXXXXXXXXXXXXXXXXXXXXXXXX","Token, not ID").getString();
	}
}
