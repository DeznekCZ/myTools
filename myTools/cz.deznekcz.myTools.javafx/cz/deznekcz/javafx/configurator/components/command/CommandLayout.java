package cz.deznekcz.javafx.configurator.components.command;

import java.util.function.Consumer;
import java.util.function.Supplier;

import cz.deznekcz.javafx.configurator.components.Command;
import javafx.beans.property.Property;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public interface CommandLayout {

	default CommandInstance getCommandInstance() {
		return commandInstanceProperty().getValue();
	}

	default void setCommand(CommandInstance commandInstance) {
		commandInstanceProperty().setValue(commandInstance);
	}

	Property<CommandInstance> commandInstanceProperty();

	Consumer<String> getOutputConsumer();

	Consumer<String> getErrorConsumer();

	Supplier<String> getInputSupplier();

	int run(JavaProcess javaProcess, String[] subArray);

	default Button getEnterButton() { return new Button(); }
	default TextField getCommandLine() { return new TextField(); }

}
