package freakr.client.apps;

import freakr.streams_lib.apps.Client;


public class Client_Main {

	public static void main(String[] args) {
		new Thread(new Client(null,Client.LOCALHOST,Client.DB_UPDATE_FULL,null)).start();
	}
}
