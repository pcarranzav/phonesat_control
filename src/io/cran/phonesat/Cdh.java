package io.cran.phonesat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

// comunicacion con el xbee

public class Cdh {
	
	InputStream uartInputStream;
	OutputStream uartOutputStream;
	
	Cdh(InputStream is, OutputStream os) {
		uartInputStream = is;
		uartOutputStream = os;
	}
	
	public void loop() throws IOException{
		
		if(uartInputStream.available()>0){
			// Process incoming data
		}
		
	}

}
