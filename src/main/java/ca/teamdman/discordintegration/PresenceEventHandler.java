package ca.teamdman.discordintegration;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings({"Duplicates", "ConstantConditions"})
@Mod.EventBusSubscriber
public class PresenceEventHandler {
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {

		RichPresence.setState(RichPresence.State.ENABLED);
		RichPresence.update(presence -> {
			presence.details = "In Game";
			presence.state = localizeDimension(event.player.getEntityWorld().provider.getDimensionType().getName());
		});

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
		if (event.player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> {
				presence.details = "Main Menu";
				presence.state = Loader.instance().getActiveModList().size() + " mods loaded";
			});
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (event.player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> {
				presence.details = "In Game";
				presence.state = localizeDimension(event.player.getEntityWorld().provider.getDimensionType().getName());
			});
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (event.player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> {
				presence.details = "In Game";
				presence.largeImageText = "Respawning";
				presence.state = localizeDimension(event.player.getEntityWorld().provider.getDimensionType().getName());
			});
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Researching " + event.getItemStack().getDisplayName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onSleep(PlayerSleepInBedEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			if (event.getResultStatus() == EntityPlayer.SleepResult.OK)
				RichPresence.update(presence -> presence.largeImageText = "Sleeping");
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onHoe(UseHoeEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Farming");
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onItemUse(PlayerEvent.ItemPickupEvent event) {
		if (event.player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Using " + event.getStack().getDisplayName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onBucket(FillBucketEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Bucketing " + event.getFilledBucket().getDisplayName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onAttack(AttackEntityEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Attacking " + event.getTarget().getName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onHarvest(BlockEvent.BreakEvent event) {
		if (event.getPlayer().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Mining " + event.getState().getBlock().getLocalizedName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onSmelt(PlayerEvent.ItemSmeltedEvent event) {
		if (event.player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Smelting " + event.smelting.getDisplayName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
		if (event.player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Crafting " + event.crafting.getDisplayName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onPickup(PlayerEvent.ItemPickupEvent event) {
		if (event.player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Obtaining " + event.getStack().getDisplayName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onCraft(AnvilRepairEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Anviling " + event.getItemResult().getDisplayName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onToss(ItemTossEvent event) {
		if (event.getPlayer().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Tossing " + event.getEntityItem().getItem().getDisplayName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onHurt(LivingHurtEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Taking Damage");
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onInteract(PlayerInteractEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Interacting With " + event.getWorld().getBlockState(event.getPos()).getBlock().getLocalizedName());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void onCraft(PlayerPickupXpEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving().getUniqueID() != null && event.getEntityLiving().getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
			RichPresence.update(presence -> presence.largeImageText = "Gaining Experience");
	}

	private static String localizeDimension(String dim) {
		return Arrays.stream(dim.replaceAll("_", " ").split("\\s+")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase()).collect(Collectors.joining(" "));
	}

}