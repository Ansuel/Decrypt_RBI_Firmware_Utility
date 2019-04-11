package decrypt_rbi;

/*
0x4 MAGIC VALUE
0x2 fim
0x2 fia
0x12 prodid
0x12 varid
0x4 version hex value 01 02 03 04 = 1.2.3.4
0x4 data_offset 0x28
0x4 data_size 0x2c
*/

/* dynamic
 * id len of data
02 04 timestamp
08 06 boardname
09 13 prodname
0a 07 varname
20 03 tagpparserversion
81 04 flashaddress
 */

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class header_parser {
	private static Map<String, String> static_header;
	static {
		static_header = new LinkedHashMap<>();
		static_header.put("magic", new String(new byte[4]));
		static_header.put("fim", new String(new byte[2]));
		static_header.put("fia", new String(new byte[2]));
		static_header.put("prodid", new String(new byte[12]));
		static_header.put("varid", new String(new byte[12]));
		static_header.put("version", new String(new byte[4]));
		static_header.put("unknown", new String(new byte[4]));
		static_header.put("data_offset", new String(new byte[4]));
		static_header.put("data_size", new String(new byte[4]));
	}
	private static Map<Byte, String> known_dynamic_header;
	static {
		known_dynamic_header = new HashMap<>();
		known_dynamic_header.put((byte) 0x2, "timestamp");
		known_dynamic_header.put((byte) 0x8, "boardname");
		known_dynamic_header.put((byte) 0x9, "prodname");
		known_dynamic_header.put((byte) 0xa, "varname");
		known_dynamic_header.put((byte) 0x20, "tagpparserversion");
		known_dynamic_header.put((byte) 0x81, "flashaddress");
	}
	
	private static String getNameFromId(Byte data) {
		return known_dynamic_header.get(data);
	}
	
	public static void parse(byte[] header,Map<String,String> header_table) {
		
		ByteArrayInputStream data = new ByteArrayInputStream(header);
		byte[] buf;
		byte[] id  = new byte[1];
		byte[] len  = new byte[1];
		int data_len = 0;
		
		header_table.putAll(static_header);
		
		for (Map.Entry<String, String> entry : header_table.entrySet()) {
		    buf = new byte[entry.getValue().length()];
			data.read(buf,0,entry.getValue().length());
			if (entry.getKey() == "version") {
				entry.setValue(new String(buf[0]+"."+buf[1]+"."+buf[2]+"."+buf[3]));
			} else if (entry.getKey() == "data_size" || entry.getKey() == "data_offset") {
				entry.setValue(new BigInteger(buf).toString());
			} else {
				entry.setValue(new String(buf));
			}
		}
		
		//Skip unknown data that we don't know what are for.
		// Signature? Hash? 
		data.skip(0x104);

		while(data.available()!=0) {
			data.read(id,0,1);
			data.read(len,0,1);
			data_len = new BigInteger(len).intValue();
			buf = new byte[data_len];
			data.read(buf, 0 ,data_len);
			if ( id[0] == (byte) 0x02 || id[0] == (byte) 0x81) {
				StringBuilder string = new StringBuilder(buf.length);
				string.append("0x");
				for (int j=0; j<buf.length; j++) {
					   string.append(String.format("%02X", buf[j]));
				}
				header_table.put(getNameFromId(id[0]), string.toString());
			} else {
				header_table.put(getNameFromId(id[0]), new String(buf));
			}
		}
		
	}
}
