package com.giantpurplekitty.raspberrysponge.dispatch;

import com.giantpurplekitty.raspberrysponge.api.OriginalApi;
import com.giantpurplekitty.raspberrysponge.api.V2Api;
import com.giantpurplekitty.raspberrysponge.game.GameWrapper;
import com.giantpurplekitty.raspberrysponge.raspberryserver.RemoteSession;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

public class ApiInvocationHandler {
  private final GameWrapper gameWrapper;
  private final Logger logger;
  private final RemoteSession.Out out;

  //private final ArrayDeque<BlockRightClickHook> blockHitQueue =
  //    new ArrayDeque<BlockRightClickHook>();

  // TODO: isolate all of this reflection stuff, make it more robust, test reporting of errors and failures and misconfigurations, etc
  private final Map<Pair<String, Integer>, Pair<Object, Method>>
      apiMethodNameAndParameterCountToApiObjectAndMethod =
      new LinkedHashMap<Pair<String, Integer>, Pair<Object, Method>>();
  private final Map<String, Pair<Object, Method>>
      apiMethodNameAcceptingRawArgStringToApiObjectAndMethod =
      new LinkedHashMap<String, Pair<Object, Method>>();

  public ApiInvocationHandler(
      GameWrapper gameWrapper,
      Logger logger,
      RemoteSession.Out out) { // TODO: move ToOutQueue to top level

    this.gameWrapper = gameWrapper;
    this.logger = logger;
    this.out = out;

    registerApiMethods(new OriginalApi(gameWrapper));
    registerApiMethods(new V2Api(gameWrapper));
  }

  private void registerApiMethods(Object api) {
    for (Method m : api.getClass().getMethods()) {
      if (m.isAnnotationPresent(RPC.class)) {
        String apiMethodName = m.getAnnotation(RPC.class).value();

        if (m.getParameterAnnotations().length == 1 &&
            m.getParameterAnnotations()[0].getClass().equals(RawArgString.class)) {
          apiMethodNameAcceptingRawArgStringToApiObjectAndMethod.put(
              apiMethodName,
              ImmutablePair.of(api, m));
        } else {
          apiMethodNameAndParameterCountToApiObjectAndMethod.put(
              ImmutablePair.of(apiMethodName, m.getParameterTypes().length),
              ImmutablePair.of(api, m));
        }
      }
    }
  }

  public void handleLine(String line) {
    //System.out.println(line);
    String methodName = line.substring(0, line.indexOf("("));
    //split string into args, handles , inside " i.e. ","
    String rawArgStr = line.substring(line.indexOf("(") + 1, line.length() - 1);
    String[] args = rawArgStr.equals("") ? new String[] {} : rawArgStr.split(",");
    //System.out.println(methodName + ":" + Arrays.toString(args));
    handleCommand(methodName, args, rawArgStr);
  }

  protected void handleCommand(String c, String[] args, String rawArgStr) {

    try {
      Pair<String, Integer> key = ImmutablePair.of(c, args.length);

      Object apiObject = null;
      Method method = null;
      Object[] convertedArgs = null;

      if (apiMethodNameAcceptingRawArgStringToApiObjectAndMethod.containsKey(c)) {
        Pair<Object, Method> apiObjectAndMethod =
            apiMethodNameAcceptingRawArgStringToApiObjectAndMethod.get(c);
        apiObject = apiObjectAndMethod.getLeft();
        method = apiObjectAndMethod.getRight();

        convertedArgs = new String[] {rawArgStr};
      } else if (apiMethodNameAndParameterCountToApiObjectAndMethod.containsKey(key)) {
        Pair<Object, Method> apiObjectAndMethod =
            apiMethodNameAndParameterCountToApiObjectAndMethod.get(key);
        apiObject = apiObjectAndMethod.getLeft();
        method = apiObjectAndMethod.getRight();

        convertedArgs = ApiIO.convertArguments(args, method);
      }

      if (method != null) {
        if (method.getReturnType().equals(Void.TYPE)) {
          method.invoke(apiObject, convertedArgs);
        } else {
          Object result = method.invoke(apiObject, convertedArgs);
          out.send(ApiIO.serializeResult(result));
        }
        return;
      }

      logger.warn(c + " is not supported.");
      out.send("Fail");
    } catch (Exception e) {

      logger.warn("Error occured handling command");
      e.printStackTrace();
      out.send("Fail");
    }
  }
}
