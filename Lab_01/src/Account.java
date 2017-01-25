
public class Account
{
	private String mName;
	private String mCardNumber;
	private String mExpiration;
	private int mPin;
	private int mCvv;

	public Account( String name, String cardNumber, String expiration, int pin, int cvv )
	{
		mName = name;
		mCardNumber = cardNumber;
		mExpiration = expiration;
		mPin = pin;
		mCvv = cvv;
	}

	public String getName()
	{
		return mName;
	}

	public String getCardNumber()
	{
		return mCardNumber;
	}

	public String getExpiration()
	{
		return mExpiration;
	}

	public int getPin()
	{
		return mPin;
	}

	public int getCvv()
	{
		return mCvv;
	}
}
