import java.net.*;
import java.io.*;

/**
 * Singleton class for managing the connection to the drink server.
 * 
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
public class CommLink{
    /**
     * Server hostname to connect to
     */
    private String host = "drink";
    
    /**
     * Port number server runs on
     */
    private int port = 4343;
    
    /**
     * Socket we're using to connect through
     */
    private Socket socket = null;

    /**
     * Object which handles incoming commands from server
     */
    private IncomingLink inlink = null;

    /**
     * Object which handles outgoing commands to server
     */
    private OutgoingLink outlink = null;

    private static CommLink instance = null;
    
    /**
     * Sets the Host and Port the server is located at
     *
     * @param host Hostname of the system running the drink server
     * @param port Port number server runs on
     */
    public void setConnectionInfo( String host, int port ) {
        this.host = host;
        this.port = port;
    }
    
    /**
     * Start the connection sequence to the drink server.
     */
    private synchronized void StartConnection() {
        if( ConfigMgr.getInstance().getConnected() == true ) { return; } //already connected. get out.

        if( socket != null ) {
            try {
                socket.close();
            }catch ( Exception e ) { }
        }

        try {
            //connect to the drink server?
            socket = new Socket( host, port );
            System.out.println( "Connected" );
        }catch( Exception e ) {
            //e.printStackTrace();

            System.out.println( "Error Connecting" );
            ConfigMgr.getInstance().setConnected( false ); //we didn't connect correctly, make sure we know that
            return;
        }
        inlink = new IncomingLink( socket );
        inlink.start();

        outlink = new OutgoingLink( socket );
        outlink.start();
        outlink.sendLogin();

        //for what we know, we're connected
        ConfigMgr.getInstance().setConnected( true );
    }//StartConnection()

    /**
     * Launches the initial connection to the drink server
     */
    public void Connect() {
        while( ConfigMgr.getInstance().getConnected() == false ) {
            System.out.println( "Connecting..." );

            StartConnection();

            if( ConfigMgr.getInstance().getConnected() == false ) {
                try {
                    Thread.sleep( 30000 );
                }catch( Exception e ) {

                }
            }

        }
    }//Connect()

    /**
     * Starts a reconnect cycle to the drink server on 30 second intervals.
     */
    public void Reconnect() {
        while( ConfigMgr.getInstance().getConnected() == false ) {
            System.out.println( "Reconnecting..." );
            try {
                Thread.sleep( 30000 );
            }catch( Exception e ) {

            }
            if( ConfigMgr.getInstance().getConnected() == false ) {
                StartConnection();
            }
        }
    }//Reconnect()

    /**
     * Return the OutgoingLink responsible for sending commands to the server
     */
    public OutgoingLink getOutgoingLink() {
        return outlink;
    }

    /**
     * Return the IncomingLink responsible for commands coming from the server
     */
    public IncomingLink getIncomingLink() {
        return inlink;
    }

    public static CommLink getInstance() {
        if( instance == null ) {
            instance = new CommLink();
        }
        return instance;
    }

}
