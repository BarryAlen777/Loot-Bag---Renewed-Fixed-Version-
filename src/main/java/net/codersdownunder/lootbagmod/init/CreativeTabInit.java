package net.codersdownunder.lootbagmod.init;

import java.util.Objects;

import net.codersdownunder.lootbagmod.LootBagMod;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * M-05: the bag was registered without a creative tab, so it never showed up in the creative
 * inventory or the item search and could only be obtained from a mob kill or /give.
 */
@Mod.EventBusSubscriber(modid = LootBagMod.modid, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CreativeTabInit {

	private CreativeTabInit() {
	}

	@SubscribeEvent
	public static void addContents(BuildCreativeModeTabContentsEvent event) {
		if (Objects.equals(event.getTabKey(), CreativeModeTabs.INGREDIENTS)) {
			event.accept(ItemInit.LOOTBAG);
		}
	}
}
