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
        switches[0] = new String("AE0000000E19A805");
        switches[1] = new String("A60000000E112C05");
        switches[2] = new String("BB0000000E069D05");
        switches[3] = new String("F80000000E1A7C05");
        switches[4] = new String("660000000E169505");
        try {
            this.adapter = OneWireAccessProvider.getDefaultAdapter();
        }catch( Exception e ) {
            System.out.println( "Oh Snap. Can't get adapter." ); 
        }
    }
    
    public int drop ( int slot ) {
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
        OneWireContainer05 owc = getSwitch( switches[slot] );
        byte[] state = owc.readDevice();
        boolean latch = owc.getLatchState( 0, state );
        
        if( latch == true ) {
            System.out.println( "LATCHED!?" );
            throw new Exception();
        }
        System.out.println( "Motor on!" );
        //toggle the motor on 
        owc.setLatchState( 0, !latch, false, state );
        owc.writeDevice( state );
        
        System.out.println( "Wait!" );
        //do the 2 second wait
        Thread.sleep( 1000 );
        
        System.out.println( "Motor off!" );
        //turn the motor off
        state = owc.readDevice();
        latch = owc.getLatchState( 0, state );
        owc.setLatchState( 0, !latch, false, state );
        owc.writeDevice( state );
        
        //check is empty for status?
        }catch( Exception e ) {
            System.out.println( "Oh Snap, again." );
            e.printStackTrace();
        }finally{
            //end use of the bus
            adapter.endExclusive();
        }
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
