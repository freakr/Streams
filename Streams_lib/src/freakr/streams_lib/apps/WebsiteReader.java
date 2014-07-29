package freakr.streams_lib.apps;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

 
public class WebsiteReader {
    
	public static Object[] WBAuswerten(String s,String SName) {
    
    BufferedReader br = null;
    int indexzeile = 0;
    int ZeilenAnfang = 0;
    int ZeilenEnde = 0;
    int Season=0;
    int Episode= 0;
    int strpos=0;
    int strpos2=0;
    String season= null;
    InputStreamReader isr = null;
    URL url = null;
    Object[] StaffelArray = null;
    
    try {
    	url = new URL(s);
    	List<List<Integer>> Staffel = new ArrayList();
    	//url = new URL("http://kinox.to/Stream/Revolution_german_subbed.html");
    	isr = new InputStreamReader(url.openStream());
    	br = new BufferedReader(isr);
        String line = null;
        
        while((line = br.readLine()) != null) {
            // Ganze Zeile:
        	indexzeile = indexzeile +1;
            //System.out.println(line); 
            if(line.contains("MirrorModule")) {
            	//System.out.println(line);
            	ZeilenAnfang = indexzeile +2;
            	
            	//System.out.println("ZeilenAnfang = " + ZeilenAnfang);
            }
            
            if(indexzeile==ZeilenAnfang){
            	br.mark(1000);
            }
            
            if(line.contains("EpisodeSelection")) {
            	//System.out.println(line);
            	ZeilenEnde = indexzeile -2;
            	//System.out.println("ZeilenEnde = " + ZeilenEnde);
            	break;
            }
            }
        	br.reset();
        	List<Integer>[] Episodelist= new ArrayList[ZeilenEnde-ZeilenAnfang];
        	int count1=0;
        	for(int i = ZeilenAnfang;i< ZeilenEnde;i++){
        		line=br.readLine();
        		strpos=line.indexOf("value=")+7;
        		strpos2 = line.indexOf(" rel=")-1;
        		season=line.substring(strpos, strpos2);
        		Season=Integer.parseInt(season); //Character.getNumericValue(line.charAt(strpos));
        		Episodelist[count1]= new ArrayList();
        		
        		String[] arr1= line.split("\"");
        		String[] arr = arr1[3].split(",");
        		Episodelist[count1].add(Season);
        		for (int j = 0; j < arr.length; j++) {
        			  
        			  Episode = Integer.parseInt(arr[j]);
        			  Episodelist[count1].add(Episode);
        			  //System.out.println("Season = "+ Season +" - Episode = "+ Episode);
        			  // hier function einfügen "eintrag in Datenbank"
        			  //Datenbank.DBeintragen(SName,Season,Episode);
        			}
        		Staffel.add(Episodelist[count1]);
        		//System.out.println(Staffel);
        		count1++;
        	}
        	StaffelArray= Staffel.toArray();
            
        	
        	
    } catch(FileNotFoundException e) {
        e.printStackTrace();
    } catch(IOException e) {
        e.printStackTrace();
    } finally {
        if(br != null) {
            try {
                br.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
	return StaffelArray;
}

	public Object[] WBAuswerten(Context context,String s,String SName) {
	    
	    BufferedReader br = null;
	    int indexzeile = 0;
	    int ZeilenAnfang = 0;
	    int ZeilenEnde = 0;
	    int Season=0;
	    int Episode= 0;
	    int strpos=0;
	    int strpos2=0;
	    String season= null;
	    InputStreamReader isr = null;
	    URL url = null;
	    Object[] StaffelArray = null;
	    	    
	    try {
	    	url = new URL(s);
	    	
	    	List<List<Integer>> Staffel = new ArrayList();
	    	try{
	    		isr = new InputStreamReader(url.openStream());
	    	}catch(IOException e) {
            	//Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
	    		
	    	
	    	
	    	br = new BufferedReader(isr);
	    	
	        String line = null;
	        
	        
	        while((line = br.readLine()) != null) {
	            // Ganze Zeile:
	        	indexzeile = indexzeile +1;
	            //System.out.println(line); 
	            if(line.contains("MirrorModule")) {
	            	//System.out.println(line);
	            	ZeilenAnfang = indexzeile +2;
	            	
	            	//System.out.println("ZeilenAnfang = " + ZeilenAnfang);
	            }
	            
	            if(indexzeile==ZeilenAnfang){
	            	br.mark(1000);
	            }
	            
	            if(line.contains("EpisodeSelection")) {
	            	//System.out.println(line);
	            	ZeilenEnde = indexzeile -2;
	            	//System.out.println("ZeilenEnde = " + ZeilenEnde);
	            	break;
	            }
	            }
	        	br.reset();
	        	List<Integer>[] Episodelist= new ArrayList[ZeilenEnde-ZeilenAnfang];
	        	int count1=0;
	        	for(int i = ZeilenAnfang;i< ZeilenEnde;i++){
	        		line=br.readLine();
	        		strpos=line.indexOf("value=")+7;
	        		strpos2 = line.indexOf(" rel=")-1;
	        		season=line.substring(strpos, strpos2);
	        		Season=Integer.parseInt(season); //Character.getNumericValue(line.charAt(strpos));
	        		Episodelist[count1]= new ArrayList();
	        		
	        		String[] arr1= line.split("\"");
	        		String[] arr = arr1[3].split(",");
	        		Episodelist[count1].add(Season);
	        		for (int j = 0; j < arr.length; j++) {
	        			  
	        			  Episode = Integer.parseInt(arr[j]);
	        			  Episodelist[count1].add(Episode);
	        			  //System.out.println("Season = "+ Season +" - Episode = "+ Episode);
	        			  // hier function einfügen "eintrag in Datenbank"
	        			  //Datenbank.DBeintragen(SName,Season,Episode);
	        			}
	        		Staffel.add(Episodelist[count1]);
	        		//System.out.println(Staffel);
	        		count1++;
	        	}
	        	StaffelArray= Staffel.toArray();
	            
	        	
	        	
	    } catch(FileNotFoundException e) {
	    	Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
	        e.printStackTrace();
	    } catch(IOException e) {
	    	Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
	        e.printStackTrace();
	    } finally {
	        if(br != null) {
	            try {
	                br.close();
	            } catch(IOException e) {
	            	Toast.makeText(context,e.getMessage(), Toast.LENGTH_LONG).show();
	                e.printStackTrace();
	            }
	        }
	    }
		return StaffelArray;
	}

}