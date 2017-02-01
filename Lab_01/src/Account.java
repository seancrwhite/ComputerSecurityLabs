
public class Account
{
	private String mName;
	private String mCardNumber;
	private String mExpiration;
	private String mPin;
	private String mCvv;

	public Account( String name, String cardNumber, String expiration, String pin, String cvv )
	{
		mName = name;
		mCardNumber = cardNumber;
		mExpiration = expiration;
		mPin = pin;
		mCvv = cvv;
	}

	public void print()
	{
		System.out.println( "Cardholder's Name: " + mName );
		System.out.println( "Card Number: " + mCardNumber );
		System.out.println( "Expiration Date: " + mExpiration );
		System.out.println( "Encrypted PIN " + mPin );
		System.out.println( "CVV Number: : " + mCvv );
	}
}
