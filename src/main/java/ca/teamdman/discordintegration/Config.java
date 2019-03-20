package ca.teamdman.discordintegration;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

//@net.minecraftforge.common.config.Config(modid = DiscordIntegration.MODID)
public class Config {
	public static Configuration config;
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

	//@net.minecraftforge.common.config.Config.Comment("Settings for when ran on clients")
	public static Client client = new Client();
	//@net.minecraftforge.common.config.Config.Comment("Settings for when ran on servers")
	public static Server server = new Server();

	public static class Client {
		public String appid = "";
	}

	public static class Server {
		public boolean enabled = false;
		public String channel_id = "";
		public String token      = "";
		public String topic      = "Welcome! Players online: {1} of {2}.";
	}

	public static void syncConfig() {
		client.appid = config.get("Client", "App ID", "000000000000000000", "Client ID of the Discord Rich Presence as seen in the developer portal").getString();
		server.enabled = config.get("Server","Enable Chat Bridge", false, "Whether or not messages will be connected to a Discord channel").getBoolean();
		server.channel_id = config.get("Server", "Channel ID", "000000000000000000", "Channel ID for the chat bridge").getString();
		server.token = config.get("Server", "Bot Token", "XXXXXXXXXXXXXXXXXXXXXXXX.XXXXXX.XXXXXXXXXXXXXXXXXXXXXXXXXXX","Token, not ID").getString();
	}
}
