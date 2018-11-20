package cz.deznekcz.javafx.components;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import com.sun.org.apache.bcel.internal.generic.ISUB;

import cz.deznekcz.javafx.configurator.components.CheckValue;
import cz.deznekcz.javafx.configurator.components.PasswordEntry;
import cz.deznekcz.javafx.configurator.components.TextEntry;
import cz.deznekcz.reference.OutException;
import cz.deznekcz.tool.i18n.Arguments;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.util.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Pair;

public abstract class Dialogs {

	static final ILangKey ILK_LOADING = ILangKey.simple("Dialog.Loading.TITLE", "LOADING");
	static final ILangKey ILK_EXCEPTION = ILangKey.simple("Dialog.Exception.").initDefault("Exception");
	static final ILangKey ILK_EXCEPTION_TITLE = ILK_EXCEPTION.extended("Title")
										.initDefault("Error cached");
	static final ILangKey ILK_EXCEPTION_LOAD_TITLE = ILK_EXCEPTION.extended("LoadTitle")
										.initDefault("Error cached while loading");
	static final @Arguments(types = {String.class}, hints = {"exception name"})
	 			 ILangKey ILK_EXCEPTION_NAME  = ILK_EXCEPTION.extended("Name")
	 									.initDefault("Exception: %s");
	static final @Arguments(types = {String.class, String.class}, hints = {"loading state", "exception name"})
	 			 ILangKey ILK_EXCEPTION_LOAD_NAME  = ILK_EXCEPTION.extended("LoadName")
	 									.initDefault("Loading: %s\nException: %s");
	static final ILangKey ILK_LOGIN_CANCEL = ILangKey.simple("Dialog.Login.CANCEL", "Cancel");
	static final ILangKey ILK_LOGIN_LOGIN = ILangKey.simple("Dialog.Login.LOGIN", "Login");
	static final ILangKey ILK_LOGIN_UN = ILangKey.simple("Dialog.Login.USERNAME", "Username");
	static final ILangKey ILK_LOGIN_PW = ILangKey.simple("Dialog.Login.USERNAME", "Password");
	static final ILangKey ILK_LOGIN_STORE = ILangKey.simple("Dialog.Login.STORE", "Save password");

	public static final ExceptionDialog EXCEPTION = new ExceptionDialog();
	public static final class ExceptionDialog extends Dialogs {
		private TextArea textArea;
		private Alert alert;
		private Object lock = new Object();

		private ExceptionDialog() {
			alert = new Alert(AlertType.ERROR);
			alert.setTitle(ILK_EXCEPTION_TITLE.value());
			alert.setHeaderText(null);
			textArea = new TextArea();
			textArea.setMaxWidth(200);
		}

		public Alert getDialogInstance() {
			return alert;
		}

		public void show(Throwable e) {
			show(e, null);
		}

		public void show(Throwable e, String contentText) {
			System.err.println("============== Log error");
			System.err.println("= "+Date.from(Instant.now()).toString());
			System.err.println("==============");

			if (Dialogs.LOADING.alert.getHeaderText() != null) {
				alert.setTitle(ILK_EXCEPTION_LOAD_TITLE.value());
				alert.setContentText(
						ILK_EXCEPTION_LOAD_NAME
						.value(
							Dialogs.LOADING.alert.getHeaderText() + (Dialogs.LOADING.alert.getContentText() != null ? ( '\n' + Dialogs.LOADING.alert.getContentText() ) : ""),
							e.getLocalizedMessage()
						)
				);
				Dialogs.LOADING.close();
			} else {
				alert.setTitle(ILK_EXCEPTION_TITLE.value());
				alert.setContentText(
						ILK_EXCEPTION_NAME
						.value(e.getLocalizedMessage()));
			}

			if (contentText != null) {
				alert.setContentText(contentText + '\n' + alert.getContentText());
			}

			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));

			textArea.setText(writer.toString());
			alert.getDialogPane().setExpandableContent(textArea);

