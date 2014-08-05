package freakr.client.apps;

import freakr.streams_lib.apps.Client;
import freakr.streams_lib.apps.Setup_Client_Android;
import freakr.streams_lib.apps.Setup_Server_PC;
import freakr.streams_lib.apps.Streams_lib;


public class Client_Main implements Streams_lib{

	public static void main(String[] args) {
		Thread Online_Thread = new Thread (new Runnable() {
			
	        public void run() {
	            Thread t1 = Thread.currentThread();
	            t1.setName("CONNECTION_KEEP Thread");
	            Setup_Server_PC setup = new Setup_Server_PC();
	            setup.set_Parameter(ONLINE_THREAD, ONLINE_THREAD_NOT_RUNNING);
	            
	            System.out.println(setup.get_Parameter(ONLINE_THREAD));
	            while(true){
	            	System.out.println(setup.get_Parameter(ONLINE_THREAD));
	            	if(setup.get_Parameter(ONLINE_THREAD).equals(ONLINE_THREAD_NOT_RUNNING)){
	            		setup.set_Parameter(ONLINE_THREAD, ONLINE_THREAD_RUNNING);
	            		new Thread(new Client(setup,LOCALHOST,CONNECTION_KEEP, null)).start();
	            	}
	            	
	            	try {
						Thread.sleep(11000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	            	
	            }
	        }
	    });
		Online_Thread.start();
	
	
	
	}
}
