/**
 * @author Sean White and Tyler Bass
 *		
 */

public class Main
{
	public static void main( String[] args )
	{
		if ( args.length != 1 )
		{
			System.out.println( "Incorrect input format" );
		}
		else
		{
			final String path = args[0];
			
			final scannerfinder finder = new scannerfinder( path );
			
			String[] results = finder.scan();
			
			for ( String result : results )
			{
				System.out.println( result );
			}
		}
	}
}
