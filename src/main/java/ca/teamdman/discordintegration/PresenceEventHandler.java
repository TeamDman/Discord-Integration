package ca.teamdman.discordintegration;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.Arrays;
import java.util.stream.Collectors;

//@SuppressWarnings({"Duplicates", "ConstantConditions"})
@SuppressWarnings("ConstantConditions")
@Mod.EventBusSubscriber(modid = DiscordIntegration.MODID)
public class PresenceEventHandler {
	@SubscribeEvent()
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
		RichPresence.setState(RichPresence.State.ENABLED);
		RichPresence.update(presence -> {
			presence.details = "In Game";
			presence.state = localizeDimension(event.player.getEntityWorld().provider.getDimensionType().getName());
		});
	}

	private static String localizeDimension(String dim) {
		return Arrays.stream(dim.replaceAll("_", " ").split("\\s+")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).collect(Collectors.joining(" "));
	}

	@SubscribeEvent
	public static void onClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
		if (isEntityPlayer(event.getEntity())) {
			RichPresence.setState(RichPresence.State.ENABLED);
			RichPresence.update(presence -> {
				presence.details = "Spawning";
				presence.state = localizeDimension(event.getEntity().getEntityWorld().provider.getDimensionType().getName());
			});
		}
	}

	private static boolean isEntityPlayer(Entity entity) {
		return entity != null
				&& entity.getUniqueID() != null
				&& Minecraft.getMinecraft() != null
				&& Minecraft.getMinecraft().player != null
				&& Minecraft.getMinecraft().player.getUniqueID() != null
				&& entity.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID());
	}

	@SubscribeEvent
	public static void onClientConnect(FMLNetworkEvent.ClientConnectedToServerEvent event) {
		RichPresence.setState(RichPresence.State.ENABLED);
		RichPresence.update(presence -> {
			presence.details = "In Game";
		});
	}

	@SubscribeEvent
	public static void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
		RichPresence.update(presence -> {
			presence.details = "Main Menu";
			presence.state = Loader.instance().getActiveModList().size() + " mods loaded";
		});
	}

	@SubscribeEvent
	public static void onJoinWorld(EntityJoinWorldEvent event) {
		if (isEntityPlayer(event.getEntity())) {
			RichPresence.setState(RichPresence.State.ENABLED);
			RichPresence.update(presence -> {
				presence.details = "In Game";
				presence.state = getWorldName(event.getWorld());
			});
		}
	}

	private static String getWorldName(World world) {
		if (world != null
				&& world.provider != null
				&& world.provider.getDimensionType() != null
				&& world.provider.getDimensionType().getName() != null)
			return localizeDimension(world.provider.getDimensionType().getName());
		return "";
	}

	// only fired on single player worlds since client
