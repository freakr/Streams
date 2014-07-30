package freakr.streams.apps;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import freakr.streams_lib.apps.Setup_Client_Android;
import freakr.streams_lib.apps.Streams_lib;

public class Alarm {

	private Context context;

	public Alarm(Context context) {
		this.context = context;
	}

	public void Start() {
		Setup_Client_Android setup = new Setup_Client_Android();
		setup.Create(context);
		String zeit = setup
				.get_Parameter(Streams_lib.PARAMETER_UPDATE_ZEIT);
		Intent i = new Intent(context, MyUpdate.class);
		i.setAction("UpdateService");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		int tage = Integer.parseInt(zeit.substring(0, 2));
		int std = Integer.parseInt(zeit.substring(3, 5));
		int min = Integer.parseInt(zeit.substring(6, 8));
		long itage = DateUtils.MINUTE_IN_MILLIS * 60 * 24 * tage;
		long istd = DateUtils.MINUTE_IN_MILLIS * 60 * std;
		long imin = DateUtils.MINUTE_IN_MILLIS * min;
		long interval = imin + istd + itage;
		long firstStart = System.currentTimeMillis()
				+ (DateUtils.MINUTE_IN_MILLIS * 2);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstStart, interval,
				pi);

	}

	public void Update(String zeit) {

		Intent i = new Intent(context, MyUpdate.class);
		i.setAction("UpdateService");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		int tage = Integer.parseInt(zeit.substring(0, 2));
		int std = Integer.parseInt(zeit.substring(3, 5));
		int min = Integer.parseInt(zeit.substring(6, 8));
		long itage = DateUtils.MINUTE_IN_MILLIS * 60 * 24 * tage;
		long istd = DateUtils.MINUTE_IN_MILLIS * 60 * std;
		long imin = DateUtils.MINUTE_IN_MILLIS * min;
		long interval = imin + istd + itage;
		long firstStart = System.currentTimeMillis()
				+ (DateUtils.MINUTE_IN_MILLIS * 2);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstStart, interval,
				pi);

	}

}
