package net.codersdownunder.lootbagmod.events;

import java.util.Objects;
import java.util.Random;

import net.codersdownunder.lootbagmod.LootBagConfigNew;
import net.codersdownunder.lootbagmod.LootBagMod;
import net.codersdownunder.lootbagmod.init.ItemInit;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = LootBagMod.modid, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootHandler {

	@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent evt) {
		String prefix = "minecraft:chests/";
		String name = evt.getName().toString();
		

		if (name.startsWith(prefix)) {
			String file = name.substring(name.indexOf(prefix) + prefix.length());
			switch (file) {
			case "abandoned_mineshaft":
			case "desert_pyramid":
			case "jungle_temple":
			case "simple_dungeon":
			case "stronghold_crossing":
			case "stronghold_library":
			case "stronghold_corridor": evt.getTable().addPool(getInjectPool(file)); break;
			default: break;
			}
		}

	}

	
	public static LootPool getInjectPool(String entryName) {
		return LootPool.lootPool()
				.add(getInjectEntry(entryName))
				.name("lootbagmod_inject")
				.build();
	}

	@SuppressWarnings("rawtypes")
	private static LootPoolEntryContainer.Builder getInjectEntry(String name) {
		ResourceLocation table = new ResourceLocation(LootBagMod.modid, "inject/" + name);
		return LootTableReference.lootTableReference(table);
	}
	
	private static final Random rand = new Random();
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onMobDrops(LivingDropsEvent event) {
		
		if (!event.getEntity().level().isClientSide) {

		//System.out.print("testing");
		String entityName = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType())).toString();
		
		int dropchance = rand.nextInt(100);
		int testchance = LootBagConfigNew.Common.DropChance.get();
		
		
		
		if (LootBagConfigNew.Common.AllHostileMobs.get() && event.getEntity().getType().getCategory() == MobCategory.MONSTER) {

			if (event.getLootingLevel() != 0) {
			if (dropchance / event.getLootingLevel() / 1 <= testchance) {
				//LootBagMod.LOGGER.info("Bag Dropped");
				event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().xOld, event.getEntity().yOld, event.getEntity().zOld, new ItemStack(ItemInit.LOOTBAG.get(), 1)));
			}
			} else {
				if (dropchance <= testchance) {
					
					event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().xOld, event.getEntity().yOld, event.getEntity().zOld, new ItemStack(ItemInit.LOOTBAG.get(), 1)));
					//LootBagMod.LOGGER.info("Bag Dropped");
				}
			}
			
			if (LootBagConfigNew.Common.EntityName.get() == true) {
				LootBagMod.LOGGER.info(entityName);
				LootBagMod.LOGGER.info("Drop Chance: " + dropchance  + "   " + testchance);
				LootBagMod.LOGGER.info("Mob was selected from list");
				
				try {
				if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Player) {
				Player player = (Player)event.getSource().getEntity();
				((Player) event.getSource().getEntity()).displayClientMessage(Component.nullToEmpty("entityname: " + entityName + ""), false);
				}
				} catch (Exception e) {
					return;
				}
			}
		} else if (LootBagConfigNew.Common.AllNonHostileMobs.get() && event.getEntity().getType().getCategory() != MobCategory.MONSTER) {
			if (event.getLootingLevel() != 0) {
				if (dropchance / event.getLootingLevel() / 1 <= testchance) {
					//LootBagMod.LOGGER.info("Bag Dropped");
					event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().xOld, event.getEntity().yOld, event.getEntity().zOld, new ItemStack(ItemInit.LOOTBAG.get(), 1)));
				}
				} else {
					if (dropchance <= testchance) {
						
						event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().xOld, event.getEntity().yOld, event.getEntity().zOld, new ItemStack(ItemInit.LOOTBAG.get(), 1)));
						//LootBagMod.LOGGER.info("Bag Dropped");
					}
				}
				
				if (LootBagConfigNew.Common.EntityName.get() == true) {
					LootBagMod.LOGGER.info(entityName);
					LootBagMod.LOGGER.info("Drop Chance: " + dropchance  + "   " + testchance);
					LootBagMod.LOGGER.info("Mob was selected from list");
					
					try {
					if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Player) {
					Player player = (Player)event.getSource().getEntity();
					((Player) event.getSource().getEntity()).displayClientMessage(Component.nullToEmpty("entityname: " + entityName + ""), false);
					}
					} catch (Exception e) {
						return;
					}
				}
		}
		else if (LootBagConfigNew.Common.MobList.get().contains(entityName) ) {

			if (event.getLootingLevel() != 0) {
			if (dropchance / event.getLootingLevel() / 1 <= testchance) {
				//LootBagMod.LOGGER.info("Bag Dropped");
				event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().xOld, event.getEntity().yOld, event.getEntity().zOld, new ItemStack(ItemInit.LOOTBAG.get(), 1)));
			}
			} else {
				if (dropchance <= testchance) {
					
					event.getDrops().add(new ItemEntity(event.getEntity().level(), event.getEntity().xOld, event.getEntity().yOld, event.getEntity().zOld, new ItemStack(ItemInit.LOOTBAG.get(), 1)));
					//LootBagMod.LOGGER.info("Bag Dropped");
				}
			}
			
			if (LootBagConfigNew.Common.EntityName.get() == true) {
				LootBagMod.LOGGER.info(entityName);
				LootBagMod.LOGGER.info("Drop Chance: " + dropchance  + "   " + testchance);
				LootBagMod.LOGGER.info("Mob was selected from list");
				
				try {
				if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Player) {
				Player player = (Player)event.getSource().getEntity();
				((Player) event.getSource().getEntity()).displayClientMessage(Component.nullToEmpty("entityname: " + entityName + ""), false);
				}
				} catch (Exception e) {
					return;
				}
			}
		} else {
		
		if (LootBagConfigNew.Common.EntityName.get() == true) {
			LootBagMod.LOGGER.info(entityName);
			LootBagMod.LOGGER.info("Drop Chance: " + dropchance  + "   " + testchance);
			LootBagMod.LOGGER.info("Mob wasn't selected from list");
			
			try {
			if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Player) {
			Player player = (Player)event.getSource().getEntity();
			((Player) event.getSource().getEntity()).displayClientMessage(Component.nullToEmpty("entityname: " + entityName + ""), false);
			}
			} catch (Exception e) {
				LootBagMod.LOGGER.info("LootBagMod Handler Error: " + e);
				return;
			}
		}
	  }
	}
  }
	

}