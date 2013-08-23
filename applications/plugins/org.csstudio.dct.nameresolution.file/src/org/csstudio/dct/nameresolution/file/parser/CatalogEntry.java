package org.csstudio.dct.nameresolution.file.parser;

import org.csstudio.dct.nameresolution.file.types.TcpConnectionNr;

import com.google.common.base.Preconditions;

public class CatalogEntry {

    private final String fileName;

    private final TcpConnectionNr tcpConnectionNr;

    public CatalogEntry(final String fileName, final TcpConnectionNr tcpConnectionNr) {
        Preconditions.checkNotNull(fileName, "fileName must not be null");
        Preconditions.checkNotNull(tcpConnectionNr, "tcpConnectionNr must not be null");
        this.fileName = fileName;
        this.tcpConnectionNr = tcpConnectionNr;
    }

    public String getFileName() {
        return fileName;
    }

    public TcpConnectionNr getTcpConnectionNr() {
        return tcpConnectionNr;
    }

}
