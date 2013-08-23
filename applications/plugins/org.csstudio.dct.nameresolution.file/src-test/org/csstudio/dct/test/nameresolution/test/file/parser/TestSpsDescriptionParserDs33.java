package org.csstudio.dct.test.nameresolution.test.file.parser;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.InputStream;
import java.util.List;

import org.csstudio.dct.nameresolution.file.parser.SpsDescriptionParser;
import org.csstudio.dct.nameresolution.file.types.DescriptionEntry;
import org.csstudio.dct.nameresolution.file.types.TcpConnectionNr;
import org.csstudio.dct.nameresolution.file.util.Utils;
import org.junit.Test;

public class TestSpsDescriptionParserDs33 {

    @Test
    public void testParserWithTcpConnection1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/fixtures/ds33.txt");
        List<String> content = Utils.readTextFromInputStream(is);
        SpsDescriptionParser spsDescriptionParser = new SpsDescriptionParser(new TcpConnectionNr(1), content,
                "ds33.txt");
        spsDescriptionParser.parse();

        List<DescriptionEntry> result = spsDescriptionParser.getParseResult();

        assertThat(result.size(), is(9));

        assertThat(result.get(0).getSpsAddress().getAddress(), is(0));
        assertThat(result.get(1).getSpsAddress().getAddress(), is(2));
        assertThat(result.get(2).getSpsAddress().getAddress(), is(8));
        assertThat(result.get(3).getSpsAddress().getAddress(), is(10));
        assertThat(result.get(4).getSpsAddress().getAddress(), is(16));
        assertThat(result.get(5).getSpsAddress().getAddress(), is(22));
        assertThat(result.get(6).getSpsAddress().getAddress(), is(28));
        assertThat(result.get(6).getSpsAddress().getBitPos().get(), is(0));
        assertThat(result.get(7).getSpsAddress().getAddress(), is(28));
        assertThat(result.get(7).getSpsAddress().getBitPos().get(), is(1));
        assertThat(result.get(8).getSpsAddress().getAddress(), is(30));
    }

}
