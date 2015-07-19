package com.giantpurplekitty.raspberrysponge.game;

import java.awt.Color;
import net.minecraft.block.BlockColored;
import net.minecraft.item.EnumDyeColor;
import org.spongepowered.api.block.BlockState;

import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getColorForIntegerId;
import static com.giantpurplekitty.raspberrysponge.game.TypeMappings.getIntegerIdForColor;

//TODO everything related to "data" needs to be deprecated for sure. This is nuts.
public class DataHelper {
  public static int getData(BlockState blockState) {
    if (hasColor(blockState)) {
      // assume "data" is a color. see TypeMappings for possible values.
      return getIntegerIdForColor(getDyeColor(blockState));
    } else {
      return 0;
    }
  }

  public static BlockState setData(BlockState blockState, int data) {
    if (data == 0) {
      return blockState;
    } else if (hasColor(blockState)) {
      // assume "data" is a color. see TypeMappings for possible values.
      return setDyeColor(blockState, getColorForIntegerId(data));
    } else {
      throw new RuntimeException(
          String.format("Unsupported data value (%d) for block type (%s).",
              data, blockState.getType().getId()));
    }
  }

  // this is a temporary means of setting properties, while sponge devs are working on data 2.0
  // once that's done, we can remove the plugin dependency on SpongeCommon, and depend only on
  // SpongeApi

  private static boolean hasColor(BlockState blockState) {
    net.minecraft.block.state.BlockState.StateImplementation nmBlockState =
        (net.minecraft.block.state.BlockState.StateImplementation) blockState;
    return nmBlockState.getProperties().containsKey(BlockColored.COLOR);
  }

  private static BlockState setDyeColor(BlockState blockState, Color color) {
    int integerIdForColor = TypeMappings.getIntegerIdForColor(color);
    net.minecraft.block.state.BlockState.StateImplementation nmBlockState =
        (net.minecraft.block.state.BlockState.StateImplementation) blockState;
    return (BlockState)nmBlockState.withProperty(BlockColored.COLOR, getEnumDyeColorByOrdinal(integerIdForColor));
  }

  private static Color getDyeColor(BlockState blockState) {
    net.minecraft.block.state.BlockState.StateImplementation nmBlockState =
        (net.minecraft.block.state.BlockState.StateImplementation) blockState;
    EnumDyeColor enumDyeColor = (EnumDyeColor)nmBlockState.getProperties().get(BlockColored.COLOR);
    return getColorForIntegerId(enumDyeColor.ordinal());
  }

  private static EnumDyeColor getEnumDyeColorByOrdinal(int integerIdForColor) {
    for (EnumDyeColor dyeColor: EnumDyeColor.values()) {
      if (dyeColor.ordinal() == integerIdForColor) {
        return dyeColor;
      }
    }
    return null;
  }

}
