package freakr.streams.apps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyUpdate extends BroadcastReceiver {

	boolean threadDBUStarted = false;

	@Override
	public void onReceive(final Context context, Intent intent) {

		Thread DBUpdate = new Thread(new MakeUpdate(
				context.getApplicationContext()));
		DBUpdate.start();

	}
}