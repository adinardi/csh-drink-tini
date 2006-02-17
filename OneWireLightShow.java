import java.util.*;
import java.io.*;
import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.*;

public class OneWireLightShow implements Runnable{
    private OneWireContainer05[] owc = null;
    byte[][] stateOff = new byte[5][];
    byte[][] stateOn = new byte[5][];
    private boolean stop = false;
    
    private DSPortAdapter adapter = null;
    //Stuff here.
    protected String[] lights = new String[6];

    private static OneWireLightShow instance = null;

    public static OneWireLightShow getInstance() {
        if( instance == null ) {
            instance = new OneWireLightShow();
        }
        return instance;
    }
    
    private OneWireLightShow() {

        lights[0] = new String();
        lights[1] = new String("AE0000000E19A805");
        lights[2] = new String("A60000000E112C05");
        lights[3] = new String("BB0000000E069D05");
        lights[4] = new String("F80000000E1A7C05");
        lights[5] = new String("660000000E169505");

        try {
            this.adapter = OneWireAccessProvider.getDefaultAdapter();
        }catch( Exception e ) {
            System.out.println( "Oh Snap. Can't get adapter." ); 
        }
 

        this.owc = getLightSwitches();
        getSwitchStates( owc, stateOff, stateOn );
    }

    public void setStop( boolean go ) {
        stop = go;
    }

    public void run () {
        while ( true ) {
            knightRider( false, 10 );
            //display slot status
            slotStatus();
            bouncingDot( false, 10 );
            //display slot status
            slotStatus();
            worm( false, 10 );
            slotStatus();
        }
    }

    private OneWireContainer05[] getLightSwitches() {
        OneWireContainer05[] owc = new OneWireContainer05[5];
        owc[0] = getSwitch( lights[1] );
        owc[1] = getSwitch( lights[2] );
        owc[2] = getSwitch( lights[3] );
        owc[3] = getSwitch( lights[4] );
        owc[4] = getSwitch( lights[5] );

        return owc;
    }

    protected OneWireContainer05 getSwitch( String id ) {
        System.out.println( "Getting Slot: " + id );
        OneWireContainer owc = this.adapter.getDeviceContainer( id );

        if( owc instanceof OneWireContainer05 ) {
            return (OneWireContainer05) owc;
        } else {
            return null;
        }
    }

