package freakr.streams.apps;

import android.content.Context;
import android.widget.RadioButton;
import freakr.streams_lib.apps.Setup_Client_Android;
import freakr.streams_lib.apps.Streams_lib;

public class Connection_Alive implements Runnable,Streams_lib{

	Setup_Client_Android setup;
	Context context;
	private RadioButton rbutton;
	
	public Connection_Alive(RadioButton rbutton, Setup_Client_Android setup) {
		this.setup = setup;
		this.rbutton = rbutton;
		
	}

	@Override
	public void run() {
		Thread t = Thread.currentThread();
		t.setName("Connection_Alive Thread");
		rbutton.setClickable(false);
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(setup.get_Parameter(ONLINESTATUS).equals (ONLINESTATUS_ON)){
				rbutton.setChecked(true);
				}
				if(setup.get_Parameter(ONLINESTATUS).equals (ONLINESTATUS_OFF)){
				rbutton.setChecked(false);
			}
			} catch (Exception e){
				System.out.println(e.getMessage()) ;
			}
			
		}
	}
}
