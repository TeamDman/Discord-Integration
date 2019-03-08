package ca.teamdman.discordbutinminecraft;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid=DiscordButInMinecraft.MODID, name = "Discord, But In Minecraft", version = "@VERSION", serverSideOnly = true, acceptableRemoteVersions = "*")
public class DiscordButInMinecraft {
	static final String MODID  = "discordbutinminecraft";
	static final Logger logger = LogManager.getLogger(MODID);
	static DiscordClient client;
	//@Shadows @CritFlaw
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Mod.EventHandler
	public void onFMLServerStarted(FMLServerStartedEvent e) {
		client = new DiscordClientBuilder(Config.token).build();
		client.getEventDispatcher().on(ReadyEvent.class)
				.subscribe(event -> {
					User self = event.getSelf();
					System.out.println("Logged in as " + self.getUsername() + "\t" + self.getDiscriminator());
				});
		client.getEventDispatcher().on(MessageCreateEvent.class)
				.map(MessageCreateEvent::getMessage)
				.subscribe(msg -> System.out.println(msg.getContent()));
		System.out.println("asd");

		client.getChannelById(Snowflake.of(Config.channel_id))
				.map(channel -> (MessageChannel) channel)
				.subscribe(channel -> channel.createMessage("Test"));

		client.login();
		System.out.println("Done");
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		System.out.println("Got " + event.getMessage());
		client.getChannelById(Snowflake.of(Config.channel_id))
				.map(channel -> (TextChannel) channel)
				.subscribe(channel -> channel.createMessage(event.getUsername() + ": " + event.getMessage()).block());
	}

}
