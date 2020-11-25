package com.thehuxley.event;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.thehuxley.event.Event;


public class EventManagerProxySender {
	
	private String host;
	private int portNumber;
	
	public EventManagerProxySender(String host, int portNumber){
		this.host = host;
		this.portNumber = portNumber;
	}
	
	public void update(Event e){
		Socket serverSocket = null;
		ObjectOutputStream out = null;
		try {
			serverSocket = new Socket(host, portNumber);
			out = new ObjectOutputStream(serverSocket.getOutputStream());
			out.writeObject(e);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}finally{
			if(out!= null){
				try {
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		
			}
			if(serverSocket!= null){
				try {
					serverSocket.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

}