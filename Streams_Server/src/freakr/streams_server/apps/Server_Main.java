package freakr.streams_server.apps;




public class Server_Main{

	
	public static void main(String[] args) {
		ThreadPooledServer server = new ThreadPooledServer(15000);
		SysTray st = new SysTray(server);
		server.tray = st;
		st.start();
		new Thread(server).start();
	}
	
}
