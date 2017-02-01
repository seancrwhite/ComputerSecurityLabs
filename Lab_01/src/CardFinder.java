import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardFinder
{
	/***
	 * Scans contents of file for instances of valid data from track 1 of card
	 * 
	 * @param fileContents
	 *            contents of file in string format
	 * @return array of all unique matches
	 */
	public String[] scanForTrackOneCardInfo( String fileContents )
	{
		Set<String> cardSet = new HashSet<>();

		Pattern trackOnePattern = Pattern.compile( "%B\\d{13,19}\\^[\\w/]{2,26}\\^\\d{7}\\w*\\?" );

		Matcher patternMatcher = trackOnePattern.matcher( fileContents );

		while ( patternMatcher.find() )
		{
			cardSet.add( patternMatcher.group() );
		}

		return cardSet.toArray( new String[cardSet.size()] );
	}

	/***
	 * Scans contents of file for instances of valid data from track 2 of card
	 * 
	 * @param fileContents
	 *            contents of file in string format
	 * @return array of all unique matches
	 */
	public String[] scanForTrackTwoCardInfo( String fileContents )
	{
		Set<String> cardSet = new HashSet<>();

		Pattern trackTwoPattern = Pattern.compile( ";\\d{13,19}=\\d{14}\\w*\\?" );

		Matcher patternMatcher = trackTwoPattern.matcher( fileContents );

		while ( patternMatcher.find() )
		{
			cardSet.add( patternMatcher.group() );
		}

		return cardSet.toArray( new String[cardSet.size()] );
	}

	/***
	 * finds all valid accounts in the two given arrays of card matches
	 */
	public Account[] findValidAccounts( String[] trackOneMatches, String[] trackTwoMatches )
	{
		List<Account> accounts = new ArrayList<>();

		for ( String trackOneMatch : trackOneMatches )
		{
			String trackOneAccountNumber = trackOneMatch.substring( 2, trackOneMatch.indexOf( '^' ) );

			for ( String trackTwoMatch : trackTwoMatches )
			{
				String trackTwoAccountNumber = trackTwoMatch.substring( 1, trackTwoMatch.indexOf( '=' ) );

				if ( trackOneAccountNumber.contentEquals( trackTwoAccountNumber ) )
				{
					accounts.add( _CreateAccount( trackOneMatch, trackTwoMatch ) );
				}
			}
		}

		return accounts.toArray( new Account[accounts.size()] );
	}

	private Account _CreateAccount( String trackOneMatch, String trackTwoMatch )
	{
		String[] trackOneInfo = trackOneMatch.split( "\\^" );
		String[] trackTwoInfo = trackTwoMatch.split( "=" );

		String accountNumber = trackOneInfo[0].substring( 2 );

		String[] nameArray = trackOneInfo[1].split( "/" );
		String fullName = nameArray[0] + " " + nameArray[1];

		String year = trackTwoInfo[1].substring( 0, 2 );
		String month = trackTwoInfo[1].substring( 2, 4 );
		String date = month + "/20" + year;

		String pin = trackTwoInfo[1].substring( 7, 11 );

		String cvv = trackTwoInfo[1].substring( 11, 14 );

		return new Account( fullName, accountNumber, date, pin, cvv );
	}
}
