import java.net.*;
import java.io.*;

/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
public class CommLink{

    Socket socket = null;

    IncomingLink inlink = null;

    OutgoingLink outlink = null;

    public void StartConnection() {
        try {
            //connect to the drink server?
            socket = new Socket( "danlaptop", 4343 );
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
                        outlink.sendDropACK();
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
            }
        }

        public void sendDropACK() {
            try {
                out = new DataOutputStream(socket.getOutputStream());

                out.writeBytes( "4\n" );
                out.flush();
                
            }catch( Exception e ) {
                e.printStackTrace();
            }
        }
    }
    public static void main( String[] args ) {
        CommLink c = new CommLink();
        System.out.println( "Start Connection" );
        c.StartConnection();
    }
}
