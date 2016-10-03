package cz.deznekcz.javafx.components;

import java.io.PrintWriter;
import java.io.StringWriter;

import cz.deznekcz.tool.i18n.Arguments;
import cz.deznekcz.tool.i18n.ILangKey;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

public abstract class Dialog extends Alert {
	
	final static ILangKey ILK_EXCEPTION       = ILangKey.simple("Dialog.Exception.");
	final static ILangKey ILK_EXCEPTION_TITLE = ILK_EXCEPTION.extended("Title");
	final static @Arguments(types = {String.class}, hints = {"exception name"}) 
				 ILangKey ILK_EXCEPTION_NAME  = ILK_EXCEPTION.extended("Name[%s]");
	
	public static final Dialog EXCEPTION = new Dialog(AlertType.ERROR) {
		private TextArea textArea;
		{
			setTitle(ILK_EXCEPTION_TITLE.value());
			setHeaderText(null);
			textArea = new TextArea();
			textArea.setMaxWidth(200);
		}
		@Override
		public void show(Object message) {
			Exception e = (Exception) message;
			
			StringWriter writer = new StringWriter();
			e.printStackTrace(new PrintWriter(writer));
			
			setContentText(
					ILK_EXCEPTION_NAME
					.format(e.getClass().getName())
					.value(e.getMessage()));
			
			textArea.setText(writer.toString());
			setGraphic(textArea);
			
			showAndWait();
		}
		
	};
	
	public Dialog(AlertType type) {
		super(type);
	}
	
	public abstract void show(Object message);

}
