package net.codersdownunder.lootbagmod;


import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;

import com.google.common.collect.Lists;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public class LootBagConfigNew {

//	public static class Common {
//
//		
//		Common(ForgeConfigSpec.Builder builder) {
//			builder.comment("Client Configuration Settings").push("client");
//			{
//				builder.comment("No configs yet");
//			}
//			builder.pop();
//		}
//	}
	
	public static class Common {

		public static final String MOB_DROPS = "Mob Drops";
		public static final String BLACK_WHITE_LISTS = "Black And White Lists";
		public static final String MISC = "Misc";
		public static final String DEBUG = "Debug/Modpack tools";
		public static final String TAG_LIST = "Tag List";
		public static final String MOD_LIST = "Mod List";
		public static final String CONTAINS_LIST = "Contains List";
		
		private static List<String> BlackListList = Lists.newArrayList("lootbagmod:lootbag","minecraft:air","minecraft:command_block","minecraft:structure_block","minecraft:knowledge_book","minecraft:chain_command_block","minecraft:repeating_command_block","minecraft:barrier","minecraft:enchanted_book","minecraft:written_book","minecraft:potion","minecraft:splash_potion","minecraft:lingering_potion","minecraft:spawn_egg","minecraft:structure_void","minecraft:mob_spawner", "minecraft:bedrock");
		private static List<String> WhiteListList = Lists.newArrayList("minecraft:stone", "minecraft:nether_star");
		//private static List<String> TagList = Lists.newArrayList("minecraft:planks");
		private static List<String> MobListList = Lists.newArrayList("minecraft:blaze", "minecraft:cave_spider", "minecraft:creeper", "minecraft:drowned", "minecraft:elder_guardian", "minecraft:enderman", "minecraft:endermite", "minecraft:evoker");
		private static List<String> ModNames = Lists.newArrayList("tconstruct");
		private static List<String> ContainsListValues = Lists.newArrayList("apples", "creative");
		
	    public static ForgeConfigSpec SERVER_CONFIG;
	    public static ForgeConfigSpec CLIENT_CONFIG;
	    public static ForgeConfigSpec COMMON_CONFIG;
				
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> BlackList;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> WhiteList;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> TagsList;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> MobList;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> ModName;
		public static ForgeConfigSpec.ConfigValue<List<? extends String>> ContainsList;
		
		public static ForgeConfigSpec.BooleanValue EnableWhitelist;
		public static ForgeConfigSpec.BooleanValue EntityName;
		public static ForgeConfigSpec.BooleanValue ItemsDropped;
		public static ForgeConfigSpec.BooleanValue ItemsDroppedChat;
		public static ForgeConfigSpec.BooleanValue ItemsInInventory;
		public static ForgeConfigSpec.BooleanValue EnableContainsList;
		
		public static ForgeConfigSpec.BooleanValue AllHostileMobs;
		public static ForgeConfigSpec.BooleanValue AllNonHostileMobs;
		
		public static ForgeConfigSpec.IntValue StackSize;
		public static ForgeConfigSpec.IntValue Drops;
		public static ForgeConfigSpec.IntValue DropChance;
		
		Common(ForgeConfigSpec.Builder builder) {
			builder.push("Loot Bag Mod Config");
			{
			builder.push(MISC);
			StackSize = builder.comment("Maximum possible stack size for dropped items").defineInRange("stacksize", 15, 1, 64);
			Drops = builder.comment("How many items drop when a loot bag is used").defineInRange("dropamount", 10, 1, 1000);
			ItemsInInventory = builder.comment("Try to place items into inventory before dropping them onto the ground").define("itemsininventory", true);
			builder.pop();
			
			builder.push(DEBUG);
			EntityName = builder.comment("Print mobs registry name to chat and log file when killed").define("entityname", false);
			ItemsDropped = builder.comment("Print list of items dropped to debug.log").define("itemsdropped", false);
			ItemsDroppedChat = builder.comment("Print list of items dropped to the chat").define("itemsdroppedchat", false);
			builder.pop();
			
			builder.push(BLACK_WHITE_LISTS);
			EnableWhitelist = builder.comment("Use the whitelist").define("enablewhitelist", false);
			BlackList = builder.comment("BlackList (Syntax \"minecraft or modid : item or block name\"; example \"minecraft:air\" or \"lootbagmod:lootbag\")").define("blacklist", BlackListList);
			WhiteList = builder.comment("WhiteList Syntax \"minecraft or modid : item or block name\"; example minecraft:air or lootbagmod:lootbag)").define("whitelist", WhiteListList);
			builder.pop();
			
//			builder.push(TAG_LIST);
//			TagsList = builder.comment("TagList (Syntax \"minecraft, forge or modid : tag name\"; example \"minecraft:planks\" or \"forge:planks\")").define("taglist", TagList);
//			builder.pop();
			
			builder.push(MOD_LIST);
			ModName = builder.comment("Mod black/whitelist (Syntax \" modid \")").define("modlist", ModNames);
			builder.pop();
			
			builder.push(CONTAINS_LIST);
			ContainsList = builder.comment("Contains List (Synyax \"word\")").define("contains", ContainsListValues);
			EnableContainsList = builder.comment("Enable Contains List").define("enablecontains", false);
			builder.pop();
			
			builder.push(MOB_DROPS);
			MobList = builder.comment("Mob drops list (Syntax \" minecraft or modname : mobregistryname \")").define("mobdropslist", MobListList);
			DropChance = builder.comment("Loot Bag drop chance from mobs (the higher the number the more common they are)").defineInRange("dropchance", 10, 1, 100);
			AllHostileMobs = builder.comment("Make Loot Bags dropppable from all hostile Mobs").define("allHostileMobs", false);
			AllNonHostileMobs = builder.comment("Make Loot Bags droppable from all passive and neutral mobs").define("allNonHostileMobs", false);
			builder.pop();
			}
			builder.pop();
			
		}
	}
	
	  	//static final ForgeConfigSpec clientSpec;
	    //public static final Config.Client CLIENT;

	    static final ForgeConfigSpec commonSpec;
	    public static final Common COMMON;

	    static
	    {
//	        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
//	        clientSpec = clientSpecPair.getRight();
//	        CLIENT = clientSpecPair.getLeft();

	        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
	        commonSpec = commonSpecPair.getRight();
	        COMMON = commonSpecPair.getLeft();
	    }
	    
	    @SubscribeEvent
	    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
//	    	LootBagItem.firstTime = false;
	        LogManager.getLogger().debug(LootBagMod.modid, " config just got changed on the file system!");
	    }
}

