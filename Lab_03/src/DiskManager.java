import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DiskManager
{
	// region Constructors

	public DiskManager()
	{
		root = Paths.get( System.getProperty( "user.dir" ) );

		while ( root.getParent() != null )
		{
			root = root.getParent();
		}
	}

	// end region Constructors

	// region Methods

	public double diskFull()
	{
		FileStore fileStore;

		try
		{
			fileStore = Files.getFileStore( root );

			long usableSpace = fileStore.getUsableSpace();
			long totalSpace = fileStore.getTotalSpace();

			return 1 - ((double) usableSpace / (double) totalSpace);
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}

		return 1;
	}

	public Path getRoot()
	{
		return root;
	}

	// end region Methods

	// region Properties

	private Path root;

	// end region Properties
}
