package net.codersdownunder.lootbagmod.items;

import java.util.Collections;
import java.util.Random;

import net.codersdownunder.lootbagmod.LootBagConfigNew;
import net.codersdownunder.lootbagmod.LootBagMod;
import net.codersdownunder.lootbagmod.droplist.DropList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class LootBagItem extends Item {

	public boolean firstTime = true;
	
	public LootBagItem(Properties p_i48487_1_) {
		super(p_i48487_1_);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
	    
	    try {
		Random rand = new Random();
		
//		if (!firstTime) {
//			firstTime = true;
//		}
		
//		if (firstTime) {
//		DropList.getItems();
//
//		if (!DropList.taglistcomplete) {
//			//DropList.tagList();
//		}
//			firstTime = false;
//		}

		//LootBagMod.LOGGER.info("*********************" + LootBagConfig.EnableWhitelist.get() + "******************");
		if (LootBagConfigNew.Common.EnableWhitelist.get()) {
			Collections.shuffle(DropList.whitelistedItems);
			
			int drops = LootBagConfigNew.Common.Drops.get();
			for (drops = 0; drops < LootBagConfigNew.Common.Drops.get(); drops++) {
				int n = rand.nextInt(DropList.whitelistedItems.size());
				int n2 = rand.nextInt(LootBagConfigNew.Common.StackSize.get());
				Collections.shuffle(DropList.whitelistedItems);
				if (LootBagConfigNew.Common.ItemsDropped.get()) {
					LootBagMod.LOGGER.info(DropList.whitelistedItems.get(n));
				}
				
				if (LootBagConfigNew.Common.ItemsDroppedChat.get()) {
					playerIn.displayClientMessage(Component.nullToEmpty(DropList.whitelistedItems.get(n)), false);
				}
				
				if (LootBagConfigNew.Common.ItemsInInventory.get()) {
					
					if (!new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString()))).isStackable()) {
						
						
							if (!(playerIn.getInventory().getFreeSlot() <= 0)) {
							    playerIn.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), 1));
							} else {
								playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), 1), true);
							}
						
						
						
					} else {
					if (n2 >= 1) {
						
					    if (!(playerIn.getInventory().getFreeSlot() <= 0)) {
							    playerIn.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), n2));
							} else {
								playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), n2), true);
							}
						
						
					} else if (n2 <= 0) {
						int n3 = n2 + 1;
					
						if (!(playerIn.getInventory().getFreeSlot() <= 0)) {
							    playerIn.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), n3));
							} else {
								playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), n3), true);
							}
						
					}
					}
					
				} else {
				
				if (new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString()))).isStackable() != true) {
					playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), 1), true);
					
				} else {
				if (n2 >= 1) {
					playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), n2), true);
				} else if (n2 <= 0) {
					int n3 = n2 + 1;
					playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.whitelistedItems.get(n).toString())), n3), true);
				}
				}
				}
			}

		} else {
		    
		    Collections.shuffle(DropList.potentialItems);
            
            int drops = LootBagConfigNew.Common.Drops.get();
            for (drops = 0; drops < LootBagConfigNew.Common.Drops.get(); drops++) {
                int n = rand.nextInt(DropList.potentialItems.size());
                int n2 = rand.nextInt(LootBagConfigNew.Common.StackSize.get());
                Collections.shuffle(DropList.potentialItems);
                if (LootBagConfigNew.Common.ItemsDropped.get() == true) {
                    LootBagMod.LOGGER.info(DropList.potentialItems.get(n));
                }
                
                if (LootBagConfigNew.Common.ItemsDroppedChat.get() == true) {
                    playerIn.displayClientMessage(Component.nullToEmpty(DropList.potentialItems.get(n)), false);
                }
                
                if (LootBagConfigNew.Common.ItemsInInventory.get()) {
                    
                    if (new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString()))).isStackable() != true) {
                        
                    
                            if (!(playerIn.getInventory().getFreeSlot() <= 0)) {
                                playerIn.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), 1));
                            } else {
                                playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), 1), true);
                            }
                        
                        
                        
                    } else {
                    if (n2 >= 1) {
                        
                        if (!(playerIn.getInventory().getFreeSlot() <= 0)) {
                                playerIn.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), n2));
                            } else {
                                playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), n2), true);
                            }
                        
                        
                    } else if (n2 <= 0) {
                        int n3 = n2 + 1;
                       
                            if (!(playerIn.getInventory().getFreeSlot() <= 0)) {
                                playerIn.addItem(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), n3));
                            } else {
                                playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), n3), true);
                            }
                        
                    }
                    }
                    
                } else {
                
                if (new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString()))).isStackable() != true) {
                    playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), 1), true);
                    
                } else {
                if (n2 >= 1) {
                    playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), n2), true);
                } else if (n2 <= 0) {
                    int n3 = n2 + 1;
                    playerIn.drop(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(DropList.potentialItems.get(n).toString())), n3), true);
                }
                }
                }
            }

		}
		
	    } catch (Exception e) {
            LootBagMod.LOGGER.info("LootBagMod Error: " + e);
        }

		// Consume exactly one loot bag in survival.
		// The old code checked the held stack with "getItemInHand(handIn).equals(new ItemStack(this))".
		// ItemStack does not override equals() in 1.20.1, so that was reference equality and was
		// always false - the bag was therefore never used up and could be opened forever.
		if (!playerIn.isCreative()) {
			playerIn.getItemInHand(handIn).shrink(1);
		}
		
		if (playerIn.isCreative()) {
			firstTime = true;
		}

		return super.use(worldIn, playerIn, handIn);
	}

}
