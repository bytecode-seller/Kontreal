/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kontreal.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Dell
 */
public class FileUtils {
    public static String convertFileToString(UploadedFile file) throws IOException{
      String html = new BufferedReader(new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8))
              .lines()
              .collect(Collectors.joining("\n"));
      
      return html;
    }
}
