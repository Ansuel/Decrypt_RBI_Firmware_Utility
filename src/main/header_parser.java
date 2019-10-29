/*******************************************************************************
 * Copyright (C) 2019, Christian Marangi
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package main;

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
	private static final Map<String, String> STATIC_HEADER;
	static {
		STATIC_HEADER = new LinkedHashMap<>();
		STATIC_HEADER.put("magic", new String(new byte[4]));
		STATIC_HEADER.put("fim", new String(new byte[2]));
		STATIC_HEADER.put("fia", new String(new byte[2]));
		STATIC_HEADER.put("prodid", new String(new byte[12]));
		STATIC_HEADER.put("varid", new String(new byte[12]));
		STATIC_HEADER.put("version", new String(new byte[4]));
		STATIC_HEADER.put("unknown", new String(new byte[4]));
		STATIC_HEADER.put("data_offset", new String(new byte[4]));
		STATIC_HEADER.put("data_size", new String(new byte[4]));
	}
	private static final Map<Byte, String> KNOWN_DYNAMIC_HEADER;
	static {
		KNOWN_DYNAMIC_HEADER = new HashMap<>();
		KNOWN_DYNAMIC_HEADER.put((byte) 0x2, "timestamp");
		KNOWN_DYNAMIC_HEADER.put((byte) 0x8, "boardname");
		KNOWN_DYNAMIC_HEADER.put((byte) 0x9, "prodname");
		KNOWN_DYNAMIC_HEADER.put((byte) 0xa, "varname");
		KNOWN_DYNAMIC_HEADER.put((byte) 0x20, "tagpparserversion");
		KNOWN_DYNAMIC_HEADER.put((byte) 0x81, "flashaddress");
	}
	
	private static String getNameFromId(Byte data) {
		return KNOWN_DYNAMIC_HEADER.get(data);
	}
	
	public static void parse(byte[] header,Map<String,String> header_table) {
		
		ByteArrayInputStream data = new ByteArrayInputStream(header);
		byte[] buf;
		byte[] id  = new byte[1];
		byte[] len  = new byte[1];
		int data_len;
		
		header_table.putAll(STATIC_HEADER);
		
		for (Map.Entry<String, String> entry : header_table.entrySet()) {
		    buf = new byte[entry.getValue().length()];
			data.read(buf, 0, entry.getValue().length());
			if (null == entry.getKey()) {
                entry.setValue(new String(buf));
            } else switch (entry.getKey()) {
                case "version":
                    entry.setValue(String.format("%d.%x.%x.%x", buf[0], buf[1], buf[2], buf[3]));
                    break;
                case "data_size":
                case "data_offset":
                    entry.setValue(new BigInteger(buf).toString());
                    break;
                default:
                    entry.setValue(new String(buf));
                    break;
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
