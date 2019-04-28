package main;

import javax.xml.bind.DatatypeConverter;

public class string_util {
    public static byte[] hexStringToByteArray(String s) {
          return DatatypeConverter.parseHexBinary(s);
    }
    
    public static String bytesToHexString(byte[] bArray) {
        return DatatypeConverter.printHexBinary(bArray);
    }
}