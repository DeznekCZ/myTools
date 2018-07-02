package cz.deznekcz.javafx.configurator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.deznekcz.javafx.components.Dialog;
import cz.deznekcz.javafx.configurator.components.CheckValue;
import cz.deznekcz.javafx.configurator.components.Command;
import cz.deznekcz.javafx.configurator.components.FXML_CFG;
import cz.deznekcz.javafx.configurator.components.support.AValue;
import cz.deznekcz.javafx.configurator.components.support.HasArgsProperty;
import cz.deznekcz.javafx.configurator.components.support.HasCmdProperty;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import cz.deznekcz.javafx.configurator.components.support.AListValue;
import cz.deznekcz.javafx.configurator.components.support.ReadOnlyValue;
import cz.deznekcz.javafx.configurator.components.support.Storeable;
import cz.deznekcz.reference.Out;
import cz.deznekcz.util.LiveStorage;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

public abstract class ASetup implements Initializable {

	@FXML
	private MenuBar menus;

	@FXML
	private Menu commandsMenu;

	@FXML
	private BorderPane init;

	@FXML
	private Tab root;

	protected LiveStorage storage;

	private ConfiguratorController ctrl;

	private List<Storeable> values;

	private boolean tittleAsStorageFileName;

	public ASetup() {
		values = new ArrayList<>();
	}

	public final void externalInitialization(ConfiguratorController ctrl, LiveStorage storage) {
		this.ctrl = ctrl;
		this.storage = storage;
		this.root.getTabPane().getTabs().remove(this.root);

		if (menus != null) {
			ctrl.registerExtendedMenus(root, menus.getMenus());
			init.setTop(null);
		}

		if (tittleAsStorageFileName) {
			root.textProperty().unbind();
			root.setText(this.storage.getName());
		}

		lateInitialization();

		for (Storeable value : values) {
			String newValue = storage.getValue(value.getId());
			if (newValue != null) value.setValue(newValue);
			value.valueProperty().addListener((o,l,n) -> storage.setValue(value.getId(), n != null ? n : ""));
		}
		
		System.out.println(getClass().getPackage().getName() + "." + getClass().getSimpleName() + ": Init Complete");
	}

	protected abstract void lateInitialization();

	public final Tab getRoot() {
		return root;
	}

	public final LiveStorage getStorage() {
		return storage;
	}

	public final ConfiguratorController getCtrl() {
		return ctrl;
	}

	protected final void storedValues(Storeable...values) {
		this.values.addAll(Arrays.asList(values));
	}

	protected final void command(Command...commands) {
		if (commands == null || commands.length == 0) return;

		if (commandsMenu != null) {
			for (Command command : commands) {
				Menu menu = new Menu(command.getText());
				command.setCommandsMenu(menu);
				commandsMenu.getItems().add(menu);

				MenuItem exec = new MenuItem();
				exec.setText(Configurator.command.EXECUTE.value());
				exec.setOnAction(command::runCommand);
				exec.disableProperty().bind(command.runnableProperty().not());
				menu.getItems().add(exec);

				menu.getItems().addListener(new ListChangeListener<MenuItem>() {
					@Override
					public void onChanged(javafx.collections.ListChangeListener.Change<? extends MenuItem> c) {
						c.next();
						if (c.wasRemoved()) {
							if (menu.getItems().size() == 2) {
								Platform.runLater(() -> menu.getItems().remove(1));
							}
						}
					}
				});
			}
			commandsMenu.getItems().sort((o1, o2) -> Collator.getInstance().compare(o1.getText(), o2.getText()));
		}

		for (Command command : commands) {
			command.setActive();
		}
	}

	protected final void format(Property<String> result) {
		format(result, result.getValue());
	}

	@SafeVarargs
	protected final void formatEach(Property<String>...results) {
		for (Property<String> result : results) {
			format(result, result.getValue());
		}
	}

