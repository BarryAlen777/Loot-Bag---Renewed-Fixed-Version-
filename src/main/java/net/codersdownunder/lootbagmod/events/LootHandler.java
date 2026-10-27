package net.codersdownunder.lootbagmod.events;

import java.util.Set;

import net.codersdownunder.lootbagmod.LootBagConfigNew;
import net.codersdownunder.lootbagmod.LootBagMod;
import net.codersdownunder.lootbagmod.init.ItemInit;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Static Forge bus listeners. They are registered once by {@code @Mod.EventBusSubscriber}; the old
 * extra {@code MinecraftForge.EVENT_BUS.register(new LootHandler())} in FMLCommonSetupEvent made
 * every handler run twice per event (H-01).
 */
@Mod.EventBusSubscriber(modid = LootBagMod.modid, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LootHandler {

	private static final String CHESTS = "chests/";
	private static final String INJECT_POOL = "lootbagmod_inject";

	/**
	 * Structures that get a loot bag pool. This used to be a switch statement that had drifted away
	 * from the resources folder: buried_treasure.json and end_city_treasure.json shipped in the jar
	 * but were never wired up (H-08).
	 */
	private static final Set<String> INJECT_CHESTS = Set.of(
			"abandoned_mineshaft",
			"buried_treasure",
			"desert_pyramid",
			"end_city_treasure",
			"jungle_temple",
			"simple_dungeon",
			"stronghold_corridor",
			"stronghold_crossing",
			"stronghold_library");

	private LootHandler() {
	}

	@SubscribeEvent
	public static void lootLoad(LootTableLoadEvent evt) {
		ResourceLocation name = evt.getName();
		if (!"minecraft".equals(name.getNamespace()) || !name.getPath().startsWith(CHESTS)) {
			return;
		}
		String file = name.getPath().substring(CHESTS.length());
		if (!INJECT_CHESTS.contains(file)) {
			return;
		}
		// LootTable#addPool throws if a pool with the same name already exists, so never add twice.
		if (evt.getTable().getPool(INJECT_POOL) != null) {
			return;
		}
		evt.getTable().addPool(getInjectPool(file));
	}

	public static LootPool getInjectPool(String entryName) {
		return LootPool.lootPool()
				.add(getInjectEntry(entryName))
				.name(INJECT_POOL)
				.build();
	}

	private static LootPoolEntryContainer.Builder<?> getInjectEntry(String name) {
		ResourceLocation table = new ResourceLocation(LootBagMod.modid, "inject/" + name);
		return LootTableReference.lootTableReference(table);
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onMobDrops(LivingDropsEvent event) {
		if (event.getEntity().level().isClientSide) {
			return;
		}

		Entity entity = event.getEntity();
		// H-04: MobCategory is not a hostile/passive split - players and armour stands are MISC, and
		// LivingDropsEvent fires for players too, so "allNonHostileMobs" used to be farmable by killing
		// another player. Only real Mob instances may drop a bag. (R-02: a separate "instanceof
		// ArmorStand" test would be dead code - ArmorStand extends LivingEntity, never Mob, so the
		// check below already excludes it. Players likewise are not Mobs.)
		if (!(entity instanceof Mob)) {
			return;
		}

		String entityName = registryName(entity);
		boolean hostile = entity.getType().getCategory() == MobCategory.MONSTER;
		// R-01: villagers, wandering traders, iron golems and snow golems ARE mobs (MISC is only their
		// category), so allNonHostileMobs on its own let villager breeding pens and iron farms farm
		// loot bags. They are skipped by default; an explicit name in mobdropslist still works,
		// because that is the pack author asking for it by hand.
		boolean utilityNpc = entity instanceof AbstractVillager || entity instanceof AbstractGolem;

		boolean eligible;
		String source;
		if (LootBagConfigNew.Common.AllHostileMobs.get() && hostile) {
			eligible = true;
			source = "all hostile mobs";
		} else if (LootBagConfigNew.Common.AllNonHostileMobs.get() && !hostile
				&& !(utilityNpc && LootBagConfigNew.Common.ExcludeVillagersAndGolems.get())) {
			eligible = true;
			source = "all non-hostile mobs";
		} else if (LootBagConfigNew.Common.MobList.get().contains(entityName)) {
			eligible = true;
			source = "mob list";
		} else {
			eligible = false;
			source = "not in any drop list";
		}

		// M-04: nextInt(100) <= chance gave 11% for a configured 10, and the looting branch divided
		// ints ("roll / lootingLevel / 1"), which is an unclear formula that saturated at 100%.
		// Looting now multiplies the configured percentage.
		int chance = Math.max(0, LootBagConfigNew.Common.DropChance.get());
		int looting = Math.max(0, event.getLootingLevel());
		// R-03: some mods report absurd looting levels; multiplying those in int overflowed to a
		// negative "percent", which silently disabled drops. Multiply in long, then clamp.
		int percent = (int) Math.min(100L, (long) chance * Math.max(1, looting));
		int roll = entity.level().random.nextInt(100);
		boolean dropped = eligible && percent > 0 && roll < percent;

		if (dropped) {
			ItemStack bag = new ItemStack(ItemInit.LOOTBAG.get(), 1);
			// xOld/yOld/zOld is the position of the previous tick; a moving mob dropped its bag a
			// block away, sometimes inside a wall.
			event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), bag));
		}

		if (LootBagConfigNew.Common.EntityName.get()) {
			LootBagMod.LOGGER.info("LootBagMod: {} ({}) roll {}/{} -> {}", entityName, source, roll, percent,
					dropped ? "bag dropped" : "no bag");
			if (event.getSource().getEntity() instanceof Player player) {
				player.displayClientMessage(Component.literal("LootBagMod: " + entityName + " [" + roll + "/" + percent + "]"), false);
			}
		}
	}

	private static String registryName(Entity entity) {
		ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
		return key == null ? entity.getType().toString() : key.toString();
	}
}
