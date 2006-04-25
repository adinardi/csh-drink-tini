import java.io.*;
import java.net.*;

/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
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
            System.out.println( "Cannot get input stream");
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
                //e.printStackTrace();
                System.out.println( "Lost Connection To Server" );
                ConfigMgr.getInstance().setConnected( false );
                CommLink.getInstance().Reconnect();
                break;
            }catch( IOException e ) {
                System.out.println( "Lost Connection To Server" );
                ConfigMgr.getInstance().setConnected( false );
                CommLink.getInstance().Reconnect();
                break;
            }catch( Exception e ) {
                e.printStackTrace();
                System.out.println( "Error reading Incoming Data.");
            }
            if( fullLine == null ) {
                active = false;
                System.out.println( "Lost Connection To Server" );
                ConfigMgr.getInstance().setConnected( false );
                CommLink.getInstance().Reconnect();
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
                    CommLink.getInstance().getOutgoingLink().sendSlotInfo( OneWireCommands.getInstance().getEmptyInfo() );
                    break;
                case 2:
                    //Login NACK
                    System.out.println( "Login NACK!" );
                    break;
                case 3:
                    //drop slot
                    System.out.println( "Drop Slot!" );
                    //OneWireLightShow.getInstance().setStop( true );

                    this.setPriority( Thread.MAX_PRIORITY );
                    int stat = OneWireCommands.getInstance().drop( Integer.parseInt(data) );
                    this.setPriority( Thread.NORM_PRIORITY );

                    if( stat == 200 ) {
                        CommLink.getInstance().getOutgoingLink().sendDropACK();
                    }else{
                        CommLink.getInstance().getOutgoingLink().sendDropNACK();
                    }
                    //OneWireLightShow.getInstance().setStop( false );
                    break;
                case 6:
                    //Slot Status Req
                    System.out.println( "Slot Status Req!" );

                    //OneWireLightShow.getInstance().setStop( true );

                    CommLink.getInstance().getOutgoingLink().sendSlotInfo( OneWireCommands.getInstance().getEmptyInfo() );

                    //OneWireLightShow.getInstance().setStop( false );
                    break;
            }
            opcode = -1;
        }

    }
}

