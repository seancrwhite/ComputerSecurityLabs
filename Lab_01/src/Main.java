import java.io.File;
import java.io.IOException;

/**
 * @author Sean White and Tyler Bass
 *
 */
public class Main
{
	public static void main( String[] args )
	{
		for ( String file : args )
		{
			try
			{
				// Reads dump file and turns contents into a string
				String fileContents = DumpFileReader.readFileContents( new File( file ) );

				CardFinder finder = new CardFinder();

				// Get all UNIQUE matches, duplicates are ignored
				String[] trackOneMatches = finder.scanForTrackOneCardInfo( fileContents );
				String[] trackTwoMatches = finder.scanForTrackTwoCardInfo( fileContents );

				Account[] accounts = finder.findValidAccounts( trackOneMatches, trackTwoMatches );

				System.out.println(
						"There is " + accounts.length + " piece of credit card information in the account data!\n" );
				int place = 1;

				for ( Account account : accounts )
				{
					System.out.println( "<Information for card number " + place + ">" );

					account.print();

					place++;
				}
			}
			catch ( IOException e )
			{
				e.printStackTrace();
			}
		}
	}
}
