package cz.deznekcz.javafx.configurator;

import java.util.ArrayList;
import java.util.List;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.tool.i18n.ILangKey;

public abstract class ConfiguratorApplication {

	private static List<Exception> errors = new ArrayList<>();

	private String[] args;
	public ConfiguratorApplication(String[] args) {
		this.args = args;
	}

	public abstract String getLang();
	public abstract ILangKey getTitle();
	public abstract String getProject();
	public abstract String[] getIconPaths();
	public abstract ConfigEntry[] getDefaultConfigs();
	public abstract boolean getDefaultUnnecessary();
	public abstract Class<? extends ConfiguratorApplication> thisClass();
	public abstract boolean hasExtendedMenus();
	public abstract void onStartUp();
	public abstract boolean hasRibbonHeader();
	public abstract void editRibbon(ConfiguratorRibbonController controller);

	public static void initialize() {}

	public String[] getArgs() {
		return args;
	}

	public static void catchException(Exception e) {
		errors.add(e);
	}

	public static void showErrors() {
		errors.forEach(Dialogs.EXCEPTION::show);
	}

}
