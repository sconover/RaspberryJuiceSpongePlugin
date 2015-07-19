package com.giantpurplekitty.raspberrysponge.game;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.DyeColors;

import static com.google.common.base.Preconditions.checkState;

public class TypeMappings {

  private static Map<BlockType, Integer> BLOCK_TYPE_TO_MINECRAFT_INT_ID = null;
  private static Map<Integer, BlockType> MINECRAFT_INT_ID_TO_BLOCK_TYPE = null;

  private static Map<Color, Integer> JAVA_AWT_COLOR_TO_MINECRAFT_INT_ID = null;
  private static Map<Integer, Color> MINECRAFT_INT_ID_TO_JAVA_AWT_COLOR = null;

  static {

    // see http://minecraft.gamepedia.com/Data_values/Block_IDs
    BLOCK_TYPE_TO_MINECRAFT_INT_ID =
        new LinkedHashMap<BlockType, Integer>() {{
          put(BlockTypes.AIR, 0);
          put(BlockTypes.STONE, 1);
          put(BlockTypes.GRASS, 2);
          put(BlockTypes.DIRT, 3);
          put(BlockTypes.COBBLESTONE, 4);
          put(BlockTypes.PLANKS, 5);
          put(BlockTypes.SAPLING, 6);
          put(BlockTypes.BEDROCK, 7);
          put(BlockTypes.FLOWING_WATER, 8);
          put(BlockTypes.WATER, 9);
          put(BlockTypes.FLOWING_LAVA, 10);
          put(BlockTypes.LAVA, 11);
          put(BlockTypes.SAND, 12);
          put(BlockTypes.GRAVEL, 13);
          put(BlockTypes.GOLD_ORE, 14);
          put(BlockTypes.IRON_ORE, 15);
          put(BlockTypes.COAL_ORE, 16);
          put(BlockTypes.LOG, 17);
          put(BlockTypes.LEAVES, 18);
          put(BlockTypes.SPONGE, 19);
          put(BlockTypes.GLASS, 20);
          put(BlockTypes.LAPIS_ORE, 21);
          put(BlockTypes.LAPIS_BLOCK, 22);
          put(BlockTypes.DISPENSER, 23);
          put(BlockTypes.SANDSTONE, 24);
          put(BlockTypes.NOTEBLOCK, 25);
          put(BlockTypes.BED, 26);
          put(BlockTypes.GOLDEN_RAIL, 27);
          put(BlockTypes.DETECTOR_RAIL, 28);
          put(BlockTypes.STICKY_PISTON, 29);
          put(BlockTypes.WEB, 30);
          put(BlockTypes.TALLGRASS, 31);
          put(BlockTypes.DEADBUSH, 32);
          put(BlockTypes.PISTON, 33);
          put(BlockTypes.PISTON_HEAD, 34);
          put(BlockTypes.WOOL, 35);
          put(BlockTypes.PISTON_EXTENSION, 36);
          put(BlockTypes.YELLOW_FLOWER, 37);
          put(BlockTypes.RED_FLOWER, 38);
          put(BlockTypes.BROWN_MUSHROOM, 39);
          put(BlockTypes.RED_MUSHROOM, 40);
          put(BlockTypes.GOLD_BLOCK, 41);
          put(BlockTypes.IRON_BLOCK, 42);
          put(BlockTypes.DOUBLE_STONE_SLAB, 43);
          put(BlockTypes.STONE_SLAB, 44);
          put(BlockTypes.BRICK_BLOCK, 45);
          put(BlockTypes.TNT, 46);
          put(BlockTypes.BOOKSHELF, 47);
          put(BlockTypes.MOSSY_COBBLESTONE, 48);
          put(BlockTypes.OBSIDIAN, 49);
          put(BlockTypes.TORCH, 50);
          put(BlockTypes.FIRE, 51);
          put(BlockTypes.MOB_SPAWNER, 52);
          put(BlockTypes.OAK_STAIRS, 53);
          put(BlockTypes.CHEST, 54);
          put(BlockTypes.REDSTONE_WIRE, 55);
          put(BlockTypes.DIAMOND_ORE, 56);
          put(BlockTypes.DIAMOND_BLOCK, 57);
          put(BlockTypes.CRAFTING_TABLE, 58);
          put(BlockTypes.WHEAT, 59);
          put(BlockTypes.FARMLAND, 60);
          put(BlockTypes.FURNACE, 61);
          put(BlockTypes.LIT_FURNACE, 62);
          put(BlockTypes.STANDING_SIGN, 63);
          put(BlockTypes.WOODEN_DOOR, 64);
          put(BlockTypes.LADDER, 65);
          put(BlockTypes.RAIL, 66);
          put(BlockTypes.STONE_STAIRS, 67);
          put(BlockTypes.WALL_SIGN, 68);
          put(BlockTypes.LEVER, 69);
          put(BlockTypes.STONE_PRESSURE_PLATE, 70);
          put(BlockTypes.IRON_DOOR, 71);
          put(BlockTypes.WOODEN_PRESSURE_PLATE, 72);
          put(BlockTypes.REDSTONE_ORE, 73);
          put(BlockTypes.LIT_REDSTONE_ORE, 74);
          put(BlockTypes.UNLIT_REDSTONE_TORCH, 75);
          put(BlockTypes.REDSTONE_TORCH, 76);
          put(BlockTypes.STONE_BUTTON, 77);
          put(BlockTypes.SNOW_LAYER, 78);
          put(BlockTypes.ICE, 79);
          put(BlockTypes.SNOW, 80);
          put(BlockTypes.CACTUS, 81);
          put(BlockTypes.CLAY, 82);
          put(BlockTypes.REEDS, 83);
          put(BlockTypes.JUKEBOX, 84);
          put(BlockTypes.FENCE, 85);
          put(BlockTypes.PUMPKIN, 86);
          put(BlockTypes.NETHERRACK, 87);
          put(BlockTypes.SOUL_SAND, 88);
          put(BlockTypes.GLOWSTONE, 89);
          put(BlockTypes.PORTAL, 90);
          put(BlockTypes.LIT_PUMPKIN, 91);
          put(BlockTypes.CAKE, 92);
          put(BlockTypes.UNPOWERED_REPEATER, 93);
          put(BlockTypes.POWERED_REPEATER, 94);
          put(BlockTypes.STAINED_GLASS, 95);
          put(BlockTypes.TRAPDOOR, 96);
          put(BlockTypes.MONSTER_EGG, 97);
          put(BlockTypes.STONEBRICK, 98);
          put(BlockTypes.BROWN_MUSHROOM_BLOCK, 99);
          put(BlockTypes.RED_MUSHROOM_BLOCK, 100);
          put(BlockTypes.IRON_BARS, 101);
          put(BlockTypes.GLASS_PANE, 102);
          put(BlockTypes.MELON_BLOCK, 103);
          put(BlockTypes.PUMPKIN_STEM, 104);
          put(BlockTypes.MELON_STEM, 105);
          put(BlockTypes.VINE, 106);
          put(BlockTypes.FENCE_GATE, 107);
          put(BlockTypes.BRICK_STAIRS, 108);
          put(BlockTypes.STONE_BRICK_STAIRS, 109);
          put(BlockTypes.MYCELIUM, 110);
          put(BlockTypes.WATERLILY, 111);
          put(BlockTypes.NETHER_BRICK, 112);
          put(BlockTypes.NETHER_BRICK_FENCE, 113);
          put(BlockTypes.NETHER_BRICK_STAIRS, 114);
          put(BlockTypes.NETHER_WART, 115);
          put(BlockTypes.ENCHANTING_TABLE, 116);
          put(BlockTypes.BREWING_STAND, 117);
          put(BlockTypes.CAULDRON, 118);
          put(BlockTypes.END_PORTAL, 119);
          put(BlockTypes.END_PORTAL_FRAME, 120);
          put(BlockTypes.END_STONE, 121);
          put(BlockTypes.DRAGON_EGG, 122);
          put(BlockTypes.REDSTONE_LAMP, 123);
          put(BlockTypes.LIT_REDSTONE_LAMP, 124);
          put(BlockTypes.DOUBLE_WOODEN_SLAB, 125);
          put(BlockTypes.WOODEN_SLAB, 126);
          put(BlockTypes.COCOA, 127);
          put(BlockTypes.SANDSTONE_STAIRS, 128);
          put(BlockTypes.EMERALD_ORE, 129);
          put(BlockTypes.ENDER_CHEST, 130);
          put(BlockTypes.TRIPWIRE_HOOK, 131);
          put(BlockTypes.TRIPWIRE, 132);
          put(BlockTypes.EMERALD_BLOCK, 133);
          put(BlockTypes.SPRUCE_STAIRS, 134);
          put(BlockTypes.BIRCH_STAIRS, 135);
          put(BlockTypes.JUNGLE_STAIRS, 136);
          put(BlockTypes.COMMAND_BLOCK, 137);
          put(BlockTypes.BEACON, 138);
          put(BlockTypes.COBBLESTONE_WALL, 139);
          put(BlockTypes.FLOWER_POT, 140);
          put(BlockTypes.CARROTS, 141);
          put(BlockTypes.POTATOES, 142);
          put(BlockTypes.WOODEN_BUTTON, 143);
          put(BlockTypes.SKULL, 144);
          put(BlockTypes.ANVIL, 145);
          put(BlockTypes.TRAPPED_CHEST, 146);
          put(BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE, 147);
          put(BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE, 148);
          put(BlockTypes.UNPOWERED_COMPARATOR, 149);
          put(BlockTypes.POWERED_COMPARATOR, 150);
          put(BlockTypes.DAYLIGHT_DETECTOR, 151);
          put(BlockTypes.REDSTONE_BLOCK, 152);
          put(BlockTypes.QUARTZ_ORE, 153);
          put(BlockTypes.HOPPER, 154);
          put(BlockTypes.QUARTZ_BLOCK, 155);
          put(BlockTypes.QUARTZ_STAIRS, 156);
          put(BlockTypes.ACTIVATOR_RAIL, 157);
          put(BlockTypes.DROPPER, 158);
          put(BlockTypes.STAINED_HARDENED_CLAY, 159);
          put(BlockTypes.STAINED_GLASS_PANE, 160);
          put(BlockTypes.LEAVES2, 161);
          put(BlockTypes.LOG2, 162);
          put(BlockTypes.ACACIA_STAIRS, 163);
          put(BlockTypes.DARK_OAK_STAIRS, 164);
          put(BlockTypes.SLIME, 165);
          put(BlockTypes.BARRIER, 166);
          put(BlockTypes.IRON_TRAPDOOR, 167);
          put(BlockTypes.PRISMARINE, 168);
          put(BlockTypes.SEA_LANTERN, 169);
          put(BlockTypes.HAY_BLOCK, 170);
          put(BlockTypes.CARPET, 171);
          put(BlockTypes.HARDENED_CLAY, 172);
          put(BlockTypes.COAL_BLOCK, 173);
          put(BlockTypes.PACKED_ICE, 174);
          put(BlockTypes.DOUBLE_PLANT, 175);
          put(BlockTypes.STANDING_BANNER, 176);
          put(BlockTypes.WALL_BANNER, 177);
          put(BlockTypes.DAYLIGHT_DETECTOR_INVERTED, 178);
          put(BlockTypes.RED_SANDSTONE, 179);
          put(BlockTypes.RED_SANDSTONE_STAIRS, 180);
          put(BlockTypes.DOUBLE_STONE_SLAB2, 181);
          put(BlockTypes.STONE_SLAB2, 182);
          put(BlockTypes.SPRUCE_FENCE_GATE, 183);
          put(BlockTypes.BIRCH_FENCE_GATE, 184);
          put(BlockTypes.JUNGLE_FENCE_GATE, 185);
          put(BlockTypes.DARK_OAK_FENCE_GATE, 186);
          put(BlockTypes.ACACIA_FENCE_GATE, 187);
          put(BlockTypes.SPRUCE_FENCE, 188);
          put(BlockTypes.BIRCH_FENCE, 189);
          put(BlockTypes.JUNGLE_FENCE, 190);
          put(BlockTypes.DARK_OAK_FENCE, 191);
          put(BlockTypes.ACACIA_FENCE, 192);
          put(BlockTypes.SPRUCE_DOOR, 193);
          put(BlockTypes.BIRCH_DOOR, 194);
          put(BlockTypes.JUNGLE_DOOR, 195);
          put(BlockTypes.ACACIA_DOOR, 196);
          put(BlockTypes.DARK_OAK_DOOR, 197);
        }};

    MINECRAFT_INT_ID_TO_BLOCK_TYPE = new LinkedHashMap<Integer, BlockType>();
    for (Map.Entry<BlockType, Integer> blockTypeAndIntegerId : BLOCK_TYPE_TO_MINECRAFT_INT_ID.entrySet()) {
      MINECRAFT_INT_ID_TO_BLOCK_TYPE.put(
          blockTypeAndIntegerId.getValue(),
          blockTypeAndIntegerId.getKey());
    }

    // see http://minecraft.gamepedia.com/Data_Values#Wool.2C_Stained_Clay.2C_Stained_Glass_and_Carpet
    JAVA_AWT_COLOR_TO_MINECRAFT_INT_ID =
        new LinkedHashMap<Color, Integer>() {{
          put(DyeColors.WHITE.getColor(), 0);
          put(DyeColors.ORANGE.getColor(), 1);
          put(DyeColors.MAGENTA.getColor(), 2);
          put(DyeColors.LIGHT_BLUE.getColor(), 3);
          put(DyeColors.YELLOW.getColor(), 4);
          put(DyeColors.LIME.getColor(), 5);
          put(DyeColors.PINK.getColor(), 6);
          put(DyeColors.GRAY.getColor(), 7);
          put(DyeColors.SILVER.getColor(), 8);
          put(DyeColors.CYAN.getColor(), 9);
          put(DyeColors.PURPLE.getColor(), 10);
          put(DyeColors.BLUE.getColor(), 11);
          put(DyeColors.BROWN.getColor(), 12);
          put(DyeColors.GREEN.getColor(), 13);
          put(DyeColors.RED.getColor(), 14);
          put(DyeColors.BLACK.getColor(), 15);
        }};

    MINECRAFT_INT_ID_TO_JAVA_AWT_COLOR = new LinkedHashMap<Integer, Color>();
    for (Map.Entry<Color, Integer> colorAndIntegerId : JAVA_AWT_COLOR_TO_MINECRAFT_INT_ID.entrySet()) {
      MINECRAFT_INT_ID_TO_JAVA_AWT_COLOR.put(
          colorAndIntegerId.getValue(),
          colorAndIntegerId.getKey());
    }
  }

