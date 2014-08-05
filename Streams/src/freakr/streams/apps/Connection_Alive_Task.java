package freakr.streams.apps;

import android.os.AsyncTask;
import android.widget.RadioButton;
import freakr.streams_lib.apps.Setup_Client_Android;
import freakr.streams_lib.apps.Streams_lib;

public class Connection_Alive_Task extends AsyncTask<Void, String, Void> implements Streams_lib{

	private Setup_Client_Android setup;
	private Boolean Merker1 = false ;
	private RadioButton rbutton;
	
	public Connection_Alive_Task(RadioButton rbutton,Setup_Client_Android setup) {
		
		this.setup = setup;
		this.rbutton = rbutton;
		
	}

	
	@Override
	protected Void doInBackground(Void... params) {
		Thread t = Thread.currentThread();
		t.setName("Connection_Alive Thread");
		
		while(true){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(setup.get_Parameter(ONLINESTATUS).equals (ONLINESTATUS_ON)){
				if(!Merker1){
					Merker1=true;
					publishProgress("true");
				}
				
				}
				if(setup.get_Parameter(ONLINESTATUS).equals (ONLINESTATUS_OFF)){
				if(Merker1){
					Merker1=false;
					publishProgress("false");
				}
			}
				
			} catch (Exception e){
				System.out.println(e.getMessage()) ;
			}
			
		}
		
	}
	@Override
	protected void onProgressUpdate(String... progress) {
        if (progress[0].equals("true")){
        	rbutton.setChecked(true);
        }
        if (progress[0].equals("false")){
        	rbutton.setChecked(false);
        }
    }
}
	
