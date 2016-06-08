package cz.deznekcz.javafx.parametricPane;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.stream.Stream;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;
import cz.deznekcz.javafx.parametricPane.parameters.MissingParameter;
import cz.deznekcz.javafx.parametricPane.parsing.ParameterElement;
import cz.deznekcz.reference.Out;
import cz.deznekcz.reference.Out.OutString;
import cz.deznekcz.tool.ILangKey;
import cz.deznekcz.tool.Interruptable;
import cz.deznekcz.util.ForEach;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

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
			System.out.println(ILangKey.simple(NO_LAST_SETTING).value());
		}
	}
	private static ParametricPane instance;

	private TableView<AParameter<?>> tableView;
	private ReadOnlyObjectWrapper<TableView<AParameter<?>>> hiddenCenter;
	
	@SuppressWarnings("unchecked")
	public ParametricPane() {
		instance = this;
		tableView = new TableView<>();
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		tableView.setSortPolicy((b) -> false);

		TableColumn<AParameter<?>, String> nameColumn
				= new TableColumn<>(ILangKey.simple(NAME_COLLUMN).value());
		nameColumn.setCellValueFactory(new AParameter.NameValueCallback());
		nameColumn.setPrefWidth(225);
		nameColumn.setStyle( "-fx-alignment: CENTER-LEFT;");
		
		nameColumn.setCellFactory(column -> new AParameter.NameCellCallback());
		
		TableColumn<AParameter<?>, AParameter<?>> valueColumn
				= new TableColumn<>(ILangKey.simple(VALUE_COLLUMN).value());
		valueColumn.setCellValueFactory(new AParameter.ComponentValueCallback());
		valueColumn.setPrefWidth(350);
		
		valueColumn.setCellFactory(column -> new AParameter.ComponentCellCalback());
		
		tableView.getColumns().setAll(nameColumn, valueColumn);
		tableView.getSelectionModel().setCellSelectionEnabled(false);
		
		setCenter(tableView);
		
		hiddenCenter = new ReadOnlyObjectWrapper<TableView<AParameter<?>>>(tableView);
		centerProperty().bind(hiddenCenter);
		
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
		initDynamics();
	}

	private void initDynamics() {
		for (AParameter<?> parameter : tableView.getItems()) {
			if (parameter.isDynamic())
				parameter.initDynamic();
		}
		for (AParameter<?> parameter : tableView.getItems()) {
			if (parameter.isDynamic())
				parameter.refresh();
		}
	}

	public static ParametricPane getInstance() {
		return instance;
	}

	public AParameter<?> parameterByName(String searchedId) {
		Out<AParameter<?>> value = Out.init();
		ForEach.start(tableView.getItems(), (v) -> {
			if (v.getId().compareTo(searchedId) == 0) {
				value.set(v);
				return false; // BREAK
			} else {
				return true; // CONTINUE
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
		OutString stringBuilder = OutString.empty();
		
		getStreamOfParams().map((value) -> {
			return value.getValue().contains(" ")
					? String.format("\"-D%s=%s\"", value.getKey(), value.getValue())
					: String.format(  "-D%s=%s"  , value.getKey(), value.getValue());
		}).forEach((value) -> {
			stringBuilder.append(value).append(" ");
		});
		
		return stringBuilder.length() > 0 ? stringBuilder.subSequence(0, stringBuilder.length()-2) : stringBuilder.get();
	}

	public List<Pair<String,String>> getListOfParams() {
		Stream<Pair<String,String>> stream = getStreamOfParams();
		List<Pair<String,String>> list = new ArrayList<>((int)stream.count());
		stream.forEach((item) -> list.add(item));
		return list;
	}
	
	public Stream<Pair<String, String>> getStreamOfParams() {
		return tableView.getItems().stream().map((param) -> new Pair<String,String>(param.getId(), param.get()));
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
		}
		
		for (String string : toRemove) {
			PARAMETERS.remove(string);
		}
	}

	public TableView<AParameter<?>> getTableView() {
		return tableView;
	}
}
