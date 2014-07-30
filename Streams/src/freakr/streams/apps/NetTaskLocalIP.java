package freakr.streams.apps;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;

import android.os.AsyncTask;

public class NetTaskLocalIP extends AsyncTask<Void, Void, String> {

	@Override
	protected String doInBackground(Void... params) {
		Thread t = Thread.currentThread();
		t.setName("NetTaskLocalIP Thread");
		String addr = null;
		Enumeration<NetworkInterface> netInter = null;
		try {
			netInter = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		int n = 0;

		while (netInter.hasMoreElements()) {
			NetworkInterface ni = netInter.nextElement();

			System.out.println("NetworkInterface " + n++ + ": "
					+ ni.getDisplayName());

			for (InetAddress iaddress : Collections.list(ni.getInetAddresses())) {
				System.out.println("CanonicalHostName: "
						+ iaddress.getCanonicalHostName());

				System.out.println("IP: " + iaddress.getHostAddress());
				if (!iaddress.isLoopbackAddress()
						&& iaddress.isSiteLocalAddress()) {
					addr = iaddress.getHostAddress();
				}

				System.out.println("Loopback? " + iaddress.isLoopbackAddress());
				System.out.println("SiteLocal? "
						+ iaddress.isSiteLocalAddress());
				System.out.println();
			}
		}

		// try
		// {
		// addr = InetAddress.getLocalHost().getHostAddress();
		// }

		// catch (UnknownHostException e)
		// {
		// e.printStackTrace();
		// }
		return addr;
	}
}
