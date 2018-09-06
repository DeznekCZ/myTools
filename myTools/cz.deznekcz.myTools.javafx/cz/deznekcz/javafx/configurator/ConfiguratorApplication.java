package cz.deznekcz.javafx.configurator;

import cz.deznekcz.tool.i18n.ILangKey;

public abstract class ConfiguratorApplication {

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

	public String[] getArgs() {
		return args;
	}




}
