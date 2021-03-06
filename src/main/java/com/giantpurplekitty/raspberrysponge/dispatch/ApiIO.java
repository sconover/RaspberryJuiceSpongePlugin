package com.giantpurplekitty.raspberrysponge.dispatch;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.giantpurplekitty.raspberrysponge.api.OriginalApi;
import com.giantpurplekitty.raspberrysponge.game.GameWrapper;
import com.giantpurplekitty.raspberrysponge.game.LocationSnapshot;
import com.giantpurplekitty.raspberrysponge.game.TypeMappings;
import com.google.common.base.Joiner;
import java.lang.reflect.Method;
import java.util.*;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

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
    } else if (parameterType.equals(Map.class)) {
      // TODO: validate the string
      String[] pairs = arg.split("\\;");
      Map<String,String> keyToValue = new LinkedHashMap<String, String>();
      for (String pair: pairs) {
        String[] keyAndValue = pair.split("=");
        String key = keyAndValue[0];
        String value = keyAndValue[1];
        keyToValue.put(key, value);
      }
      return keyToValue;
    }
    throw new RuntimeException(String.format(
        "not sure how to convert arg %s to %s", arg, parameterType.getName()));
  }

  public static String serializeResult(Object objectResult, GameWrapper game) {
    if (objectResult instanceof BlockType) {
      return String.valueOf(TypeMappings.getIntegerIdForBlockType(((BlockType) objectResult)));
    } else if (objectResult instanceof Pair) {
      Pair pair = (Pair) objectResult;
      return Joiner.on(",").join(
          serializeResult(pair.getLeft(), game),
          serializeResult(pair.getRight(), game));
    } else if (objectResult instanceof Player) {
      return String.valueOf(((Player) objectResult).getEntityId());
    } else if (objectResult instanceof Player[]) {
      Player[] players = (Player[]) objectResult;
      List<String> strings = new ArrayList<String>();
      for (Player p : players) {
        strings.add(serializeResult(p, game));
      }
      return Joiner.on("|").join(strings);
    } else if (objectResult instanceof Vector3i) {
      Vector3i v = ((Vector3i) objectResult);
      return String.format("%d,%d,%d", v.getX(), v.getY(), v.getZ());
    } else if (objectResult instanceof OriginalApi.Direction) {
      Vector3d v = ((OriginalApi.Direction) objectResult).vector;
      return String.format("%f,%f,%f", v.getX(), v.getY(), v.getZ());
    } else if (objectResult instanceof Vector3d) {
      Vector3d v = ((Vector3d) objectResult);
      return String.format("%.1f,%.1f,%.1f", v.getX(), v.getY(), v.getZ());
    } else if (objectResult instanceof LocationSnapshot) {
      LocationSnapshot locationSnapshot = ((LocationSnapshot) objectResult);
      return locationSnapshot.toApiResultString();
    } else if (objectResult instanceof Float) {
      return String.valueOf((Float) objectResult);
    } else if (objectResult instanceof Integer) {
      return String.valueOf((Integer) objectResult);
    } else if (objectResult instanceof Entity[]) {
      Entity[] entities = (Entity[]) objectResult;
      List<String> strings = new ArrayList<String>();
      for (Entity entity: entities) {
        strings.add(serializeResult(entity, game));
      }
      return Joiner.on(",").join(strings);
    } else if (objectResult instanceof Entity) {
      Entity entity = (Entity)objectResult;
      return String.format("%s:%s",
          game.getSupportedEntityTypeToName().get(entity.getType()),
          entity.getUniqueId().toString());
    }
    throw new RuntimeException(String.format(
        "not sure how to serialize %s %s",
        objectResult.getClass().getName(),
        objectResult.toString()));
  }
}
