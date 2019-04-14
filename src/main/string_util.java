package main;

public class string_util {
    public static byte[] toByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    public static String ByteToString(byte[] bArray) {
    	StringBuilder s = new StringBuilder();
    	
    	for(byte b : bArray){
    	    s.append(String.format("%02X", b));
    	}
    	
    	return s.toString();
    }
}