    private void getSwitchStates( OneWireContainer05[] owc, byte[][] stateOff, byte[][] stateOn ) {
        try {
            for( int x = 0; x < owc.length; x++ ) {
                stateOff[x] = owc[x].readDevice();
                owc[x].setLatchState( 0, false, false, stateOff[x] );

                stateOn[x] = owc[x].readDevice();
                owc[x].setLatchState( 0, true, false, stateOn[x] ); 
            }
        }catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void slotStatus() {
        try {
            adapter.beginExclusive( true );
            owc[0].writeDevice( stateOn[0] );
            owc[1].writeDevice( stateOn[1] );
            owc[2].writeDevice( stateOn[2] );
            owc[3].writeDevice( stateOn[3] );
            owc[4].writeDevice( stateOn[4] );

            Thread.sleep( 2000 );

            owc[0].writeDevice( stateOff[0] );
            owc[1].writeDevice( stateOff[1] );
            owc[2].writeDevice( stateOff[2] );
            owc[3].writeDevice( stateOff[3] );
            owc[4].writeDevice( stateOff[4] );
            adapter.endExclusive();
        }catch( Exception e ) {
            e.printStackTrace();
        }
    }

    public void knightRider() {
        knightRider( true, 0 );
    }
    public void knightRider( boolean loopForever ) {
        knightRider( loopForever, 0 );
    }
    public void knightRider( int loops ) {
        knightRider( false, loops );
    }
    public void knightRider( boolean loopForever, int loops ) {
        if( stop ) return;

        int x = 0;

        do { //this will keep this fun thing looping even after it exceptions
            try {
                do { //make the lights flash.. here's the sequece
                    owc[0].writeDevice( stateOn[0] );
                    owc[4].writeDevice( stateOn[4] );
                    owc[1].writeDevice( stateOn[1] );
                    owc[3].writeDevice( stateOn[3] );
                    owc[0].writeDevice( stateOff[0] );
                    owc[4].writeDevice( stateOff[4] );
                    owc[2].writeDevice( stateOn[2] );
                    owc[1].writeDevice( stateOff[1] );
                    owc[3].writeDevice( stateOff[3] );

                    owc[1].writeDevice( stateOn[1] );
                    owc[3].writeDevice( stateOn[3] );
                    owc[2].writeDevice( stateOff[2] );
                    owc[0].writeDevice( stateOn[0] );
                    owc[4].writeDevice( stateOn[4] );
                    owc[1].writeDevice( stateOff[1] );
                    owc[3].writeDevice( stateOff[3] );

                    x++;
                } while ( (loopForever || x < loops) && !stop );

                owc[0].writeDevice( stateOff[0] );
                owc[4].writeDevice( stateOff[4] );
            }catch( Exception e ) {
                System.out.println( "Oh Shazbot!" );
                e.printStackTrace();
            }
        } while ( (loopForever || x < loops) && !stop );
    }

    public void bouncingDot() {
        knightRider( true, 0 );
    }
    public void bouncingDot( boolean loopForever ) {
        knightRider( loopForever, 0 );
    }
    public void bouncingDot( int loops ) {
        knightRider( false, loops );
    }
    public void bouncingDot( boolean loopForever, int loops ) {
        if( stop ) return;

        int x = 0;
        do { //this will keep this fun thing looping even after it exceptions
            try {
                do { //make the lights flash.. here's the sequece
                    owc[0].writeDevice( stateOn[0] );
                    owc[1].writeDevice( stateOn[1] );
                    owc[0].writeDevice( stateOff[0] );

                    owc[2].writeDevice( stateOn[2] );
                    owc[1].writeDevice( stateOff[1] );

                    owc[3].writeDevice( stateOn[3] );
                    owc[2].writeDevice( stateOff[2] );

                    owc[4].writeDevice( stateOn[4] );
                    owc[3].writeDevice( stateOff[3] );

                    owc[3].writeDevice( stateOn[3] );
                    owc[4].writeDevice( stateOff[4] );

                    owc[2].writeDevice( stateOn[2] );
                    owc[3].writeDevice( stateOff[3] );

                    owc[1].writeDevice( stateOn[1] );
                    owc[2].writeDevice( stateOff[2] );

                    owc[0].writeDevice( stateOn[0] );
                    owc[1].writeDevice( stateOff[1] );

                    x++;
                } while ( (loopForever || x < loops) && !stop );

                owc[0].writeDevice( stateOff[0] );
            }catch( Exception e ) {
                System.out.println( "Oh Shazbot!" );
                e.printStackTrace();
            }
        } while ( (loopForever || x < loops) && !stop );
    }

    public void worm() {
        knightRider( true, 0 );
    }
    public void worm( boolean loopForever ) {
        knightRider( loopForever, 0 );
    }
    public void worm( int loops ) {
        knightRider( false, loops );
    }
    public void worm( boolean loopForever, int loops ) {
        if( stop ) return;

        int x = 0;
        do { //this will keep this fun thing looping even after it exceptions
            try {
                do { //make the lights flash.. here's the sequece
                    owc[4].writeDevice( stateOff[4] );

                    owc[0].writeDevice( stateOn[0] );
                    owc[1].writeDevice( stateOn[1] );
                    owc[2].writeDevice( stateOn[2] );
                    owc[0].writeDevice( stateOff[0] );
                    owc[3].writeDevice( stateOn[3] );
                    owc[1].writeDevice( stateOff[1] );
                    owc[4].writeDevice( stateOn[4] );
                    owc[2].writeDevice( stateOff[2] );
                    owc[3].writeDevice( stateOff[3] );


                    x++;

                } while ( (loopForever || x < loops) && !stop );
            }catch( Exception e ) {
                System.out.println( "Oh Shazbot!" );
                e.printStackTrace();
            }
        } while ( (loopForever || x < loops) && !stop );
    }

}
