import java.util.*;
import java.io.*;
import java.net.*;

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
}

