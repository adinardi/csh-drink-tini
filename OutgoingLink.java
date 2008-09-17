import java.util.*;
import java.io.*;
import java.net.*;

/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
class OutgoingLink extends Thread {
    Socket socket = null;
    DataOutputStream out = null;

    public OutgoingLink( Socket socket ) {
        this.socket = socket;
    }

    public void run() {
        //do stuff
    }

    public void sendLogin() {
        try {
            out = new DataOutputStream(socket.getOutputStream());

            out.writeBytes( "0" );
            //this should eventually pull from a config
            out.writeBytes( ConfigMgr.getInstance().getPassword() );
            out.writeBytes( "\n" );
            out.flush();

        }catch( Exception e ) {
            e.printStackTrace();
            CommLink.getInstance().Reconnect();
        }
    }

    public void sendDropACK() {
        try {
            System.out.println("Sending Drop ACK");
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Writing 4");
            out.writeBytes( "4\n" );
            out.flush();

        }catch( Exception e ) {
            e.printStackTrace();
            CommLink.getInstance().Reconnect();
        }
        System.out.println("ack'ed");
    }

    public void sendTemp( double temp ) {
        try {

            System.out.println( "Temp: " +  (new Double(temp)).toString() );
            out = new DataOutputStream( socket.getOutputStream() );
            
            out.writeBytes( "8" );
            out.writeBytes( (new Double(temp)).toString() );
            out.writeBytes( "\n" );
            
            out.flush();
            
        }catch( Exception e ) {
            CommLink.getInstance().Reconnect();
        }
    }

    public void sendDropNACK() {
        try {
            out = new DataOutputStream( socket.getOutputStream() );

            out.writeBytes( "5\n" );
            out.flush();
        }catch( Exception e ) {
            CommLink.getInstance().Reconnect();
        }
    }

    public void sendSlotInfo( int slot, boolean empty ) {
        try {
            out = new DataOutputStream( socket.getOutputStream() );

            out.writeBytes( "7" );

            out.writeBytes( "" + slot + " " + (empty ? 0 : 1) );
            out.writeBytes( "\n" );

            out.flush();
        }catch( Exception e ) {
            CommLink.getInstance().Reconnect();
        }
    }

    public void sendSlotInfo( boolean[] empty ) {
        //If the empty info hasn't been scanned from the machine yet, ignore.
        if( empty[0] == false ) { return; }
        System.out.println( "Sending Complete Slot Info" );
        try {
            out = new DataOutputStream( socket.getOutputStream() );

            out.writeBytes( "7" );

            int slots = ConfigMgr.getInstance().getNumSlots() + 1;
            
            for( int x = 1; x < slots; x++ ) {
                if( x > 1 ) {
                    out.writeBytes( "`" );
                }
                out.writeBytes( "" + x + " " + ( empty[x] ? 0 : 1 ) );
            }
            out.writeBytes( "\n" );
            
            out.flush();
        }catch( Exception e ) {
            CommLink.getInstance().Reconnect();
        }
    }
}

