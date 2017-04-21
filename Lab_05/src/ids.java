import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

import javafx.util.Pair;

public class ids
{
	public static int matches, currentRule = 0;
	public static List<Pair<Integer, StringBuilder>> packets = new ArrayList<Pair<Integer, StringBuilder>>();
	
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
			policy.nextLine();
			name = policy.nextLine().substring(5);
			type = policy.nextLine().substring(5);
			if (type.equals("stateless")) {
				proto = policy.nextLine().substring(6);
			} else {
				proto = "tcp";
			}
			host_port = policy.nextLine().substring(10);
			attacker_port = policy.nextLine().substring(14);
			attacker = policy.nextLine().substring(9);
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
				final Udp udp = new Udp();

				@Override
				public void nextPacket( JPacket packet, StringBuilder errorBuffer )
				{
					boolean useudp = proto.equals("udp");
					
					if ( (packet.hasHeader( Tcp.ID ) || useudp) && (packet.hasHeader( Udp.ID ) || !useudp) && packet.hasHeader( ip4 ) ) {
						byte[] payload;
						if (useudp) {
							payload = packet.getHeader(udp).getPayload();
						} else {
							payload = packet.getHeader(tcp).getPayload();
						}
						String text = new String(payload);
						
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
								String sourcePort;
								String destinationPort;
								if (useudp) {
									sourcePort = Integer.toString(udp.source());
									destinationPort = Integer.toString(udp.destination());
								} else {
									sourcePort = Integer.toString(tcp.source());
									destinationPort = Integer.toString(tcp.destination());
								}
								String hostPort = from ? sourcePort : destinationPort;
								String attackerPort = from ? destinationPort : sourcePort;
								if (host_port.equals("any") || host_port.equals(hostPort)) {
									if (attacker_port.equals("any")
									|| attacker_port.equals(attackerPort)) {
										if (type.equals("stateful")) {
											boolean foundip = false;
											for (Pair item : packets) {
												if ((int) item.getKey() == ip4.sourceToInt()) {
													((StringBuilder) item.getValue()).append(text);
													text = ((StringBuilder) item.getValue()).toString();
													foundip = true;
												}
											}
											if (!foundip) {
												StringBuilder sb = new StringBuilder(text);
												Pair<Integer, StringBuilder> item = new Pair<Integer, StringBuilder>(ip4.sourceToInt(), sb);
												packets.add(item);
												text = ((StringBuilder) item.getValue()).toString();
											}
										}

										Pattern p = Pattern.compile(regex);
										Matcher m = p.matcher(text);
										if (m.find()) {
											currentRule++;
											matches++;
											
											if (type.equals("stateful")) {
												for (Pair item : packets) {
													if ((int) item.getKey() == ip4.sourceToInt()) {
														((StringBuilder) item.getValue()).setLength(0);
													}
												}
											}
											
											if (matches == regexes.size()) {
												System.out.println("Intrusion Detected!");
											}
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
