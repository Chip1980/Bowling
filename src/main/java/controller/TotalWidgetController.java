package controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import view.BowlingTotalScoreStringConverter;

/*
 * TotalWidgetController, Verwaltung den einzelnen TotalContent
 * */
public class TotalWidgetController {
	@FXML
	private Label totalLabel;

	public void bindTotalScore(SimpleIntegerProperty totalScore) {
		Bindings.bindBidirectional(totalLabel.textProperty(), totalScore, new BowlingTotalScoreStringConverter());
	}

}
