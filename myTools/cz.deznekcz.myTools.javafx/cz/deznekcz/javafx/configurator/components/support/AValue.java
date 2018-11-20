package cz.deznekcz.javafx.configurator.components.support;

import java.awt.Dialog;
import java.lang.reflect.InvocationTargetException;

import cz.deznekcz.javafx.binding.OnSetAction;
import cz.deznekcz.javafx.components.Dialogs;
import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.Configurator;
import cz.deznekcz.javafx.configurator.ConfiguratorController;
import javafx.application.Platform;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.Toggle;
import javafx.scene.control.Tooltip;

public abstract class AValue extends Control implements Refreshable {

    public static ObservableValue<? extends Tooltip> tooltipBind(ObservableValue<? extends Tooltip> property) {
        return new ObjectBinding<Tooltip>() {
            ObservableValue<? extends Tooltip> tooltipProperty = property;
            {
                bind(tooltipProperty);
            }
            @Override
            protected Tooltip computeValue() {
                Tooltip newTooltip = new Tooltip();
                if (tooltipProperty.getValue() != null) {
                    newTooltip.textProperty().bind(tooltipProperty.getValue().textProperty());
                }
                return newTooltip;
            }
        };
    }

    public static ObservableValue<? extends Tooltip> tooltipBind(StringProperty property, ObservableValue<Boolean> extended, String concat) {
        return new ObjectBinding<Tooltip>() {
            ObservableValue<? extends String> stringProperty = property;
            ObservableValue<Boolean> extendedProperty = extended;
            String concatString = concat;
            {
                bind(stringProperty, extendedProperty);
            }
            @Override
            protected Tooltip computeValue() {
                if (stringProperty.getValue() != null) {
                    return new Tooltip(stringProperty.getValue() + " " + concatString);
                } else {
                    return new Tooltip(concatString);
                }
            }
        };
    }

    public static ObservableValue<? extends Tooltip> tooltipBind(StringProperty property) {
        return new ObjectBinding<Tooltip>() {
            ObservableValue<? extends String> stringProperty = property;
            {
                bind(stringProperty);
            }
            @Override
            protected Tooltip computeValue() {
                if (stringProperty.getValue() != null) {
                    return new Tooltip(stringProperty.getValue());
                } else {
                    return null;
                }
            }
        };
    }

    public AValue() {
        global = AccessType.NOT_GLOBAL;
    }

    private Property<ASetup> configuration = new SimpleObjectProperty<>();

    public Property<ASetup> configurationProperty() {
        return configuration;
    }

    public ASetup getConfiguration() {
        if (configuration.getValue() != null) return configuration.getValue();

        for (Tab tab : Configurator.getCtrl().getConfigs()) {
            Node tabContent = tab.getContent();
            Node parent = getParent();
            while (parent != null && !parent.equals(tabContent))
                parent = parent.getParent();
            if (parent != null) {
                configuration.setValue((ASetup) tab.getProperties().get(ASetup.class));
            }
        }

        return configuration.getValue();
    }

    public abstract void refresh();

    public abstract Property<String> valueProperty();

    public final String getValue() {
        return valueProperty().getValue();
    }

    public final String getValueSafe() {
        return valueProperty().getValue() != null ? valueProperty().getValue() : "";
    }

    public final void setValue(String value) {
        valueProperty().setValue(value);
    }

    private AccessType global;

    public final boolean isGlobal() {
        return global != AccessType.NOT_GLOBAL;
    }

    public final AccessType getGlobal() {
        return global;
    }

    public final void setGlobal(AccessType newState) {
        ConfiguratorController controller = Configurator.getCtrl();
        if (getId() == null) {
            OnSetAction.invalidated(idProperty(), this::setGlobal, newState);
            return;
        }

        if (newState == null || newState == AccessType.NOT_GLOBAL) {
            if (this.global == AccessType.SOURCE) {
                controller.getGlobal(getId()).unbindBidirectional(valueProperty());
            } else if (this.global == AccessType.LINK) {
                controller.getGlobal(getId()).unbindBidirectional(valueProperty());
            } else if (this.global == AccessType.BOUND) {
                valueProperty().unbind();
            } else {
                // was not bound by global access
            }
        } else {
            if (newState == AccessType.SOURCE) {
                StringProperty globalValue = controller.getGlobal(getId());
                globalValue.setValue(getValueSafe());
                controller.getGlobal(getId()).bindBidirectional(valueProperty());
            } else if (newState == AccessType.LINK) {
                StringProperty globalValue = controller.getGlobal(getId());
                setValue(globalValue.getValue());
                globalValue.bindBidirectional(valueProperty());
            } else if (newState == AccessType.BOUND) {
                valueProperty().bind(controller.getGlobal(getId()));
            } else {
                // was not bound by global access
            }
        }
        this.global = newState == null ? AccessType.NOT_GLOBAL : newState;
    }

