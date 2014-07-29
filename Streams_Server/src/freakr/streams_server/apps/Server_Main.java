package freakr.streams_server.apps;




public class Server_Main{

	
	public static void main(String[] args) {
		
		ThreadPooledServer server = new ThreadPooledServer(15000);
		SysTray st = new SysTray(server);
		server.tray = st;
		st.start();
		//st.update("A");
		new Thread(server).start();

		
		//try {
		//    Thread.sleep(1 * 60 * 1000);
		//} catch (InterruptedException e) {
		//    e.printStackTrace();
		//}
		
		//System.out.println("Stopping Server");
		//st.update("I");
		//server.stop();
		
	}
	
}
