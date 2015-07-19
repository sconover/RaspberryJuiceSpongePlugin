package com.giantpurplekitty.raspberrysponge;

import com.google.common.base.Optional;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.slf4j.Logger;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandCallable;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

@Plugin(id = "RaspberryJuiceTestRunnerPlugin", name = "RaspberryJuiceTestRunnerPlugin", version = "0.1")
public class RaspberryJuiceTestRunnerPlugin {

  public static final String TEST_CLASSES_COMPILE_PATH = "../../target/classes";
  private final Logger logger;

  @Inject
  public RaspberryJuiceTestRunnerPlugin(Logger logger) {
    this.logger = logger;
  }

  @Subscribe
  public void onServerStarting(ServerStartingEvent event) {
    event.getGame().getCommandDispatcher().register(this, new TestCommand(), "test");
    event.getGame().getCommandDispatcher().register(this, new TCommand(), "t");

    InWorldTestSupport.init(event.getGame(), logger);
  }

  public class TestCommand implements CommandCallable {
    public Optional<CommandResult> process(CommandSource commandSource, String arguments)
        throws CommandException {

      ClassesAndClassesAndMethods classesAndClassesAndMethods = scanDir(TEST_CLASSES_COMPILE_PATH);

      Request request = null;
      if (!arguments.isEmpty()) {
        request = junitRequestFromSearchString(arguments, classesAndClassesAndMethods);
        if (request == null) {
          logger.info(String.format("'%s' not found.", arguments));
          return Optional.of(CommandResult.empty());
        } else {
          rememberTest(arguments);
        }
      } else {
        request = Request.classes(
            classesAndClassesAndMethods.classes.toArray(
                new Class[classesAndClassesAndMethods.classes.size()]));
      }

      JUnitCore runner = new JUnitCore();
      runner.addListener(new TestRunListener(logger));
      runner.run(request);

      return Optional.of(CommandResult.success());
    }

    public List<String> getSuggestions(CommandSource commandSource, String arguments)
        throws CommandException {
      return new ArrayList<String>();
    }

    public boolean testPermission(CommandSource commandSource) {
      return true;
    }

    public Optional<? extends Text> getShortDescription(CommandSource commandSource) {
      return Optional.of(Texts.of("Run one or more tests."));
    }

    public Optional<? extends Text> getHelp(CommandSource commandSource) {
      return Optional.of(Texts.of(
          "Run one or more tests. " +
          "Zero arguments runs all tests. " +
          "Optional argument is used to search for either a test class or method, " +
          "to run in isolation."));
    }

    public Text getUsage(CommandSource commandSource) {
      return Texts.of("/test [test class or method name]");
    }
  }

  public class TCommand implements CommandCallable {
    public Optional<CommandResult> process(CommandSource commandSource, String arguments)
        throws CommandException {

      logger.info(String.format("Last test run is '%s'", getLastTest()));

      ClassesAndClassesAndMethods classesAndClassesAndMethods = scanDir(TEST_CLASSES_COMPILE_PATH);

      Request request = null;
      if (getLastTest() == null) {
        request = Request.classes(
            classesAndClassesAndMethods.classes.toArray(
                new Class[classesAndClassesAndMethods.classes.size()]));
      } else {
        request = junitRequestFromSearchString(getLastTest(), classesAndClassesAndMethods);
        if (request == null) {
          logger.info(String.format("'%s' not found.", getLastTest()));
          return Optional.of(CommandResult.empty());
        }
      }

      JUnitCore runner = new JUnitCore();
      runner.addListener(new TestRunListener(logger));
      runner.run(request);

      return Optional.of(CommandResult.success());
    }

    public List<String> getSuggestions(CommandSource commandSource, String arguments)
        throws CommandException {
      return new ArrayList<String>();
    }

    public boolean testPermission(CommandSource commandSource) {
      return true;
    }

    public Optional<? extends Text> getShortDescription(CommandSource commandSource) {
      return Optional.of(Texts.of("Run the previously-run test or tests, or all tests."));
    }

    public Optional<? extends Text> getHelp(CommandSource commandSource) {
      return Optional.of(Texts.of(
          "Runs the previously-run /test invocation, " +
              "or if this is the first invocation, run all tests."));
    }

    public Text getUsage(CommandSource commandSource) {
      return Texts.of("/t");
    }
  }

