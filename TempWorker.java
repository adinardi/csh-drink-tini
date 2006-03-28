/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
class TempWorker extends Thread {
    
    public void run() {
        double temp;
        OneWireCommands owc = OneWireCommands.getInstance();

        owc.setTempRes();

        while( true ) {
            //grab temp
            //System.out.println( "Read Temp" );
            temp = owc.readTemp();
            
            //send it
            CommLink.getInstance().getOutgoingLink().sendTemp( temp );
            //System.out.println( "Sending" );
            
            //sleep
            try {
                sleep( 60000 );
            }catch( Exception e ) {
                System.out.println( "Sleep Issue." );
            }
        }        
    }
}
