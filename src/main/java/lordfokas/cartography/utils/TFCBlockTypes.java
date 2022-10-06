package lordfokas.cartography.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.RegistryObject;

import lordfokas.cartography.Cartography;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.crop.Crop;
import net.dries007.tfc.common.blocks.plant.fruit.FruitBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.soil.SandBlockType;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.common.blocks.wood.Wood;

public class TFCBlockTypes {
    private static final Rock.BlockType[] STONE_TYPES = new Rock.BlockType[] {Rock.BlockType.RAW, Rock.BlockType.HARDENED};
    private static final Wood.BlockType[] LOG_TYPES = new Wood.BlockType[] {Wood.BlockType.LOG, Wood.BlockType.STRIPPED_LOG};
    private static final Map<Block, Profile> TYPES = new HashMap<>();

    public static Profile getProfile(Block block) {
        return TYPES.get(block);
    }

    public static Profile getProfile(Block block, Classification... classifications) {
        Profile profile = TYPES.get(block);
        if(profile != null) {
            for(Classification classification : classifications) {
                if(profile.type.classification != classification) continue;
                return profile;
            }
        }
        return null;
    }

    public static Profile getProfile(Block block, Type... types) {
        Profile profile = TYPES.get(block);
        if(profile != null) {
            for(Type t : types) {
                if(t != profile.type) continue;
                return profile;
            }
        }
        return null;
    }

    public static ResourceLocation getTexturePath(Profile profile) {
        return switch(profile.type) {
            case ORE, STONE -> new ResourceLocation("tfc", "textures/block/rock/raw/" + profile.name + ".png");
            case GRAVEL -> new ResourceLocation("tfc", "textures/block/rock/gravel/" + profile.name + ".png");
            case DIRT -> new ResourceLocation("tfc", "textures/block/dirt/" + profile.name + ".png");
            case CLAY -> new ResourceLocation("tfc", "textures/block/clay/" + profile.name + ".png");
            case SAND -> new ResourceLocation("tfc", "textures/block/sand/" + profile.name + ".png");
            case SAPLING -> new ResourceLocation("tfc", "textures/block/wood/sapling/" + profile.name + ".png");
            default -> null;
        };
    }

    public static ResourceLocation getLooseRockTexturePath(String rock) {
        return new ResourceLocation("tfc", "textures/item/loose_rock/" + rock + ".png");
    }

    public static ResourceLocation getNuggetTexturePath(String nugget) {
        return new ResourceLocation("tfc", "textures/item/ore/normal_" + nugget + ".png");
    }

    public static ResourceLocation getFruitTexturePath(String fruit) {
        return new ResourceLocation("tfc", "textures/item/food/" + fruit + ".png");
    }

    public static ResourceLocation getCropTexturePath(String crop) {
        return new ResourceLocation("tfc", "textures/item/food/" + crop + ".png");
    }

    public static class Profile {
        public final Type type;
        public final String name;

        public Profile(Type type, String name) {
            this.type = type;
            this.name = name;
        }
    }

    public enum Classification {
        ROCK,
        SOIL,
        FLUID,
        TREE,
        DISCOVERY
    }

    public enum Type {
        STONE(Classification.ROCK),
        ORE(Classification.ROCK),
        GRAVEL(Classification.ROCK),
        DIRT(Classification.SOIL),
        CLAY(Classification.SOIL),
        SAND(Classification.SOIL),
        LOG(Classification.TREE),
        LEAVES(Classification.TREE),
        SAPLING(Classification.TREE),
        WATER(Classification.FLUID),
        NUGGET(Classification.DISCOVERY),
        CROP(Classification.DISCOVERY),
        FRUIT(Classification.DISCOVERY);

        public final Classification classification;

        Type(Classification classification) {this.classification = classification;}
    }

