package org.csstudio.dct.nameresolution.file.parser;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.nameresolution.file.service.SpsParseException;
import org.csstudio.dct.nameresolution.file.types.TcpConnectionNr;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class CatalogParser {

   private static final int ENTRIES_PER_LINE = 2;

   private final List<String> lines;

   private List<CatalogEntry> parseResult;

   public CatalogParser(final List<String> lines) {
      Preconditions.checkNotNull(lines, "lines must not be null");
      this.lines = lines;
   }

   public void parse() throws SpsParseException {

      parseResult = Lists.newArrayList();

      List<TcpConnectionNr> tcpConnections = Lists.newArrayList();

      for (String line : lines) {
         if (!line.startsWith(Constant.COMMENT_TOKEN)) {
            ArrayList<String> parts = Lists.newArrayList(Splitter.on(Constant.CSV_SEPARATOR).split(line));
            if (parts.size() == ENTRIES_PER_LINE) {
               TcpConnectionNr tcpConnectionNr = new TcpConnectionNr(Integer.parseInt(parts.get(0)));
               if (tcpConnections.contains(tcpConnectionNr)) {
                  throw new SpsParseException("Duplicate entry for TCP-Connection: "
                        + tcpConnectionNr.getTcpConnectionNr());
               }
               tcpConnections.add(tcpConnectionNr);
               parseResult.add(new CatalogEntry(parts.get(1), tcpConnectionNr));
            } else {
               throw new SpsParseException("Invalid format");
            }
         }
      }
   }

   public List<CatalogEntry> getParseResult() {
      if (parseResult == null) {
         throw new IllegalStateException("no parseResult available");
      }
      return ImmutableList.copyOf(parseResult);
   }

}
