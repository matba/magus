package edu.ls3.magus.utility;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class SimpleLogger {
	private String address;
	private boolean persistent;
	private Writer writer;
	
	public SimpleLogger(String address, boolean persistent) throws UnsupportedEncodingException, FileNotFoundException{
		this.address  = address;
		this.persistent = persistent;
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.address), "utf-8"));
		
	}
	public void log(String log) throws IOException{
		writer.write(log+System.lineSeparator());
		if(persistent)
			writer.flush();
	}
	public void close() throws IOException{
		writer.flush();
		writer.close();
	}
}
