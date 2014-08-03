package freakr.streams_lib.apps;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable,Streams_lib {

	
	
	public String command;
	private String host;
	private int port;
	private String value;
	private Setup_Client_Android setup_android;
	private Setup_Server_PC setup_pc;
	private int setup_choice;
	
	public Client(Setup_Client_Android setup,String host,String command,String value) {
		this.setup_android = setup;
		this.host = "192.168.42.174";//host;
		this.port = PORT;
		this.command=command;
		this.value = value;
		this.setup_choice = 1;
		
	}
	public Client(Setup_Server_PC setup,String host,String command,String value) {
		this.setup_pc = setup;
		this.host = "192.168.42.174";//host;
		this.port = PORT;
		this.command=command;
		this.value = value;
		this.setup_choice = 2;
	}
	@Override
	public void run() {
		Thread t = Thread.currentThread();
	    t.setName(command+" - Thread");
	    Socket server = new Socket();
		InetAddress ihost = null;
		try {
			ihost = InetAddress.getByName(host);
		} catch (UnknownHostException e) {
			System.out.println(e.getMessage());
		}
		try {
			server.connect(new InetSocketAddress( ihost, port),10000 );
			BufferedReader input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        	PrintWriter output = new PrintWriter(server.getOutputStream(), true);
			output.println(CONNECTION_REQUEST);
			String line;  
            while((line = input.readLine())!=null){
            	System.out.println(line);
            	switch(line){
            	case CONNECTION_KEEP:
            		try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		switch(setup_choice){
    				case 1:
    					setup_android.set_Parameter(ONLINESTATUS, ONLINESTATUS_ON);
    					System.out.println(setup_android.get_Parameter(ONLINESTATUS));
    					break;
    				case 2:
    					setup_pc.set_Parameter(ONLINESTATUS, ONLINESTATUS_ON);
    					System.out.println(setup_pc.get_Parameter(ONLINESTATUS));
    					break;
    				}	
            		output.println(CONNECTION_KEEP);
            		break;
            	case CONNECTION_CLOSE:
            		output.println(line);
            		server.shutdownInput();
            		server.shutdownOutput();
            		server.close();
            		break;
            	case REQUEST_URL:
            		output.println(value);
            		break;
            	case CONNECTION_ACCEPTED:
            		output.println(command);
            		break;
            	case ACTION_COMPLETE:
            		output.println(CONNECTION_CLOSE);
            		break;
            	case REQUEST_DB_FILES:
            		File f = new File(DB_FOLDER+DB_NAME+".lck");
            		FileInputStream fileinput = new FileInputStream(f);
            		BufferedOutputStream socketOut = new BufferedOutputStream(server.getOutputStream());
                    System.out.println(f.getAbsolutePath());
                    int read = 0;
                    while ((read = fileinput.read()) != -1) {
                        socketOut.write(read);
                        //System.out.println("write " + read + " to socket");
                     }
                    socketOut.write(read);
                    socketOut.flush();
                    line = input.readLine();
                    if(!line.equals(FILE_COMPLETE)){
                    	break;
                    }
                    f = new File(DB_FOLDER+DB_NAME+".log");
                    fileinput = new FileInputStream(f);
                    System.out.println(f.getAbsolutePath());
                    read = 0;
                    while ((read = fileinput.read()) != -1) {
                        socketOut.write(read);
                        //System.out.println("write " + read + " to socket");
                     }
                    socketOut.write(read);
                    socketOut.flush();
                    line = input.readLine();
                    if(!line.equals(FILE_COMPLETE)){
                    	break;
                    }
                    f = new File(DB_FOLDER+DB_NAME+".properties");
                    fileinput = new FileInputStream(f);
                    System.out.println(f.getAbsolutePath());
                    read = 0;
                    while ((read = fileinput.read()) != -1) {
                        socketOut.write(read);
                        //System.out.println("write " + read + " to socket");
                     }
                    socketOut.write(read);
                    socketOut.flush();
                    line = input.readLine();
                    if(!line.equals(FILE_COMPLETE)){
                    	break;
                    }
                    f = new File(DB_FOLDER+DB_NAME+".script");
                    fileinput = new FileInputStream(f);
                    System.out.println(f.getAbsolutePath());
                    read = 0;
                    while ((read = fileinput.read()) != -1) {
                        socketOut.write(read);
                        //System.out.println("write " + read + " to socket");
                     }
                    socketOut.write(read);
                    socketOut.flush();
                    line = input.readLine();
                    if(!line.equals(FILE_COMPLETE)){
                    	break;
                    }
                    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
                	output = new PrintWriter(server.getOutputStream(), true);
                    break;
            	default :
            		output.println(CONNECTION_CLOSE);
            		break;
            	}
            }  
		} catch (IOException e) {
			
			if(command.equals(CONNECTION_KEEP)){
				switch(setup_choice){
				case 1:
					setup_android.set_Parameter(ONLINE_THREAD, ONLINE_THREAD_NOT_RUNNING);
					System.out.println(setup_android.get_Parameter(ONLINE_THREAD));
					setup_android.set_Parameter(ONLINESTATUS, ONLINESTATUS_OFF);
					System.out.println(setup_android.get_Parameter(ONLINESTATUS));
				break;
				case 2:
					setup_pc.set_Parameter(ONLINE_THREAD, ONLINE_THREAD_NOT_RUNNING);
					System.out.println(setup_pc.get_Parameter(ONLINE_THREAD));
					setup_pc.set_Parameter(ONLINESTATUS, ONLINESTATUS_OFF);
					System.out.println(setup_pc.get_Parameter(ONLINESTATUS));
					break;
				}
				
				
			}
			System.out.println(e.getMessage());
		}
		
		
	}
}