	protected final void format(Property<String> result, String format, ReadOnlyValue...values) {
		List<Object> texts = new ArrayList<>(values != null ? values.length * 2 : 10);
		Pattern pattern = Pattern.compile("\\$(\\$|\\{([0-9]+|((_|)[0-9a-zA-Z])[_0-9a-zA-Z]+(:((value)|(dir)|(args)|(cmd)))?(#((_+|)[0-9a-zA-Z])[_0-9a-zA-Z]*(@((_|)[0-9a-zA-Z])[_0-9a-zA-Z]+(:((value)|(dir)|(args)|(cmd)))?)?)?)})");
		Matcher matcher = pattern.matcher(format);

		Pattern idPattern = Pattern.compile("[_0-9a-zA-Z]+");

		int lastEnd = 0;
		while(matcher.find()) {
			int start = matcher.start();
			if (start > lastEnd)  {
				texts.add(format.substring(lastEnd, start));
			}
			lastEnd = matcher.end();
			String match = matcher.group();
			if (match.length() == 2) {
				texts.add("$");
			}
			else if (match.matches("\\$\\{[0-9]+}")) {
				texts.add(values[Integer.parseInt(match.substring(2, match.length() - 1))].valueProperty());
			}
			else {
				Matcher idMatcher = idPattern.matcher(match);
				String id = idMatcher.find() ? idMatcher.group() : "";
				ReadOnlyValue value = getVariable(id);
				
				if (value == null) {
					Dialog.EXCEPTION.show(new Exception("Value ID: \"" + getClass().getSimpleName() + "." + id + "\" not found"));
					continue;
				} else {
					ObservableValue<String> propertyValue = value.valueProperty();
					ObservableValue<String> argumentPropertyValue = null;
					
					Out<ObservableValue<String>> out = Out.init(propertyValue);
					
					while (match.charAt(idMatcher.end()) != '}') {
						if (getIfDefinedProperty(id, match, idMatcher, value, out)) {
							propertyValue = out.get();
						} else if (match.charAt(idMatcher.end()) == '#') {
							String methodName = idMatcher.find() ? idMatcher.group() : "";
							Method method = null;
							String type = "(String)";
							boolean isArgumented = false;
							if ((match.charAt(idMatcher.end()) == '@') && idMatcher.find()) {
								type = "(String,String)";
								String argumentId = idMatcher.group();
								ReadOnlyValue argumentValue = getVariable(argumentId);
								if (argumentValue == null) {
									Dialog.EXCEPTION.show(new Exception("Parameter value ID: \"" + getClass().getSimpleName() + "." + argumentId + "\" for method: \"String " + methodName + "(String,ReadOnlyValue)\" for format ID: \"" + id + "\" not found"));
									continue;
								}
								out.set(argumentValue.valueProperty());
								getIfDefinedProperty(argumentId, match, idMatcher, argumentValue, out);
								argumentPropertyValue = out.get();
								try {
									method = ASetup.this.getClass().getDeclaredMethod(methodName, String.class, String.class);
								} catch (NoSuchMethodException | SecurityException e) {
									try {
										method = ASetup.class.getDeclaredMethod(methodName, String.class, String.class);
									} catch (NoSuchMethodException | SecurityException e1) {
									}
								}
								isArgumented = true;
							} else {
								try {
									method = ASetup.this.getClass().getDeclaredMethod(methodName, String.class);
								} catch (NoSuchMethodException | SecurityException e) {
									try {
										method = ASetup.class.getDeclaredMethod(methodName, String.class);
									} catch (NoSuchMethodException | SecurityException e1) {}
								}
							}
							
							if (method == null) {
								Dialog.EXCEPTION.show(new Exception("Method: \n\"String " + getClass().getSimpleName() + "." + methodName + type + "\"\nnot found"));
							} else if (isArgumented) {
								final Method methodf = method;
								final ObservableValue<String> propertyValueF = propertyValue;
								final ObservableValue<String> argumentPropertyValueF = argumentPropertyValue;
								propertyValue = new StringBinding(){
									private final Method _method = methodf;
									private final ObservableValue<String> _value = propertyValueF;
									private final ObservableValue<String> _argument = argumentPropertyValueF;
									private final ASetup _thisSetup = ASetup.this;
									{
										methodf.setAccessible(true);
										bind(_value, _argument);
									}
									@Override
									protected String computeValue() {
										try {
											return (String) _method.invoke(_thisSetup, _value.getValue(), _argument.getValue());
										} catch (ClassCastException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
											return _value.getValue();
										}
									}
								};
							} else {
								final Method methodf = method;
								final ObservableValue<String> propertyValueF = propertyValue;
								propertyValue = new StringBinding(){
									private final Method _method = methodf;
									private final ObservableValue<String> _value = propertyValueF;
									private final ASetup _thisSetup = ASetup.this;
									{
										methodf.setAccessible(true);
										bind(_value);
									}
									@Override
									protected String computeValue() {
										try {
											return (String) _method.invoke(_thisSetup, _value.getValue());
										} catch (ClassCastException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
											return _value.getValue();
										}
									}
								};
							}
						} else {
							Dialog.EXCEPTION.show(new Exception("Illegal format: \"" + match + "\""));
							break;
						}
					}
					
					if (propertyValue == null) {
						propertyValue = value.valueProperty();
					}
					
					texts.add(propertyValue);
				}
			}
		}
		if (lastEnd <= format.length()) texts.add(format.substring(lastEnd));

		result.bind(Bindings.concat(texts.toArray()));
	}

