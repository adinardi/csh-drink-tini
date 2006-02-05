import java.net.*;

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
            socket = new Socket( "drink", 4343 );
        }catch( Exception e ) {
            e.printStackTrace();
        }
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
            InputStream in = socket.getInputStream();
            
            byte[] opcode;
            byte[] data;
            byte[] odata;
            byte temp;
            String msg = null;
            int lo = 0; //length opcode
            int ld = 0; //length data
            
            while( active ) {
                lo = 0;
                ld = 0;
                opcode = new byte[4];
                data = new byte[1];
                
                while( x < 4 ) {
                    opcode[lo] = in.read();
                    lo++;
                }
                
                //read bytes in until end of message
                if( (temp = in.read()) != null ) {
                    odata = data;
                    ld++;
                    data = new byte[ld+1];
                    for( int q = 0; q < odata.length; q++ ) {
                        data[q] = odata[q];
                    }
                    data[ld] = temp;
                    temp = null;
                    odata = null;
                }
                //convert our bytes into a real message
                msg = new String( data );
            }

        }
    }

    class OutgoingLink extends Thread {

    }

}
