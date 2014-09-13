package freakr.streams_server.apps;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import freakr.streams_lib.apps.Setup_Server_PC;
import freakr.streams_lib.apps.Streams_lib;

public class WorkerRunnable implements Runnable,Streams_lib{

    protected Socket clientSocket = null;
    private SysTray tray;
	private Setup_Server_PC setup;
    

    public WorkerRunnable(Socket clientSocket, SysTray tray, Setup_Server_PC setup) {
        this.clientSocket = clientSocket;
        this.tray   = tray;
        this.setup = setup;
        this.tray.clientip = clientSocket.getLocalSocketAddress();
        tray.update("C");
    }

    public void run() {
        try {
        	BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        	PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
        	String line; 
        	
            while( (line = input.readLine())!=null){
            	System.out.println(line);
            	String serverstatus = setup.get_Parameter(SERVERSTATUS);
            	if (serverstatus.equals(SERVERSTATUS_OFF)){
            		line = CONNECTION_CLOSE;
            	}
            	switch(line){
            	case CONNECTION_KEEP:
            		try {
						Thread.sleep(1000);
					} catch (InterruptedException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
            		if(tray.seriename!=null){
            			output.println(GET_NEW_SERIE);
            		}else{
            			output.println(CONNECTION_KEEP);
            		}
            		break;
            	case CONNECTION_CLOSE:
            		output.println(CONNECTION_CLOSE);
            		output.close();
            		input.close();
            		break;
            	case CONNECTION_REQUEST:
            		output.println(CONNECTION_ACCEPTED);
            		break;
            	case OPEN_LINK:
            		output.println(REQUEST_URL);
            		line = input.readLine();
            		URI uri = null;
					try {
						uri = new URI(line);
						if (Desktop.isDesktopSupported()) {
	            		      try {
	            		        Desktop.getDesktop().browse(uri);
	            		      } catch (IOException e) { 
	            		    	  System.out.println(e.getMessage());
	            		    	  }
	            		    } else { 
	            		    	System.out.println("Kein Desktop gefunden !");
	            		    	}
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						System.out.println(e1.getMessage());
					}
					output.println(ACTION_COMPLETE);
            		break;
            	case GET_NEW_SERIE:
            		output.println(GET_SERIE);
            		output.println(tray.seriename);
            		
            		break;
            	case DB_UPDATE_FULL:
            		output.println(REQUEST_DB_FILES);
            		
            		String newFolder = DB_FOLDER;
            	    File myNewFolder = new File(newFolder);
            	    myNewFolder.mkdir();
            		            		
            		BufferedInputStream fileReader = new BufferedInputStream(clientSocket.getInputStream());
            	    FileWriter writer = new FileWriter(DB_FOLDER+DB_NAME+".lck");
            	    System.out.println(DB_FOLDER+DB_NAME+".lck");
            	    int read = 0;
            	    while ((read = fileReader.read()) != 255) {
            	        writer.write(read);
            	        //System.out.println("read " + read + " from socket");
            	        }
            	    writer.close();
            	    output.println(FILE_COMPLETE);
            	    writer = new FileWriter(DB_FOLDER+DB_NAME+".log");
            	    System.out.println(DB_FOLDER+DB_NAME+".log");
            	    read = 0;
            	    while ((read = fileReader.read()) != 255) {
            	        writer.write(read);
            	        //System.out.println("read " + read + " from socket");
            	        }
            	    writer.close();
            	    output.println(FILE_COMPLETE);
            	    writer = new FileWriter(DB_FOLDER+DB_NAME+".properties");
            	    System.out.println(DB_FOLDER+DB_NAME+".properties");
            	    read = 0;
            	    while ((read = fileReader.read()) != 255) {
            	        writer.write(read);
            	        //System.out.println("read " + read + " from socket");
            	        }
            	    writer.close();
            	    output.println(FILE_COMPLETE);
            	    writer = new FileWriter(DB_FOLDER+DB_NAME+".script");
            	    System.out.println(DB_FOLDER+DB_NAME+".script");
            	    read = 0;
            	    while ((read = fileReader.read()) != 255) {
            	        writer.write(read);
            	        //System.out.println("read " + read + " from socket");
            	        }
            	    writer.close();
            	    output.println(FILE_COMPLETE);
            	    input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                	output = new PrintWriter(clientSocket.getOutputStream(), true);
            	    output.println(ACTION_COMPLETE);
            		break;
            	default :
            		output.close();
            		input.close();
            		output.println(CONNECTION_CLOSE);
                	break;
            	}
            }  
        } catch (IOException e) {
        	tray.update("A");
        	System.out.println(e.getMessage());
        	//e.printStackTrace();
        }
    }
}