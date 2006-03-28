import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

class OneWireLights {
    
    /**
     * 1-Wire bus adapter
     */
    private DSPortAdapter adapter = null;
    
    /**
     * 1-Wire IDs of the switches
     */
    protected String[] lights = ConfigMgr.getInstance().getLights();

    private static OneWireLights instance = null;

    public synchronized static OneWireLights getInstance() {
        if( instance == null ) {
            instance = new OneWireLights();
        }
        return instance;
    }
    
    private OneWireLights() {

        try {
            this.adapter = OneWireAccessProvider.getDefaultAdapter();
        }catch( Exception e ) {
            System.out.println( "Oh Snap. Can't get adapter." ); 
        }
    }

    /**
     * Get the OneWireContainer associated with the given 1-Wire id
     *
     * @param id 1-Wire Device ID to fetch
     *
     * @return OneWireContainer corresponding to the 1-wire id
     */
    protected OneWireContainer05 getSwitch( String id ) {
        System.out.println( "Getting Light: " + id );
        OneWireContainer owc = this.adapter.getDeviceContainer( id );

        if( owc instanceof OneWireContainer05 ) {
            return (OneWireContainer05) owc;
        } else {
            return null;
        }
    }

    /**
     * Set the status light of a slot
     *
     * @param slot Slot number to change light for
     * @param empty Empty status to set light to (true = on)
     */
    public void slotStatus( int slot, boolean empty) {
        //if we don't have any lights loaded in, don't do anything.
        if( lights == null ) {
            return;
        }
        
        OneWireContainer05 owc = getSwitch( lights[slot] );

        try {
            byte[] state = owc.readDevice();
            owc.setLatchState( 0, empty, false, state );
            owc.writeDevice( state );
        } catch ( Exception e ) {
            System.out.println( "Error Setting Slot Status Light" );
            e.printStackTrace();
        }
    }


}
