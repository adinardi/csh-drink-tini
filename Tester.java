
public class Tester {
    public static void main(String[] args) {
        OneWireLightShow owc = OneWireLightShow.getInstance();
        /*
        if( args.length > 0 ) {
            owc.drop( Integer.parseInt(args[0]) );
        }else{
            while ( true ) {
                owc.knightRider( false, 10 );
                owc.bouncingDot( false, 10 );
                owc.worm( false, 10 );
            }
        }*/
        Thread t = new Thread(owc);
        t.start();
    }
}
