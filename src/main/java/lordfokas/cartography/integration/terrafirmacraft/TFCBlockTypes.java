package lordfokas.cartography.integration.terrafirmacraft;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.types.Ore;
import net.dries007.tfc.common.types.Rock;
import net.dries007.tfc.common.types.Wood;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.RegistryObject;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TFCBlockTypes {
    private static final Rock.BlockType[] STONE_TYPES = new Rock.BlockType[]{ Rock.BlockType.RAW, Rock.BlockType.HARDENED};
    private static final Wood.BlockType[] LOG_TYPES = new Wood.BlockType[]{ Wood.BlockType.LOG, Wood.BlockType.STRIPPED_LOG };

    private static final Map<Block, Type> TYPES = new HashMap<>();
    private static final Map<Classification, Map<Block, String>> BLOCKS = new EnumMap<>(Classification.class);

    public static Type getType(Block block){
        return TYPES.get(block);
    }

    public static Tuple<String, Type> getName(Block block, Classification ... classifications){
        Type type = TYPES.get(block);
        if(type != null){
            for(Classification classification : classifications){
                if(type.classification != classification) continue;
                String name = BLOCKS.computeIfAbsent(classification, c -> new HashMap<>()).get(block);
                return new Tuple<>(name, type);
            }
        }
        return null;
    }

    public static Tuple<String, Type> getName(Block block, Type ... types){
        Type type = TYPES.get(block);
        if(type != null){
            for(Type t : types){
                if(t != type) continue;
                String name = BLOCKS.computeIfAbsent(t.classification, c -> new HashMap<>()).get(block);
                return new Tuple<>(name, type);
            }
        }
        return null;
    }

    public static ResourceLocation getTexturePath(String name){
        String[] parts = name.split(":");
        if(parts.length != 2) return null;
        String type = parts[0], variant = parts[1];
        switch(type) {
            case "ORE":
            case "STONE":
                return new ResourceLocation("tfc", "textures/block/rock/raw/" + variant + ".png");
            case "GRAVEL":
                return new ResourceLocation("tfc", "textures/block/rock/gravel/" + variant + ".png");
            case "DIRT":
                return new ResourceLocation("tfc", "textures/block/dirt/" + variant + ".png");
            case "CLAY":
                return new ResourceLocation("tfc", "textures/block/clay/" + variant + ".png");
            case "SAND":
                return new ResourceLocation("tfc", "textures/block/sand/" + variant + ".png");
            case "SAPLING":
                return new ResourceLocation("tfc", "textures/block/wood/sapling/" + variant + ".png");
            default: return null;
        }
    }

    public enum Classification {
        ROCK,
        SEDIMENT,
        FLUID,
        TREE
    }

    public enum Type {
        STONE(Classification.ROCK),
        ORE(Classification.ROCK),
        GRAVEL(Classification.ROCK),
        DIRT(Classification.SEDIMENT),
        CLAY(Classification.SEDIMENT),
        SAND(Classification.SEDIMENT),
        LOG(Classification.TREE),
        LEAVES(Classification.TREE),
        SAPLING(Classification.TREE),
        WATER(Classification.FLUID);

        public final Classification classification;
        Type(Classification classification){ this.classification = classification; }
    }

    static {
        // ROCKS  ======================================================================================================
        for(Rock.Default rock : Rock.Default.values()){
            String name = rock.name().toLowerCase();
            for(Rock.BlockType type : STONE_TYPES){
                put(Type.STONE, TFCBlocks.ROCK_BLOCKS.get(rock).get(type).get(), name);
            }
            put(Type.GRAVEL, TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.GRAVEL).get(), name);
            for(RegistryObject<Block> obj : TFCBlocks.ORES.get(rock).values()){
                put(Type.ORE, obj.get(), name);
            }
            for(Map<Ore.Grade, RegistryObject<Block>> ores : TFCBlocks.GRADED_ORES.get(rock).values()) {
                for (RegistryObject<Block> obj : ores.values()) {
                    put(Type.ORE, obj.get(), name);
                }
            }
        }

        // SOILS  ======================================================================================================
        for(SoilBlockType.Variant var : SoilBlockType.Variant.values()){
            String name = var.name().toLowerCase();
            for(SoilBlockType type : SoilBlockType.values()){
                Block block = TFCBlocks.SOIL.get(type).get(var).get();
                if(type == SoilBlockType.CLAY || type == SoilBlockType.CLAY_GRASS){
                    put(Type.CLAY, block, name);
                }else{
                    put(Type.DIRT, block, name);
                }
            }
        }
        put(Type.DIRT, TFCBlocks.PEAT.get(), "peat");
        put(Type.DIRT, TFCBlocks.PEAT_GRASS.get(), "peat");
        for(SandBlockType sand : SandBlockType.values()){
            String name = sand.name().toLowerCase();
            put(Type.SAND, TFCBlocks.SAND.get(sand).get(), name);
        }

        // WOODS  ======================================================================================================
        for(Wood.Default wood : Wood.Default.values()){
            String name = wood.name().toLowerCase();
            for(Wood.BlockType log : LOG_TYPES){
                put(Type.LOG, TFCBlocks.WOODS.get(wood).get(log).get(), name);
            }
            put(Type.LEAVES, TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LEAVES).get(), name);
            put(Type.SAPLING, TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SAPLING).get(), name);
        }

        // WATER  ======================================================================================================
        put(Type.WATER, TFCBlocks.SALT_WATER.get(), "salt_water");
        put(Type.WATER, TFCBlocks.SPRING_WATER.get(), "spring_water");
        put(Type.WATER, Blocks.WATER.getBlock(), "fresh_water");
    }

    private static void put(Type type, Block block, String name){
        BLOCKS.computeIfAbsent(type.classification, c -> new HashMap<>()).put(block, name);
        TYPES.put(block, type);
    }
}