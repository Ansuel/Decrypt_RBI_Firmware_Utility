package main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class rbi_info {
	
	File file;
	ByteArrayOutputStream outputStream;
	Map<String,String> header_table;
	
	public rbi_info(File file) {
		this.file = file;
		this.outputStream = new ByteArrayOutputStream();
		this.header_table = new LinkedHashMap<String,String>();
	}
	
	public void setHeaderTable(Map<String,String> header_table) {
		this.header_table = header_table;
	}
	
	public void RegenerateOutputStream() {
		this.outputStream = new ByteArrayOutputStream();
	}
	
	public File getFile() {
		return file;
	}
	
	public ByteArrayOutputStream getOutputStream() {
		return outputStream;
	}
	
	public Map<String,String> getHeaderTable() {
		return header_table;
	}
}
