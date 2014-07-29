package freakr.streams_lib.apps;

import android.content.Context;
import android.content.SharedPreferences;

public class Setup_Client_Android implements Streams_lib{
	
	Context context = null;
	SharedPreferences pref = null; 
	
	public void Create(Context context){
		this.context = context;
		pref = this.context.getSharedPreferences(MYAPP, Context.MODE_PRIVATE);
		if(get_Parameter(PARAMETER_NEU_TAGE) == ""){
			set_Parameter(PARAMETER_NEU_TAGE,DEFAULT_NEU_TAGE);
		}
		if(get_Parameter(PARAMETER_UPDATE_ZEIT) == ""){
			set_Parameter(PARAMETER_UPDATE_ZEIT,DEFAULT_UPDATE_ZEIT);
		}
		if(get_Parameter(PARAMETER_WINDOW) == ""){
			set_Parameter(PARAMETER_WINDOW,DEFAULT_WINDOW);
		}
	}
	
	public String get_Parameter(String S){
		
		return pref.getString(S, "");
	}
	public void set_Parameter(String S,String value){
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(S, value).commit();
	}
}
