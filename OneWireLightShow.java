import java.util.*;
import java.io.*;
import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.*;

public class OneWireLightShow extends OneWireCommands {
    private OneWireContainer05[] owc = null;
    byte[][] stateOff = new byte[5][];
    byte[][] stateOn = new byte[5][];
    
    public OneWireLightShow() {
        super();

        this.owc = getLightSwitches();
        getSwitchStates( owc, stateOff, stateOn );
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

    private void clearLights() {
        
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
        int x = 0;

        //OneWireContainer05[] owc = getLightSwitches();

        //byte[][] stateOff = new byte[5][];
        //byte[][] stateOn = new byte[5][];
        do { //this will keep this fun thing looping even after it exceptions
            try {
                //lets get the on and off states of all the lights
                //getSwitchStates( owc, stateOff, stateOn );

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
                } while ( loopForever || x < loops );
            }catch( Exception e ) {
                System.out.println( "Oh Shazbot!" );
                e.printStackTrace();
            }
        } while ( loopForever || x < loops );
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
                } while ( loopForever || x < loops );
            }catch( Exception e ) {
                System.out.println( "Oh Shazbot!" );
                e.printStackTrace();
            }
        } while ( loopForever || x < loops );
    }

}
