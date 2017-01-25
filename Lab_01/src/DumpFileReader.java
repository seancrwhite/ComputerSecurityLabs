import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class DumpFileReader
{
	/***
	 * Transforms the contents of the given file to a string
	 * 
	 * @return String representing the contents of the dump file
	 * @throws IOException
	 */
	public static String readFileContents( File file ) throws IOException
	{
		byte[] fileBytes = Files.readAllBytes( file.toPath() );

		return new String( fileBytes );
	}
}
