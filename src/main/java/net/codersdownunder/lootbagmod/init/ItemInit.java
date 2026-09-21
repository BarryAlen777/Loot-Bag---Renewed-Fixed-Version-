package net.codersdownunder.lootbagmod.init;

import net.codersdownunder.lootbagmod.LootBagMod;
import net.codersdownunder.lootbagmod.items.LootBagItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemInit {
	
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LootBagMod.modid);
	
	public static final RegistryObject<Item> LOOTBAG = ITEMS.register("lootbag", () -> new LootBagItem(new Item.Properties()));
}
