package tyra314.toolprogression.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import tyra314.toolprogression.ToolProgressionMod;
import tyra314.toolprogression.compat.exnihilo.ECHelper;
import tyra314.toolprogression.compat.tconstruct.TiCHelper;
import tyra314.toolprogression.compat.tconstruct.TiCMiningLevels;
import tyra314.toolprogression.compat.waila.WailaPlugin;
import tyra314.toolprogression.config.ConfigHandler;
import tyra314.toolprogression.handlers.HarvestEventHandler;
import tyra314.toolprogression.harvest.Overwrites;

import java.io.File;


public class CommonProxy
{

    // ConfigHandler INSTANCE
    public static Configuration base_config;
    public static Configuration mining_level_config;

    public static Configuration blocks_config;
    public static Configuration block_overwrites_config;
    public static Configuration tools_config;
    public static Configuration tool_overwrites_config;
    public static Configuration mats_config;
    public static Configuration mat_overwrites_config;

    public void preInit(FMLPreInitializationEvent e)
    {
        File directory = e.getModConfigurationDirectory();
        base_config =
                new Configuration(new File(directory.getPath(), "tool_progression/general.cfg"));
        mining_level_config =
                new Configuration(new File(directory.getPath(),
                        "tool_progression/mining_level_names.cfg"));

        blocks_config =
                new Configuration(new File(directory.getPath(), "tool_progression/blocks.cfg"));
        blocks_config.removeCategory(blocks_config.getCategory("block"));

        block_overwrites_config =
                new Configuration(new File(directory.getPath(),
                        "tool_progression/block_overwrites.cfg"));

        tools_config =
                new Configuration(new File(directory.getPath(), "tool_progression/tools.cfg"));
        tools_config.removeCategory(tools_config.getCategory("tool"));

        tool_overwrites_config =
                new Configuration(new File(directory.getPath(),
                        "tool_progression/tool_overwrites.cfg"));

        mats_config = new Configuration(new File(directory.getPath(),
                "tool_progression/materials.cfg"));
        mats_config.removeCategory(mats_config.getCategory("material"));

        mat_overwrites_config =
                new Configuration(new File(directory.getPath(),
                        "tool_progression/materials_overwrites.cfg"));

        ConfigHandler.readBaseConfig();

        if (TiCHelper.isLoaded())
        {
            TiCHelper.preInit();
        }

        WailaPlugin.preInit();
    }

    public void init(@SuppressWarnings("unused") FMLInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(new HarvestEventHandler());

        if (TiCHelper.isLoaded())
        {
            TiCHelper.init();
            TiCMiningLevels.overwriteMiningLevels();
        }
    }

    public void postInit(@SuppressWarnings("unused") FMLPostInitializationEvent e)
    {

        blocks_config.addCustomCategoryComment("block",
                "The list of all block harvest levels with toolclass\nThis file will be generated on every launch\n >>> DO NOT EDIT THIS FILE <<<");

        tools_config.addCustomCategoryComment("tool",
                "The list of all tool harvest levels with toolclass\nThis file will be generated on every launch\n >>> DO NOT EDIT THIS FILE <<<");

        mats_config.addCustomCategoryComment("material",
                "The list of all tool materials with harvest level\nThis file will be generated on every launch\n >>> DO NOT EDIT THIS FILE <<<");

        ToolProgressionMod.logger.info("Start doing stupid things");

        for (Block block : GameRegistry.findRegistry(Block.class))
        {
            Overwrites.handleBlock(block);
        }

        for (Item item : GameRegistry.findRegistry(Item.class))
        {
            Overwrites.handleItem(item);
        }

        for (ItemTool.ToolMaterial mat : ItemTool.ToolMaterial.values())
        {
            Overwrites.handleMaterial(mat);
        }

        blocks_config.save();
        tools_config.save();
        mats_config.save();

        if (ECHelper.isLoaded())
        {
            ECHelper.postInit();
        }

        ToolProgressionMod.logger.info("Finished doing stupid things");
    }
}

