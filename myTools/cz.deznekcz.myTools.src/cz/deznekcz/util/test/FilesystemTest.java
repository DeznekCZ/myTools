package cz.deznekcz.util.test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.Configurator.result;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;

public class FilesystemTest {

	private Configurator.result result;
	private int closed;
	private File parentDirectory;
	private File lastFile;
	private long lastTime;

	private static int INPUT = 1;
	private static int DIR = 2;
	private static int FILE = 3;
	private static int CONTENT = 4;

	public FilesystemTest() {
		result = Configurator.result.OK;
		lastTime = 0L;
		closed = Integer.MAX_VALUE;
	}

	public static FilesystemTest startTest() {
		return new FilesystemTest();
	}

	public FilesystemTest testInput(result ok, result fail, boolean inputTest) {
		if (closed < INPUT) return this;
		if (inputTest) {
			result = ok;
			closed = Integer.MAX_VALUE;
		} else {
			result = fail;
			closed = INPUT;
		}
		return this;
	}

	public FilesystemTest dirExist(Configurator.result ok, Configurator.result fail, String directoryPath) {
		if (closed < DIR) return this;
		try {
			parentDirectory = new File(directoryPath);
			if (!parentDirectory.isDirectory()) throw new Exception();

			result = ok;
			closed = Integer.MAX_VALUE;
		} catch (Exception e) {
			result = fail;
			closed = DIR;
		}
		return this;
	}

	public FilesystemTest fileExist(Configurator.result ok, Configurator.result fail, String filePath) {
		if (closed < FILE) return this;
		try {
			if (parentDirectory != null) {
				filePath = parentDirectory.getAbsolutePath() + "\\" + filePath;
			}
			File newLastFile = new File(filePath);
			long newLastTime = Files.getLastModifiedTime(newLastFile.toPath()).toMillis();
			if (newLastTime > lastTime) {
				lastTime = newLastTime;
				lastFile = newLastFile;
			}
			if (!lastFile.isFile()) throw new Exception();
			
			result = ok;
			closed = Integer.MAX_VALUE;
		} catch (Exception e) {
			result = fail;
			closed = FILE;
		}
		return this;
	}

	public FilesystemTest fileContains(Configurator.result ok, Configurator.result fail, String regex) {
		if (closed < CONTENT) return this;
		if (lastFile != null) {
			try {
				if (Pattern
					.compile(regex)
					.matcher(
						Files
						.readAllLines(lastFile.toPath())
						.stream()
						.collect(Collectors.joining("\n")))
					.find()
				) {
					result = ok;
					closed = Integer.MAX_VALUE;
				} else {
					result = fail;
					closed = CONTENT;
				}
			} catch (IOException e) {
				result = fail;
				closed = CONTENT;
			}
		} else {
			result = fail;
			closed = CONTENT;
		}
		return this;
	}

	public FilesystemTest fileNotContains(Configurator.result ok, Configurator.result fail, String regex) {
		if (closed < CONTENT) return this;
		if (lastFile != null) {
			try {
				if (!Pattern
					.compile(regex)
					.matcher(
						Files
						.readAllLines(lastFile.toPath())
						.stream()
						.collect(Collectors.joining("\n")))
					.find()
				) {
					result = ok;
					closed = Integer.MAX_VALUE;
				} else {
					result = fail;
					closed = CONTENT;
				}
			} catch (IOException e) {
				result = fail;
				closed = CONTENT;
			}
		} else {
			result = fail;
			closed = CONTENT;
		}
		return this;
	}

	public void apply(Property<Configurator.result> resultProperty, LongProperty timeProperty) {
		resultProperty.setValue(this.result);
		timeProperty.setValue(this.lastTime);
	}

	public Configurator.result getResult() {
		return result;
	}

	public long getTime() {
		return lastTime;
	}
	
}
