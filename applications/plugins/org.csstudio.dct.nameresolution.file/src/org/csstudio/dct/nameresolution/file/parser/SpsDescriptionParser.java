package org.csstudio.dct.nameresolution.file.parser;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.dct.nameresolution.file.service.SpsParseException;
import org.csstudio.dct.nameresolution.file.types.DescriptionEntry;
import org.csstudio.dct.nameresolution.file.types.IoName;
import org.csstudio.dct.nameresolution.file.types.SpsAddress;
import org.csstudio.dct.nameresolution.file.types.SpsType;
import org.csstudio.dct.nameresolution.file.types.TcpConnectionNr;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SpsDescriptionParser {

    private final List<String> lines;
    private final TcpConnectionNr tcpConnectionNr;
    private final String fileName;

    private List<DescriptionEntry> parseResult;

    public SpsDescriptionParser(final TcpConnectionNr tcpConnectionNr, final List<String> lines, String fileName) {
        Preconditions.checkNotNull(tcpConnectionNr, "tcpConnectionNr must not be null");
        Preconditions.checkNotNull(lines, "lines must not be null");
        Preconditions.checkNotNull(fileName, "fileName must not be null");
        this.lines = lines;
        this.tcpConnectionNr = tcpConnectionNr;
        this.fileName = fileName;
    }

    public void parse() throws SpsParseException {
        parseResult = Lists.newArrayList();

        Optional<SpsType> lastSpsType = Optional.absent();
        SpsAddress nextAddress = new SpsAddress(0);

        for (String line : lines) {

            if (!line.startsWith(Constant.COMMENT_TOKEN)) {

                ArrayList<String> parts = Lists.newArrayList(Splitter.on(Constant.CSV_SEPARATOR).split(line));
                if (parts.size() > 1) {

                    SpsType currentSpsType = SpsType.getSpsType(parts.get(1).trim());
                    nextAddress = currentSpsType.calculateAddress(nextAddress, lastSpsType);
                    IoName ioName = new IoName(parts.get(0).trim());

                    if (!ioName.isEmpty()) {
                        //@formatter:off
                        DescriptionEntry descriptionEntry = new DescriptionEntry(
                                tcpConnectionNr,   
                                ioName, 
                                currentSpsType,
                                nextAddress,
                                fileName);
                                //@formatter:on                            
                        parseResult.add(descriptionEntry);
                    }

                    lastSpsType = Optional.of(currentSpsType);

                }

            }
        }

    }

    public List<DescriptionEntry> getParseResult() {
        if (parseResult == null) {
            throw new IllegalStateException("no parseResult available");
        }
        return ImmutableList.copyOf(parseResult);
    }

}
