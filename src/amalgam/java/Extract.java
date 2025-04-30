import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Extract {
	public static void main(String[] args) throws Exception {
		Path myPath = Paths.get(Extract.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		System.out.println("I live at: " + myPath);

		try(ZipFile zip = new ZipFile(myPath.toFile())) {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while(entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();

				if(entry.getName().endsWith(".jar")) {
					String justFilename = entry.getName();
					int slsh = justFilename.lastIndexOf('/');
					if(slsh != -1) justFilename = justFilename.substring(slsh + 1);

					System.out.println("Extracting " + justFilename + "...");
					Path target = myPath.resolveSibling(justFilename);
					if(Files.exists(target)) {
						System.out.println("\\-> Skipping, it already exists");
					} else {
						try(InputStream in = zip.getInputStream(entry)) {
							Files.copy(in, myPath.resolveSibling(justFilename));
						}
					}
				}
			}
		}

		try {
			//Deleterious.abscond();
			Files.delete(myPath);
			System.out.println("Deleted myself!");
		} catch (Throwable e) {
			System.err.println();
			System.err.println("Failed to delete myself - make sure to delete " + myPath.getFileName());
		}
	}
}
