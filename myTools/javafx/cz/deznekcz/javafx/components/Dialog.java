package cz.deznekcz.javafx.components;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import cz.deznekcz.tool.i18n.Arguments;
import cz.deznekcz.tool.i18n.ILangKey;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

public abstract class Dialog extends Alert {
	
	final static ILangKey ILK_EXCEPTION       = ILangKey.simple("Dialog.Exception.");
	final static ILangKey ILK_EXCEPTION_TITLE = ILK_EXCEPTION.extended("Title")
													.initDefault("Error cached");
	final static @Arguments(types = {String.class}, hints = {"exception name"}) 
				 ILangKey ILK_EXCEPTION_NAME  = ILK_EXCEPTION.extended("Name")
				 									.initDefault("Exception: %s");
	
	@Arguments(types={Exception.class}, hints={"Exception to throw"})
	public static final Dialog EXCEPTION = new Dialog(AlertType.ERROR) {
		private TextArea textArea;
		{
			setTitle(ILK_EXCEPTION_TITLE.value());
			setHeaderText(null);
			textArea = new TextArea();
			textArea.setMaxWidth(200);
		}
		@Override
		public ButtonType show(Object...message) {
			Exception e = (Exception) message[0];
			
			setTitle(ILK_EXCEPTION_TITLE.value());
			
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			
			setContentText(
					ILK_EXCEPTION_NAME
					.value(e.getLocalizedMessage()));

			textArea.setText(writer.toString());
			getDialogPane().setExpandableContent(textArea);
			
			return showAndWait().get();
		}
		
	};
	@Arguments(hints={"Question",   "Default button", "Buttons"},
			   types={String.class, ButtonType.class, ButtonType[].class})
	public static final Dialog ASK = new Dialog(AlertType.CONFIRMATION) {
		
		@Override
		public ButtonType show(Object... message) {
			getButtonTypes().setAll((ButtonType[]) message[2]);
			
			Button yesButton = (Button) getDialogPane().lookupButton( (ButtonType) message[1] );
		    yesButton.setDefaultButton( true );
		    
		    setHeaderText((String) message[0]);
		    
		    return showAndWait().get();
		}
	};
	
	public Dialog(AlertType type) {
		super(type);
	}
	
	public abstract ButtonType show(Object...message);

}
