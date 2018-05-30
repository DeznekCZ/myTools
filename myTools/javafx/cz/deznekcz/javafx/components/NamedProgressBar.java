package cz.deznekcz.javafx.components;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.text.TextAlignment;

public class NamedProgressBar extends AnchorPane {
	private ProgressBar progressBar;
	private Label progressText;

	public NamedProgressBar(String initValue) {
		progressBar = new ProgressBar();
		progressBar.setProgress(0);
		AnchorPane.setBottomAnchor(progressBar, 0.0);
		AnchorPane.setLeftAnchor(progressBar, 0.0);
		AnchorPane.setRightAnchor(progressBar, 0.0);
		progressText = new Label(initValue);
		progressText.setBackground(Background.EMPTY);
		progressText.setTextAlignment(TextAlignment.CENTER);
		progressText.setAlignment(Pos.CENTER);
		AnchorPane.setBottomAnchor(progressText, 0.0);
		AnchorPane.setLeftAnchor(progressText, 0.0);
		AnchorPane.setRightAnchor(progressText, 0.0);
		getChildren().addAll(progressBar, progressText);
	}
	
	public final void setProgress(String label, double value) {
		progressText.setText(label);
		progressBar.setProgress(value);
	}
	
	public final void setProgress(double value) {
		progressBar.setProgress(value);
	}
	
	public final void setProgressLabel(String value) {
		progressText.setText(value);
	}
	
	public final DoubleProperty progressProperty() {
		return progressBar.progressProperty();
	}
	
	public final double getProgress() {
		return progressBar.getProgress();
	}
	
	public final String getProgressLabel() {
		return progressText.getText();
	}
}
