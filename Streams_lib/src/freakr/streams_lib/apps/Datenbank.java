package freakr.streams_lib.apps;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import android.content.Context;
import android.os.Environment;



public class Datenbank implements Streams_lib{
	
	static {
        try {
            Class.forName("org.hsqldb.jdbcDriver");
        	
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
	public static Connection dbVerbindung = null;
    
	public static String url1 = "http://kinox.to/Stream/";
	public static String url2 = "http://www.kinox.to/Stream/";
	public static String url3 = ".html";
	
 	public static String strsql = null;
	public static Statement stmt = null;
	
	public void initialisiern_datenbank() {
		 String newFolder = "/"+DB_FOLDER;
		 String extStorageDirectory = "";
		 if(Environment.getExternalStorageDirectory()!= null){
	     extStorageDirectory = Environment.getExternalStorageDirectory().toString();
	     }
	     File myNewFolder = new File(extStorageDirectory + newFolder);
	     myNewFolder.mkdir();
		
		File path = myNewFolder; 
	    File file = new File(path, DB_NAME);
	    String filepath = file.getAbsolutePath();
	    try {
	    	dbVerbindung = DriverManager.getConnection("jdbc:hsqldb:file:"+filepath+";ifexists=true", "SA", "");
	    	dbVerbindung.setAutoCommit(true);
	    	stmt = dbVerbindung.createStatement();
	    } catch (SQLException e) {
	    	System.out.println(e.getErrorCode());
	    	if(e.getErrorCode()==-465){
	    		try {
					dbVerbindung = DriverManager.getConnection("jdbc:hsqldb:file:"+file.getAbsolutePath()+"", "SA", "");
					dbVerbindung.setAutoCommit(true);
					stmt = dbVerbindung.createStatement();
					erstellen_datenbank();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
		    }
	    }
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	        public void run() {
	            try {
	                if (!dbVerbindung.isClosed()) {
	                	dbVerbindung.close();
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    });
	}

static void erstellen_datenbank(){
	
	strsql = "CREATE TABLE Serien (SerienID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1)PRIMARY KEY, Serienname VARCHAR(255))"; 
	try {
		stmt.executeUpdate(strsql);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	strsql = "CREATE TABLE Staffeln (StaffelnID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1)PRIMARY KEY, StaffelNr INTEGER)"; 
	try {
		stmt.executeUpdate(strsql);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	strsql = "CREATE TABLE Episoden (EpisodenID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1)PRIMARY KEY, EpisodeNr INTEGER)"; 
	try {
		stmt.executeUpdate(strsql);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	strsql = "CREATE TABLE Vorhanden (VorhandenID INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1)PRIMARY KEY, Serie INTEGER, Staffel INTEGER,Episode INTEGER,"
				+ "Vorhanden BIT,Gesehen BIT DEFAULT FALSE NOT NULL,Datum TIMESTAMP,"
				+ "FOREIGN KEY (Serie) REFERENCES Serien (SerienID),"
				+ "FOREIGN KEY (Staffel) REFERENCES Staffeln (StaffelnID),"
				+ "FOREIGN KEY (Episode) REFERENCES Episoden (EpisodenID))"; 
	try {
		stmt.executeUpdate(strsql);
	} catch (SQLException e) {
		e.printStackTrace();
	}
	for(int i=1;i<50;i++){
		strsql="INSERT INTO Staffeln (StaffelNr) VALUES ("+i+")"; 
		try {
			stmt.executeUpdate(strsql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	for(int i=1;i<200;i++){
		strsql="INSERT INTO Episoden (EpisodeNr) VALUES ("+i+")"; 
		try {
			stmt.executeUpdate(strsql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
	
public ArrayList<Integer> aktualisieren_datenbank(Context context){
	WebsiteReader WSR = new WebsiteReader();
	int[] SA = ausgabe_serien_ids();
	ArrayList<Integer> neue_hinzugefuegt = new ArrayList<Integer>();
	for(int k=0;k<SA.length;k++){
		String s = url1+konvert_serien_id_to_name(SA[k])+url3;
		//Toast.makeText(context, "vor Auswertung", Toast.LENGTH_LONG).show();
		Object[] Auswertung = WSR.WBAuswerten(context,s, konvert_serien_id_to_name(SA[k]));
		//Toast.makeText(context, "nach Auswertung", Toast.LENGTH_LONG).show();
		for(int i = 0;i< Auswertung.length;i++){
			String ausstr = Auswertung[i].toString();
			ausstr = ausstr.substring(1, ausstr.length() - 1);
			ausstr = ausstr.replaceAll(" ","");
			String [] arr = ausstr.split(",");
			for(int j = 1;j<arr.length;j++){
				boolean status = false;
				status = hinzufuegen_episode(SA[k],Integer.parseInt(arr[0]),Integer.parseInt(arr[j]));
				if (status){
					neue_hinzugefuegt.add(ausgabe_vid(SA[k],Integer.parseInt(arr[0]),Integer.parseInt(arr[j])));
					
				}
			}
			
		}
		
	}
	return neue_hinzugefuegt;
	
}
		
static int ausgabe_serien_anzahl() {
	int SAnzahl=0;
	try
    {
    strsql= "SELECT SerienID FROM Serien";  
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	SAnzahl++;
        }
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return SAnzahl+1;
}

static ArrayList<String> ausgabe_staffel(String serie) {
	
	ArrayList<String> r�ck = new ArrayList<String>();;
	try
    {
    
	strsql= "SELECT DISTINCT Vorhanden.Staffel,Vorhanden.Serie "
			+ "FROM Vorhanden "
			+ "WHERE Vorhanden.Serie = '"+konvert_serienname_to_id(serie)+"'";

    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck.add(rs.getString("Vorhanden.Staffel"));
    	}
    rs.close();    
} catch(SQLException e)
    {
      System.exit(0);
    }
return r�ck;
}

static int ausgabe_vid(int Snr,int S,int E) {
	int VorhandenID=0;
	try
    {
    strsql= "SELECT VorhandenID FROM Vorhanden WHERE Serie = "+Snr+" AND Staffel = "+S+" AND Episode = "+E;  
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
    	VorhandenID= Integer.parseInt(rs.getString("VorhandenID"));
        }
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return VorhandenID;
}


public static String konvert_serien_id_to_name(int Sid){
	
	String Name = null;
	
	try
    {
    strsql= "SELECT SerienID,Serienname FROM Serien WHERE SerienID = "+ Sid;
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	Name= rs.getString("Serienname");
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return Name;
}
static Vector ausgabe_serien_alle() {
	String SName,ST,EP;
    Vector results = new Vector();
    try {
    	ResultSet rs = stmt
            .executeQuery("SELECT Vorhanden.Serie, Vorhanden.Staffel, Vorhanden.Episode, Vorhanden.Vorhanden, Vorhanden.Gesehen, Serien.Serienname FROM Vorhanden,Serien WHERE Serien.SerienID = Vorhanden.Serie");

        while (rs.next()) {
            Vector person = new Vector();
            
            SName=rs.getString("Serienname");
            ST = rs.getString("Staffel");
            EP = rs.getString("Episode");
            
            person.add(SName);
            person.add(rs.getString("Serie"));
            person.add(ST);
            person.add(EP);
            person.add(rs.getBoolean("Vorhanden"));
            person.add(rs.getBoolean("Gesehen"));
            person.add(url1+SName+url3+",s"+ST+"e"+EP);
            
            results.add(person);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return results;
}	

static boolean hinzufuegen_episode(int Snr,int S,int E) {
		
	int Nid=Snr;
	int Vid=0;
	boolean vorhanden=true;
	boolean Status = false;
	Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
    try
    {
    strsql= "SELECT VorhandenID FROM Vorhanden WHERE Serie = "+ Snr+" AND Staffel = "+S+" AND Episode = "+E; 
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
      	{
        	Vid = Integer.parseInt(rs.getString("VorhandenID"));
        	Status = false;
        }
    rs.close();
        if(Vid==0){
        	strsql= "INSERT INTO Vorhanden (Serie,Staffel,Episode,Vorhanden,Datum) VALUES ("+Nid+","+S+","+E+","+ vorhanden +",'"+date+"')"; 
            stmt.executeUpdate(strsql); 
            Status = true;
        }
    }    
    catch(SQLException e)
    {
      System.exit(0);
    }
	return Status;	
	}
public static void aendern_gesehen_status(int Nid, int S, int E,boolean value) {

	int Vid=0;
	boolean gesehen=false;
	try
    {
    strsql= "SELECT VorhandenID,Gesehen FROM Vorhanden WHERE Serie = "+ Nid+" AND Staffel = "+S+" AND Episode = "+E+" AND Vorhanden = True"; 
    ResultSet rs = stmt.executeQuery(strsql); 
   rs = stmt.executeQuery(strsql);
    while(rs.next())
      	{
        	Vid = Integer.parseInt(rs.getString("VorhandenID"));
        	gesehen = rs.getBoolean("Gesehen");
        }
    rs.close();
        	
    if(gesehen==value){	
	}
	else{
		strsql= "UPDATE Vorhanden SET Gesehen = "+value+" WHERE VorhandenID = "+Vid;
        stmt.executeUpdate(strsql);
	}
    }
    catch(SQLException e)
    {
      System.exit(0);
    }
}

public static void hinzufuegen_serie(String inhalt) {
	try {
		if(inhalt.contains(url1) || inhalt.contains(url2)){
		if(inhalt.contains(url1)){
		inhalt= inhalt.substring(url1.length(), inhalt.indexOf(url3));
		}
		if(inhalt.contains(url2)){
			inhalt= inhalt.substring(url2.length(), inhalt.indexOf(url3));
		}
		strsql= "INSERT INTO Serien (Serienname) VALUES ('"+ inhalt +"')"; 
		stmt.executeUpdate(strsql);
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
}

public static boolean check_serie_vorhanden(String inhalt) {
	boolean back = false;
	int Sid=0;
	try {
		//dbVerbindung = DriverManager.getConnection("jdbc:odbc:MS Access Database");
		if(inhalt.contains(url1)|| inhalt.contains(url2)){
			if(inhalt.contains(url1)){
		
			inhalt= inhalt.substring(url1.length(), inhalt.indexOf(url3));
		}
		if(inhalt.contains(url2)){
			inhalt= inhalt.substring(url2.length(), inhalt.indexOf(url3));
		}
		strsql= "SELECT SerienID,Serienname FROM Serien WHERE Serienname = '"+ inhalt+"'"; 
		ResultSet rs = stmt.executeQuery(strsql);
		rs = stmt.executeQuery(strsql);
		while(rs.next())
      		{
        		Sid = Integer.parseInt(rs.getString("SerienID"));
      		}
		rs.close();
		if(Sid==0){
			back = true;
		}
		else{
			back = false;
		}
		} 
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return back;
}
public static boolean loeschen_serie(String inhalt) {
	boolean back = false;
	int Sid=0;
	try {
		strsql= "SELECT SerienID,Serienname FROM Serien WHERE Serienname = '"+ inhalt+"'"; 
		ResultSet rs = stmt.executeQuery(strsql);
		rs = stmt.executeQuery(strsql);
		while(rs.next())
      		{
        		Sid = Integer.parseInt(rs.getString("SerienID"));
      		}
		rs.close();
		if(Sid==0){
			back = false;
		}
		else{
			strsql = "DELETE FROM Vorhanden WHERE Serie = " + Sid; 
			stmt.executeUpdate(strsql);
			strsql = "DELETE FROM Serien WHERE SerienID = " + Sid; 
			stmt.executeUpdate(strsql);
			back = true;
		}
	} catch (SQLException e) {
		e.printStackTrace();
	}
	return back;
}
static ArrayList<String> ausgabe_episoden(String serie, int staffel) {
	
	ArrayList<String> r�ck = new ArrayList<String>();
	try
    {
    
	strsql= "SELECT DISTINCT Vorhanden.Episode,Vorhanden.Staffel,Vorhanden.Serie "
			+ "FROM Vorhanden "
			+ "WHERE Vorhanden.Serie = '"+konvert_serienname_to_id(serie)+"' AND Vorhanden.Staffel = "+staffel;
    
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck.add(rs.getString("Vorhanden.Episode"));
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return r�ck;
}

static ArrayList<String> ausgabe_serie() {
	
	ArrayList<String> r�ck = new ArrayList<String>();;
	try
    {

    strsql= "SELECT Serienname FROM Serien";

    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck.add(rs.getString("Serienname"));
    	}
    rs.close();    

    } catch(SQLException e)
    {
      System.out.println("DB-Verbindungsfehler: "+e);
      System.exit(0);
    }

	return r�ck;
}

public static int konvert_serienname_to_id(String Sid){
	
	int Name = 0;
	try
    {
    strsql= "SELECT SerienID,Serienname FROM Serien WHERE Serienname = '"+ Sid+"'";
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
         	Name= Integer.parseInt(rs.getString("SerienID"));
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.out.println("DB-Verbindungsfehler: "+e);
      System.exit(0);
    }
	return Name;
}

public static boolean ausgabe_vorhanden(String serie, int staffel,int episode) {
	boolean r�ck = false;
	
	try
    {
    
	strsql= "SELECT Vorhanden.Vorhanden,Vorhanden.Staffel,Vorhanden.Serie,Vorhanden.Episode "
			+ "FROM Vorhanden "
			+ "WHERE Vorhanden.Serie = '"+konvert_serienname_to_id(serie)+"' AND Vorhanden.Staffel = "+staffel+" AND Vorhanden.Episode = "+episode;
    
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck=rs.getBoolean("Vorhanden.Vorhanden");
    	}
    rs.close();    
                
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return r�ck;
}

public static boolean ausgabe_gesehen(String serie, int staffel,int episode) {
	boolean r�ck = false;
	
	try
    {
    
	strsql= "SELECT Vorhanden.Gesehen,Vorhanden.Staffel,Vorhanden.Serie,Vorhanden.Episode "
			+ "FROM Vorhanden "
			+ "WHERE Vorhanden.Serie = '"+konvert_serienname_to_id(serie)+"' AND Vorhanden.Staffel = "+staffel+" AND Vorhanden.Episode = "+episode;
    
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck=rs.getBoolean("Vorhanden.Gesehen");
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return r�ck;
}

public static String ausgabe_link(String serie, int staffel,int episode) {
	String r�ck = "";
	try
    {
    
	strsql= "SELECT Vorhanden.Staffel,Vorhanden.Serie,Vorhanden.Episode "
			+ "FROM Vorhanden "
			+ "WHERE Vorhanden.Serie = "+konvert_serienname_to_id(serie)+" AND Vorhanden.Staffel = "+staffel+" AND Vorhanden.Episode = "+episode;
    
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck=url1+serie+url3+",s"+staffel+"e"+episode;
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return r�ck;
}


static int[] ausgabe_serien_ids() {
	int[] SAnzahl = new int[ausgabe_serien_anzahl()-1];
	int z�hler=0;
	try
    {
    strsql= "SELECT SerienID FROM Serien";  
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	SAnzahl[z�hler]= Integer.parseInt(rs.getString("SerienID"));
    		z�hler++;
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return SAnzahl;
}

static int[] check_neue_folgen(int tage) {
	
	int z�hler=0;
	
	Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	Timestamp zeitraum = new Timestamp(date.getTime()-(tage*24*60*60*1000));
	try
    {
    strsql= "SELECT Vorhanden.VorhandenID FROM Vorhanden WHERE Vorhanden.Datum > '"+zeitraum+"'";  
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	z�hler++;
    	}
    rs.close();    
    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	int[] SAnzahl = new int[z�hler];
	z�hler=0;
	try
    {
    strsql= "SELECT Vorhanden.VorhandenID FROM Vorhanden WHERE Vorhanden.Datum > '"+zeitraum+"' ORDER BY Vorhanden.Datum ASC";  
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	SAnzahl[z�hler]= Integer.parseInt(rs.getString("VorhandenID"));
    		z�hler++;
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return SAnzahl;
}

public static ArrayList ausgabe_folge(int VID) {
	
	ArrayList r�ck = new ArrayList<String>();;
	try
    {
    
	strsql= "SELECT Vorhanden.Episode,Vorhanden.Staffel,Vorhanden.Serie,Vorhanden.Datum,Vorhanden.Gesehen,Vorhanden.Vorhanden "
			+ "FROM Vorhanden "
			+ "WHERE Vorhanden.VorhandenID = "+VID;
    
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck.add(Integer.parseInt(rs.getString("Vorhanden.Serie")));
        	r�ck.add(Integer.parseInt(rs.getString("Vorhanden.Staffel")));
        	r�ck.add(Integer.parseInt(rs.getString("Vorhanden.Episode")));
        	r�ck.add(rs.getBoolean("Vorhanden.Vorhanden"));
        	r�ck.add(rs.getBoolean("Vorhanden.Gesehen"));
        	r�ck.add(rs.getDate("Vorhanden.Datum"));
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return r�ck;
}

static int[] check_ungesehen() {
	
	int z�hler=0;
	try
    {
    strsql= "SELECT VorhandenID FROM Vorhanden WHERE Gesehen = FALSE";  
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	z�hler++;
    	}
    rs.close();    
    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	int[] SAnzahl = new int[z�hler];
	z�hler=0;
	try
    {
    strsql= "SELECT VorhandenID FROM Vorhanden WHERE Gesehen = FALSE"; 
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	SAnzahl[z�hler]= Integer.parseInt(rs.getString("VorhandenID"));
    		z�hler++;
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return SAnzahl;
}

static int[] check_alle_folgen() {
	
	int z�hler=0;
	try
    {
    strsql= "SELECT Vorhanden.VorhandenID FROM Vorhanden";  
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	z�hler++;
    	}
    rs.close();    
    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	int[] SAnzahl = new int[z�hler];
	z�hler=0;
	try
    {
    strsql= "SELECT Vorhanden.VorhandenID FROM Vorhanden"; 
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	SAnzahl[z�hler]= Integer.parseInt(rs.getString("VorhandenID"));
    		z�hler++;
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return SAnzahl;
}

static void aktualisieren_datenbank(){
	
	int[] SA = ausgabe_serien_ids();
	for(int k=0;k<SA.length;k++){
		String s = url1+konvert_serien_id_to_name(SA[k])+url3;
		//Toast.makeText(context, "vor Auswertung", Toast.LENGTH_LONG).show();
		Object[] Auswertung = WebsiteReader.WBAuswerten(s, konvert_serien_id_to_name(SA[k]));
		//Toast.makeText(context, "nach Auswertung", Toast.LENGTH_LONG).show();
		for(int i = 0;i< Auswertung.length;i++){
			String ausstr = Auswertung[i].toString();
			ausstr = ausstr.substring(1, ausstr.length() - 1);
			ausstr = ausstr.replaceAll(" ","");
			String [] arr = ausstr.split(",");
			for(int j = 1;j<arr.length;j++){
				hinzufuegen_episode(SA[k],Integer.parseInt(arr[0]),Integer.parseInt(arr[j]));
				
			}
			
		}
		
	}
	
}

public static ArrayList<String> ausgabe_serien_all() {
	ArrayList<String> r�ck = new ArrayList<String>();
	try
    {

    strsql= "SELECT Serienname FROM Serien";

    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck.add(rs.getString("Serienname"));
    	}
    rs.close();    

    } catch(SQLException e)
    {
      System.out.println("DB-Verbindungsfehler: "+e);
      System.exit(0);
    }

	return r�ck;
}

public static ArrayList<String> ausgabe_serien_neu(int tage) {
	
	Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	Timestamp zeitraum = new Timestamp(date.getTime()-(tage*24*60*60*1000));
	
	ArrayList<String> r�ck = new ArrayList<String>();
	try
    {
    strsql= "SELECT DISTINCT Serien.Serienname FROM Serien,Vorhanden WHERE Serien.SerienID = Vorhanden.Serie AND Vorhanden.Datum > '"+zeitraum+"'";  
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
    	r�ck.add(rs.getString("Serienname"));
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return r�ck;

}

public static ArrayList<String> ausgabe_serien_ungesehen() {
		
	ArrayList<String> r�ck = new ArrayList<String>();
	try
    {
    strsql= "SELECT DISTINCT Serien.Serienname FROM Serien,Vorhanden WHERE Serien.SerienID = Vorhanden.Serie AND Vorhanden.Gesehen = FALSE"; 
    ResultSet rs = stmt.executeQuery(strsql);
    while(rs.next())
    	{
        	r�ck.add(rs.getString("Serienname"));
        	
    	}
    rs.close();    
    } catch(SQLException e)
    {
      System.exit(0);
    }
	return r�ck;
}

public static ArrayList<String> ausgabe_staffeln(String head, int serie,int tage) {
	ArrayList<String> r�ck = new ArrayList<String>();
	Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	Timestamp zeitraum = new Timestamp(date.getTime()-(tage*24*60*60*1000));
	
	
	switch (head){
	case ALLE_FOLGEN:
		
		try
	    {

	    strsql= "SELECT DISTINCT Vorhanden.Staffel FROM Vorhanden WHERE Vorhanden.Serie = "+serie+" ORDER BY Vorhanden.Staffel ASC";

	    ResultSet rs = stmt.executeQuery(strsql);
	    while(rs.next())
	    	{
	        	r�ck.add(rs.getString("Staffel"));
	    	}
	    rs.close();    

	    } catch(SQLException e)
	    {
	      System.out.println("DB-Verbindungsfehler: "+e);
	      System.exit(0);
	    }
		
    	break;
	case NEUE_FOLGEN:
		
		try
	    {

	    strsql= "SELECT DISTINCT Vorhanden.Staffel FROM Vorhanden WHERE Vorhanden.Serie = "+serie+" AND Vorhanden.Datum > '"+zeitraum+"' ORDER BY Vorhanden.Staffel ASC";

	    ResultSet rs = stmt.executeQuery(strsql);
	    while(rs.next())
	    	{
	        	r�ck.add(rs.getString("Staffel"));
	    	}
	    rs.close();    

	    } catch(SQLException e)
	    {
	      System.out.println("DB-Verbindungsfehler: "+e);
	      System.exit(0);
	    }
		
		break;	
	case NOCH_NICHT_GESEHEN_FOLGEN:
		
		try
	    {

	    strsql= "SELECT DISTINCT Vorhanden.Staffel FROM Vorhanden WHERE Vorhanden.Serie = "+serie+" AND Vorhanden.Gesehen = FALSE ORDER BY Vorhanden.Staffel ASC";

	    ResultSet rs = stmt.executeQuery(strsql);
	    while(rs.next())
	    	{
	        	r�ck.add(rs.getString("Staffel"));
	    	}
	    rs.close();    

	    } catch(SQLException e)
	    {
	      System.out.println("DB-Verbindungsfehler: "+e);
	      System.exit(0);
	    }
		
		break;
	default:
		
		break;
	}
	return r�ck;
}

public static ArrayList<String> ausgabe_episoden(String head,int serie, int staffel, int tage) {
	ArrayList<String> r�ck = new ArrayList<String>();
	Timestamp date = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
	Timestamp zeitraum = new Timestamp(date.getTime()-(tage*24*60*60*1000));
	
	
	switch (head){
	case ALLE_FOLGEN:
		
		try
	    {

	    strsql= "SELECT DISTINCT Vorhanden.Episode FROM Vorhanden WHERE Vorhanden.Serie = "+serie+" AND Vorhanden.Staffel = "+staffel+" ORDER BY Vorhanden.Episode ASC";

	    ResultSet rs = stmt.executeQuery(strsql);
	    while(rs.next())
	    	{
	        	r�ck.add(rs.getString("Episode"));
	    	}
	    rs.close();    

	    } catch(SQLException e)
	    {
	      System.out.println("DB-Verbindungsfehler: "+e);
	      System.exit(0);
	    }
		
    	break;
	case NEUE_FOLGEN:
		
		try
	    {

	    strsql= "SELECT DISTINCT Vorhanden.Episode FROM Vorhanden WHERE Vorhanden.Serie = "+serie+" AND Vorhanden.Staffel = "+staffel+" AND Vorhanden.Datum > '"+zeitraum+"' ORDER BY Vorhanden.Episode ASC";

	    ResultSet rs = stmt.executeQuery(strsql);
	    while(rs.next())
	    	{
	        	r�ck.add(rs.getString("Episode"));
	    	}
	    rs.close();    

	    } catch(SQLException e)
	    {
	      System.out.println("DB-Verbindungsfehler: "+e);
	      System.exit(0);
	    }
		
		break;	
	case NOCH_NICHT_GESEHEN_FOLGEN:
		
		try
	    {

	    strsql= "SELECT DISTINCT Vorhanden.Episode FROM Vorhanden WHERE Vorhanden.Serie = "+serie+" AND Vorhanden.Staffel = "+staffel+" AND Vorhanden.Gesehen = FALSE ORDER BY Vorhanden.Episode ASC";

	    ResultSet rs = stmt.executeQuery(strsql);
	    while(rs.next())
	    	{
	        	r�ck.add(rs.getString("Episode"));
	    	}
	    rs.close();    

	    } catch(SQLException e)
	    {
	      System.out.println("DB-Verbindungsfehler: "+e);
	      System.exit(0);
	    }
		
		break;
	default:
		
		break;
	}
	return r�ck;
	
	}

}





