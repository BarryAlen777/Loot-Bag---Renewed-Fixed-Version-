package net.codersdownunder.lootbagmod.droplist;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import net.codersdownunder.lootbagmod.LootBagConfigNew;
import net.codersdownunder.lootbagmod.LootBagMod;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;

public class DropList {

	/**
	 * Registry names of the current candidate lists. Only informational - the loot bag draws from
	 * {@link #currentPool()}, which holds real, already resolved {@link Item}s.
	 */
	public static List<String> potentialItems = new ArrayList<>();
	public static List<String> whitelistedItems = new ArrayList<>();

	private static List<Item> potentialPool = List.of();
	private static List<Item> whitelistedPool = List.of();

	private static volatile boolean useWhitelist = false;
	private static volatile boolean loaded = false;

	/**
	 * H-07: things a bag must never produce because they can break a world or mean nothing outside
	 * of a creative inventory. This filter is deliberately independent of the config blacklist, so an
	 * old lootbagmod-common.toml that was written before this fix is still protected (the previous
	 * default list relied on "minecraft:spawn_egg", an id that does not even exist in 1.20.1, and on
	 * "minecraft:mob_spawner", which is called "minecraft:spawner" here).
	 */
	private static final Set<String> ALWAYS_EXCLUDED = Set.of(
			"minecraft:air", "minecraft:cave_air", "minecraft:void_air",
			LootBagMod.modid + ":lootbag",
			"minecraft:command_block", "minecraft:chain_command_block", "minecraft:repeating_command_block",
			"minecraft:command_block_minecart", "minecraft:structure_block", "minecraft:structure_void",
			"minecraft:jigsaw", "minecraft:barrier", "minecraft:light", "minecraft:bedrock",
			"minecraft:debug_stick", "minecraft:spawner", "minecraft:end_portal_frame",
			"minecraft:budding_amethyst", "minecraft:knowledge_book");

	/** Called when the config is reloaded so the lists get rebuilt on the next bag (H-05). */
	public static void markDirty() {
		loaded = false;
	}

	/** The pool the loot bag draws from right now, honouring the current whitelist setting. */
	public static List<Item> currentPool() {
		ensureLoaded();
		return useWhitelist ? whitelistedPool : potentialPool;
	}

	public static synchronized void ensureLoaded() {
		if (!loaded) {
			rebuild();
		}
	}

