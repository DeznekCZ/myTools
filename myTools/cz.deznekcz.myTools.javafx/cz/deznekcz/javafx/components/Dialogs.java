package cz.deznekcz.javafx.components;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import cz.deznekcz.javafx.configurator.components.CheckValue;
import cz.deznekcz.javafx.configurator.components.PasswordEntry;
import cz.deznekcz.javafx.configurator.components.TextEntry;
import cz.deznekcz.reference.OutException;
import cz.deznekcz.tool.i18n.Arguments;
import cz.deznekcz.tool.i18n.ILangKey;
import cz.deznekcz.util.Utils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
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
	static final ILangKey ILK_EXCEPTION = ILangKey.simple("Dialog.Exception.");
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
			alert.close();
		}

		public void start(String headerText, String contentText, String expandedText, double progress) {
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

			if (isHidden) alert.show();
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
			content.setText(contentText);
		}

		public void updateProgress(double progress) {
			progressIndicator.setProgress(progress);
		}

		public void expandedText(String expandedText) {
			expandable.setText(expandedText);
			if (expandedText == null || expandedText.isEmpty()) {
				alert.getDialogPane().setExpandableContent(null);
			} else {
				alert.getDialogPane().setExpandableContent(expandable);
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
		}

		private Alert alert;
		private CheckValue store;
		private TextEntry username;
		private PasswordEntry password;
		private Button login;
		private Button cancel;
		private ProgressIndicator doing;
		private VBox content;
		private TitledPane errorCollapser;
		private Text errorText;

		public LoginDialog() {
			alert = new Alert(AlertType.WARNING);

			VBox loginBox = new VBox();
			loginBox.getStyleClass().add("parameters");

			doing = new ProgressIndicator();
			doing.setProgress(-1);
			doing.setOpacity(0.5);

			StackPane stack = new StackPane(loginBox, doing);

			store = new CheckValue();
			store.setText(ILK_LOGIN_STORE.value());
			login = new Button();
			login.setText(ILK_LOGIN_LOGIN.value());
			alert.getButtonTypes().setAll(ButtonType.CANCEL);
			cancel = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
			alert.getButtonTypes().clear();

			Node buttonFill = new Pane(); HBox.setHgrow(buttonFill, Priority.ALWAYS);
			HBox buttons = new HBox(buttonFill, store, login, cancel);

			buttons.getStyleClass().add("parameters");

			content = new VBox(stack, buttons);
			alert.getDialogPane().setContent(content);

			errorText = new Text();
			errorCollapser = new TitledPane(ILK_EXCEPTION.value(), errorText);
		}

		public boolean show(LoginInfo loginInfo, OutException error) {
			doing.setVisible(false);
			login.setDisable(false);
			content.getChildren().remove(errorCollapser);
			errorCollapser.setExpanded(false);
			store.setValue(loginInfo.store());

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
							login.setDisable(true);
							doing.setVisible(true);
							content.getChildren().add(errorCollapser);
							errorText.setText(Utils.getStackTrace(error.get()));

							if (stored.containsKey(loginInfo.storeKey())) {
								stored.remove(loginInfo.storeKey());
							}
						}
					});
				});
			});

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
				} catch (InterruptedException e) {
					error.set(e);
					return false;
				} catch (ExecutionException e) {
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
