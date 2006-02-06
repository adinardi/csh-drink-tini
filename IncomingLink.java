import java.io.*;
import java.net.*;

class IncomingLink extends Thread {
    Socket socket = null;
    
    public IncomingLink( Socket socket ) {
        this.socket = socket;
    }

    public void run() {
        listen();
    } 

    private void listen() {
        InputStream in = null;
        try {
            in = socket.getInputStream();
        }catch( Exception e ) {
            e.printStackTrace();
        }

        int opcode = 0;
        String data = null;
        String fullLine = null;
        //int[] odata;
        //int temp;
        //String datamsg = null;
        //Long opcodemsg; 
        int lo = 0; //length opcode
        int ld = 0; //length data

        boolean active = true;
        BufferedReader read = new BufferedReader( new InputStreamReader( in ) );

        while( active ) {
            try {
                fullLine = read.readLine();
                System.out.println( "Raw Crap: " + fullLine );
            }catch( SocketException e ) {
                //active = false;
                e.printStackTrace();
                break;
            }catch( Exception e ) {
                e.printStackTrace();
            }
            if( fullLine == null ) {
                active = false;
                break;
            }

            //System.out.println( read.readLine() );
            opcode = Integer.parseInt( fullLine.substring(0,1) );
            data = fullLine.substring(1, fullLine.length()  );
            switch( opcode ) {
                case -1:
                    //Um, no?
                    break;
                case 1:
                    //Login ACK
                    System.out.println( "Login ACK!" );
                    break;
                case 2:
                    //Login NACK
                    System.out.println( "Login NACK!" );
                    break;
                case 3:
                    //drop slot
                    System.out.println( "Drop Slot!" );
                    CommLink.getInstance().getOutgoingLink().sendDropACK();
                    break;
                case 6:
                    //Slot Status Req
                    System.out.println( "Slot Status Req!" );
                    break;
            }
            opcode = -1;
        }

    }
}

