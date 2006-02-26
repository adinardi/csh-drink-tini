class CollectGarbage implements Runnable {
    public void run() {
        while( true ) {
            System.out.println( "Cleaning Garbage..." );
            System.gc();
            try {
                Thread.sleep( 60000 );
            }catch( Exception e ) {
                e.printStackTrace();
                System.out.println( "Thread Interrupted" );
            }
        }
    }
}
