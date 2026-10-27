package net.codersdownunder.lootbagmod;


import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;

import net.codersdownunder.lootbagmod.droplist.DropList;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class LootBagConfigNew {

	public static class Common {

		public static final String MOB_DROPS = "Mob Drops";
		public static final String BLACK_WHITE_LISTS = "Black And White Lists";
		public static final String MISC = "Misc";
		public static final String DEBUG = "Debug/Modpack tools";
		public static final String MOD_LIST = "Mod List";
		public static final String CONTAINS_LIST = "Contains List";

		// 1.20.1 note: there is no "minecraft:spawn_egg" item (every mob has its own
		// <mob>_spawn_egg) and the spawner block item is "minecraft:spawner", not
		// "minecraft:mob_spawner" - both of those used to be dead entries that blocked nothing.
		// Spawn eggs are now filtered by item type in DropList instead.
		private static List<String> BlackListList = Lists.newArrayList("lootbagmod:lootbag","minecraft:air","minecraft:command_block","minecraft:structure_block","minecraft:knowledge_book","minecraft:chain_command_block","minecraft:repeating_command_block","minecraft:barrier","minecraft:enchanted_book","minecraft:written_book","minecraft:potion","minecraft:splash_potion","minecraft:lingering_potion","minecraft:structure_void","minecraft:spawner","minecraft:bedrock","minecraft:command_block_minecart","minecraft:jigsaw","minecraft:debug_stick","minecraft:light","minecraft:end_portal_frame","minecraft:budding_amethyst");
		private static List<String> WhiteListList = Lists.newArrayList("minecraft:stone", "minecraft:nether_star");
		private static List<String> MobListList = Lists.newArrayList("minecraft:blaze", "minecraft:cave_spider", "minecraft:creeper", "minecraft:drowned", "minecraft:elder_guardian", "minecraft:enderman", "minecraft:endermite", "minecraft:evoker");
		private static List<String> ModNames = Lists.newArrayList("tconstruct");
		private static List<String> ContainsListValues = Lists.newArrayList("apples", "creative");

		public static ForgeConfigSpec.ConfigValue<List<? extends String>> BlackList;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> WhiteList;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> MobList;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> ModName;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> ContainsList;

		public static ForgeConfigSpec.BooleanValue EnableWhitelist;
		public static ForgeConfigSpec.BooleanValue ExcludeAdminItems;
		public static ForgeConfigSpec.BooleanValue EntityName;
		public static ForgeConfigSpec.BooleanValue ItemsDropped;
		public static ForgeConfigSpec.BooleanValue ItemsDroppedChat;
		public static ForgeConfigSpec.BooleanValue ItemsInInventory;
		public static ForgeConfigSpec.BooleanValue EnableContainsList;

		public static ForgeConfigSpec.BooleanValue AllHostileMobs;
		public static ForgeConfigSpec.BooleanValue AllNonHostileMobs;
		public static ForgeConfigSpec.BooleanValue ExcludeVillagersAndGolems;

		public static ForgeConfigSpec.IntValue StackSize;
		public static ForgeConfigSpec.IntValue Drops;
		public static ForgeConfigSpec.IntValue DropChance;

		Common(ForgeConfigSpec.Builder builder) {
			builder.push("Loot Bag Mod Config");
			{
			builder.push(MISC);
			StackSize = builder.comment("Stack size range for dropped items: every drop is between 1 and this value (still capped by the item's own maximum stack size)").defineInRange("stacksize", 15, 1, 64);
			Drops = builder.comment("How many items drop when a loot bag is used").defineInRange("dropamount", 10, 1, 1000);
			ItemsInInventory = builder.comment("Try to place items into inventory before dropping them onto the ground").define("itemsininventory", true);
			ExcludeAdminItems = builder.comment("Never hand out spawn eggs and creative/admin blocks (command blocks, jigsaw, spawner, debug stick, ...) even if the lists below would allow them").define("excludeadminitems", true);
			builder.pop();

			builder.push(DEBUG);
			EntityName = builder.comment("Print mobs registry name to chat and log file when killed").define("entityname", false);
			ItemsDropped = builder.comment("Print list of items dropped to debug.log").define("itemsdropped", false);
			ItemsDroppedChat = builder.comment("Print list of items dropped to the chat").define("itemsdroppedchat", false);
			builder.pop();

			builder.push(BLACK_WHITE_LISTS);
			EnableWhitelist = builder.comment("Use the whitelist").define("enablewhitelist", false);
			BlackList = builder.comment("BlackList, only applied while the whitelist is off (Syntax \"minecraft or modid : item or block name\"; example \"minecraft:air\" or \"lootbagmod:lootbag\")").define("blacklist", BlackListList);
			WhiteList = builder.comment("WhiteList, used instead of the BlackList when enablewhitelist is true (Syntax \"minecraft or modid : item or block name\"; example minecraft:air or lootbagmod:lootbag)").define("whitelist", WhiteListList);
			builder.pop();

			builder.push(MOD_LIST);
			ModName = builder.comment("Mod black/whitelist, matched against the namespace in front of the colon (Syntax \" modid \")").define("modlist", ModNames);
			builder.pop();

			builder.push(CONTAINS_LIST);
			ContainsList = builder.comment("Contains List (Synyax \"word\")").define("contains", ContainsListValues);
			EnableContainsList = builder.comment("Enable Contains List").define("enablecontains", false);
			builder.pop();

			builder.push(MOB_DROPS);
			MobList = builder.comment("Mob drops list (Syntax \" minecraft or modname : mobregistryname \")").define("mobdropslist", MobListList);
			DropChance = builder.comment("Loot Bag drop chance from mobs in percent (the higher the number the more common they are); Looting II/III multiplies it").defineInRange("dropchance", 10, 1, 100);
			AllHostileMobs = builder.comment("Make Loot Bags dropppable from all hostile Mobs").define("allHostileMobs", false);
			AllNonHostileMobs = builder.comment("Make Loot Bags droppable from all passive and neutral mobs (players, armour stands and other non-mob entities never drop bags)").define("allNonHostileMobs", false);
			ExcludeVillagersAndGolems = builder.comment("While allNonHostileMobs is on, keep villagers, wandering traders, iron golems and snow golems out of it - they count as mobs, so without this their breeding farms and golem farms would print loot bags. Mobs named in mobdropslist are still allowed.").define("excludevillagersandgolems", true);
			builder.pop();
			}
			builder.pop();

		}
	}

	    static final ForgeConfigSpec commonSpec;
	    public static final Common COMMON;

	    static
	    {
	        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
	        commonSpec = commonSpecPair.getRight();
	        COMMON = commonSpecPair.getLeft();
	    }

	    /**
	     * The item lists are derived data: they have to be thrown away whenever the config is loaded or
	     * reloaded, otherwise flipping enablewhitelist at runtime left LootBagItem reading the new value
	     * while the list it draws from was still the old (often empty) one - the bag got consumed and
	     * gave nothing. Loading is covered as well because the first build (during FMLCommonSetupEvent)
	     * can happen before the file has actually been read. Rebuilding stays lazy and runs on the server
	     * thread on the next bag, never from this (possibly async) config callback.
	     */
	    @SubscribeEvent
	    public static void onConfigChange(final ModConfigEvent configEvent) {
	    	if (!(configEvent instanceof ModConfigEvent.Loading) && !(configEvent instanceof ModConfigEvent.Reloading)) {
	    		return;
	    	}
	    	ModConfig config = configEvent.getConfig();
	    	if (config == null || !LootBagMod.modid.equals(config.getModId())) {
	    		return;
	    	}
	    	DropList.markDirty();
	    	LootBagMod.LOGGER.debug("{} config loaded/reloaded, loot lists will be rebuilt", LootBagMod.modid);
	    }
}
