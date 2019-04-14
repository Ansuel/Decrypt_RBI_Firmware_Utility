package decrypt_rbi;

import java.util.LinkedHashMap;
import java.util.Map;

public class osck_table {
	
	// Add here the osck key
	// Put in the first value the desription of the model
	// Put in the second value the OSCK key in the HEX value
	public static Map<String, String> osck_table;
	static {
		osck_table = new LinkedHashMap<>();
		osck_table.put("TG789vac v2","546259AFD4E85AA6FFCE358CE0A93452E25A848138A67C142E42FEC79F4F3784");
		osck_table.put("TG799vac","7FA2FDF4D4DC31BF66F91DDA9A3E8777B7D7D2EC6E8DB1926C0831CA2A279FDB");
		osck_table.put("TG800vac","8E07111F188641948E84506DB65270BD26595AD41327235A53998DB068DC3833");
		osck_table.put("TG789vac Xtream","FCD9BE1D6D8EA65968E77A89B8AFCA98A1467FEEE87A87BD276C91DD94D41D59");
		osck_table.put("DJN2130","222C4DC4A9DF952B02D5A489A112CF5E29AAEDF86ADB634410D6721F15F451E4");
		osck_table.put("DGA4130","FFD56A4E3A21401BF1798B3CD8AD54D238BA80039623BBA08B6D50B8EC73F7B4");
		osck_table.put("TG799vn-v2","A484245CCFBE2541B0C5C5E923BE67A7DEB9A823DD5CBAB92CC619DEA1391A42");
		osck_table.put("DGA4131","916AEB569D8CBF8CFAF060AEC533D43A9EF0ACB3138F8351C4112674212975A5");
		osck_table.put("DGA4132","0EF34D972945869EF40F89873FED30269020E107685C097751BEF9479D75D620");
		osck_table.put("DJA0230","7BFFB7EBBE416D38078712EC5AC5DEF6E4E50EE58848D6F2C072DF6E0C6CEFE7");
		osck_table.put("TG799vn-v2","EFA9268D1455DF20E8F73084E5D67F3D3B91961680E54732178BD7EC5D94AAC3");
	}
	
	// Add here the connection between model and boardname value
	// This will help to autodetect the model based on the boardname
	// The second value should be equal to the osck linked to the model
	public static Map<String, String> boardname_map;
	static {
		boardname_map = new LinkedHashMap<>();
		boardname_map.put("vant-6","TG789vac v2");
		boardname_map.put("vant-f","TG799vac");
		boardname_map.put("vant-y","TG800vac");
		boardname_map.put("vbnt-f","TG789vac Xtream");
		boardname_map.put("vbnt-j","DJN2130");
		boardname_map.put("vbnt-k","DGA4130");
		boardname_map.put("vbnt-l","TG789vac v2 HP");
		boardname_map.put("vbnt-o","DGA4131");
		boardname_map.put("vbnt-s","DGA4132");
		boardname_map.put("vbnt-v","DJA0230");
		boardname_map.put("vdnt-o","TG799vn-v2");
	}
}
