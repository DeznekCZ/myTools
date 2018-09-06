package cz.deznekcz.javafx.configurator;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import cz.deznekcz.util.Utils;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;

public class ConfiguratorRibbonController extends ConfiguratorController {


	@FXML Button ribbonMinimizeButton;

	@FXML Button ribbonMaximizeButton;

	@FXML Button ribbonCloseButton;

	@FXML HBox ribbonToolButtons;

	@FXML Label ribbonTitle;

	@FXML Button ribbonButton;

	private Stage stage;

	private double lastX;

	private double lastY;

	private double stageX;

	private double stageY;

	@FXML ContextMenu ribbonMenu;

	@FXML MenuItem ribbonOpenConfiguration;

	@FXML BorderPane ribbonTitlePane;

	@FXML public void minimize() {
		stage.setIconified(true);
	}

	@FXML public void maximize() {
		stage.setMaximized(!stage.isMaximized());
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);

		Rectangle square = new Rectangle(10, 10);
		square.getStyleClass().add("label");
		square.setFill(Color.TRANSPARENT);
		square.strokeProperty().bind(new ObjectBinding<Paint>() {
			{
				bind(ribbonMaximizeButton.hoverProperty());
			}
			@Override
			protected Paint computeValue() {
				return ribbonMaximizeButton.isHover() ? Color.BLACK : Color.WHITE;
			}
		});
		ribbonMaximizeButton.setGraphic(square);

		Rectangle backgroundC = new Rectangle(10, 10);
		backgroundC.setFill(Color.TRANSPARENT);
		backgroundC.setStroke(Color.TRANSPARENT);
		Line crossL1 = new Line(0, 0, 10, 10);
		crossL1.getStyleClass().add("label");
		crossL1.strokeProperty().bind(new ObjectBinding<Paint>() {
			{
				bind(ribbonCloseButton.hoverProperty());
			}
			@Override
			protected Paint computeValue() {
				return ribbonCloseButton.isHover() ? Color.BLACK : Color.WHITE;
			}
		});
		Line crossL2 = new Line(0, 10, 10, 0);
		crossL2.getStyleClass().add("label");
		crossL2.strokeProperty().bind(new ObjectBinding<Paint>() {
			{
				bind(ribbonCloseButton.hoverProperty());
			}
			@Override
			protected Paint computeValue() {
				return ribbonCloseButton.isHover() ? Color.BLACK : Color.WHITE;
			}
		});
		ribbonCloseButton.setGraphic(new Group(backgroundC, crossL1, crossL2));

		Rectangle backgroundUL = new Rectangle(10, 10);
		backgroundUL.setFill(Color.TRANSPARENT);
		backgroundUL.setStroke(Color.TRANSPARENT);
		Line underline = new Line(0,10,10,10);
		underline.getStyleClass().add("label");
		underline.strokeProperty().bind(new ObjectBinding<Paint>() {
			{
				bind(ribbonMinimizeButton.hoverProperty());
			}
			@Override
			protected Paint computeValue() {
				return ribbonMinimizeButton.isHover() ? Color.BLACK : Color.WHITE;
			}
		});
		ribbonMinimizeButton.setGraphic(new Group(backgroundUL, underline));

		for (Button button : Utils.array(ribbonMinimizeButton, ribbonMaximizeButton, ribbonCloseButton)) {
			button.setMinWidth(24);
			button.setMaxWidth(24);
			button.setPrefWidth(24);
		}
	}

	@Override
	public void handleApplication(Stage stage, ConfiguratorApplication application) {
//		stage.initStyle(StageStyle.UNDECORATED);
		this.stage = stage;
		ribbonTitle.textProperty().bind(stage.titleProperty());

		Image image = stage.getIcons().get(0);
		ImageView imageView = new ImageView(image);
		imageView.setFitHeight(46);
		imageView.setPreserveRatio(true);
		ribbonButton.setGraphic(imageView);
		ribbonButton.setGraphicTextGap(0);

		ribbonTitlePane.setOnMousePressed(this::dragDetect);
		ribbonTitlePane.setOnMouseDragged(this::dragContinues);
	}

	private synchronized void dragDetect(MouseEvent event) {
		lastX = event.getScreenX();
		lastY = event.getScreenY();

		stageX = stage.getX();
		stageY = stage.getY();
	}

	private synchronized void dragContinues(MouseEvent event) {
		double deltaX = event.getScreenX() - lastX;
		double deltaY = event.getScreenY() - lastY;

		stage.setX(stageX + deltaX);
		stage.setY(stageY + deltaY);
	}

	@FXML public void openRibbonMenu(ActionEvent event) {
		ribbonButton.getContextMenu().show(ribbonButton, Side.BOTTOM, 0, 0);
	}

	public ContextMenu getRibbonMenu() {
		return ribbonMenu;
	}

	public ObservableList<Menu> getFixedMenus() {
		return fixedMenus;
	}

	public void addMenuToRibbon(Menu menu) {
		List<MenuItem> ribbonItems = ribbonMenu.getItems();
		Object data = menu.getUserData();
		if ("before:settings".equals(data)) {
			for (int i = 0; i < ribbonItems.size(); i++) {
				MenuItem menuItem = ribbonItems.get(i);
				if (menuItem.getId() != null && menuItem.getId().endsWith("Settings")) {
					List<MenuItem> menuItems = menu.getItems();
					for (int n = 0; n < menuItems.size(); i++, n++) {
						ribbonItems.add(i, menuItems.get(n));
					}
					ribbonItems.add(i++, new SeparatorMenuItem());
				}
			}
		}
	}


}