	private boolean getIfDefinedProperty(String id, String match, Matcher idMatcher, ReadOnlyValue value, Out<ObservableValue<String>> result) {
		result.set(value.valueProperty());
		if (match.charAt(idMatcher.end()) == ':') {
			String property = idMatcher.find() ? idMatcher.group() : "";
			if (property.equals("value")) {
				result.set( value.valueProperty() );
			} else if (property.equals("dir") && value instanceof HasDirProperty) {
				result.set( ((HasDirProperty) value).dirProperty() );
			} else if (property.equals("cmd") && value instanceof HasCmdProperty) {
				result.set( ((HasCmdProperty) value).cmdProperty() );
			} else if (property.equals("args") && value instanceof HasArgsProperty) {
				result.set( ((HasArgsProperty) value).argsProperty() );
			} else {
				Dialog.EXCEPTION.show(new Exception("Propterty: \"" + value.getClass().getSimpleName() + "." + property + "Property()\" for ID: \"" + id + "\" not found"));
				return false;
			}
			return true;
		}
		return false;
	}

	protected final ReadOnlyValue getVariable(String fieldName) {
		try {
			Field field = getClass().getDeclaredField(fieldName);
			boolean accesible = field.isAccessible();
			field.setAccessible(true);
			Object value = field.get(this);
			field.setAccessible(accesible);
			return (ReadOnlyValue) value;
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			Dialog.EXCEPTION.show(new Exception("Illegal access: " + getClass().getSimpleName() + "." + fieldName));
		} catch (NoSuchFieldException e) {
			Dialog.EXCEPTION.show(new Exception("Missing variable: " + getClass().getSimpleName() + "." + fieldName));
		} catch (NullPointerException | ClassCastException e) {
			return null;
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected final void mapChange(AListValue output, Predicate<String> filter, Function<String, String> subValue,
			Pair<ObservableMap, Supplier<Iterable<String>>>...pairs) {
		for (Pair<ObservableMap, Supplier<Iterable<String>>> pair : pairs) {
			pair.getKey().addListener(new MapChangeListener() {
				Pair<ObservableMap, Supplier<Iterable<String>>>[] _pairs = pairs;
				Function<String, String> _subValue = subValue;
				Predicate<String> _filter = filter;
				AListValue _output = output;
				@Override
				public void onChanged(MapChangeListener.Change change) {
					List<String> values = new ArrayList<>();
					for (Pair<ObservableMap, Supplier<Iterable<String>>> pair : _pairs) {
						for (String value : pair.getValue().get()) {
							if (_filter.test(value)) {
								String vvalue = _subValue.apply(value);
								if (!values.contains(vvalue)) values.add(vvalue);
							}
						}
					}
					_output.setItems(FXCollections.observableList(values));
				}
			});
		}

		List<String> values = new ArrayList<>();
		for (Pair<ObservableMap, Supplier<Iterable<String>>> pair : pairs) {
			for (String value : pair.getValue().get()) {
				if (filter.test(value)) {
					String vvalue = subValue.apply(value);
					if (!values.contains(vvalue)) values.add(vvalue);
				}
			}
		}
		output.setItems(FXCollections.observableList(values));
	}

	protected final void choiceGroups(AListValue list, AValue selector, Map<String, AListValue> table) {
		selector.valueProperty().addListener(new ChangeListener<String>() {
			Map<String, AListValue> _table = table;
			AListValue _list = list;
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null && _table.containsKey(newValue))
					_list.setItems(_table.get(newValue).getItems());
				else
					_list.getItems().clear();
			}
		});
		if (selector.getValue() != null && table.containsKey(selector.getValue()))
			list.setItems(table.get(selector.getValue()).getItems());
		else
			list.getItems().clear();
	}

	protected final void setTittleAsStorageFileName(boolean tittleAsStorageFileName) {
		this.tittleAsStorageFileName = tittleAsStorageFileName;
	}

	public final boolean isTittleAsStorageFileName() {
		return tittleAsStorageFileName;
	}
	
	//## Converter functions ##
	
	@FXML_CFG
	private String boolTo(String bool, String value) {
		return Boolean.parseBoolean(bool) ? value : "" ;
	}
	
	@FXML_CFG
	private String toUpperCase(String string) {
		return string == null ? "" : string.toUpperCase() ;
	}
	
	@FXML_CFG
	private String toLowerCase(String string) {
		return string == null ? "" : string.toLowerCase() ;
	}
}
