package cz.deznekcz.javafx.configurator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.javafx.configurator.components.support.AListValue;
import cz.deznekcz.javafx.configurator.components.support.AValue;
import cz.deznekcz.javafx.configurator.components.support.HasArgsProperty;
import cz.deznekcz.javafx.configurator.components.support.HasCmdProperty;
import cz.deznekcz.javafx.configurator.components.support.HasDirProperty;
import cz.deznekcz.javafx.configurator.components.support.ReadOnlyValue;
import cz.deznekcz.javafx.configurator.components.support.Storeable;
import cz.deznekcz.reference.Out;
import cz.deznekcz.util.LiveStorage;
import cz.deznekcz.util.LiveStorage.EntryValue;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.util.Pair;

public abstract class ATemplate implements Initializable  {

    protected <T extends ATemplate> T loadTemplate(ATemplate rootTemplate, String templatePackage) throws IOException {
    	parent = rootTemplate;
    	setup = rootTemplate.setup;

		URL fxmlFile = new File("config\\" + setup.getStorage().getId() + "\\" + templatePackage + "\\Layout.fxml").toURI().toURL();

		FXMLLoader loader = new FXMLLoader(fxmlFile, setup.getResourceBundle());
		loader.load();

		T template = loader.<T>getController();
		template.setup = setup;
		template.parent = this;
		template.id = Bindings.concat(templatePackage + ".", template.root.textProperty(), ".");
		template.lateInitialization();
		id.addListener(o -> template.readStored());
		return template;
    }

    public ATemplate() {
        values = new ArrayList<>();
		if (this instanceof ASetup)
			setup = (ASetup) this;

		parent = this;
	}

    @FXML
    protected Tab root;
	protected ATemplate parent;
	protected ASetup setup;
	protected StringExpression id;

    protected List<Storeable> values;

    protected abstract void lateInitialization();

    protected void readStored() {
    	for (Storeable value : values) {
        	if (value != null) {
	            String newValue =
	            		this != setup
	            		? setup.storage.getValue(getId() + value.getId())
	            		: setup.storage.getValue(value.getId());
	            if (newValue != null && newValue.length() > 0) value.setValue(newValue);
	            value.valueProperty().addListener(new EntryValue<String>(setup.storage, value.getId())); // vymazat pùvodní !!!!
        	}
        }
	}

    private String getId() {
    	return this != setup ? (id.get() + ".") : "";
	}

	protected final void storedValues(Storeable...values) {
        this.values.addAll(Arrays.asList(values));
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

    public final void format(Property<String> result, String format, ReadOnlyValue...values) {
    	if (format != null) {
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
	                    Dialogs.EXCEPTION.show(new Exception("Value ID: \"" + getClass().getName() + "." + id + "\" not found"));
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
	                                    Dialogs.EXCEPTION.show(new Exception("Parameter value ID: \"" + getClass().getName() + "." + argumentId + "\" for method: \"String " + methodName + "(String,ReadOnlyValue)\" for format ID: \"" + id + "\" not found"));
	                                } else {
	                                    out.set(argumentValue.valueProperty());
	                                    getIfDefinedProperty(argumentId, match, idMatcher, argumentValue, out);
	                                    argumentPropertyValue = out.get();
	                                    method = getMethod(methodName, new Class<?>[]{String.class, String.class});
	                                    isArgumented = true;
	                                }
	                            } else {
	                            	method = getMethod(methodName, new Class<?>[]{String.class});
	                            }

	                            if (method == null) {
	                                Dialogs.EXCEPTION.show(new Exception("Method: \n\"String " + getClass().getName() + "." + methodName + type + "\"\nnot found"));
	                            } else if (isArgumented) {
	                                final Method methodf = method;
	                                methodf.setAccessible(true);
	                                final ObservableValue<String> propertyValueF = propertyValue;
	                                final ObservableValue<String> argumentPropertyValueF = argumentPropertyValue;
	                                propertyValue = new StringBinding(){
	                                    private final Method bMethod = methodf;
	                                    private final ObservableValue<String> pValue = propertyValueF;
	                                    private final ObservableValue<String> pArgument = argumentPropertyValueF;
	                                    private final ATemplate pThisSetup = ATemplate.this;
	                                    {
	                                        bind(pValue, pArgument);
	                                    }
	                                    @Override
	                                    protected String computeValue() {
	                                        try {
	                                            return (String) bMethod.invoke(pThisSetup, pValue.getValue(), pArgument.getValue());
	                                        } catch (ClassCastException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	                                            return pValue.getValue();
	                                        }
	                                    }
	                                };
	                            } else {
	                                final Method methodf = method;
	                                methodf.setAccessible(true);
	                                final ObservableValue<String> propertyValueF = propertyValue;
	                                propertyValue = new StringBinding(){
	                                    private final Method bMethod = methodf;
	                                    private final ObservableValue<String> pValue = propertyValueF;
	                                    private final ATemplate pThisSetup = ATemplate.this;
	                                    {
	                                        bind(pValue);
	                                    }
	                                    @Override
	                                    protected String computeValue() {
	                                        try {
	                                            return (String) bMethod.invoke(pThisSetup, pValue.getValue());
	                                        } catch (ClassCastException | NullPointerException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	                                            return pValue.getValue();
	                                        }
	                                    }
	                                };
	                            }
	                        } else {
	                            Dialogs.EXCEPTION.show(new Exception("Illegal format: \"" + match + "\""));
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
    }

