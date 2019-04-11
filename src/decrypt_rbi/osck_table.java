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
		osck_table.put("DGA4130", "FFD56A4E3A21401BF1798B3CD8AD54D238BA80039623BBA08B6D50B8EC73F7B4");
		osck_table.put("DGA4131", "916AEB569D8CBF8CFAF060AEC533D43A9EF0ACB3138F8351C4112674212975A5");
		osck_table.put("DGA4132", "0EF34D972945869EF40F89873FED30269020E107685C097751BEF9479D75D620");
	}
	
	// Add here the connection between model and boardname value
	// This will help to autodetect the model based on the boardname
	// The second value should be equal to the osck linked to the model
	public static Map<String, String> boardname_map;
	static {
		boardname_map = new LinkedHashMap<>();
		boardname_map.put("VBNT-O", "DGA4131");
		boardname_map.put("VBNT-K", "DGA4130");
		boardname_map.put("VBNT-S", "DGA4132");
	}
}
