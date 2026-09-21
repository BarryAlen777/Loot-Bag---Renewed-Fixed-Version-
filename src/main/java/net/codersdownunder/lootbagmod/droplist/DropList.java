package net.codersdownunder.lootbagmod.droplist;

import net.codersdownunder.lootbagmod.LootBagConfigNew;
import net.codersdownunder.lootbagmod.LootBagMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class DropList {

	public static List<String> potentialItems = new ArrayList<String>();
	public static List<String> whitelistedItems = new ArrayList<String>();

	private static Boolean whitelist = LootBagConfigNew.Common.EnableWhitelist.get();
	public static Boolean taglistcomplete = false;

	public static void getItems() {

		for (Object item : ForgeRegistries.ITEMS) {
			String name = ForgeRegistries.ITEMS.getKey((Item) item).toString();
			if (true) {
				potentialItems.add(name);
			}
		}
		
		DropList.whitelist();
		
		LootBagMod.LOGGER.info("All potential items retrieved");

	}

	public static void modList() {

		List<String> modNames = new ArrayList<String>(LootBagConfigNew.Common.ModName.get());
		List<String> stringList = new ArrayList<String>();
		
		if (whitelist == true) {
			for (String drops : potentialItems) {

				for (int i = 0; i < modNames.size(); i++) {

					if (drops.contains(modNames.get(i))) {
						stringList.add(drops);
						LootBagMod.LOGGER.info("modlist: " + drops);
						
					}
				}
			}
			whitelistedItems.addAll(stringList);
			LootBagMod.LOGGER.info("ModList Completed");
		} else if (whitelist == false) {
			for (String drops : potentialItems) {
				for (int i = 0; i < modNames.size(); i++) {			    
					if (drops.contains(modNames.get(i))) {
						stringList.add(drops);
						//LootBagMod.LOGGER.info("" + drops);
					}
				}

			}
			potentialItems.removeAll(stringList);
			LootBagMod.LOGGER.info("ModList Completed");
		} else {
		    LootBagMod.LOGGER.info("ModList Failed");
		}
	}
	
	public static void containslist() {
	    
	    List<String> stringList = new ArrayList<String>();
        
	    if (whitelist == true) {
            for (String drops : potentialItems) {

                for (int i = 0; i < LootBagConfigNew.Common.ContainsList.get().size(); i++) {

                    if (drops.toString().contains(LootBagConfigNew.Common.ContainsList.get().get(i))) {
                        stringList.add(drops);
                        //LootBagMod.LOGGER.info("" + drops);
                    }
                }
            }
            whitelistedItems.addAll(stringList);
            LootBagMod.LOGGER.info("ModList Completed");
	    } else if (whitelist == false) {
	        for (String drops : potentialItems) {

                for (int i = 0; i < LootBagConfigNew.Common.ContainsList.get().size(); i++) {

                    if (drops.contains(LootBagConfigNew.Common.ContainsList.get().get(i))) {
                        stringList.add(drops);
                        //LootBagMod.LOGGER.info("" + drops);
                    }
                }
            }
            potentialItems.removeAll(stringList);
	        LootBagMod.LOGGER.info("ModList Completed");
	    } else {
	        LootBagMod.LOGGER.info("ModList Failed");
	    }
	    
	    DropList.modList();
	}

//	public static void tagList() {
//
//		ArrayList<String> tags = new ArrayList<String>(LootBagConfigNew.Common.TagsList.get());
//		
//		if (!tags.isEmpty()) {
//		if (whitelist == true) {
//
//			for (int i = 0; i < tags.size(); i++) {
//
//				ResourceLocation location = ItemTags.create();
//				
//				Tag<Item> t = TagUtils.getValues(null);
//				if (t == null) {
//
//					LootBagMod.LOGGER.error("Invalid or empty Tag: " + tag.toString());
//					continue;
//				}
//				Collection<Item> tagCollection = t.getValues();
//				for (Item items : tagCollection) {
//					whitelistedItems.add(items.getRegistryName().toString());
//					
//				}
//
//				Tag<Block> b = BlockTags.getAllTags().getTag(tag);
//
//				if (b == null) {
//					LootBagMod.LOGGER.error("Invalid or empty Tag: " + tag.toString());
//					continue;
//				}
//
//				Collection<Block> tagCollection2 = b.getValues();
//				for (Block items : tagCollection2) {
//					whitelistedItems.add(items.getRegistryName().toString());
//					
//				}
//			}
//			taglistcomplete = true;
//			LootBagMod.LOGGER.info("TagList Completed");
//
//		} else {
//
//			for (int i = 0; i < tags.size(); i++) {
//
//				ResourceLocation tag = new ResourceLocation(tags.get(i));
//				Tag<Item> t = ItemTags.getAllTags().getTag(tag);
//				if (t == null) {
//
//					LootBagMod.LOGGER.error("Invalid or empty Tag: " + tag.toString());
//					continue;
//				}
//				Collection<Item> tagCollection = t.getValues();
//				for (Item items : tagCollection) {
//					potentialItems.remove(items.getRegistryName().toString());
//					
//				}
//
//				Tag<Block> b = BlockTags.getAllTags().getTag(tag);
//
//				if (b == null) {
//					LootBagMod.LOGGER.error("Invalid or empty Tag: " + tag.toString());
//					continue;
//				}
//
//				Collection<Block> tagCollection2 = b.getValues();
//				for (Block items : tagCollection2) {
//					potentialItems.remove(items.getRegistryName().toString());
//	
//				}
//				
//			}
//			taglistcomplete = true;
//			LootBagMod.LOGGER.info("TagList Completed");
//		}
//		} else {
//			taglistcomplete = true;
//			LootBagMod.LOGGER.info("TagList Empty");
//		}
//	}

	public static void blacklist() {
		
	    if (whitelist == false) {
		potentialItems.removeAll(LootBagConfigNew.Common.BlackList.get());
	    }
	    
	    if (LootBagConfigNew.Common.EnableContainsList.get()) {
	    	DropList.containslist();
	    } else {
	    	DropList.modList();
	    }
		   
	}

	public static void whitelist() {
	    if (whitelist == true) {
		whitelistedItems.addAll(LootBagConfigNew.Common.WhiteList.get());
	    }
	    
	    DropList.blacklist();
	}
}
