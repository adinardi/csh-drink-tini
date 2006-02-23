/*
 * OneWireCommands.java
 *
 * 
 *
 */

import java.util.*;
import java.io.*;
import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;
import com.dalsemi.onewire.utils.*;
import com.dalsemi.onewire.application.monitor.*;


/**
 * This is the fun class that will handle 1-wire commands on the tinis 
 * 
 * @author Angelo DiNardi
 */
public class OneWireCommands {
    private DSPortAdapter adapter = null;
    //Stuff here.
    protected String[] switches = new String[6];
    protected String[] lights = new String[6];
    protected boolean[] empty = new boolean[6];

    private DeviceMonitor dm = null;

    private int numSlots = ConfigMgr.getInstance().getNumSlots();
    
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

        /*
         lights[0] = new String();
         lights[1] = new String("AE0000000E19A805");
         lights[2] = new String("A60000000E112C05");
         lights[3] = new String("BB0000000E069D05");
         lights[4] = new String("F80000000E1A7C05");
         lights[5] = new String("660000000E169505");
         */

        try {
            this.adapter = OneWireAccessProvider.getDefaultAdapter();
        }catch( Exception e ) {
            System.out.println( "Oh Snap. Can't get adapter." ); 
        }

        System.out.println( "Check Empty Slots" );
        //run through all the switches and check empty state
        /*
         for( int q = 1; q <= 5; q++ ) {
         isEmpty( q );
         }
         System.out.println( "Checked All Slots" );
         */

        //reset all the slots to empty
        for( int q = 1; q <= numSlots; q++ ) {
            empty[q] = true;
            
            //initialize all the lights to empty to start
            OneWireLights.getInstance().slotStatus( q, empty[q] );
        }

        //load the DeviceMonitor to watch the bus for slots to appear/disapear
        //This will also find everything when it loads -- so the slots are activated
        dm = new DeviceMonitor( adapter );
        dm.addDeviceMonitorEventListener( new SlotMonitorListener() );
        Thread dmrun = new Thread( dm );
        dmrun.start();
    }

    public int drop ( int slot ) {
        byte[] state = {0,0,0};
        boolean latch = false;
        OneWireContainer05 owc = null; 

        dm.pauseMonitor( true );

        try {
            //check if slot is empty
            if( isEmpty( slot ) ) {
                //return empty code
                System.out.println( "Slot is empty!" );
                //CommLink.getInstance().getOutgoingLink().sendDropNACK();
                return 304; //Slot is Empty
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

        //****Replaced by DM?*****
        //check is empty for status?
        //isEmpty( slot );

        //end use of the bus
        adapter.endExclusive();
        dm.resumeMonitor( false );
        return 200; 
    }

    /**
     * Check a slot's empty switch manually and if it has changed, notify the system.
     *
     * @param slot 1-based slot number to check
     * @return True if the slot is empty
     */
    public boolean isEmpty( int slot ) {
        System.out.println( "Checking Slot: " + switches[slot] );
        boolean last = empty[slot]; //save the most recent empty state of the slot
        try { 
            if( this.adapter.isPresent( switches[slot] ) ) { //check if the 1-wire device for the slot is present
                empty[slot] = false;
            }else{
                empty[slot] = true;
            }
        }catch( Exception e ) {
            System.out.println( "Unable to get slot: " + slot );
            e.printStackTrace();
        }
        if( empty[slot] != last ) { //empty status has changed
            setEmpty( switches[slot], empty[slot] );
        }

        return empty[slot]; 
    }

    /**
     * Set the empty state of a 1-wire switch device
     *
     * @param id 64-bit 1-wire device id
     * @param isempty True if slot is empty
     */
    public void setEmpty( String id, boolean isempty ) {
        for( int x = 1; x < switches.length; x++ ) {
            if( switches[x].equals( id ) ) {
                boolean last = empty[x];

                empty[x] = isempty;

                //if( empty[x] != last ) {
                //this should always just be a change
                CommLink.getInstance().getOutgoingLink().sendSlotInfo( x, empty[x] );
                OneWireLights.getInstance().slotStatus( x, empty[x] );
                //}
            }
        }
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

    /**
     * Set the latch state of a DS2405
     * 
     * @deprecated
     */
    protected void setLatch( OneWireContainer05 owc, boolean latch ) {
        try {
            byte[] state = owc.readDevice();
            owc.setLatchState( 0, latch, false, state );
            owc.writeDevice( state );
        }catch( Exception e ) {

        }
    }

    public double readTemp() {
        dm.pauseMonitor( true );
        OneWireContainer28 owc = (OneWireContainer28) this.adapter.getDeviceContainer( "B80000000D93E528" );

        double temp = 0;

        try {
            byte[] state = owc.readDevice();
            owc.doTemperatureConvert( state );
            state = owc.readDevice(); //we have to read it again. Not sure why. Doesn't actually give temperature otherwise.
            temp = owc.getTemperature( state ) * (9.0/5.0) + 32.0;
        }catch( Exception e ) {

        }
        dm.resumeMonitor( false );
        return temp;
    }

    public boolean[] getEmptyInfo() {
        return empty;
    }
}
