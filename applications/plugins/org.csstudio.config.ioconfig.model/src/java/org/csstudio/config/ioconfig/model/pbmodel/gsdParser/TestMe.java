package org.csstudio.config.ioconfig.model.pbmodel.gsdParser;

public class TestMe {

    public static void main(String[] args) {
        String hex = "FFEF";
        int value =  (Integer.parseInt(hex,16) ^ (Integer.parseInt("FFFF",16))) + 1;
        System.out.println(value * -1);
        
        int dataMinBit = 0;
        int dataMaxBit = 7;
        int val = 17;
        
        // mask for minBit = 1 and maxBit = 3 is gives a mask of 14
        final int mask = (int) (Math.pow(2, dataMaxBit + 1) - Math.pow(2, dataMinBit));

        System.out.println(mask);

        System.out.println("***");
        System.out.println(mask);
        System.out.println(val);

        
        // 15 & 14 = 1111 & 1110 = 1110
        val = (val & mask);

        System.out.println("***");
        System.out.println(mask);
        System.out.println(val);

        // 1110 >> 1 = 0111 => 7
        val = val >> dataMinBit;
        
      
        
    }
}
