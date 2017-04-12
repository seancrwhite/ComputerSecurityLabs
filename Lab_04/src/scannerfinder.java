import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JFlowMap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

/**
 * @author Sean White and Tyler Bass
 * 		
 */

public class scannerfinder
{
	private String mFilePath;
	
	public scannerfinder( String path )
	{
		mFilePath = path;
	}
	
	public String[] scan()
	{
		final StringBuilder errorBuffer = new StringBuilder();
		final Pcap pcap = Pcap.openOffline( mFilePath, errorBuffer );
		final JFlowMap flowMap = new JFlowMap();
		final List<String> resultList = new ArrayList<String>();
		final HashMap packetCounts = new HashMap();
		
		if ( pcap == null )
		{
			System.err.println( errorBuffer );
		}
		else
		{
			pcap.loop( Pcap.LOOP_INFINITE, new JPacketHandler<StringBuilder>()
			{
				final Ip4 ip4 = new Ip4();
				final Tcp tcp = new Tcp();
				
				@Override
				public void nextPacket( JPacket packet, StringBuilder errorBuffer )
				{
					if ( packet.hasHeader( Tcp.ID ) && packet.hasHeader( ip4 ) )
					{
						packet.getHeader( tcp );
						packet.getHeader( ip4 );
						byte[] sourceip = new byte[4];
						sourceip = ip4.source();
						List ip = Arrays.asList( sourceip[0], sourceip[1], sourceip[2], sourceip[3] );
						if ( !packetCounts.containsKey( ip ) )
						{
							packetCounts.put( ip, new PacketCount() );
						}
						if ( tcp.flags_SYN() && tcp.flags_ACK() )
						{
							( (PacketCount) packetCounts.get( ip ) ).addSynack();
						}
						else if ( tcp.flags_SYN() && !tcp.flags_ACK() )
						{
							( (PacketCount) packetCounts.get( ip ) ).addSyn();
						}
					}
				}
			}, errorBuffer );
		}
		
		Iterator i = packetCounts.entrySet().iterator();
		while( i.hasNext() )
		{
			Map.Entry current = (Map.Entry) i.next();
			Object[] ipobj = ( (List) current.getKey() ).toArray();
			byte[] ip = new byte[4];
			for ( int j = 0; j < 4; j++ )
			{
				ip[j] = (Byte) ipobj[j];
			}
			double ratio = ( (PacketCount) current.getValue() ).getRatio();
			if ( ratio > 3 )
			{
				// System.out.println( FormatUtils.ip( ip ) + ", " + ratio );
				resultList.add( FormatUtils.ip( ip ) );
			}
		}
		
		return resultList.toArray( new String[resultList.size()] );
	}
}
