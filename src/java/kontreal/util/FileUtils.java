package kontreal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Martin Tepostillo
 */
public class FileUtils {
    public static String convertFileToString(UploadedFile file) throws IOException{
      String html = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8))
              .lines()
              .collect(Collectors.joining("\n"));
      
      return html;
    }
}
