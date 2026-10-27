package net.codersdownunder.lootbagmod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.codersdownunder.lootbagmod.droplist.DropList;
import net.codersdownunder.lootbagmod.init.ItemInit;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod("lootbagmod")
public class LootBagMod
{	
	public static final String modid = "lootbagmod";
    public static final Logger LOGGER = LogManager.getLogger();

    public LootBagMod() {

    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, LootBagConfigNew.commonSpec);
    	bus.register(LootBagConfigNew.class);

        bus.addListener(this::setup);

        ItemInit.ITEMS.register(bus);

        // LootHandler carries @Mod.EventBusSubscriber(bus = FORGE) and is registered by Forge on its
        // own; the extra MinecraftForge.EVENT_BUS.register(new LootHandler()) in setup() (and the
        // register(this) here, for a class without a single @SubscribeEvent method) used to make
        // every mob kill and every chest table run the handlers twice.
    }

    private void setup(final FMLCommonSetupEvent event)
    {
    	// R-06: the pool is only ever drawn from on the server (H-02), so a client must not scan the
    	// whole item registry for a list it will never read. DropList still rebuilds lazily and
    	// thread-safely on the first bag, so the dedicated server keeps its startup warm-up.
    	if (FMLEnvironment.dist.isClient()) {
    		return;
    	}
    	event.enqueueWork(DropList::rebuild);
    }
}
