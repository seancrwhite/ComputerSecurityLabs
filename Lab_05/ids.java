import java.io.File;

import org.jnetpcap.Pcap;
import org.jnetpcap.packet.JFlowMap;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.tcpip.Tcp;

public class ids {
    public static void main( String[] args ) {
        //File retreival
        String policyFile, traceFile;

        if ( args.length >= 2 ) {
            policyFile = args[0];
            traceFile = args[1];
        } else {
            System.out.println( "Invalid input" );
            return;
        }

        final StringBuilder errorBuffer = new StringBuilder();
        final Pcap pcap = Pcap.openOffline( traceFile, errorBuffer );

        if (pcap == null) {
            System.err.println(errorBuffer);
        } else {
            pcap.loop(Pcap.LOOP_INFINITE, new JPacketHandler<StringBuilder>() {
                final Tcp tcp = new Tcp();

                @Override
                public void nextPacket( JPacket packet, StringBuilder errorBuffer ) {

                }
            }, errorBuffer);
        }
    }
}
