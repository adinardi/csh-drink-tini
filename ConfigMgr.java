/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
class ConfigMgr {
    private static ConfigMgr instance = null;
    
    /**
     * The connected state of the client to the server
     */
    private boolean connected = false;

    //1 based int for the number of slots in the system
    private int numSlots = 5;
    
    private ConfigMgr() {

    }

    public static ConfigMgr getInstance() {
        if( instance == null ) {
            instance = new ConfigMgr();
        }
        return instance;
    }

    public boolean getConnected() {
        return connected;
    }

    public void setConnected( boolean s ) {
        connected = s;
    }

    public int getNumSlots() {
        return numSlots;
    }
}
