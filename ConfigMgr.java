import java.io.*;

/**
 * @author Angelo DiNardi (adinardi@csh.rit.edu)
 */
class ConfigMgr {
    private static ConfigMgr instance = null;

    /**
     * The connected state of the client to the server
     */
    private boolean connected = false;

    /**
     * 1 based int for the number of slots in the system
     */
    private int numSlots = 5;

    /**
     * Contains the 1-Wire IDs of the Motor Switches
     */
    private String[] switches = new String[numSlots + 1]; //add one to make it a 1-based array
    
    /**
     * Contains the 1-Wire IDs of the Light Switches
     */
    private String[] lights = null;//new String[numSlots + 1]; //add one to make it a 1-based array

    /**
     * Contains the 1-Wire IDs of the Temp Monitors
     */
    private String[] temps = null; //new String[1];

    /**
     * Contains the timings for motor on time for drops
     * Default is 1500 ms (1.5 seconds)
     */
    private int dropTime = 1500;

    /**
     * Contains the password for the machine to authenticate with the server
     */
    private String password = "";


    private ConfigMgr() {
        readConfig();
    }

    public synchronized static ConfigMgr getInstance() {
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

    public String[] getSwitches() {
        return switches;
    }

    public String[] getLights() {
        return lights;
    }

    /**
     *  Returns true if the machine has lights we should operate
     */
    public boolean runLights() {
        if( lights == null ) {
            return false;
        }else{
            return true;
        }
    }

    public String[] getTemps() {
        return temps;
    }
    
    /**
     *  Return true if tempterature sensors exist in the system
     */
    public boolean runTemps() {
        if (temps == null) {
            return false;
        } else {
            return true;
        }
    }

    public int getDropTime() {
        return dropTime;
    }

    public String getPassword() {
        return password;
    }

    private void readConfig() {
        try {
        BufferedReader in = new BufferedReader( new FileReader("config") );

        String temp = null;
        int num = 0;
        String id = null;

        while( (temp = in.readLine()) != null ) {
            if( temp.charAt(0) == 's' ) { //switch id
                System.out.println( "Got Switch Row" );
                if( temp.charAt(1) == 't' ) { //total #
                    this.numSlots = Integer.parseInt(temp.substring(2));
                    this.switches = new String[this.numSlots + 1];
                    System.out.println( "Got Switch Total: " + this.switches.length );
                    continue;
                }

                num = Integer.parseInt(temp.substring(1,3));
                id = temp.substring(4);
                System.out.println( "" + num + " : " + id );
                this.switches[num] = id;
                
            }else if( temp.charAt(0) == 'l' ) { //light id
                System.out.println( "Got Light Row" );
                if( temp.charAt(1) == 't' ) { //total #
                    if( Integer.parseInt(temp.substring(2)) != 0 ) { 
                        this.lights = new String[Integer.parseInt( temp.substring(2) ) + 1 ];
                        System.out.println( "Got Light Total: " + this.lights.length );
                    }else{
                        System.out.println( "Lights Disabled" );
                    }
                    continue;
                }

                num = Integer.parseInt(temp.substring(1,3));
                id = temp.substring(4);
                System.out.println( "" + num + " : " + id );
                this.lights[num] = id;

            }else if( temp.charAt(0) == 't' ) { //temp id
                System.out.println( "Got Temp Row" );
                if( temp.charAt(1) == 't' ) { //total #
                  if (Integer.parseInt(temp.substring(2)) != 0) {
                    this.temps = new String[Integer.parseInt( temp.substring(2) ) ];
                    System.out.println( "Got Temps Total: " + this.temps.length );
                  } else {
                    System.out.println("Temp Disabled");
                  } 
                  continue;
                }

                num = Integer.parseInt(temp.substring(1,3));
                id = temp.substring(4);
                System.out.println( "" + num + " : " + id );
                this.temps[num] = id;

            }else if( temp.charAt(0) == 'd' ) { //drink timing
                System.out.println( "Got Drink Timing Row" );
                
                dropTime = Integer.parseInt(temp.substring(1));

            }else if( temp.charAt(0) == 'p' ) { //password row
                System.out.println( "Got Drink Server Password" );

                password = temp.substring(1);
            }

        }
        }catch( Exception e ) {
            //There was a prolem loading the config. Lets print a message.
            System.out.println("Error loading config.");
        }

    }
}