    static {
        Cartography.LOGGER.info("Initializing TFCBlockTypes");

        // ROCKS  ======================================================================================================
        for(Rock rock : Rock.values()) {
            String name = rock.name().toLowerCase(Locale.ROOT);
            for(Rock.BlockType type : STONE_TYPES) {
                put(Type.STONE, TFCBlocks.ROCK_BLOCKS.get(rock).get(type).get(), name);
            }
            put(Type.GRAVEL, TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.GRAVEL).get(), name);
            for(RegistryObject<Block> obj : TFCBlocks.ORES.get(rock).values()) {
                put(Type.ORE, obj.get(), name);
            }
            for(Map<Ore.Grade, RegistryObject<Block>> ores : TFCBlocks.GRADED_ORES.get(rock).values()) {
                for(RegistryObject<Block> obj : ores.values()) {
                    put(Type.ORE, obj.get(), name);
                }
            }
        }

        // SOILS  ======================================================================================================
        for(SoilBlockType.Variant var : SoilBlockType.Variant.values()) {
            String name = var.name().toLowerCase(Locale.ROOT);
            for(SoilBlockType type : SoilBlockType.values()) {
                Block block = TFCBlocks.SOIL.get(type).get(var).get();
                if(type == SoilBlockType.CLAY || type == SoilBlockType.CLAY_GRASS) {
                    put(Type.CLAY, block, name);
                }
                else {
                    put(Type.DIRT, block, name);
                }
            }
        }
        put(Type.DIRT, TFCBlocks.PEAT.get(), "peat");
        put(Type.DIRT, TFCBlocks.PEAT_GRASS.get(), "peat");
        for(SandBlockType sand : SandBlockType.values()) {
            String name = sand.name().toLowerCase(Locale.ROOT);
            put(Type.SAND, TFCBlocks.SAND.get(sand).get(), name);
        }

        // WOODS  ======================================================================================================
        for(Wood wood : Wood.values()) {
            String name = wood.name().toLowerCase(Locale.ROOT);
            for(Wood.BlockType log : LOG_TYPES) {
                put(Type.LOG, TFCBlocks.WOODS.get(wood).get(log).get(), name);
            }
            put(Type.LEAVES, TFCBlocks.WOODS.get(wood).get(Wood.BlockType.LEAVES).get(), name);
            put(Type.SAPLING, TFCBlocks.WOODS.get(wood).get(Wood.BlockType.SAPLING).get(), name);
        }

        // WATER  ======================================================================================================
        put(Type.WATER, TFCBlocks.SALT_WATER.get(), "salt_water");
        put(Type.WATER, TFCBlocks.SPRING_WATER.get(), "spring_water");
        put(Type.WATER, Blocks.WATER.delegate.get(), "fresh_water");

        // NUGGETS  ====================================================================================================
        for(Ore ore : Ore.values()) {
            if(!ore.isGraded()) continue;
            put(Type.NUGGET, TFCBlocks.SMALL_ORES.get(ore).get(), ore.name().toLowerCase(Locale.ROOT));
        }

        // FRUITS  =====================================================================================================
        for(FruitBlocks.Tree tree : FruitBlocks.Tree.values()){
            String name = tree.name().toLowerCase(Locale.ROOT);
            put(Type.FRUIT, TFCBlocks.FRUIT_TREE_BRANCHES.get(tree).get(), name);
            put(Type.FRUIT, TFCBlocks.FRUIT_TREE_GROWING_BRANCHES.get(tree).get(), name);
            put(Type.FRUIT, TFCBlocks.FRUIT_TREE_LEAVES.get(tree).get(), name);
            put(Type.FRUIT, TFCBlocks.FRUIT_TREE_SAPLINGS.get(tree).get(), name);
        }

        // CROPS  ======================================================================================================
        for(Crop crop : Crop.values()){
            put(Type.CROP, TFCBlocks.WILD_CROPS.get(crop).get(), crop.name().toLowerCase(Locale.ROOT));
        }

        Cartography.LOGGER.info("Done");
    }

    private static void put(Type type, Block block, String name) {
        TYPES.put(block, new Profile(type, name));
    }
}
