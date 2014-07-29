package freakr.streams_lib.apps;
import java.util.prefs.Preferences;


public class Setup_Server_PC {

	private Preferences prefs = Preferences.userRoot().node(this.getClass().getName());  
	
	public void set_setup(String id,String value) {
		//prefs = Preferences.userRoot().node(this.getClass().getName());
		prefs.put(id,value );
		
		}
	public String get_setup(String id) {
		//prefs = Preferences.userRoot().node(this.getClass().getName());
		return prefs.get(id,"");
		}
}
