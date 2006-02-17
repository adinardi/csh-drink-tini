import com.dalsemi.onewire.*;
import com.dalsemi.onewire.adapter.*;
import com.dalsemi.onewire.container.*;

class OneWireLights {
    private DSPortAdapter adapter = null;
    //Stuff here.
    protected String[] lights = new String[6];

    private static OneWireLights instance = null;

    public static OneWireLights getInstance() {
        if( instance == null ) {
            instance = new OneWireLights();
        }
        return instance;
    }
    
    private OneWireLights() {

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
    }

    protected OneWireContainer05 getSwitch( String id ) {
        System.out.println( "Getting Light: " + id );
        OneWireContainer owc = this.adapter.getDeviceContainer( id );

        if( owc instanceof OneWireContainer05 ) {
            return (OneWireContainer05) owc;
        } else {
            return null;
        }
    }

    public void slotStatus( int slot, boolean empty) {
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
