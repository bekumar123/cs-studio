package org.csstudio.dct.nameresolution.file.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

public class IoNameDictionary {

   private Map<IoName, DescriptionEntry> dictionary;

   public IoNameDictionary() {
      dictionary = new HashMap<IoName, DescriptionEntry>();
   }

   public void addEntries(List<DescriptionEntry> entries) {
      Preconditions.checkNotNull(entries, "entries must not be null");
      for (DescriptionEntry descriptionEntry : entries) {
         if (dictionary.containsKey(descriptionEntry.getIoName())) {
            //@formatter:off
            throw new IllegalStateException("duplicate entry for ioname: " 
                  + descriptionEntry.getIoName().getIoName()
                  + ". Name exists in file " + descriptionEntry.getFileName() 
                  + " and "
                  + dictionary.get(descriptionEntry.getIoName()).getFileName());
                  //@formatter:on
         }
         dictionary.put(descriptionEntry.getIoName(), descriptionEntry);
      }
   }

   public EpicsAddress get(IoName ioName) {
      Preconditions.checkNotNull(ioName, "ioName must not be null");
      if (dictionary.containsKey(ioName)) {
         return dictionary.get(ioName).getEcpisAddress();
      } else {
         return new EpicsAddress("<Error: unknown " + ioName.getIoName() + ">");
      }
   }

}
