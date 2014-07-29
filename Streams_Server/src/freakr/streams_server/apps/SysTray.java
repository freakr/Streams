package freakr.streams_server.apps;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


public class SysTray {

	TrayIcon trayIcon = null;
	final static Image IMAGE_START = set_image("A");//Toolkit.getDefaultToolkit().getImage(Server_Main.class.getResource("/S.png"));
	ThreadPooledServer server;
	
	public SysTray(ThreadPooledServer server) {
		this.server = server ;
	}
	public void start() {
	
	
    if (SystemTray.isSupported()) {
        SystemTray tray = SystemTray.getSystemTray();
        ActionListener beenden = new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
        	}

        };
        ActionListener serverstart = new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
        		
        		if (server.isStopped){
        			server = new ThreadPooledServer(15000);
        			System.out.println("Starting Server");
        			server.tray = SysTray.this;
        			update("A");
        			new Thread(server).start();
        		}
        		else{
        			System.out.println("Server is already running");
        		}
            }

        };
        ActionListener serverstop = new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
        		System.out.println("Stopping Server");
        		update("S");
        		server.stop();		
            }

        };
        PopupMenu popup = new PopupMenu();
        MenuItem sstopItem = new MenuItem();
        sstopItem.addActionListener(serverstop);
        sstopItem.setLabel("Server-Stop");
        MenuItem sstartItem = new MenuItem();
        sstartItem.addActionListener(serverstart);
        sstartItem.setLabel("Server-(Re)-Start");
        MenuItem endItem = new MenuItem();
        endItem.addActionListener(beenden);
        endItem.setLabel("Streams_Server Beenden");
        popup.add(sstopItem);
        popup.add(sstartItem);
        popup.add(endItem);
        trayIcon = new TrayIcon(IMAGE_START, "Streams_Server", popup);
        trayIcon.addActionListener(beenden);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.err.println(e);
        }
    } else {
    }
 }
	public void update(int umkehrer){
		if (trayIcon != null) {
			
			BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);  
			Graphics2D g2d = image.createGraphics();
			Font font = g2d.getFont();  
			font = font.deriveFont(Font.BOLD,18.0f); // or any other size  
			g2d.setFont(font);
			g2d.setColor(Color.GREEN);
			if(umkehrer == 7 || umkehrer == 8){
				g2d.setColor(Color.YELLOW);
			}
			if(umkehrer == 9 || umkehrer == 10){
				g2d.setColor(Color.RED);
			}
			g2d.drawString(String.valueOf(umkehrer),3, 15);
			g2d.dispose(); 
			trayIcon.setImageAutoSize(true);
			trayIcon.setImage(image);
		}
	}
	
	

	public void update(String status){
		if (trayIcon != null) {
			
			BufferedImage image = set_image(status);
			trayIcon.setImageAutoSize(true);
			trayIcon.setImage(image);
			if (status.equals("C")){
				trayIcon.setToolTip("Streams_Server : Lokale IP = "+String.valueOf(server.serverSocket.getLocalSocketAddress()));
			}else
			{
			trayIcon.setToolTip("Streams_Server");
			}
		}
	}
	private static BufferedImage set_image(String status){
		
		BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);  
			Graphics2D g2d = image.createGraphics();
			Font font = g2d.getFont();  
			font = font.deriveFont(Font.BOLD,18.0f); // or any other size  
			g2d.setFont(font);
			g2d.setColor(Color.GREEN);
			g2d.drawString(status,3, 15);
			g2d.dispose(); 
		
		return image;
	}
	private static String pad(String s, int length)
	{
	    while (s.length() < length) s = "0"+s;
	    return s;
	}
}
	

