package org.csstudio.diag.jmssender.views;


import java.text.SimpleDateFormat;
import java.util.Date;

public class SJCAUtils  {
  public static final SimpleDateFormat defaultFormatter= new SimpleDateFormat(
    "MMM dd, yyyy HH:mm:ss.SSS");

  /**
   * Default constructor
   */
  public SJCAUtils() {
  }

  /**
   * Generates a timestamp
   * @return String timestamp with the current time
   */
  public static String timeStamp() {
    Date now=new Date();
    return defaultFormatter.format(now);
  }

  /**
   * Generates a timestamp given a pattern
   * @param pattern appropriate for SimpleDateFormat
   * @return String timestamp with the current time
   */
  public static String timeStamp(String pattern) {
    SimpleDateFormat dateFormatter= new SimpleDateFormat(pattern);
    Date now=new Date();
    return dateFormatter.format(now);
  }

}