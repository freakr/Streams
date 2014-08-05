package freakr.streams_server.apps;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import freakr.streams_lib.apps.Setup_Server_PC;
import freakr.streams_lib.apps.Streams_lib;

public class ThreadPooledServer implements Runnable,Streams_lib{

    protected int          serverPort   = 9000;
    protected ServerSocket serverSocket = null;
    protected boolean      isStopped    = false;
    protected Thread       runningThread= null;
    protected ExecutorService threadPool =
        Executors.newFixedThreadPool(10);
	public SysTray tray;
	public Setup_Server_PC setup = new Setup_Server_PC();
	

    public ThreadPooledServer(int port){
        this.serverPort = port;
    }

    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                	tray.update("S");
                	this.threadPool.shutdown();
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            
            this.threadPool.execute(
                new WorkerRunnable(clientSocket,tray,setup));
        }
        tray.update("S");
        this.threadPool.shutdownNow();
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
            setup.set_Parameter(SERVERSTATUS, SERVERSTATUS_OFF);
            System.out.println(setup.get_Parameter(SERVERSTATUS));
            tray.update("S");
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            setup.set_Parameter(SERVERSTATUS, SERVERSTATUS_ON);
            tray.update("A");
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 9000", e);
        }
    }
}