  public static int getIntegerIdForBlockType(BlockType blockType) {
    checkState(BLOCK_TYPE_TO_MINECRAFT_INT_ID.containsKey(blockType),
        String.format("no mapping found for block_type=%s", blockType.getId()));
    return BLOCK_TYPE_TO_MINECRAFT_INT_ID.get(blockType);
  }

  public static BlockType getBlockTypeForIntegerId(int blockTypeId) {
    checkState(MINECRAFT_INT_ID_TO_BLOCK_TYPE.containsKey(blockTypeId),
        String.format("no mapping found for block_type=%d", blockTypeId));
    return MINECRAFT_INT_ID_TO_BLOCK_TYPE.get(blockTypeId);
  }

  public static int getIntegerIdForColor(Color color) {
    checkState(JAVA_AWT_COLOR_TO_MINECRAFT_INT_ID.containsKey(color),
        String.format("no mapping found for color=%s", color.toString()));
    return JAVA_AWT_COLOR_TO_MINECRAFT_INT_ID.get(color);
  }

  public static Color getColorForIntegerId(int colorId) {
    checkState(MINECRAFT_INT_ID_TO_BLOCK_TYPE.containsKey(colorId),
        String.format("no mapping found for color_id=%d", colorId));
    return MINECRAFT_INT_ID_TO_JAVA_AWT_COLOR.get(colorId);
  }
}
