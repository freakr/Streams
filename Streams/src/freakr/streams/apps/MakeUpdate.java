package freakr.streams.apps;

import java.util.ArrayList;

import android.content.Context;
import freakr.streams_lib.apps.Datenbank;

public class MakeUpdate implements Runnable{

	private Context context;
	private boolean tStarted;
	
	public MakeUpdate(Context context) {
		this.context = context.getApplicationContext();
	}
	
	public MakeUpdate(Context context,boolean tStarted) {
		this.context = context.getApplicationContext();
		this.tStarted = tStarted;
		
	}
	
	@Override
	public void run() {
		Thread t = Thread.currentThread();
	    t.setName("MakeUpdate Thread");
		MyNotification MyNotify = new MyNotification(context);
    	Datenbank DB = new Datenbank();
    	ArrayList<Integer> liste = new ArrayList<Integer>();
    	try {
        	DB.initialisiern_datenbank();
			MyNotify.addNotification(context,MyNotification.DATENBANK_AKTUALISIEREN);
        	liste = DB.aktualisieren_datenbank(context);
        	MyNotify.removeNotification(context,MyNotification.DATENBANK_AKTUALISIEREN);
        	if (!liste.isEmpty()){
        		MyNotify.addNotification(context, MyNotification.DATENBANK_NEUE_FOLGEN_GEFUNDEN,liste);
        	}
        	if(!tStarted){
        		MainActivity.threadDBUStarted = tStarted;
        	}
        	
        } catch (Exception e) {
        	if(!tStarted){
        		MainActivity.threadDBUStarted = tStarted;
        	}
            MyNotify.removeNotification(context,MyNotification.DATENBANK_AKTUALISIEREN);
        	e.printStackTrace();
        }
    
		
	}

	

}
