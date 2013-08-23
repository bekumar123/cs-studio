package org.csstudio.dct.nameresolution.file.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public final class Utils {

   private Utils() {
   }

   public static List<String> readTextFromFile(String fileName) throws IOException {

      Preconditions.checkNotNull(fileName, "fileName must not be null");

      File file = new File(fileName);
      if (!file.exists()) {
         throw new IllegalStateException("File not found: " + fileName);
      }
      InputStream is = new FileInputStream(file);
      try {
         return Utils.readTextFromInputStream(is);
      } finally {
         if (is != null) {
            is.close();
         }
      }
   }

   public static List<String> readTextFromInputStream(InputStream is) throws IOException {

      Preconditions.checkNotNull(is, "InputStream must not be null");

      BufferedReader br = null;
      List<String> content = Lists.newArrayList();

      String line;

      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
         content.add(line);
      }

      if (br != null) {
         br.close();
      }

      return content;

   }
}
