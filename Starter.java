/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
class Starter {
    public static void main( String[] args ) {
        //Load up the CommLink
        CommLink c = CommLink.getInstance();
        System.out.println( "Start Connection" );
        if( args.length > 0 ) {
            c.setConnectionInfo( args[0], Integer.parseInt(args[1]) );
        }
        c.Connect(); //start the connection to the server
        
        //start up our minutely temperature sensor/sender
        TempWorker temp = new TempWorker();
        temp.start();
    }
}