			e.printStackTrace(System.err);
			handle();
		}
	}

	public static final AskDialog ASK = new AskDialog();
	public static final class AskDialog extends Dialogs {
		private Alert alert;
		private Label content;

		private AskDialog() {
			alert = new Alert(AlertType.CONFIRMATION);
		}

		public ButtonType show(String headerText, ButtonType defaultButton, ButtonType...buttonTypes) {
			alert.getButtonTypes().setAll(buttonTypes);

			Button yesButton = (Button) alert.getDialogPane().lookupButton( defaultButton );
		    yesButton.setDefaultButton( true );

		    alert.setHeaderText(null);
		    alert.setContentText(headerText);

		    return handle();
		}

		public Alert getDialogInstance() {
			return alert;
		}
	}

	public static final LoadingDialog LOADING = new LoadingDialog();
	public static final class LoadingDialog extends Dialogs {

		private Alert alert;
		private ProgressIndicator progressIndicator;
		private TextArea expandable = new TextArea();
		private boolean isHidden;
		private Label content;

		private LoadingDialog() {
			alert = new Alert(AlertType.INFORMATION);
			isHidden = true;
			alert.setTitle(ILK_LOADING.value());

			alert.initModality(Modality.APPLICATION_MODAL);

			progressIndicator = new ProgressIndicator();
			alert.setGraphic(progressIndicator);

			alert.setOnShown((event) -> isHidden = false);
			alert.setOnHidden((event) -> isHidden = true);

			content = new Label("");
			content.setWrapText(true);
			content.setPadding(new Insets(10));
			alert.getDialogPane().setContent(content);

			alert.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(progressIndicator.progressProperty().isNotEqualTo(1));
		}

		public void close() {
			if (Platform.isFxApplicationThread()) {
				alert.close();
			} else {
				Platform.runLater(alert::close);
			}
		}

		public void start(String headerText, String contentText, String expandedText, double progress) {
			if (Platform.isFxApplicationThread()) {
				alert.setResult(null);

				if (headerText != null) {
					alert.setHeaderText(headerText);
				}

				if (expandedText != null && expandedText.length() > 0) {
					expandable.setText(expandedText);
					alert.getDialogPane().setExpandableContent(expandable);
				} else {
					expandable.setText("");
					alert.getDialogPane().setExpandableContent(null);
					content.setText(contentText);
				}

				progressIndicator.setProgress(progress < 0.0 ? -1 : Math.min(1, progress));

				alert.show();

			} else {
				Platform.runLater(() -> start(headerText, contentText, expandedText, progress));
			}

		}

		public void start(String headerText, String contentText, double progress) {
			start(headerText, contentText, null, progress);
		}

		public void start(String headerText, double progress) {
			start(headerText, null, null, progress);
		}

		public void start(String headerText) {
			start(headerText, null, null, -1);
		}

		public void updateLoadingText(String contentText) {
			if (Platform.isFxApplicationThread()) {
				content.setText(contentText);
				alert.getDialogPane().requestLayout();
			} else {
				Platform.runLater(() -> updateLoadingText(contentText));
			}
		}

		public void updateProgress(double progress) {
			if (Platform.isFxApplicationThread()) {
				progressIndicator.setProgress(progress);
				alert.getDialogPane().requestLayout();
			} else {
				Platform.runLater(() -> updateProgress(progress));
			}
		}

		public void expandedText(String expandedText) {
			if (Platform.isFxApplicationThread()) {
				expandable.setText(expandedText);
				if (expandedText == null || expandedText.isEmpty()) {
					alert.getDialogPane().setExpandableContent(null);
				} else {
					alert.getDialogPane().setExpandableContent(expandable);
				}
				alert.getDialogPane().requestLayout();
			} else {
				Platform.runLater(() -> expandedText(expandedText));
			}
		}

		public void finish() {
			updateProgress(1);
		}

		public Alert getDialogInstance() {
			return alert;
		}
	}

	public static void initOwner(Window window) {
		for (Dialogs dialog : Utils.array(ASK, EXCEPTION, LOADING)) {
			dialog.getDialogInstance().initOwner(window);
		}
	}

	public static final LoginDialog LOGIN = new LoginDialog();
	public static final class LoginDialog extends Dialogs {

		private static final Map<String, Pair<String, String>> stored;
		static {
			stored = new HashMap<>();
		}

		public interface LoginInfo {
			String  username();
			String  password();
			String  storeKey();
			boolean store();
			void    login(String name, String password, OutException error, Consumer<Boolean> resultAction);
			String title();
		}

		private Alert alert;
		private CheckValue store;
		private TextEntry username;
		private PasswordEntry password;
		private Button login;
		private Button cancel;
		private ProgressIndicator doing;
		private VBox content;
		private Text errorText;
		private ScrollPane errorTextSP;
		private Tab errorTab;

		public LoginDialog() {
			alert = new Alert(AlertType.CONFIRMATION);

			TabPane tabs = new TabPane();
			tabs.getStylesheets().add("Configurator.css");

			VBox loginBox = new VBox();
			loginBox.getStyleClass().add("parameters");

			doing = new ProgressIndicator();
			doing.setProgress(-1);
			doing.setOpacity(0.5);

			Tab loginBoxTab = new Tab(ILK_LOGIN_LOGIN.value(), loginBox);
			loginBoxTab.setClosable(false);
			tabs.getTabs().add(loginBoxTab);

			store = new CheckValue();
			store.setText(ILK_LOGIN_STORE.value());
			login = new Button();
			login.setText(ILK_LOGIN_LOGIN.value());
			alert.getButtonTypes().setAll(ButtonType.CANCEL);
			cancel = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
			alert.getButtonTypes().clear();

			Node buttonFill = new Pane(); HBox.setHgrow(buttonFill, Priority.ALWAYS);
			HBox buttons = new HBox(store, buttonFill, login, cancel);

			buttons.getStyleClass().add("parameters");

			errorText = new Text();
			errorTextSP = new ScrollPane();
			errorTextSP.setContent(errorText);
			errorTab = new Tab(ILK_EXCEPTION.value(), errorTextSP);
			errorTab.setClosable(false);
			tabs.getTabs().add(errorTab);

			content = new VBox(new StackPane(tabs, doing), buttons);
			VBox.setVgrow(content.getChildren().get(0), Priority.ALWAYS);
			content.setMaxHeight(250);
			content.setMaxWidth(400);
			content.setPrefHeight(250);
			content.setPrefWidth(400);
			alert.getDialogPane().setContent(content);

			username = new TextEntry();
			username.setId("loginUsername");
			username.setText("Username");
			password = new PasswordEntry();
			password.setText("Password");
			password.setId("loginPassword");

			loginBox.getChildren().addAll(username, password);

			alert.getDialogPane().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					if (username.getEventTarget().equals(event.getTarget())) {
						switch (event.getCode()) {
						case TAB:
							password.requestFocus();
							event.consume();
							break;
						case ESCAPE:
							cancel.fire();
							event.consume();
							break;

						default:
							break;
						}
					} else if (password.getEventTarget().equals(event.getTarget())) {
						switch (event.getCode()) {
						case ESCAPE:
							cancel.fire();
							event.consume();
							break;
						case ENTER:
							login.getOnAction().handle(null);
							event.consume();
							break;

						default:
							break;
						}
					}
				}

			});
		}

		public boolean show(LoginInfo loginInfo, OutException error) {
			doing.setVisible(false);
			login.setDisable(false);
			errorTab.setDisable(true);
			store.setValue(loginInfo.store());
			errorText.setText("");

			alert.setTitle(loginInfo.title());

			username.setValue(loginInfo.username());
			password.setValue(loginInfo.password());

			login.setOnAction(e -> {
				login.setDisable(true);
				doing.setVisible(true);
				loginInfo.login(username.getValue(), password.getValue(), error, result -> {
					Platform.runLater(() -> {
						if (result) {
							alert.setResult(ButtonType.OK);
							alert.close();

							if (store.isSelected()) {
								stored.put(loginInfo.storeKey(), new Pair<>(username.getValue(), password.getValue()));
							}
						} else {
							login.setDisable(false);
							doing.setVisible(false);
							password.setFailed(true);
							errorTab.setDisable(false);
							errorText.setText(Utils.getStackTrace(error.get()));

							if (stored.containsKey(loginInfo.storeKey())) {
								stored.remove(loginInfo.storeKey());
							}
						}
					});
				});
			});

			Platform.runLater(() -> password.requestFocus());
			alert.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> cancel.fire());
			return handle() == ButtonType.OK;
		}

		@Override
		protected Alert getDialogInstance() {
			return alert;
		}

		public boolean hasStored(Class<?> clazz) {
			return stored.containsKey(clazz.getName());
		}

		public Pair<String, String> getStored(Class<?> clazz) {
			return stored.get(clazz.getName());
		}

		public boolean showNoFX(LoginInfo loginInfo, OutException error) {
			if (Platform.isFxApplicationThread()) {
				return show(loginInfo, error);
			} else {
				Task<Boolean> task = new Task<Boolean>() {
					@Override
					protected Boolean call() throws Exception {
						return show(loginInfo, error);
					}
				};
				Platform.runLater(task);
				try {
					return task.get();
				} catch (InterruptedException | ExecutionException e) {
					error.set(e);
					return false;
				}
			}
		}
	}

	protected abstract Alert getDialogInstance();

	protected final ButtonType handle() {
		Optional<ButtonType> result = getDialogInstance().showAndWait();
		return result.isPresent() ? result.get() : ButtonType.CANCEL;
	}
}
