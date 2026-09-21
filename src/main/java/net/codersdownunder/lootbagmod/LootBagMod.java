package net.codersdownunder.lootbagmod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.codersdownunder.lootbagmod.droplist.DropList;
import net.codersdownunder.lootbagmod.events.LootHandler;
import net.codersdownunder.lootbagmod.init.ItemInit;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("lootbagmod")
public class LootBagMod
{	
	public static final String modid = "lootbagmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public LootBagMod() {
    	
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LootBagConfigNew.commonSpec);
    	bus.register(LootBagConfigNew.class);
       
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        ItemInit.ITEMS.register(bus);
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	MinecraftForge.EVENT_BUS.register(new LootHandler());
    	
    	event.enqueueWork(() -> {
    		DropList.getItems();
    	});
    	
    }


//    
    

    private void doClientStuff(final FMLClientSetupEvent event) {
     
    }

}
