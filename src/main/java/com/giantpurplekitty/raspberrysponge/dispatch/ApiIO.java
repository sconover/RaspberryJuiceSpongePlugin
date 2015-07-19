package com.giantpurplekitty.raspberrysponge.dispatch;

import com.giantpurplekitty.raspberrysponge.game.TypeMappings;
import com.google.common.base.Joiner;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.player.Player;

public class ApiIO {
  public static Object[] convertArguments(String[] args, Method m) {
    // TODO: validate args length == method param length

    Object[] convertedArgs = new Object[args.length];
    Class<?>[] parameterTypes = m.getParameterTypes();
    for (int i = 0; i < args.length; i++) {
      convertedArgs[i] = convertArgument(args[i], parameterTypes[i]);
    }
    return convertedArgs;
  }

  public static Object convertArgument(String arg, Class parameterType) {
    if (parameterType.equals(String.class)) {
      return arg;
    } else if (parameterType.equals(int.class)) {
      // TODO: validate the string
      return Integer.parseInt(arg);
    } else if (parameterType.equals(short.class)) {
      // TODO: validate the string
      return Short.parseShort(arg);
    } else if (parameterType.equals(float.class)) {
      // TODO: validate the string
      return Float.parseFloat(arg);
    }
    throw new RuntimeException(String.format(
        "not sure how to convert arg %s to %s", arg, parameterType.getName()));
  }

  public static String serializeResult(Object objectResult) {
    if (objectResult instanceof BlockType) {
      return String.valueOf(TypeMappings.getIntegerIdForBlockType(((BlockType) objectResult)));
    }
    //} else if (objectResult instanceof BlockType[]) {
    //  BlockType[] blockTypes = (BlockType[]) objectResult;
    //  String[] strings = new String[blockTypes.length];
    //  for (int i = 0; i < blockTypes.length; i++) {
    //    strings[i] = serializeResult(blockTypes[i]);
    //  }
    //  return serializeResult(strings);
    //} else if (objectResult instanceof String[]) {
    //  return Joiner.on(",").join((String[]) objectResult);
    else if (objectResult instanceof Pair) {
      Pair pair = (Pair) objectResult;
      return Joiner.on(",").join(
          serializeResult(pair.getLeft()),
          serializeResult(pair.getRight()));
    }
    //} else if (objectResult instanceof Short) {
    //  return String.valueOf(objectResult);
    //} else if (objectResult instanceof String) {
    //  return (String) objectResult;
    else if (objectResult instanceof Player) {
      return String.valueOf(((Player) objectResult).getIdentifier());
    } else if (objectResult instanceof Player[]) {
      Player[] players = (Player[]) objectResult;
      List<String> strings = new ArrayList<String>();
      for (Player p : players) {
        strings.add(serializeResult(p));
      }
      return Joiner.on("|").join(strings);
    }
    //} else if (objectResult instanceof OriginalApi.BlockEvent) {
    //  return ((OriginalApi.BlockEvent) objectResult).toApiResult();
    //} else if (objectResult instanceof OriginalApi.BlockEvent[]) {
    //  OriginalApi.BlockEvent[] blockEvents = (OriginalApi.BlockEvent[]) objectResult;
    //  List<String> strings = new ArrayList<String>();
    //  for (OriginalApi.BlockEvent blockEvent : blockEvents) {
    //    strings.add(serializeResult(blockEvent));
    //  }
    //  return Joiner.on("|").join(strings);
    //} else if (objectResult instanceof OriginalApi.BlockPosition) {
    //  return ((OriginalApi.BlockPosition) objectResult).toApiResult();
    //} else if (objectResult instanceof Vector3D) {
    //  return vectorToApiString((Vector3D) objectResult);
    //} else if (objectResult instanceof Position) {
    //  return positionToApiString((Position) objectResult);
    //} else if (objectResult instanceof Float) {
    //  return String.format("%f", (Float) objectResult);
    else if (objectResult instanceof Integer) {
      return String.valueOf((Integer) objectResult);
    }
    throw new RuntimeException(String.format(
        "not sure how to serialize %s %s",
        objectResult.getClass().getName(),
        objectResult.toString()));
  }
}
