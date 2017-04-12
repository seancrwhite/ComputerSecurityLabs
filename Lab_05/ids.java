import java.io.File;

public class ids {
    public static void main( String[] args ) {
        //File retreival
        File policyFile, traceFile;

        if ( args.length >= 2 ) {
            policyFile = new File( args[0] );
            traceFile = new File( args[1] );
        } else {
            System.out.println( "Invalid input" );
            return;
        }
    }
}
