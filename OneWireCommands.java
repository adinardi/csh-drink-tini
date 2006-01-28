/*
 * OneWireCommands.java
 *
 * 
 *
 */

/**
 *This is the fun class that will handle 1-wire commands on the tinis 
 *@author Angelo DiNardi
 */
import java.util.*;
import java.io.*;
import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.*;

public class OneWireCommands {
    private DSPortAdapter adapter = null;
    //Stuff here.
    private String[] switches = new String[5];
       
    public OneWireCommands() {
        //hard coded the lights for right now
        switches[0] = new String("830000000E0ACC05");
        switches[1] = new String("C60000000E153305");
        switches[2] = new String("750000000E178205");
        switches[3] = new String("450000000E19AD05");
        switches[4] = new String("2C0000000E1B2D05");
        /*
        switches[0] = new String("AE0000000E19A805");
        switches[1] = new String("A60000000E112C05");
        switches[2] = new String("BB0000000E069D05");
        switches[3] = new String("F80000000E1A7C05");
        switches[4] = new String("660000000E169505");
        */
        try {
            this.adapter = OneWireAccessProvider.getDefaultAdapter();
        }catch( Exception e ) {
            System.out.println( "Oh Snap. Can't get adapter." ); 
        }
    }
    
    public int drop ( int slot ) {
        byte[] state = {0,0,0};
        boolean latch = false;
        OneWireContainer05 owc = null; 
        try {
        //check if slot is empty
        if( isEmpty( slot ) ) {
            //return empty code
        } 
        System.out.println( "Getting Exclusive" );
        //grab exclusive
        adapter.beginExclusive( true );
        //Get the switch and current state
        System.out.println( "Getting Switch" );
        owc = getSwitch( switches[slot] );
        state = owc.readDevice();
        latch = owc.getLatchState( 0, state );
        }catch( Exception e ) {
            e.printStackTrace();
        }
        
        if( latch == true ) {
            System.out.println( "LATCHED!?" );
        }
        System.out.println( "Motor on!" );
        //toggle the motor on 
        try {
            owc.setLatchState( 0, !latch, false, state );
            owc.writeDevice( state );
        }catch( OneWireIOException e ) {
            e.printStackTrace();
        }catch( OneWireException e ) {}
        
        System.out.println( "Wait!" );
        //do the 2 second wait
        try {
            Thread.sleep( 1500 );
        }catch( Exception e ) {}
        
        System.out.println( "Motor off!" );
        //turn the motor off
        for( int x = 0; x < 3; x++ ) {
            try {
                state = owc.readDevice();
                latch = owc.getLatchState( 0, state );
                owc.setLatchState( 0, false, false, state );
                System.out.println( "Sending Motor Off" );
                owc.writeDevice( state );
            }catch( OneWireIOException e ) {
                e.printStackTrace();
            }catch( OneWireException e ){
                e.printStackTrace();
            }
        }
        //check is empty for status?
        
        //end use of the bus
        adapter.endExclusive();
        return 0; 
    }

    public boolean isEmpty( int slot ) {
        return false; 
    }
    
    private OneWireContainer05 getSwitch( String id ) {
        OneWireContainer owc = this.adapter.getDeviceContainer( id );
        
        if( owc instanceof OneWireContainer05 ) {
            return (OneWireContainer05) owc;
        } else {
            return null;
        }
    }
}