	/**
	 * Rebuilds both candidate lists from scratch (M-07: it used to accumulate on every call, which
	 * is why a hot reload could not simply call it again).
	 */
	public static synchronized void rebuild() {
		boolean whitelistMode = LootBagConfigNew.Common.EnableWhitelist.get();
		boolean containsEnabled = LootBagConfigNew.Common.EnableContainsList.get();
		boolean hardFilter = LootBagConfigNew.Common.ExcludeAdminItems.get();

		Set<String> blacklist = normalised(LootBagConfigNew.Common.BlackList.get());
		List<String> modNames = new ArrayList<>(normalised(LootBagConfigNew.Common.ModName.get()));
		List<String> contains = new ArrayList<>(normalised(LootBagConfigNew.Common.ContainsList.get()));

		Set<String> potential = new LinkedHashSet<>();
		Set<String> allowed = new LinkedHashSet<>();

		for (Item item : ForgeRegistries.ITEMS) {
			ResourceLocation key = ForgeRegistries.ITEMS.getKey(item);
			if (key == null) {
				continue;
			}
			String id = key.toString();

			if (hardFilter && isAlwaysExcluded(id, item)) {
				continue;
			}
			if (!blacklist.contains(id)) {
				potential.add(id);
			}
			// A mod entry filters by namespace, not by substring: "ice" used to match
			// "minecraft:ice" and "create" matched anything with "create" in its name (M-03).
			// Entries matching the contains/mod lists move to the whitelist side and are removed
			// from the default pool - the intended behaviour of the original lists.
			if ((containsEnabled && containsAny(id, contains)) || belongsToMod(id, modNames)) {
				potential.remove(id);
				allowed.add(id);
			}
		}

		if (whitelistMode) {
			for (String entry : LootBagConfigNew.Common.WhiteList.get()) {
				Item item = resolve(entry);
				if (item == null) {
					LootBagMod.LOGGER.warn("LootBagMod: whitelist entry '{}' is not a usable item (unregistered or air) and will be ignored", entry);
					continue;
				}
				allowed.add(entry.trim().toLowerCase(Locale.ROOT));
			}
			// Note: the blacklist is intentionally not applied in whitelist mode, exactly like the
			// original mod; the config comment now says so.
		}

		// Both lists are resolved, not just the active one: switching enablewhitelist in a config
		// file then always lands on a usable pool instead of the empty list that used to swallow bags.
		List<Item> potentialPoolNew = resolveAll(potential, hardFilter);
		List<Item> whitelistedPoolNew = resolveAll(allowed, hardFilter);

		potentialItems = new ArrayList<>(potential);
		whitelistedItems = new ArrayList<>(allowed);
		potentialPool = potentialPoolNew;
		whitelistedPool = whitelistedPoolNew;
		useWhitelist = whitelistMode;
		loaded = true;

		LootBagMod.LOGGER.info("LootBagMod: loot pool rebuilt - {} candidates / {} whitelisted (whitelist mode: {})",
				potentialPoolNew.size(), whitelistedPoolNew.size(), whitelistMode);
		if ((whitelistMode ? whitelistedPoolNew : potentialPoolNew).isEmpty()) {
			LootBagMod.LOGGER.warn("LootBagMod: the {} list is empty, opening a bag will do nothing until the config is fixed",
					whitelistMode ? "whitelist" : "candidate");
		}
	}

	private static List<Item> resolveAll(Set<String> names, boolean hardFilter) {
		List<Item> pool = new ArrayList<>(names.size());
		for (String id : names) {
			Item item = resolve(id);
			if (item == null) {
				LootBagMod.LOGGER.warn("LootBagMod: candidate '{}' is not a registered item and was skipped", id);
				continue;
			}
			if (hardFilter && isAlwaysExcluded(id, item)) {
				continue;
			}
			pool.add(item);
		}
		return List.copyOf(pool);
	}

	private static Item resolve(String id) {
		if (id == null) {
			return null;
		}
		String trimmed = id.trim().toLowerCase(Locale.ROOT);
		if (trimmed.isEmpty()) {
			return null;
		}
		try {
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(trimmed));
			if (item == null || item == Items.AIR) {
				return null;
			}
			return item;
		} catch (ResourceLocationException e) {
			return null;
		}
	}

	private static boolean isAlwaysExcluded(String id, Item item) {
		if (item instanceof SpawnEggItem) {
			return true;
		}
		if (id.endsWith("_spawn_egg")) {
			// Modded eggs that do not extend SpawnEggItem.
			return true;
		}
		return ALWAYS_EXCLUDED.contains(id);
	}

	private static boolean containsAny(String id, List<String> needles) {
		for (String needle : needles) {
			if (!needle.isEmpty() && id.contains(needle)) {
				return true;
			}
		}
		return false;
	}

	private static boolean belongsToMod(String id, List<String> modNames) {
		if (modNames.isEmpty()) {
			return false;
		}
		int colon = id.indexOf(':');
		if (colon <= 0) {
			return false;
		}
		String namespace = id.substring(0, colon);
		for (String mod : modNames) {
			if (!mod.isEmpty() && mod.equals(namespace)) {
				return true;
			}
		}
		return false;
	}

	private static Set<String> normalised(List<? extends String> values) {
		Set<String> out = new LinkedHashSet<>();
		if (values == null) {
			return out;
		}
		for (String value : values) {
			if (value == null) {
				continue;
			}
			String trimmed = value.trim().toLowerCase(Locale.ROOT);
			if (!trimmed.isEmpty()) {
				out.add(trimmed);
			}
		}
		return out;
	}
}
