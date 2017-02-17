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

	public boolean diskFull()
	{
		FileStore fileStore;
		boolean diskFull = false;

		try
		{
			fileStore = Files.getFileStore( root );

			long usableSpace = fileStore.getUsableSpace();
			long totalSpace = fileStore.getTotalSpace();

			if ( usableSpace / totalSpace >= 0.9 )
			{
				diskFull = true;
			}
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return diskFull;
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
