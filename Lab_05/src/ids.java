import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

public class ids
{
	public static int matches, currentRule = 0;
	
	public static void main( String[] args ) throws FileNotFoundException
	{
		// File retrieval
		String policyFile, traceFile;
		
		String host, name, type, proto, host_port, attacker_port, attacker;
		List<String> regexes = new ArrayList<String>();

		if ( args.length >= 2 )
		{
			policyFile = args[0];
			traceFile = args[1];
			
			Scanner policy = new Scanner( new File( policyFile ) );
			host = policy.nextLine().substring(5);
			System.out.println(host);
			policy.nextLine();
			name = policy.nextLine().substring(5);
			System.out.println(name);
			type = policy.nextLine().substring(5);
			System.out.println(type);
			if (type.equals("stateless")) {
				proto = policy.nextLine().substring(6);
				System.out.println(proto);
			}
			host_port = policy.nextLine().substring(10);
			System.out.println(host_port);
			attacker_port = policy.nextLine().substring(14);
			System.out.println(attacker_port);
			attacker = policy.nextLine().substring(9);
			System.out.println(attacker);
			while (policy.hasNextLine()) {
				regexes.add(policy.nextLine());
			}
		}
		else
		{
			System.out.println( "Invalid input" );
			return;
		}

		final StringBuilder errorBuffer = new StringBuilder();
		final Pcap pcap = Pcap.openOffline( traceFile, errorBuffer );

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
					if ( packet.hasHeader( Tcp.ID ) && packet.hasHeader( ip4 ) ) {
						
						byte[] payload = packet.getHeader(tcp).getPayload();
						String text = FormatUtils.hexdumpCombined(payload, 0, 0, false, true, false).replaceAll("\n *", "");
						
						String regex = regexes.get(currentRule);
						boolean from = false;
						if (regex.startsWith("from_host")) {
							regex = regex.substring(11, regex.length() - 1);
							from = true;
						}
						if (regex.startsWith("to_host")) {
							regex = regex.substring(9, regex.length() - 1);
						}
						String source = FormatUtils.ip(ip4.source());
						String destination = FormatUtils.ip(ip4.destination());
						if (from && source.equals(host) || !from && destination.equals(host)) {
							if ((from && destination.equals(attacker))
							&& (!from && source.equals(attacker))
							|| attacker.equals("any")) {
								String sourcePort = Integer.toString(tcp.source());
								String destinationPort = Integer.toString(tcp.destination());
								String hostPort = from ? sourcePort : destinationPort;
								String attackerPort = from ? destinationPort : sourcePort;
								if (host_port.equals("any") || host_port.equals(hostPort)) {
									if (attacker_port.equals("any")
									|| attacker_port.equals(attackerPort)) {
										if (text.matches(".*" + regex + ".*")) {
											System.out.println(text);
											currentRule++;
											if (currentRule == regexes.size()) {
												currentRule = 0;
											}
											
										}
									}
								}
							}
						}
					}
				}

			}, errorBuffer );
		}
	}
}
