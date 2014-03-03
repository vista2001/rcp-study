package com.qualityeclipse.favorites.wizards;

/**
 * The key/value pair of a string to be extracted
 */
public class ExtractedString
{
   private String key;
   private String value;

   public ExtractedString(String key, String value) {
      this.key = key;
      this.value = value;
   }

   public String getKey() {
      return key;
   }

   public String getValue() {
      return value;
   }
}
