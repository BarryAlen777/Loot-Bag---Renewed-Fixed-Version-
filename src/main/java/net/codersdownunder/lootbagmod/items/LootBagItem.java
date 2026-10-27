package net.codersdownunder.lootbagmod.items;

import java.util.List;

import net.codersdownunder.lootbagmod.LootBagConfigNew;
import net.codersdownunder.lootbagmod.LootBagMod;
import net.codersdownunder.lootbagmod.droplist.DropList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class LootBagItem extends Item {

	public LootBagItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		ItemStack bag = playerIn.getItemInHand(handIn);

		// H-02: the server is the only side allowed to roll loot. The client used to run the whole
		// loop a second time with its own RNG and its own copy of the config, which spawned ghost
		// ItemEntities, flashed inventory slots and printed every debug line twice. The client only
		// reports success here so the arm still swings (same pattern vanilla uses for spawn eggs).
		if (worldIn.isClientSide) {
			return InteractionResultHolder.success(bag);
		}
		if (playerIn.isSpectator()) {
			return InteractionResultHolder.fail(bag);
		}

		// H-05/H-06: the pool is (re)built lazily and handed out as real Items, so an empty or
		// half-typed config can no longer make the bag vanish without giving anything.
		List<Item> pool = DropList.currentPool();
		if (pool.isEmpty()) {
			LootBagMod.LOGGER.warn("LootBagMod: the item pool is empty (check the whitelist/blacklist config); bag NOT consumed");
			warnNoLoot(playerIn);
			return InteractionResultHolder.fail(bag);
		}

		RandomSource rand = worldIn.random;
		int rolls = Math.max(1, LootBagConfigNew.Common.Drops.get());
		int maxStack = Math.max(1, LootBagConfigNew.Common.StackSize.get());
		boolean toInventory = LootBagConfigNew.Common.ItemsInInventory.get();
		boolean logItems = LootBagConfigNew.Common.ItemsDropped.get();
		boolean chatItems = LootBagConfigNew.Common.ItemsDroppedChat.get();
		int handedOut = 0;

		for (int i = 0; i < rolls; i++) {
			Item item = pool.get(rand.nextInt(pool.size()));
			ItemStack stack = new ItemStack(item);
			if (stack.isEmpty()) {
				continue;
			}

			// M-02: nextInt(n) returns 0..n-1 and the old code patched 0 up to 1, so the configured
			// maximum was unreachable. 1..stacksize now, clamped to what the item itself can hold.
			int count = stack.isStackable() ? Math.min(1 + rand.nextInt(maxStack), stack.getMaxStackSize()) : 1;
			stack.setCount(count);

			if (toInventory) {
				giveOrDrop(playerIn, stack);
			} else {
				playerIn.drop(stack, true);
			}
			handedOut++;

			if (logItems) {
				ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
				LootBagMod.LOGGER.info("LootBagMod: {} x{} -> {}", id, count, playerIn.getScoreboardName());
			}
			if (chatItems) {
				playerIn.displayClientMessage(Component.literal(count + " x ").append(stack.getHoverName()), false);
			}
		}

		if (handedOut == 0) {
			LootBagMod.LOGGER.warn("LootBagMod: nothing could be handed out; bag NOT consumed");
			warnNoLoot(playerIn);
			return InteractionResultHolder.fail(bag);
		}

		// The bug this fork exists for: the bag has to disappear.
		// The original check was getItemInHand(hand).equals(new ItemStack(this)) - ItemStack does
		// not override equals() in 1.20.1, so that was reference equality, always false, and the bag
		// could be opened forever.
		if (!playerIn.isCreative()) {
			bag.shrink(1);
		}
		return InteractionResultHolder.consume(bag);
	}

	/**
	 * R-04: the client side has to report {@code success} so the arm swings, which means a server-side
	 * refusal (empty pool, nothing handed out) looked like a silent empty swing. The player now gets a
	 * short action bar line instead, and the bag is left in hand.
	 */
	private static void warnNoLoot(Player player) {
		player.displayClientMessage(Component.translatable("message.lootbagmod.no_loot"), true);
	}

	/**
	 * H-03/M-01: {@code Inventory#getFreeSlot()} returns 0 for a perfectly legal empty first slot
	 * and only -1 when everything is full, so the old "getFreeSlot() <= 0" test dumped the whole bag
	 * on the ground for an empty inventory. {@code Player#addItem} also leaves whatever did not fit
	 * in the stack it was given; that leftover used to be ignored and is now dropped.
	 */
	private static void giveOrDrop(Player player, ItemStack stack) {
		Inventory inventory = player.getInventory();
		boolean hasRoom = inventory.getFreeSlot() != -1 || inventory.getSlotWithRemainingSpace(stack) != -1;
		if (!hasRoom) {
			player.drop(stack, true);
			return;
		}
		ItemStack rest = stack.copy();
		player.addItem(rest);
		if (!rest.isEmpty()) {
			player.drop(rest, true);
		}
	}
}