  private void rememberTest(String testName) {
    //hackey way of getting last test to survive class reload, which makes /t much more convenient
    System.setProperty("lastTest", testName);
  }

  private String getLastTest() {
    return System.getProperty("lastTest");
  }

  private ClassesAndClassesAndMethods scanDir(final String testClassesCompilePath) {
    final List<Class> testClasses = new ArrayList<Class>();
    final List<ClassAndMethod> testClassAndMethod = new ArrayList<ClassAndMethod>();
    try {
      Files.walkFileTree(new File(testClassesCompilePath).toPath(), new SimpleFileVisitor<Path>() {
        @Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
            throws IOException {
          if (file.toString().endsWith("Test.class")) {
            String className =
                file.toString()
                    .replace(testClassesCompilePath + "/", "")
                    .replace(".class", "")
                    .replace('/', '.');
            try {
              Class testClass = Class.forName(className);
              testClasses.add(testClass);

              for (Method m : testClass.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Test.class)) {
                  testClassAndMethod.add(new ClassAndMethod(testClass, m.getName()));
                }
              }
            } catch (ClassNotFoundException e) {
              throw new RuntimeException(e);
            }
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return new ClassesAndClassesAndMethods(testClasses, testClassAndMethod);
  }

  private Request junitRequestFromSearchString(String search, ClassesAndClassesAndMethods classesAndClassesAndMethods) {
    Request request = null;
    Optional<Class> classResult = Optional.absent();
    for (Class k: classesAndClassesAndMethods.classes) {
      if (k.getSimpleName().equals(search)) {
        classResult = Optional.of(k);
        break;
      }
    }
    if (classResult.isPresent()) {
      request = Request.classes(classResult.get());
    } else {
      Optional<ClassAndMethod> methodResult = Optional.absent();
      for (ClassAndMethod classAndMethod: classesAndClassesAndMethods.classesAndMethods) {
        if (classAndMethod.getMethod().equals(search)) {
          methodResult = Optional.of(classAndMethod);
          break;
        }
      }
      if (methodResult.isPresent()) {
        request = Request.method(methodResult.get().getKlass(), methodResult.get().getMethod());
      } else {
        return null;
      }
    }
    return request;
  }

  public static class ClassesAndClassesAndMethods {
    public final List<Class> classes;
    public final List<ClassAndMethod> classesAndMethods;

    public ClassesAndClassesAndMethods(List<Class> classes,
        List<ClassAndMethod> classesAndMethods) {
      this.classes = classes;
      this.classesAndMethods = classesAndMethods;
    }
  }

  public static class ClassAndMethod {
    private final Class klass;
    private final String method;

    public ClassAndMethod(Class klass, String method) {
      this.klass = klass;
      this.method = method;
    }

    public Class getKlass() {
      return klass;
    }

    public String getMethod() {
      return method;
    }

    public String toString() {
      return String.format("%s#%s", klass.getName(), method);
    }
  }

  public static class TestRunListener extends org.junit.runner.notification.RunListener {

    private final Logger logger;

    public TestRunListener(Logger logger) {
      this.logger = logger;
    }

    private void println(String s) {
      logger.info("[TEST] " + s);
    }

    @Override public void testRunFinished(Result result) throws Exception {
      println(
          String.format(
              "RESULT: %s -- %d tests, %d failures, %d ignored, in %d seconds.",
              (result.wasSuccessful() ? "PASS" : "FAIL"),
              result.getRunCount(),
              result.getFailureCount(),
              result.getIgnoreCount(),
              (result.getRunTime() / 1000)
          ));
    }

    @Override public void testStarted(Description description) throws Exception {
      printDescription("-- ", description);
    }

    @Override public void testFinished(Description description) throws Exception {
      // printDescription("FINISHED: ", description);
    }

    @Override public void testFailure(Failure failure) throws Exception {
      println("FAILURE: " + failure.toString());
      println(failure.getTrace());
    }

    @Override public void testAssumptionFailure(Failure failure) {
      println("FAILURE: " + failure.toString());
      println(failure.getTrace());
    }

    @Override public void testIgnored(Description description) throws Exception {
      printDescription("IGNORED: ", description);
    }

    private void printDescription(String prefix, Description description) throws IOException {
      println(prefix + description.toString());
    }
  }
}
