package cz.deznekcz.javafx.configurator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.sun.org.apache.bcel.internal.generic.CPInstruction;

import cz.deznekcz.util.EqualAble;
import cz.deznekcz.util.LiveStorage;

public class ConfigEntry implements EqualAble {

	private File file;
	private File templateFile;

	private LiveStorage storage;

	private String controllerName;
	private String copyPath;

	private boolean isDefault;
	private boolean asDefaultInstance;

	private ConfigEntry(String fileName, boolean isTemplate, boolean isDefault) {
		try {
			if (isTemplate) {
				this.templateFile = new File(fileName);
				this.controllerName = LiveStorage.getId(this.templateFile);
				this.copyPath = String.format("%s\\%s\\%s.run.xml",
						System.getenv("APPDATA"), Configurator.getApplication().getProject(), this.controllerName);
				this.file = new File(this.copyPath);
				createCopyStorage();
			} else {
				this.file = new File(fileName);
				this.storage = LiveStorage.open(file);
				this.controllerName = this.storage.getId();
				this.isDefault = isDefault;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ConfigEntry templated(String fileName, boolean isDefault) {
		return new ConfigEntry(fileName, true, isDefault);
	}

	public static ConfigEntry templated(String fileName) {
		return templated(fileName, false);
	}

	public static ConfigEntry loaded(String fileName, boolean isDefault) {
		return new ConfigEntry(fileName, fileName.endsWith("template.run.xml"), isDefault);
	}

	public static ConfigEntry loaded(String fileName) {
		return loaded(fileName, false);
	}

	public void setDefault(boolean b) {
		this.isDefault = b;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public LiveStorage getStorage() {
		return storage;
	}

	public File getFile() {
		if (file.getName().equals("template.run.xml")) {
			try {
				createCopyStorage();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	public String getCopyPath() {
		return copyPath;
	}

	public LiveStorage createCopyStorage() throws IOException {
		if (file.exists()) {
			this.storage = LiveStorage.open(file);
		} else {
			if (Files.createDirectories(file.toPath().getParent()).toFile().exists()) {
				this.storage = LiveStorage.create(controllerName, file);
			} else {
				throw new IOException("Directories was not created!");
			}
		}
		return this.storage;
	}

	public boolean isTemplate() {
		return copyPath != null;
	}

	public File getTemplateFile() {
		return templateFile;
	}

	@Override
	public boolean equalsTo(Object obj) {
		return obj instanceof ConfigEntry && equalsTo((ConfigEntry) obj);
	}

	public boolean equalsTo(ConfigEntry obj) {
		return this.getFile().getAbsolutePath().equals(obj.getFile().getAbsolutePath());
	}

	public void setAsDefaultInstance() {
		this.asDefaultInstance = true;
	}

	public boolean isDefaultInstance() {
		return isDefault || asDefaultInstance;
	}

	@Override
	public String toString() {
		return "Config"+(isTemplate()?"Template:"+templateFile.getPath():"File")+":"+file.getPath();
	}
}
