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
            out.writeBytes( "password\n" );
            out.flush();

        }catch( Exception e ) {
            e.printStackTrace();
            CommLink.getInstance().Reconnect();
        }
    }

    public void sendDropACK() {
        try {
            out = new DataOutputStream(socket.getOutputStream());

            out.writeBytes( "4\n" );
            out.flush();

        }catch( Exception e ) {
            e.printStackTrace();
            CommLink.getInstance().Reconnect();
        }
    }

    public void sendTemp( double temp ) {
        try {
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

            out.writeBytes( "" + slot + " " + (empty ? 1 : 0) );
            out.writeBytes( "\n" );

            out.flush();
        }catch( Exception e ) {
            CommLink.getInstance().Reconnect();
        }
    }

    public void sendSlotInfo( boolean[] empty ) {
        try {
            out = new DataOutputStream( socket.getOutputStream() );

            out.writeBytes( "7" );

            for( int x = 1; x < 6; x++ ) {
                if( x > 1 ) {
                    out.writeBytes( "`" );
                }
                out.writeBytes( "" + x + " " + ( empty[x] ? 1 : 0 ) );
            }
            
            out.flush();
        }catch( Exception e ) {
            CommLink.getInstance().Reconnect();
        }
    }
}

