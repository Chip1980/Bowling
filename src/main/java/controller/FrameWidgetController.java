package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import model.Frame;
import view.BowlingScoreStringConverter;
import view.BowlingStringConverter;

/*
 * FrameWidgetController, Verwaltung den einzelnen FrameContent
 * */
public class FrameWidgetController implements Initializable {
	@FXML
	private Label firstAttemptLabel;

	@FXML
	private Label secondAttemptLabel;
	@FXML
	private Label thirdAttemptLabel;

	@FXML
	private Label scoreLabel;
	@FXML
	private Label currentFrameLabel;
	private Frame frame;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getThirdAttemptLabel().setVisible(false);
	}

	private Frame getFrame() {
		return frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	public void setBindings() {
		Bindings.bindBidirectional(getFirstAttemptLabel().textProperty(), getFrame().getFirstAttempt(),
				new BowlingStringConverter(getFrame().isLastFrame()));
		Bindings.bindBidirectional(getSecondAttemptLabel().textProperty(), getFrame().getSecondAttempt(),
				new BowlingStringConverter(getFrame().getFirstAttempt(), getFrame().isLastFrame()));
		Bindings.bindBidirectional(getThirdAttemptLabel().textProperty(), getFrame().getThirdAttempt(),
				new BowlingStringConverter(getFrame().isLastFrame()));

		Bindings.bindBidirectional(getScoreLabel().textProperty(), getFrame().getScore(),
				new BowlingScoreStringConverter());

		getCurrentFrameLabel().textProperty().bind(Bindings.createStringBinding(() -> " " + getFrame().getIndex(),
				new SimpleStringProperty("" + getFrame().getIndex())));

		Bindings.bindBidirectional(getThirdAttemptLabel().visibleProperty(), getFrame().hasBonusAttempt());

		Bindings.bindBidirectional(getSecondAttemptLabel().visibleProperty(),
				new SimpleBooleanProperty(!getFrame().isStrike()));
	}

	private Label getFirstAttemptLabel() {
		return firstAttemptLabel;
	}

	private Label getSecondAttemptLabel() {
		return secondAttemptLabel;
	}

	private Label getThirdAttemptLabel() {
		return thirdAttemptLabel;
	}

	private Label getScoreLabel() {
		return scoreLabel;
	}

	private Label getCurrentFrameLabel() {
		return currentFrameLabel;
	}
}
