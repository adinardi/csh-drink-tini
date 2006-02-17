import com.dalsemi.onewire.application.monitor.*;

class SlotMonitorListener implements DeviceMonitorEventListener {
    public void deviceArrival( DeviceMonitorEvent dme ) {
        for( int x = 0; x < dme.getDeviceCount(); x++ ) {
            System.out.println( "Arrival: " + dme.getAddressAsStringAt( x ) );
        }
    }

    public void deviceDeparture( DeviceMonitorEvent dme ) {
        for( int x = 0; x < dme.getDeviceCount(); x++ ) {
            System.out.println( "Deparure: " + dme.getAddressAsStringAt( x ) );
        }

    }

    public void networkException( DeviceMonitorException dme ) {

    }
}
