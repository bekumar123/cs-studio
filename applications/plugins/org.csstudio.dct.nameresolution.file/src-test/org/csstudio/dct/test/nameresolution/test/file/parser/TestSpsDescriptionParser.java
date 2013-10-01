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

public class TestSpsDescriptionParser {

    @Test
    public void testParser() throws Exception {
        InputStream is = getClass().getResourceAsStream("/fixtures/complex.txt");
        List<String> content = Utils.readTextFromInputStream(is);
        SpsDescriptionParser spsDescriptionParser = new SpsDescriptionParser(new TcpConnectionNr(0), content,
                "complex.txt");
        spsDescriptionParser.parse();
        List<DescriptionEntry> result = spsDescriptionParser.getParseResult();

        assertThat(result.size(), is(37));

        assertThat(result.get(0).getSpsAddress().getAddress(), is(0));
        assertThat(result.get(0).getSpsAddress().getBitPos().get(), is(0));

        assertThat(result.get(1).getSpsAddress().getAddress(), is(0));
        assertThat(result.get(1).getSpsAddress().getBitPos().get(), is(1));

        assertThat(result.get(2).getSpsAddress().getAddress(), is(0));
        assertThat(result.get(2).getSpsAddress().getBitPos().get(), is(2));

        assertThat(result.get(3).getSpsAddress().getAddress(), is(0));
        assertThat(result.get(3).getSpsAddress().getBitPos().get(), is(3));

        assertThat(result.get(4).getSpsAddress().getAddress(), is(0));
        assertThat(result.get(4).getSpsAddress().getBitPos().get(), is(4));

        assertThat(result.get(5).getSpsAddress().getAddress(), is(0));
        assertThat(result.get(5).getSpsAddress().getBitPos().get(), is(5));

        assertThat(result.get(6).getSpsAddress().getAddress(), is(1));
        assertThat(result.get(7).getSpsAddress().getAddress(), is(2));
        assertThat(result.get(8).getSpsAddress().getAddress(), is(4));
        assertThat(result.get(9).getSpsAddress().getAddress(), is(6));
        assertThat(result.get(10).getSpsAddress().getAddress(), is(8));
        assertThat(result.get(11).getSpsAddress().getAddress(), is(12));
        assertThat(result.get(12).getSpsAddress().getAddress(), is(14));
        assertThat(result.get(13).getSpsAddress().getAddress(), is(18));
        assertThat(result.get(14).getSpsAddress().getAddress(), is(22));
        assertThat(result.get(15).getSpsAddress().getAddress(), is(24));

        assertThat(result.get(16).getSpsAddress().getAddress(), is(30));
        assertThat(result.get(16).getSpsAddress().getBitPos().get(), is(0));

        assertThat(result.get(17).getSpsAddress().getAddress(), is(30));
        assertThat(result.get(17).getSpsAddress().getBitPos().get(), is(1));

        assertThat(result.get(18).getSpsAddress().getAddress(), is(30));
        assertThat(result.get(18).getSpsAddress().getBitPos().get(), is(2));

        assertThat(result.get(19).getSpsAddress().getAddress(), is(30));
        assertThat(result.get(19).getSpsAddress().getBitPos().get(), is(3));

        assertThat(result.get(20).getSpsAddress().getAddress(), is(30));
        assertThat(result.get(20).getSpsAddress().getBitPos().get(), is(4));

        assertThat(result.get(21).getSpsAddress().getAddress(), is(30));
        assertThat(result.get(21).getSpsAddress().getBitPos().get(), is(5));

        assertThat(result.get(22).getSpsAddress().getAddress(), is(30));
        assertThat(result.get(22).getSpsAddress().getBitPos().get(), is(6));

        assertThat(result.get(23).getSpsAddress().getAddress(), is(30));
        assertThat(result.get(23).getSpsAddress().getBitPos().get(), is(7));

        assertThat(result.get(24).getSpsAddress().getAddress(), is(31));
        assertThat(result.get(24).getSpsAddress().getBitPos().get(), is(0));

        assertThat(result.get(25).getSpsAddress().getAddress(), is(31));
        assertThat(result.get(25).getSpsAddress().getBitPos().get(), is(1));

        assertThat(result.get(26).getSpsAddress().getAddress(), is(31));
        assertThat(result.get(26).getSpsAddress().getBitPos().get(), is(2));

        assertThat(result.get(27).getSpsAddress().getAddress(), is(32));
        assertThat(result.get(28).getSpsAddress().getAddress(), is(33));
        assertThat(result.get(29).getSpsAddress().getAddress(), is(34));
        assertThat(result.get(30).getSpsAddress().getAddress(), is(36));
        assertThat(result.get(31).getSpsAddress().getAddress(), is(40));
        assertThat(result.get(32).getSpsAddress().getAddress(), is(42));
        assertThat(result.get(33).getSpsAddress().getAddress(), is(298));
        assertThat(result.get(34).getSpsAddress().getAddress(), is(300));
        assertThat(result.get(35).getSpsAddress().getAddress(), is(302));
        assertThat(result.get(36).getSpsAddress().getAddress(), is(308));
        assertThat(result.get(36).getSpsAddress().getBitPos().get(), is(0));

    }

    @Test
    public void testParserWithSimpleData() throws Exception {
        InputStream is = getClass().getResourceAsStream("/fixtures/test.txt");
        List<String> content = Utils.readTextFromInputStream(is);
        SpsDescriptionParser spsDescriptionParser = new SpsDescriptionParser(new TcpConnectionNr(0), content,
                "test.txt");
        spsDescriptionParser.parse();
        List<DescriptionEntry> result = spsDescriptionParser.getParseResult();

        assertThat(result.size(), is(12));

        assertThat(result.get(0).getSpsAddress().getAddress(), is(0));
        assertThat(result.get(1).getSpsAddress().getAddress(), is(1));
        assertThat(result.get(2).getSpsAddress().getAddress(), is(2));
        assertThat(result.get(3).getSpsAddress().getAddress(), is(4));
        assertThat(result.get(4).getSpsAddress().getAddress(), is(6));
        assertThat(result.get(5).getSpsAddress().getAddress(), is(8));
        assertThat(result.get(6).getSpsAddress().getAddress(), is(12));
        assertThat(result.get(7).getSpsAddress().getAddress(), is(14));
        assertThat(result.get(8).getSpsAddress().getAddress(), is(18));
        assertThat(result.get(9).getSpsAddress().getAddress(), is(22));
        assertThat(result.get(10).getSpsAddress().getAddress(), is(24));
        assertThat(result.get(11).getSpsAddress().getAddress(), is(30));

    }

    @Test
    public void testParserWithTcpConnection1() throws Exception {
        InputStream is = getClass().getResourceAsStream("/fixtures/complex.txt");
        List<String> content = Utils.readTextFromInputStream(is);
        SpsDescriptionParser spsDescriptionParser = new SpsDescriptionParser(new TcpConnectionNr(1), content,
                "complex.txt");
        spsDescriptionParser.parse();
        List<DescriptionEntry> result = spsDescriptionParser.getParseResult();
        assertThat(result.get(0).getEcpisAddress().getAddress(), is("@Siemens_S7: 1/0 'T=BOOL B=0'"));
    }

}
