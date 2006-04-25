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
    /**
     * 1-Wire bus adapter
     */
    private DSPortAdapter adapter = null;
    
    /**
     * 1-Wire IDs of the switches
     */
    protected String[] switches = ConfigMgr.getInstance().getSwitches();
    
    /**
     * Empty states for each switch/slot
     *
     * empty[0] = false indicated not initially scanned, empty[0] = true -> slots have
     * been scanned.
     */
    protected boolean[] empty = new boolean[ConfigMgr.getInstance().getNumSlots() + 1];

    /**
     * DeviceMonitor to monitor appearing/disapearing 1-wire devices
     */
    private DeviceMonitor dm = null;

    /**
     * Number of Slots available in the machine
     */
    private int numSlots = ConfigMgr.getInstance().getNumSlots();
    
    private static OneWireCommands instance = null;

    public synchronized static OneWireCommands getInstance() {
        if( instance == null ) {
            instance = new OneWireCommands();
        }

        return instance;
    }

    protected OneWireCommands() {
        try {
            this.adapter = OneWireAccessProvider.getDefaultAdapter();
        }catch( Exception e ) {
            System.out.println( "Oh Snap. Can't get adapter." ); 
        }

        System.out.println( "Check Empty Slots" );
        
        //reset all the slots to empty
        for( int q = 1; q <= numSlots; q++ ) {
            empty[q] = true;
            
            //initialize all the lights to empty to start
            OneWireLights.getInstance().slotStatus( q, empty[q] );

            isEmpty( q ); //check the slot for emptyness
        }

        empty[0] = true; //let us know that it's been scanned initially.

        CommLink.getInstance().getOutgoingLink().sendSlotInfo( empty ); //lets send the server our slot info
        
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
        int dropTime = ConfigMgr.getInstance().getDropTime();

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
            System.out.println( "Error In Drop Code: Switch Reading" );
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
            System.out.println( "Error Setting Drop Latch On");
        }catch( OneWireException e ) {
            e.printStackTrace();
            System.out.println( "Error Setting Drop Latch On");
        }

        System.out.println( "Wait!" );
        //do the 2 second wait
        try {
            Thread.sleep( dropTime );
        }catch( Exception e ) {
            e.printStackTrace();
            System.out.println( "Error Sleeping" );
        }

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
                System.out.println( "Error Setting Drop Latch Off" );
            }catch( OneWireException e ){
                e.printStackTrace();
                System.out.println( "Error Setting Drop Latch Off" );
            }
        }

        try {
            Thread.sleep( 2000 );
        }catch ( Exception e ) {}
        
        //check is empty for status?
        isEmpty( slot );

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
            e.printStackTrace();
            System.out.println( "Error Setting Latch State" );
        }
    }

    public double readTemp() {
        dm.pauseMonitor( true );
        OneWireContainer28 owc = (OneWireContainer28) this.adapter.getDeviceContainer( ConfigMgr.getInstance().getTemps()[0] );

        double temp = 0;

        try {
            byte[] state = owc.readDevice();
            owc.doTemperatureConvert( state );
            state = owc.readDevice(); //we have to read it again. Doesn't actually give temperature otherwise.
            temp = owc.getTemperature( state ) * (9.0/5.0) + 32.0;
        }catch( Exception e ) {
            e.printStackTrace();
            System.out.println( "Error Reading Temperatures" );
        }
        dm.resumeMonitor( false );
        return temp;
    }

    public void setTempRes() {
        dm.pauseMonitor( true );

        OneWireContainer28 owc = (OneWireContainer28) this.adapter.getDeviceContainer( ConfigMgr.getInstance().getTemps()[0] );

        try {
            byte[] state = owc.readDevice();
            owc.setTemperatureResolution( owc.RESOLUTION_12_BIT, state );
            owc.writeDevice( state );
        }catch( Exception e ) {
            e.printStackTrace();
            System.out.println( "Error Setting Temp Resolution" );
        }
        
        dm.resumeMonitor( false );
    }

    public boolean[] getEmptyInfo() {
        return empty;
    }
}