//	@SubscribeEvent()
//	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
//		if (isEntityPlayer(event.player)) {
//			RichPresence.update(presence -> {
//				presence.details = "Main Menu";
//				presence.state = Loader.instance().getActiveModList().size() + " mods loaded";
//			});
//		}
//	}

	@SubscribeEvent()
	public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (isEntityPlayer(event.player)) {
			RichPresence.update(presence -> {
				presence.details = "In Game";
				presence.state = getWorldName(event.player.world);
			});
		}
	}

	@SubscribeEvent()
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (isEntityPlayer(event.player)) {
			RichPresence.update(presence -> {
				presence.details = "In Game";
				presence.largeImageText = "Respawning";
				presence.state = getWorldName(event.player.world);
			});
		}
	}

	@SubscribeEvent()
	public static void onItemTooltip(ItemTooltipEvent event) {
		if (isEntityPlayer(event.getEntityPlayer())) {
			if (event.getItemStack() != null && !event.getItemStack().isEmpty() && event.getItemStack().getDisplayName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Researching " + event.getItemStack().getDisplayName());
		}
	}

	@SubscribeEvent()
	public static void onSleep(PlayerSleepInBedEvent event) {
		if (isEntityPlayer(event.getEntity())) {
			if (event.getResultStatus() == EntityPlayer.SleepResult.OK)
				RichPresence.update(presence -> presence.largeImageText = "Sleeping");
		}
	}

	@SubscribeEvent()
	public static void onHoe(UseHoeEvent event) {
		if (isEntityPlayer(event.getEntity())) {
			RichPresence.update(presence -> presence.largeImageText = "Farming");
		}
	}

	@SubscribeEvent()
	public static void onItemUse(PlayerEvent.ItemPickupEvent event) {
		if (isEntityPlayer(event.player)) {
			if (event.getStack() != null && !event.getStack().isEmpty() && event.getStack().getDisplayName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Using " + event.getStack().getDisplayName());
		}
	}

	@SubscribeEvent()
	public static void onAttack(AttackEntityEvent event) {
		if (isEntityPlayer(event.getEntity())) {
			if (event.getTarget() != null && event.getTarget().getName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Attacking " + event.getTarget().getName());
		}
	}

	@SubscribeEvent()
	public static void onHarvest(BlockEvent.BreakEvent event) {
		if (isEntityPlayer(event.getPlayer()))
			if (event.getState() != null && event.getState().getBlock() != null && event.getState().getBlock().getLocalizedName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Mining " + event.getState().getBlock().getLocalizedName());
	}

	@SubscribeEvent()
	public static void onSmelt(PlayerEvent.ItemSmeltedEvent event) {
		if (isEntityPlayer(event.player)) {
			if (event.smelting != null && event.smelting.getDisplayName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Smelting " + event.smelting.getDisplayName());
		}
	}

	@SubscribeEvent()
	public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
		if (isEntityPlayer(event.player)) {
			if (event.crafting != null && event.crafting.getDisplayName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Crafting " + event.crafting.getDisplayName());
		}
	}

	@SubscribeEvent()
	public static void onPickup(PlayerEvent.ItemPickupEvent event) {
		if (isEntityPlayer(event.player)) {
			if (event.getStack() != null && event.getStack().getDisplayName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Obtaining " + event.getStack().getDisplayName());
		}
	}

	@SubscribeEvent()
	public static void onCraft(AnvilRepairEvent event) {
		if (isEntityPlayer(event.getEntity())) {
			if (event.getItemResult() != null && event.getItemResult().getDisplayName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Anviling " + event.getItemResult().getDisplayName());
		}
	}

	@SubscribeEvent()
	public static void onToss(ItemTossEvent event) {
		if (isEntityPlayer(event.getEntity()))
			if (event.getEntityItem() != null && event.getEntityItem().getItem() != null && event.getEntityItem().getDisplayName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Tossing " + event.getEntityItem().getItem().getDisplayName());
	}

	@SubscribeEvent()
	public static void onHurt(LivingHurtEvent event) {
		if (isEntityPlayer(event.getEntity())) {
			RichPresence.update(presence -> presence.largeImageText = "Taking Damage");
		}
	}

	@SubscribeEvent()
	public static void onInteract(PlayerInteractEvent event) {
		if (isEntityPlayer(event.getEntity())) {
			if (event.getWorld() != null && event.getPos() != null && event.getWorld().getBlockState(event.getPos()) != null && event.getWorld().getBlockState(event.getPos()).getBlock() != null && event.getWorld().getBlockState(event.getPos()).getBlock().getLocalizedName() != null)
				RichPresence.update(presence -> presence.largeImageText = "Interacting With " + event.getWorld().getBlockState(event.getPos()).getBlock().getLocalizedName());
		}
	}

	@SubscribeEvent()
	public static void onCraft(PlayerPickupXpEvent event) {
		if (isEntityPlayer(event.getEntity())) {
			RichPresence.update(presence -> presence.largeImageText = "Gaining Experience");
		}
	}
}
