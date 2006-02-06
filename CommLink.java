import java.net.*;
import java.io.*;

/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
public class CommLink{

    private Socket socket = null;

    private IncomingLink inlink = null;

    private OutgoingLink outlink = null;

    private static CommLink instance = null;
    
    public void StartConnection() {
        try {
            //connect to the drink server?
            socket = new Socket( "scorn", 4343 );
            System.out.println( "Connected" );
        }catch( Exception e ) {
            e.printStackTrace();
        }
        inlink = new IncomingLink( socket );
        inlink.start();

        outlink = new OutgoingLink( socket );
        outlink.start();
        outlink.sendLogin();
    }
    
    public OutgoingLink getOutgoingLink() {
        return outlink;
    }

    public IncomingLink getIncomingLink() {
        return inlink;
    }

    public static CommLink getInstance() {
        if( instance == null ) {
            instance = new CommLink();
        }
        return instance;
    }
    
    public static void main( String[] args ) {
        CommLink c = CommLink.getInstance();
        System.out.println( "Start Connection" );
        c.StartConnection();
    }
}
