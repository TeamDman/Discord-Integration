package ca.teamdman.discordintegration;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;
import discord4j.core.object.util.Snowflake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Timer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.message.FormattedMessage;
import scala.xml.Atom;

import java.util.Arrays;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerProxy extends CommonProxy {
	@Override
	public void onServerStart(FMLServerStartedEvent event) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		DiscordIntegration.client = new DiscordClientBuilder(Config.server.token).build();
		DiscordIntegration.client.getEventDispatcher().on(ReadyEvent.class)
				.subscribe(e -> {
					User self = e.getSelf();
					System.out.println("Logged in as " + self.getUsername() + "\t" + self.getDiscriminator());
				});
		DiscordIntegration.client.getEventDispatcher().on(MessageCreateEvent.class)
				.map(MessageCreateEvent::getMessage)
				.subscribe(msg -> {

					if (server.isServerRunning())
						msg.getAuthorAsMember().subscribe(member ->
								server.sendMessage(
										new TextComponentString(
												String.format("<%s> %s",
														member.getNickname().orElseGet(member::getDisplayName),
														msg.getContent().orElse("")))));
				});

		DiscordIntegration.client.getChannelById(Snowflake.of(Config.server.channel_id))
				.map(channel -> (MessageChannel) channel)
				.subscribe(channel -> channel.createMessage("Test"));


		{
			AtomicInteger previousMembers = new AtomicInteger(0);
			Executors.newScheduledThreadPool(1).scheduleAtFixedRate(()->{
				int count = Arrays.stream(server.worlds).mapToInt(world -> world.playerEntities.size()).sum();
				if (count != previousMembers.getAndSet(count))
					DiscordIntegration.client
							.getChannelById(Snowflake.of(Config.server.channel_id))
							.map(channel -> (TextChannel) channel)
							.subscribe(channel ->
									channel.edit(spec ->
											spec.setTopic(
													new FormattedMessage(Config.server.topic, count, server.getMaxPlayers()).getFormattedMessage())));
			}, 0, 5, TimeUnit.SECONDS);

		}

		DiscordIntegration.client.login();
		//players online n/n?
	}

	@SubscribeEvent
	public void onServerChat(ServerChatEvent event) {
		System.out.println("Got " + event.getMessage());
		DiscordIntegration.client.getChannelById(Snowflake.of(Config.server.channel_id))
				.map(channel -> (TextChannel) channel)
				.subscribe(channel -> channel.createMessage(event.getUsername() + ": " + event.getMessage()).block());
	}

}
