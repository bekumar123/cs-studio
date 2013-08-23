package org.csstudio.dct.nameresolution.internal;

import java.util.Collections;
import java.util.List;

import org.csstudio.dct.ServiceLookup;
import org.csstudio.dct.model.IRecord;
import org.csstudio.dct.nameresolution.IFieldFunction;
import org.csstudio.dct.nameresolution.file.service.IoNameResolutionFromFile;
import org.eclipse.jface.fieldassist.IContentProposal;

public class IoNameFileFieldFunction implements IFieldFunction {

   IoNameResolutionFromFile ioNameResolutionFromFileService;

   public IoNameFileFieldFunction() {
      ioNameResolutionFromFileService = ServiceLookup.getIoNameResolutionFromFileService();
   }

   @Override
   public String evaluate(String name, String[] parameters, IRecord record, String fieldName) throws Exception {
      return ioNameResolutionFromFileService.resolveName(parameters[0], fieldName);
   }

   @Override
   public List<IContentProposal> getParameterProposal(int parameterIndex, String[] knownParameters, IRecord record) {
      return Collections.emptyList();
   }

}
