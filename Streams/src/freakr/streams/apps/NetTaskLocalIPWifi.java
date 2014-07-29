package freakr.streams.apps;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

public class NetTaskLocalIPWifi extends AsyncTask<Void, Void, String>
{
	Context context;
	public NetTaskLocalIPWifi(Context context) {
		this.context = context;
	}
	@Override
    protected String doInBackground(Void... params)
    {
			Thread t = Thread.currentThread();
			t.setName("NetTaskLocalIPWifi Thread");
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		   WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		   int ip = wifiInfo.getIpAddress();
		   if (ip==0){return null;}
		   String ipString = String.format(
		   "%d.%d.%d.%d",
		   (ip & 0xff),
		   (ip >> 8 & 0xff),
		   (ip >> 16 & 0xff),
		   (ip >> 24 & 0xff));

		   return ipString;
    }
}
