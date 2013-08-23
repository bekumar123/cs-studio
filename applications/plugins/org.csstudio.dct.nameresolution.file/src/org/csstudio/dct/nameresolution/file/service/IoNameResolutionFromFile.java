package org.csstudio.dct.nameresolution.file.service;

import java.io.IOException;

public interface IoNameResolutionFromFile {

   void loadCatalog(String catalog) throws SpsParseException, IOException;

   String resolveName(String ioname, String fieldName);

   void clearDictionary();
}