    protected Method getMethod(String methodName, Class<?>[] arguments) {
    	try {
            return ATemplate.this.getClass().getDeclaredMethod(methodName, arguments);
        } catch (NoSuchMethodException | SecurityException e) {
        	if (this != setup) {
        		return parent.getMethod(methodName, arguments);
        	} else {
        		try {
                	return ATemplate.class.getDeclaredMethod(methodName, arguments);
                } catch (NoSuchMethodException | SecurityException e1) {
                	return null;
                }
        	}
        }
	}

	public boolean getIfDefinedProperty(String id, String match, Matcher idMatcher, ReadOnlyValue value, Out<ObservableValue<String>> result) {
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
                Dialogs.EXCEPTION.show(new Exception("Propterty: \"" + value.getClass().getName() + "." + property + "Property()\" for ID: \"" + id + "\" not found"));
                return false;
            }
            return true;
        }
        return false;
    }

    public final ReadOnlyValue getVariable(String fieldName) {
        try {
            Field field = getClass().getDeclaredField(fieldName);
            boolean accesible = field.isAccessible();
            field.setAccessible(true);
            Object value = field.get(this);
            field.setAccessible(accesible);
            return (ReadOnlyValue) value;
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
            Dialogs.EXCEPTION.show(new Exception("Illegal access: " + getClass().getName() + "." + fieldName, e));
        } catch (NoSuchFieldException e) {
        	if (this != setup) {
        		return parent.getVariable(fieldName);
        	} else {
        		Dialogs.EXCEPTION.show(new Exception("Missing variable: " + getClass().getName() + "." + fieldName, e));
        	}
        } catch (NullPointerException | ClassCastException e) {
        	Dialogs.EXCEPTION.show(new Exception("Field is not initialized: " + getClass().getName() + "." + fieldName, e));
        }
        return null;
    }

    @SafeVarargs
	protected final void mapChange(AListValue output, Predicate<String> filter,
            Pair<ObservableMap<String, StringProperty>, Supplier<Iterable<String>>>...pairs) {
    	mapChange(output, filter, null, pairs);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected final void mapChange(AListValue output, Predicate<String> filter, Function<String, String> subValue,
            Pair<ObservableMap<String, StringProperty>, Supplier<Iterable<String>>>...pairs) {
        for (Pair<ObservableMap<String, StringProperty>, Supplier<Iterable<String>>> pair : pairs) {
            pair.getKey().addListener(new MapChangeListener() {
                Pair<ObservableMap<String, StringProperty>, Supplier<Iterable<String>>>[] _pairs = pairs;
                Function<String, String> _subValue = subValue;
                Predicate<String> _filter = filter;
                AListValue _output = output;
                @Override
                public void onChanged(MapChangeListener.Change change) {
                    List<String> values = new ArrayList<>();
                    for (Pair<ObservableMap<String, StringProperty>, Supplier<Iterable<String>>> pair : _pairs) {
                        for (String value : pair.getValue().get()) {
                            if (_filter.test(value)) {
                                String vvalue = _subValue != null ? _subValue.apply(value) : value;
                                if (!values.contains(vvalue)) values.add(vvalue);
                            }
                        }
                    }
                    _output.getItems().setAll(values);
                }
            });
        }

        List<String> values = new ArrayList<>();
        for (Pair<ObservableMap<String, StringProperty>, Supplier<Iterable<String>>> pair : pairs) {
            for (String value : pair.getValue().get()) {
                if (filter.test(value)) {
                	String vvalue = subValue != null ? subValue.apply(value) : value;
                    if (!values.contains(vvalue)) values.add(vvalue);
                }
            }
        }
        output.getItems().setAll(values);
    }

    protected final void choiceGroups(AListValue list, AValue selector, Map<String, AListValue> table) {
        selector.valueProperty().addListener(new ChangeListener<String>() {
            Map<String, AListValue> _table = table;
            AListValue _list = list;
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null && _table.containsKey(newValue))
                    _list.getItems().setAll(_table.get(newValue).getItems());
                else
                    _list.getItems().clear();
            }
        });
        if (selector.getValue() != null && table.containsKey(selector.getValue()))
            list.getItems().setAll(table.get(selector.getValue()).getItems());
        else
            list.getItems().clear();
    }

    //## Converter functions ##

    public String boolTo(String bool, String value) {
        return Boolean.parseBoolean(bool) ? value : "" ;
    }

    public String ifTrue(String bool, String value) {
        return Boolean.parseBoolean(bool) ? value : "" ;
    }

    public String ifFalse(String bool, String value) {
        return !Boolean.parseBoolean(bool) ? value : "" ;
    }

    public String notNull(String compared, String value) {
        return compared != null && compared.length() > 0 ? value : "" ;
    }

    public String toUpperCase(String string) {
        return string == null ? "" : string.toUpperCase() ;
    }

    public String toLowerCase(String string) {
        return string == null ? "" : string.toLowerCase() ;
    }

    public String quoted(String string) {
        return string == null ? "\"\"" : ("\"" + string + "\"");
    }

    public String getString(String string) {
        return setup.getResourceBundle().getString(string);
    }

    public final Tab getRoot() {
        return root;
    }
}
