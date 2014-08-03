package freakr.streams_lib.apps;

import java.util.ArrayList;
import java.util.Arrays;

//Lib to work together

public interface Streams_lib {
	// Socket Commandos
	public static final String CONNECTION_CLOSE = "Connection_Close";
	public static final String CONNECTION_REQUEST = "Connection_Request";
	public static final String CONNECTION_ACCEPTED = "Connection_Accepted";
	public static final String CONNECTION_KEEP = "Connection_Keep";
	public static final String DB_UPDATE_FULL = "DB_Full_Update";
	public static final String ACTION_COMPLETE = "Action_Complete";
	public static final String FILE_COMPLETE = "File_Complete";
	public static final String REQUEST_DB_FILES = "Request_DB_Files";
	public static final String REQUEST_URL = "Request_Url";
	public static final String OPEN_LINK = "Open_Link";
	// Socket Connection
	public static final String LOCALHOST = "localhost";
	public static final String HOST = "freakr.spdns.de";
	public static final int PORT = 15000;
	// Datenbank 
	public static final String DB_FOLDER = "StreamsData/";
	public static final String DB_NAME = "DBStreams";
	// Sonstige
	public final static String ALLE_FOLGEN = "Alle Folgen";
	public final static String NEUE_FOLGEN = "Neue Folgen";
	public final static String NOCH_NICHT_GESEHEN_FOLGEN = "Noch nicht gesehen";
	public final static String EINSTELLUNGEN = "Einstellungen";
	// Client Parameter
	static final String ONLINE_THREAD = "Online_Thread";
	static final String ONLINE_THREAD_NOT_RUNNING = "not_running";
	static final String ONLINE_THREAD_RUNNING = "running";
	final static String ONLINESTATUS = "Onlinestatus";
	final static String ONLINESTATUS_ON = "On";
	final static String ONLINESTATUS_OFF = "Off";
	final static String PARAMETER_NEU_TAGE = "STREAMS_NEU_TAGE";
	final static String PARAMETER_UPDATE_ZEIT = "STREAMS_UPDATE_ZEIT";
	final static String PARAMETER_WINDOW = "STREAMS_DEFAULT_WINDOW";
	final static String PARAMETER_IP = "STREAMS_IP";
	final static String MYAPP = "STREAMS";
	final static String DEFAULT_NEU_TAGE = "7";
	final static String DEFAULT_UPDATE_ZEIT = "00:01:00"; // = 00-Tage:01-Stunden:00-Minuten
	final static String DEFAULT_WINDOW = NOCH_NICHT_GESEHEN_FOLGEN;
	final static ArrayList<String> TAGELISTE = new ArrayList<String>(Arrays.asList("1","2","3","4","5","6","7","14","30"));
	final static ArrayList<String> FENSTERLISTE = new ArrayList<String>(Arrays.asList(NOCH_NICHT_GESEHEN_FOLGEN,NEUE_FOLGEN,ALLE_FOLGEN));
	
}
