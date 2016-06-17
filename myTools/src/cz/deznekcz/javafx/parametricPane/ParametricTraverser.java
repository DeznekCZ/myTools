package cz.deznekcz.javafx.parametricPane;

import cz.deznekcz.javafx.parametricPane.parameters.AParameter;
import cz.deznekcz.reference.OutInteger;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ParametricTraverser {
	
	public static final EventHandler<KeyEvent> FILTER = event -> {
		if (event.getCode() == KeyCode.TAB) {
			if (event.getEventType() == KeyEvent.KEY_PRESSED) {
				if (event.isShiftDown())
					ParametricTraverser.traversePrev();
				else
					ParametricTraverser.traverseNext();
			}
			event.consume();
		}
	};
	private static TableView<AParameter<?>> tableView;
	private static Node after;
	private static Node before;
	private static boolean nowTraversed;

	public static void traversePrev() {
		viewRegistered();
		
		TableViewSelectionModel<AParameter<?>> model = tableView.getSelectionModel();
		int index = model.getSelectedIndex();
		do {
			index--;
			if (index < 0) {
				beforeRegistered();
				before.requestFocus();
				return;
			}
		} while (!tableView.getItems().get(index).isFocusTraversable());

		nowTraversed = true;
		setFocus(index);
	}

	public static void traverseNext() {
		viewRegistered();
		
		TableViewSelectionModel<AParameter<?>> model = tableView.getSelectionModel();
		int index = model.getSelectedIndex();
		do {
			index++;
			if (index >= tableView.getItems().size()) {
				afterRegistered();
				after.requestFocus();
				return;
			}
		} while (!tableView.getItems().get(index).isFocusTraversable());

		nowTraversed = true;
		setFocus(index);
	}
	
	public static void traverseOn(String parameterId) {
		viewRegistered();
		
		OutInteger row = OutInteger.create();
		tableView.getItems().forEach((value) -> {
			if (parameterId.equals(value.getId())) {
				System.out.format(
						"ParametricTraverser: %s(%d) %s\n",
						nowTraversed ? "traversed" : "selected",
						row.get(),
						value.getId());
				
				nowTraversed = false;
				setFocus(row.get());
			}
			row.increment();
		});
	}
	
	private static void setFocus(int index) {
		tableView.getItems().get(index).setFocusComponent();
		int selected = index;
		
		index--;
		while(index > -1 && !tableView.getItems().get(index).isFocusTraversable())
			index--;
		if (index > -1)
			tableView.scrollTo(index);
		
		tableView.getSelectionModel().select(selected);
	}

	public static void registerView(TableView<AParameter<?>> tableView) {
		ParametricTraverser.tableView = tableView;
	}
	
	public static void registerAfter(Node node) {
		ParametricTraverser.after = node;
		node.focusedProperty().addListener((e,l,n) -> {
			if (n) {
				viewRegistered();
				tableView.scrollTo(tableView.getItems().size());
			}
		});
	}
	
	public static void registerBefore(Node node) {
		ParametricTraverser.before = node;
		node.focusedProperty().addListener((e,l,n) -> {
			if (n) {
				viewRegistered();
				tableView.scrollTo(0);
			}
		});
	}

	public static void registerElement(Node node) {
		node.addEventFilter(KeyEvent.ANY, FILTER);
		node.focusedProperty().addListener((e,l,n) -> {
			if (n) {
				traverseOn(node.getId());
			}
		});
	}
	
	private static void viewRegistered() {
		if (tableView == null)
			throw new RuntimeException("ParametricTraverser: unregistered table view");
	}
	
	private static void afterRegistered() {
		if (after == null)
			throw new RuntimeException("ParametricTraverser: unregistered after node");
	}
	
	private static void beforeRegistered() {
		if (before == null)
			throw new RuntimeException("ParametricTraverser: unregistered before node");
	}
}
