/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
class Starter {
    public static void main( String[] args ) {
        //Load up the CommLink
        CommLink c = CommLink.getInstance();
        System.out.println( "Start Connection" );

        //Check for arguments (server and port)
        if( args.length > 0 ) {
            c.setConnectionInfo( args[0], Integer.parseInt(args[1]) );
        }
        c.Connect(); //start the connection to the server
        
        //Start up the OneWireCommands which will make the initial empty sensor load.
        OneWireCommands.getInstance();
        
        //start up our minutely temperature sensor/sender
        TempWorker temp = new TempWorker();
        temp.start();

        /*
        OneWireLightShow owls = OneWireLightShow.getInstance();
        Thread owlst = new Thread(owls);
        owlst.start();
        */
    }
}