    public Constant ends(String decorator) {
        AValue superThis = this;
        return new Constant() {
            AValue _superThis = superThis;
            StringBinding binding = new StringBinding() {
                String _decorator = decorator;
                {
                    bind(_superThis.valueProperty());
                }
                @Override
                protected String computeValue() {
                    String value = _superThis.valueProperty().getValue();
                    if (!value.endsWith(_decorator)) {
                        return value + _decorator;
                    } else {
                        return value;
                    }
                }
            };

            @Override
            public void refresh() {
                binding.invalidate();
            }

            @Override
            public String toString() {
                return ReadOnlyValue.VALUE_PREFIX + getValue();
            }

            @Override
            public ASetup getConfiguration() {
                return _superThis.getConfiguration();
            }
        };
    }

    public Constant appendIfMatches(AValue matchedValue, String regex, AValue ifMatches, AValue ifElse) {
        return append(ReadOnlyValue.ifMatches(matchedValue, regex, ifMatches, ifElse));
    }

    public Constant appendIfNotMatches(AValue matchedValue, String regex, AValue ifMatches, AValue ifElse) {
        return append(ReadOnlyValue.ifNotMatches(matchedValue, regex, ifMatches, ifElse));
    }

    public Constant appendIfTrue(BooleanValue checkValue, AValue ifMatches, AValue ifElse) {
        return append(ReadOnlyValue.ifTrue(checkValue, ifMatches, ifElse));
    }

    public Constant appendGetNumberOnly(AValue transformed) {
        return append(ReadOnlyValue.getNumberOnly(transformed));
    }

    public AValue append(String s) {
        return s != null ? append(new Constant(s)) : this;
    }

    public Constant append(AValue appender) {
        AValue superThis = this;
        return new Constant() {
            AValue _superThis = superThis;
            StringBinding binding = new StringBinding() {
                AValue _appender = appender;
                {
                    bind(_superThis.valueProperty(), _appender.valueProperty());
                }
                @Override
                protected String computeValue() {
                    return _superThis.getValue() + _appender.getValue();
                }
            };

            @Override
            public void refresh() {
                binding.invalidate();
            }

            @Override
            public String toString() {
                return ReadOnlyValue.VALUE_PREFIX + getValue();
            }

            @Override
            public ASetup getConfiguration() {
                return _superThis.getConfiguration();
            }
        };
    }

    public Constant upperCase() {
        ObservableValue<String> thisValueProperty = this.valueProperty();
        Constant instance = new Constant() {
            @Override
            public String toString() {
                return ReadOnlyValue.VALUE_PREFIX + getValue();
            }
        };
        instance.valueProperty().bind(new StringBinding() {
            ObservableValue<String> _thisValueProperty = thisValueProperty;
            {
                bind(_thisValueProperty);
            }
            @Override
            protected String computeValue() {
                return _thisValueProperty.getValue().toUpperCase();
            }
        });
        return instance;
    }

    public Constant lowerCase() {
        ObservableValue<String> thisValueProperty = this.valueProperty();
        Constant instance = new Constant() {
            @Override
            public String toString() {
                return ReadOnlyValue.VALUE_PREFIX + getValue();
            }
        };
        instance.valueProperty().bind(new StringBinding() {
            ObservableValue<String> _thisValueProperty = thisValueProperty;
            {
                bind(_thisValueProperty);
            }
            @Override
            protected String computeValue() {
                return _thisValueProperty.getValue().toLowerCase();
            }
        });
        return instance;
    }

    public ValueLink asLink() {
        return new ValueLink(this);
    }

    @SuppressWarnings("rawtypes")
	public ObservableValue getProperty(String property) {
		try {
			return (ObservableValue) getClass().getMethod(property+"Property").invoke(this);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			Dialogs.EXCEPTION.show(e);
			return null;
		}

    }

	public static AValue toggleReference(Toggle value) {
		return new AValue() {

			private Property<String> property;

			{ // INITIALIZE
				property = new SimpleStringProperty(Boolean.toString(value.isSelected()));
				property.addListener((o,l,n) -> value.setSelected(Boolean.parseBoolean(n)));
				value.selectedProperty().addListener((o,l,n) -> property.setValue(Boolean.toString(n)));
			}

			@Override
			public Property<String> valueProperty() {
				return property;
			}

			@Override
			public void refresh() {

			}

			@Override
			public EventTarget getEventTarget() {
				return null;
			}
		};
	}

	public abstract EventTarget getEventTarget();

	@Override
	public void requestFocus() {
		if (getEventTarget() != null) {
			((Node) getEventTarget()).requestFocus();
		}
	}
}
