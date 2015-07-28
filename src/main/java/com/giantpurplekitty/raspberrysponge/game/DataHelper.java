package com.giantpurplekitty.raspberrysponge.game;

import java.util.Map;
import org.spongepowered.api.block.BlockState;

//TODO everything related to "data" needs to be deprecated for sure. This is nuts.
public class DataHelper {

  //TODO: validate the ordinal

  public static int getData(BlockState blockState) {
    if (blockState.hasPropertyEnum("color")) {
      return blockState.getPropertyEnumOrdinalValue("color");
    } else {
      return 0;
    }
  }

  public static BlockState setData(BlockState blockState, int data) {
    if (data == 0) {
      return blockState;
    } else if (blockState.hasPropertyEnum("color")) {
      return blockState.withPropertyEnumOrdinal("color", data);
    } else {
      throw new RuntimeException(
          String.format("Unsupported data value (%d) for block type (%s).",
              data, blockState.getType().getId()));
    }
  }

  public static BlockState setProperties(BlockState blockState,
      Map<String, String> propertyNameToStringifiedValue) {

    for (Map.Entry<String,String> propertyNameAndValue: propertyNameToStringifiedValue.entrySet()) {
      String propertyName = propertyNameAndValue.getKey();
      String stringifiedValue = propertyNameAndValue.getValue();
      //TODO: should be using a proper metadata api to make the conversion decisions...
      Comparable value = stringifiedValue;
      if (String.valueOf(Boolean.parseBoolean(stringifiedValue)).equals(stringifiedValue)) {
        value = Boolean.parseBoolean(stringifiedValue);
      } else {
        try{
          value = Integer.parseInt(stringifiedValue);
        } catch (NumberFormatException nex) {
          // ignore, just leave stringified value as-is
        }
      }
      blockState = blockState.withPropertyByPrimitives(propertyName, value);
    }
    return blockState;
  }
}
