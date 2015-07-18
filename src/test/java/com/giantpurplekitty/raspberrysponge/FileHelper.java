package com.giantpurplekitty.raspberrysponge;

import com.google.common.base.Joiner;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.input.ReversedLinesFileReader;

/**
 * Convenience methods for manipulating files, for testing purposes.
 */
public class FileHelper {
  public static String readEndOfLogfile() {
    return readFileBackwards("logs/latest.log", 20);
  }

  public static String readFileBackwards(String path, int numLines) {
    try {
      ReversedLinesFileReader reader =
          new ReversedLinesFileReader(new File(path));

      List<String> lines = new ArrayList<String>();
      int count = numLines;
      String line = reader.readLine();
      while (count > 0 && line != null) {
        lines.add(line);
        count--;
        line = reader.readLine();
      }
      return Joiner.on("\n").join(lines);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
