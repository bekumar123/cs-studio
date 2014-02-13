package org.csstudio.dal2.acceptance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class DBGenerator {

	public static String createRecord(int id, int value, int max) {
		return "record(calc, \"Test:Ramp_calc_" + id + "\") {\n" //
				+ "   field(DESC,\"Ramp 0 .. " + max + "\")\n" //
				+ "   field(SCAN, \".5 second\")\n"//
				+ "   field(VAL, \"" + value + "\")\n"//
				+ "   field(PINI, \"YES\")\n" //
				+ "   field(LOLO,\"0\")\n" //
				+ "   field(LLSV,\"MAJOR\")\n" //
				+ "   field(HIHI,\"" + (int) (max * 0.9) + "\")\n" //
				+ "   field(HHSV,\"MAJOR\")\n" //
				+ "   field(CALC,\"A<" + max + "?A+1:0\")\n" //
				+ "   field(INPA,\"Test:Ramp_calc_" + id + ".VAL NPP MS\")\n" //
				+ "   field(EGU, \"Counts\")\n" //
				+ "   field(LOPR,\"0\")\n" //
				+ "   field(HOPR,\"" + max + "\")\n" //
				+ "}\n";
	}

	public static void main(String[] args) throws Exception {

		int numberOfPVs = 5000;

		{
			File file = new File("PVs_" + numberOfPVs + "_re.db");
			PrintWriter writer = new PrintWriter(new FileOutputStream(file));
			for (int i = 0; i < numberOfPVs; i++) {

				//int max = (int) (Math.random() * 1000);
				int max = 10;
				int value = (int) (max * Math.random());

				String record = createRecord(i, value, max);

				writer.println(record);
				// System.out.println(record);
			}
			writer.close();
		}

		{
			File file = new File("dal2jmsConfigFragment_" + numberOfPVs + ".txt");
			PrintWriter writer = new PrintWriter(new FileOutputStream(file));
			int k= 0;
			for (int i = 0; i < numberOfPVs; i++) {
				writer.println("<eren name=\"Test:Ramp_calc_" + i + "\"/>");
				
				for (int j = 0; j < 8; j++) {
					if (Math.random() > 0.85) {
						writer.println("<eren name=\"Test:Invalid_" + (k++) + "\"/>");
					}
				}
			}
			writer.close();
			
			System.out.println("created " + k  + " invalid pvs");
		}
	}

}
