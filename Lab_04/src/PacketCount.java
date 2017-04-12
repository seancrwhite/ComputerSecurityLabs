/**
 * @author Sean White and Tyler Bass
 * 		
 */

public class PacketCount
{
	private int syn = 0;
	private int synack = 0;
	
	public int getSyn()
	{
		return syn;
	}
	
	public void addSyn()
	{
		this.syn++;
	}
	
	public int getSynack()
	{
		return synack;
	}
	
	public void addSynack()
	{
		this.synack++;
	}
	
	public double getRatio()
	{
		return (double) syn / ( (double) synack + 0.001 );
	}
}
