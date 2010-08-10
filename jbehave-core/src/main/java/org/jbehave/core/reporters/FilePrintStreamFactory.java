package org.jbehave.core.reporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jbehave.core.io.StoryLocation;

/**
 * Creates {@link PrintStream} instances that write to a file
 * identified by the {@link StoryLocation}.
 * {@link FileConfiguration} specifies file directory and the extension,
 * providing useful defaults values.
 */
public class FilePrintStreamFactory implements PrintStreamFactory {

	private final StoryLocation storyLocation;
	private FileConfiguration configuration;
	private File outputFile;

	public FilePrintStreamFactory(StoryLocation storyLocation) {
		this(storyLocation, new FileConfiguration());
	}

	public FilePrintStreamFactory(StoryLocation storyLocation,
			FileConfiguration configuration) {
		this.storyLocation = storyLocation;
		this.configuration = configuration;
	}

	public PrintStream createPrintStream() {
		try {
	        this.outputFile = outputFile();
			outputFile.getParentFile().mkdirs();
			return new FilePrintStream(outputFile, false);
		} catch (Exception e) {
			throw new PrintStreamCreationFailed(outputFile, e);
		}
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void useConfiguration(FileConfiguration configuration) {
		this.configuration = configuration;
		this.outputFile = outputFile();
	}
	
	public FileConfiguration configuration(){
	    return configuration;
	}

	protected File outputFile() {
		return new File(outputDirectory(), outputName());
	}

	protected File outputDirectory() {
		File codeLocationParent = new File(storyLocation.getCodeLocation().getFile()).getParentFile();
		return new File(codeLocationParent.getPath().replace('\\','/'), configuration.getOutputDirectory());
	}

	protected String outputName() {
		String storyName = storyLocation.getName().replace('/', '.');
		if ( storyName.startsWith(".") ){
			storyName = storyName.substring(1);
		}
		String name = stripPackage(storyName);
		return name + "." + configuration.getExtension();
	}

	private String stripPackage(String name) {
		if ( name.lastIndexOf(".") != -1){
			return name.substring(0, name.lastIndexOf("."));
		}
		return name;
	}
	
	public static class FilePrintStream extends PrintStream {

		private final File outputFile;
		private final boolean append;

		public FilePrintStream(File outputFile, boolean append) throws FileNotFoundException {
			super(new FileOutputStream(outputFile, append));
			this.outputFile = outputFile;
			this.append = append;
		}

		@Override
		public String toString() {
			return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
					.append(outputFile).append(append).toString();
		}
		
	}

	/**
	 * Configuration class for file print streams. Allows specification the
	 * output directory (either absolute or relative to the code location) and
	 * the file extension. Provides as defaults {@link #OUTPUT_DIRECTORY}
	 * (relative to class code location) and {@link #HTML}.
	 */
	public static class FileConfiguration {
		public static final String OUTPUT_DIRECTORY = "jbehave-reports";
		public static final String HTML = "html";

		private final String outputDirectory;
		private final String extension;

		public FileConfiguration() {
			this(HTML);
		}

		public FileConfiguration(String extension) {
			this(OUTPUT_DIRECTORY, extension);
		}

		public FileConfiguration(String outputDirectory,
				String extension) {
			this.outputDirectory = outputDirectory;
			this.extension = extension;
		}

		public String getOutputDirectory() {
			return outputDirectory;
		}

		public String getExtension() {
			return extension;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
		}

	}

	@SuppressWarnings("serial")
	public class PrintStreamCreationFailed extends RuntimeException {
		public PrintStreamCreationFailed(File file, Exception cause) {
			super("Failed to create print stream for file " + file, cause);
		}
	}
}
