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
public class OneWireCommands {
    private DSPortAdapter adapter = null;
    //Stuff here.
    public DSPortAdapter() {
        this.adapter = OneWireAccessProvider.getDefaultAdapter();
    }

    public int drop ( int slot ) {
        //check if slot is empty
        if( isEmpty( slot ) ) {
            //return empty code
        } 
        //grab exclusive
       
        //fire switch
        //do the 2 second wait
        //unfire switch
        
        //close adapter
        //check is empty for status?
        return 0; 
    }

    public boolean isEmpty( int slot ) {
        
    }
    
    private OneWireContainer05 getSwitch( long id ) {
        return this.adapter.getDeviceContainer( id );
    }
}
