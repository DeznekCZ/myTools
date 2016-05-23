package cz.deznekcz.javafx.parametricPane;

import static cz.deznekcz.tool.Lang.LANG;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;
import cz.deznekcz.javafx.parametricPane.parameters.MissingParameter;
import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import cz.deznekcz.reference.Out;
import cz.deznekcz.tool.ILangKey;
import cz.deznekcz.tool.Interruptable;
import cz.deznekcz.util.ForEach;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

@SuppressWarnings("serial")
public class ParametricPane extends BorderPane {

	private static final String PARAMETRIC_FILE = "config/parameter_set.xml";
	private static final Properties PARAMETERS;
	protected static final String COMMENT = "Automatic generated setting file";
	protected static final BooleanProperty STORED;
	private static final String NO_LAST_SETTING = "ParametricPane.Exception.NO_STORED_VALUES";
	private static final String NAME_COLLUMN = "ParametricPane.Table.NAME_COLLUMN";
	private static final String VALUE_COLLUMN = "ParametricPane.Table.VALUE_COLLUMN";
	
	static {
		STORED = new SimpleBooleanProperty(false);
		PARAMETERS = new Properties() {
			@Override
			public synchronized Object setProperty(String key, String value) {
				return updateFile(super.setProperty(key, value));
			}
			private Object updateFile(Object transfered) {
				Interruptable.run(() -> {
					storeToXML(new PrintStream(PARAMETRIC_FILE), COMMENT);
				}, null).start();
				return transfered;
			}
		};
		try {
			PARAMETERS.loadFromXML(new FileInputStream(PARAMETRIC_FILE));
		} catch (IOException e) {
			System.out.println(LANG(ILangKey.simple(NO_LAST_SETTING)));
		}
	}
	private static ParametricPane instance;

	private TableView<AParameter<?>> tableView;
	
	@SuppressWarnings("unchecked")
	public ParametricPane() {
		instance = this;
		tableView = new TableView<>();
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setSortPolicy((b) -> false);

		TableColumn<AParameter<?>, String> nameColumn
				= new TableColumn<>(LANG(ILangKey.simple(NAME_COLLUMN)));
		nameColumn.setCellValueFactory(new AParameter.NameValueCallback());
		nameColumn.setPrefWidth(225);
		nameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
		
		nameColumn.setCellFactory(column -> new AParameter.NameCellCallback());
		
		TableColumn<AParameter<?>, AParameter<?>> valueColumn
				= new TableColumn<>(LANG(ILangKey.simple(VALUE_COLLUMN)));
		valueColumn.setCellValueFactory(new AParameter.ComponentValueCallback());
		valueColumn.setPrefWidth(350);
		
		valueColumn.setCellFactory(column -> new AParameter.ComponentCellCalback());
		
		tableView.getColumns().setAll(nameColumn, valueColumn);
		tableView.getSelectionModel().setCellSelectionEnabled(false);
		
		setCenter(tableView);
		
		ParametricTraverser.registerView(tableView);
		tableView.setFocusTraversable(false);
		
		initTable(tableView);
		tableView.getSelectionModel().select(0);
	}

	private void initTable(TableView<AParameter<?>> table) {
		table.getItems().addAll(ParameterElement.loadFromXML());
		
		for (AParameter<?> parameter : table.getItems()) {
			parameter.valueProperty().addListener((p,l,n) -> {
				PARAMETERS.setProperty(parameter.getId(), n);
			});
		}
		
		loadLastValues();
	}

	public static ParametricPane getInstance() {
		return instance;
	}

	public AParameter<?> parameterByName(String searchedId) {
		Out<AParameter<?>> value = Out.init();
		ForEach.start(tableView.getItems(), (v) -> {
			if (v.getId().compareTo(searchedId) == 0) {
				value.set(v);
				return false;
			} else {
				return true;
			}
		});
		if (value.get() == null) {
			return new MissingParameter(searchedId);
		} else {
			return value.get();
		}
	}

	public StringProperty parameterValueByName(final String searchedId) {
		return parameterByName(searchedId).valueProperty();
	}

	public String getAntParams() {
		final Out<String> stringBuilder = Out.init("");
		
		ForEach.start(tableView.getItems(), (value) -> {
			if (value.isLogic() || !value.isEnabled())
				return true; // CONTINUE
			System.out.println(stringBuilder);
			stringBuilder.set(stringBuilder.get()+" -D"+value.getId()+"="+value.get());
			return true;
		});
		
		return stringBuilder.get().trim();
	}

	public void loadLastValues() {
		List<String> toRemove = new ArrayList<>();
		for (Entry<Object, Object> value : PARAMETERS.entrySet()) {
			String key = (String) value.getKey();
			AParameter<?> parameter = parameterByName(key);
			if (parameter instanceof MissingParameter) {
				toRemove.add(key);
			}
			parameter.fromString((String) value.getValue());
			if (parameter.isDynamic())
				parameter.initDynamic();
		}
		
		for (String string : toRemove) {
			PARAMETERS.remove(string);
		}
		
		for (AParameter<?> parameter : tableView.getItems()) {
			if (parameter.isDynamic())
				parameter.refresh();
		}
	}

	public TableView<AParameter<?>> getTableView() {
		return tableView;
	}
}
