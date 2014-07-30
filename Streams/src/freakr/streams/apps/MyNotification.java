package freakr.streams.apps;

import java.util.ArrayList;

import mei.app.streams.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import freakr.streams_lib.apps.Datenbank;

public class MyNotification {

	final static String DATENBANK_AKTUALISIEREN = "Datenbank wird aktualisiert bitte warten...";
	final static String DATENBANK_NEUE_FOLGEN_GEFUNDEN = "Neue Folgen wurden gefunden";
	private Context context;
	public static final int NOTIFICATION_GCM = 1;
	public static final int NOTIFICATION_UPDATE = 2;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public MyNotification(Context context) {
		this.context = context;
	}

	void sendNotification(String msg) {
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context, MainActivity.class), 0);

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("GCM Notification")
				.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
				.setContentText(msg);

		mBuilder.setContentIntent(contentIntent);
		mNotificationManager.notify(NOTIFICATION_GCM, mBuilder.build());
	}

	void addNotification(Context context, String text) {

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Datenbank Update").setContentText(text);

		Intent notificationIntent = new Intent(context, MyNotification.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);

		// Add as notification
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(1, builder.build());
	}

	// Remove notification
	void removeNotification(Context context, String text) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(1);
	}

	void addNotification(Context context, String text, ArrayList<Integer> liste) {

		String textplus = text + "\n";

		for (int x = 0; x < liste.size(); x++) {
			ArrayList liste_folge = Datenbank.ausgabe_folge(liste.get(x));
			int serie = (int) liste_folge.get(0);
			String name = Datenbank.konvert_serien_id_to_name(serie);
			String staffel = String.valueOf(liste_folge.get(1));
			String episode = String.valueOf(liste_folge.get(2));
			textplus = textplus + name + " - Staffel - " + staffel
					+ " Episode - " + episode + "\n";

		}

		NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
		bigTextStyle.setBigContentTitle("Datenbank Update");
		bigTextStyle.bigText(textplus);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context);

		// .setContentTitle("Datenbank Update")
		// .setContentText(textplus);

		builder.setSmallIcon(R.drawable.ic_launcher);
		builder.setStyle(bigTextStyle);
		Intent notificationIntent = new Intent(context, MyNotification.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);
		Notification note = builder.build();
		note.defaults |= Notification.DEFAULT_VIBRATE;
		note.defaults |= Notification.DEFAULT_SOUND;
		// Add as notification
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(2, builder.build());
	}

}
