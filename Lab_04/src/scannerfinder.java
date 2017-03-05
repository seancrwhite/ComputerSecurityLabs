import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JFlowMap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.tcpip.Tcp;

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
		
		if ( pcap == null )
		{
			System.err.println( errorBuffer );
		}
		else
		{
			pcap.loop( Pcap.LOOP_INFINITE, new JPacketHandler<StringBuilder>()
			{
				final Tcp tcp = new Tcp();
				
				@Override
				public void nextPacket( JPacket packet, StringBuilder errorBuffer )
				{
					if ( packet.hasHeader( Tcp.ID ) )
					{
						packet.getHeader( tcp );
						
						int source = tcp.source();
					}
				}
			}, errorBuffer );
		}
		
		return (String[]) resultList.toArray();
	}
}
