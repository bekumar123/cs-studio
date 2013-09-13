package org.csstudio.dal2.acceptance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class XMLConfigGenerator {

	private static String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<!DOCTYPE ou\n" +
			"  [\n" +
			"    <!ELEMENT ou (efan*)>\n" +
			"    <!ELEMENT efan (ecom* , eren*)>\n" +
			"    <!ELEMENT ecom (ecom* , eren*)>\n" +
			"    <!ELEMENT eren (#PCDATA)>\n" +
			"    <!ATTLIST ou name CDATA #REQUIRED>\n" +
			"    <!ATTLIST efan name CDATA #REQUIRED>\n" +
			"    <!ATTLIST ecom name CDATA #REQUIRED>\n" +
			"    <!ATTLIST eren name CDATA #REQUIRED>\n" +
			"  ]\n" +
			">\n" +
			"\n" +
			"<ou name=\"EpicsAlarmcfg\">\n" +
			"  <efan name=\"Test\">\n" +
			"    <ecom name=\"Test-IOC\">";

	
	
	private static String suffix = "    </ecom>\n  </efan>\n</ou>";
	
	
	public static String createEntry(int id) {
		// <eren name="Test:Ramp_calc_0" />
		return "      <eren name=\"Test:Ramp_calc_" + id + "\" />";
	}

	public static String createNotExistingEntry(int id) {
		return "      <eren name=\"Test:NotExisting_" + id + "\" />";
	}
	
	public static void main(String[] args) throws Exception {

		int numberOfPVs = 5000;

		{
			File file = new File("dal2jmsConfigDal2Test" + numberOfPVs + ".xml");
			PrintWriter writer = new PrintWriter(new FileOutputStream(file));
			writer.println(prefix);
			
			
			for (int i = 0; i < numberOfPVs; i++) {
				String entry = createEntry(i);

				writer.println(entry);
				// System.out.println(entry);
			}
			for (int i = 0; i < numberOfPVs; i++) {
				String entry = createNotExistingEntry(i);

				writer.println(entry);
				// System.out.println(entry);
			}
			

			writer.println(suffix);
			writer.close();
		}

	}

}
