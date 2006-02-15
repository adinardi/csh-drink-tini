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
    protected String[] switches = new String[6];
    protected String[] lights = new String[6];
    protected boolean[] empty = new boolean[6];

    private static OneWireCommands instance = null;
    
    public static OneWireCommands getInstance() {
        if( instance == null ) {
            instance = new OneWireCommands();
        }

        return instance;
    }
    
    protected OneWireCommands() {
        //hard coded the lights for right now
        switches[0] = new String();
        switches[1] = new String("830000000E0ACC05");
        switches[2] = new String("C60000000E153305");
        switches[3] = new String("750000000E178205");
        switches[4] = new String("450000000E19AD05");
        switches[5] = new String("2C0000000E1B2D05");

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
        
        System.out.println( "Check Empty Slots" );
        //run through all the switches and check empty state
        for( int q = 1; q <= 5; q++ ) {
            isEmpty( q );
        }
        System.out.println( "Checked Slots" );
        
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
                System.out.println( "Slot is empty!" );
                CommLink.getInstance().getOutgoingLink().sendDropNACK();
                return 0;
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
        isEmpty( slot );
        //end use of the bus
        adapter.endExclusive();
        return 0; 
    }

    public boolean isEmpty( int slot ) {
        System.out.println( "Checking Slot: " + switches[slot] );
        boolean last = empty[slot];
        try { 
            if( this.adapter.isPresent( switches[slot] ) ) {
                empty[slot] = false;
            }else{
                empty[slot] = true;
            }
        }catch( Exception e ) {
            System.out.println( "Unable to get slot: " + slot );
            e.printStackTrace();
        }
        if( empty[slot] != last ) {
            CommLink.getInstance().getOutgoingLink().sendSlotInfo( slot, empty[slot] );
        }

        return empty[slot]; 
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

    protected void setLatch( OneWireContainer05 owc, boolean latch ) {
        try {
            byte[] state = owc.readDevice();
            owc.setLatchState( 0, latch, false, state );
            owc.writeDevice( state );
        }catch( Exception e ) {

        }
    }

    public double readTemp() {
        OneWireContainer28 owc = (OneWireContainer28) this.adapter.getDeviceContainer( "B80000000D93E528" );

        double temp = 0;

        try {
            byte[] state = owc.readDevice();
            owc.doTemperatureConvert( state );
            temp = owc.getTemperature( state ) * (9.0/5.0) + 32.0;
        }catch( Exception e ) {

        }
        return temp;
    }

    public boolean[] getEmptyInfo() {
        return empty;
    }
}
