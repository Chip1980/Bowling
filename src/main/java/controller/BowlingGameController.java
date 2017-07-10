package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.converter.IntegerStringConverter;
import model.Attempt;
import model.Bowler;
import model.Frame;
import utils.NotificationHandler;
import view.BowlerStringConverter;

/*
 * BowlerGameController, Verwaltung den kompletten Spielablauf, sowie des DialogContents
 * */
public class BowlingGameController implements Initializable {
	private static final int PERFECTGAME = 300;
	@FXML
	private TextField attemptScore;
	@FXML
	private ListView<TitledPane> soloGameView;

	@FXML
	private Button hitPinsButton;
	private ObservableList<TitledPane> soloGames = FXCollections.observableArrayList();

	@FXML
	private ListView<Bowler> bowlerListView;
	private ObservableList<Bowler> allBowlers = FXCollections.observableArrayList();

	private int currentFrameIndex = 0;

	private Bowler currentBowler;
	private Attempt currentAttempt = Attempt.FIRST;
	private SimpleBooleanProperty attemptScoreDisable = new SimpleBooleanProperty(true);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getBowlerListView().setItems(getAllBowlers());
		getBowlerListView().setCellFactory(lv -> {
			TextFieldListCell<Bowler> cell = new TextFieldListCell<>();
			cell.setConverter(new BowlerStringConverter(cell));
			return cell;
		});

		/* Eingabe zur Prüfung senden */
		getAttemptScoreTextField().addEventFilter(KeyEvent.KEY_TYPED, numeric_Validation(2));

		/* Eingabe von gefallenen Pins, aktivieren/deaktivieren */
		Bindings.bindBidirectional(getAttemptScoreTextField().disableProperty(), getAttemptScoreDisable());
		Bindings.bindBidirectional(getHitPinsButton().disableProperty(), getAttemptScoreDisable());

		getSoloGameView().setItems(getSoloGames());
	}

	private Button getHitPinsButton() {
		return hitPinsButton;
	}

	public void initBowlingScorboard() {
		try {
			for (Bowler bowler : getAllBowlers()) {
				HBox framesBox = new HBox();
				framesBox.setSpacing(10.0d);
				for (Frame frame : bowler.getFrames()) {
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/frameWidget.fxml"));
					Parent framePane = loader.load();
					FrameWidgetController controller = loader.getController();
					controller.setFrame(frame);
					controller.setBindings();
					framesBox.getChildren().add(framePane);
				}

				FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/totalWidget.fxml"));
				Parent totalPane = loader.load();
				TotalWidgetController controller = loader.getController();
				controller.bindTotalScore(bowler.getTotalScore());
				framesBox.getChildren().add(totalPane);

				TitledPane pane = new TitledPane(bowler.getName().get(), framesBox);
				pane.setExpanded(true);
				pane.setCollapsible(false);
				getSoloGames().add(pane);
			}
		} catch (IOException e) {
			throw new BowlingGameException("" + e.getLocalizedMessage());
		}
	}

	/*
	 * Eingabe von gefallenen Pins, auf Validität prüfen, Eingabe ist valide,
	 * wenn die Eingabe zwischen 0 und 10, sowie Versuch 1 + Versuch 2 nicht
	 * größer 10 ist
	 */
	public EventHandler<KeyEvent> numeric_Validation(final Integer max_Lengh) {
		return new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				Frame currentFrame = getCurrentFrame();
				boolean isLastFrame = isLastFrame();
				if (e.getCharacter().matches("[0-9.]")) {
					TextField txt_TextField = (TextField) e.getSource();
					if (txt_TextField.getText().length() >= max_Lengh) {
						e.consume();
					}
					if (txt_TextField.getText().contains(".") && e.getCharacter().matches("[.]")) {
						e.consume();
					}
					int answer = Integer.parseInt(txt_TextField.getText() + e.getCharacter());
					int maxValue = currentFrame.getFirstAttempt().get() + answer;
					if (isLastFrame) {
						maxValue = answer;
					}

					if (answer < 0 || maxValue > 10) {
						e.consume();
					}
				} else {
					e.consume();
				}
			}
		};
	}

	/*
	 * Pins sind gefallen, merken und kalkulieren und weiter zum nächsten
	 * Versuch
	 */
	@FXML
	public void hitPins() {
		Frame frame = getCurrentFrame();

		try {
			setScoreForCurrentAttempt(getCurrentAttempt(), frame);
			calculateAndSetScore();
		} catch (Exception ex) {
			NotificationHandler.handle(ex, getOwner(), AlertType.ERROR);
		}

		getCurrentBowler().calculateTotalScore();

		setCurrentAttempt(nextAttempt(getCurrentAttempt()));
		frame.setCurrentAttempt(getCurrentAttempt());

		clearAttemptScore();

		checkGameDone();

	}

	/*
	 * Prüfung ob das Spiel das Ende erreicht hat
	 */
	public void checkGameDone() {
		if (getCurrentAttempt().equals(Attempt.DONE)) {
			getAttemptScoreDisable().set(true);
			if (getCurrentBowler().getTotalScore().get() == PERFECTGAME) {
				NotificationHandler.handlePerfectgame(getOwner());
			}
		}
	}

	public void clearAttemptScore() {
		getAttemptScoreTextField().clear();
	}

	/*
	 * owner für Notifications
	 */
	public Window getOwner() {
		return getAttemptScoreTextField().getScene().getWindow();
	}

	/*
	 * Ermittlung des nächsten Versuchen
	 */
	public Attempt nextAttempt(Attempt attempt) {
		Attempt retValue = null;
		Frame currentFrame = getCurrentFrame();
		boolean isLastFrame = isLastFrame();
		switch (attempt) {
		case FIRST:
			if (!isLastFrame) {
				if (currentFrame.isStrike()) {
					nextBowlerAndFrame();
					retValue = Attempt.FIRST;
				} else {
					retValue = Attempt.SECOND;
				}
			} else {
				retValue = Attempt.SECOND;
			}
			break;
		case SECOND:
			if (!isLastFrame) {
				nextBowlerAndFrame();
				retValue = Attempt.FIRST;
			} else if (getCurrentFrame().hasBonusAttempt().get()) {
				retValue = Attempt.THIRD;
			} else {
				retValue = Attempt.DONE;
			}
			break;
		case THIRD:
			int currentBowlerIndex = getAllBowlers().indexOf(getCurrentBowler());
			if (getAllBowlers().size() - 1 == currentBowlerIndex) {
				retValue = Attempt.DONE;
			} else {
				nextBowlerAndFrame();
				retValue = Attempt.FIRST;
			}
			break;

		default:
			break;
		}
		return retValue;
	}

	public Frame getCurrentFrame() {
		return getCurrentBowler().getFrames().get(getCurrentFrameIndex());
	}

	/*
	 * Ermittlung wer der nächster Bowler ist und ob der CurrentFrameIndex sich
	 * ändert
	 */
	private void nextBowlerAndFrame() {
		int currentBowlerIndex = getAllBowlers().indexOf(getCurrentBowler());
		if (getAllBowlers().size() - 1 == currentBowlerIndex
				&& getCurrentFrameIndex() < getCurrentBowler().getFrames().size()) {
			nextFrame();
		}
		setCurrentBowler(nextBowler());

	}

	private void nextFrame() {
		currentFrameIndex++;
	}

	private Bowler nextBowler() {
		Bowler nextBowler;
		int indexOfCurrentBowler = getAllBowlers().indexOf(getCurrentBowler());
		if (indexOfCurrentBowler < getAllBowlers().size() - 1) {
			nextBowler = getAllBowlers().get(indexOfCurrentBowler + 1);
		} else {
			nextBowler = getAllBowlers().get(0);
		}
		markNextBowler(nextBowler);

		return nextBowler;
	}

	/*
	 * Setzen der Scores für der aktuellen Versuch im aktuellen Frame
	 */
	public void setScoreForCurrentAttempt(Attempt attempt, Frame frame) throws Exception {

		checkLegalInput();

		int attemptScoureIntValue = convertInputToInteger();
		try {
			checkGutterball(attemptScoureIntValue);
		} catch (GutterballException ex) {
			NotificationHandler.handleGutterball(getOwner());
		}
		switch (attempt) {
		case FIRST:
			frame.setFirstAttempt(attemptScoureIntValue);
			break;
		case SECOND:
			frame.setSecondAttempt(attemptScoureIntValue);
			break;
		case THIRD:
			frame.setThirdAttempt(attemptScoureIntValue);
			break;

		default:
			break;
		}

	}

	public int convertInputToInteger() {
		IntegerStringConverter isc = new IntegerStringConverter();
		int attemptScoureIntValue = isc.fromString(getAttemptScore());
		return attemptScoureIntValue;
	}

	/* Exception bei Eingabe von "0" */
	public void checkGutterball(int attemptScoureIntValue) throws Exception {
		if (attemptScoureIntValue == 0) {
			throw new GutterballException();
		}
	}

	/* Eingabe darf nicht "leer" sein */
	public void checkLegalInput() {
		if (StringUtils.isEmpty(getAttemptScore())) {
			throw new BowlingGameException("illegal input");
		}
	}

	public String getAttemptScore() {
		return getAttemptScoreTextField().getText();
	}

	/*
	 * Ermittlung der Scores für den aktuellen Frames, ggf. des vorherigen
	 * Frames, ggf. des vorvorherigen Frames
	 */
	public void calculateAndSetScore() {

		// first frame
		Frame curr = getCurrentFrame();
		/* 1. Frame */
		if (getCurrentFrameIndex() < 1) {

			curr.score();

		} else {
			/* weitere Frames */
			if (!isLastFrame()) {
				Frame prev = getPrevFrame();

				if (prev.isSpare()) {
					prev.score();
					prev.setScore(prev.getScore().get() + curr.getFirstAttempt().get());
				}

				if (prev.isStrike() && curr.isStrike()) {
					if (getCurrentFrameIndex() >= 2) {
						Frame pre_prev = getPre_PrevFrame();
						pre_prev.score();
						prev.score();
						curr.score();
						pre_prev.setScore(pre_prev.getScore().get() + prev.getScore().get() + curr.getScore().get());
					}
				} else if (prev.isStrike() && !curr.isStrike()) {
					if (getCurrentFrameIndex() >= 2) {
						Frame pre_prev = getPre_PrevFrame();
						if (pre_prev.isStrike()) {
							pre_prev.score();
							prev.score();
							curr.score();
							prev.setScore(prev.getScore().get() + curr.getScore().get());
							pre_prev.setScore(pre_prev.getScore().get() + prev.getScore().get());
						} else {
							prev.score();
							prev.setScore(prev.getScore().get() + curr.getScore().get());
						}

					} else {
						prev.score();
						prev.setScore(
								prev.getScore().get() + curr.getFirstAttempt().get() + curr.getSecondAttempt().get());
					}
				}

				if (!curr.isSpare() || !curr.isStrike()) {
					curr.score();
				}

			} else {

				Frame prev = getPrevFrame();

				if (getCurrentFrameIndex() >= 2) {
					Frame pre_prev = getPre_PrevFrame();
					if (pre_prev.isStrike() && prev.isStrike()) {
						curr.score();
						prev.score();
						prev.setScore(prev.getScore().get() + curr.getScore().get());
						pre_prev.score();
						pre_prev.setScore(pre_prev.getScore().get() + +curr.getScore().get());
					}
					if (prev.isSpare()) {
						curr.score();
						prev.score();
						prev.setScore(prev.getScore().get() + curr.getFirstAttempt().get());
					} else {
						// Ermittlung des Scores des letzten Frames
						if (curr.hasBonusAttempt().get()) {
							curr.score();
							curr.scoreBonusFrame();
						}
					}
				}

			}
		}

	}

	public boolean isLastFrame() {
		return getCurrentFrame().isLastFrame();
	}

	public Frame getPre_PrevFrame() {
		return getCurrentBowler().getFrames().get(getCurrentFrameIndex() - 2);
	}

	public Frame getPrevFrame() {
		return getCurrentBowler().getFrames().get(getCurrentFrameIndex() - 1);
	}

	public void setAllBowler(ObservableList<Bowler> elements) {
		getAllBowlers().addAll(elements);
	}

	public void setStartBowler() {
		setCurrentBowler(getAllBowlers().get(0));
		getBowlerListView().getSelectionModel().select(getCurrentBowler());
		getAttemptScoreDisable().set(false);
	}

	private BooleanPropertyBase getAttemptScoreDisable() {
		return attemptScoreDisable;
	}

	public ObservableList<Bowler> getAllBowlers() {
		return allBowlers;
	}

	public int getCurrentFrameIndex() {
		return currentFrameIndex;
	}

	public Bowler getCurrentBowler() {
		return currentBowler;
	}

	private void setCurrentBowler(Bowler currentBowler) {
		this.currentBowler = currentBowler;

	}

	public Attempt getCurrentAttempt() {
		return currentAttempt;
	}

	public void setCurrentAttempt(Attempt currentAttempt) {
		this.currentAttempt = currentAttempt;
	}

	private TextInputControl getAttemptScoreTextField() {
		return attemptScore;
	}

	private ListView<Bowler> getBowlerListView() {
		return bowlerListView;
	}

	public void markNextBowler(Bowler nextBowler) {
		getBowlerListView().getSelectionModel().select(nextBowler);
	}

	public void startNewGame() {
		stopPrevGame();
		showNewBowlersDialog();
	}

	public void showNewBowlersDialog() {
		try {
			Dialog<ObservableList<Bowler>> dialog = new Dialog<>();
			dialog.initModality(Modality.APPLICATION_MODAL);

			FXMLLoader loaderBowlerDialog = new FXMLLoader(getClass().getResource("/view/bowlerDialog.fxml"));
			DialogPane dialogPane = loaderBowlerDialog.load();
			BowlerDialogController bowlerDialogController = loaderBowlerDialog.getController();
			dialog.setDialogPane(dialogPane);
			Optional<ObservableList<Bowler>> result = dialog.showAndWait();
			if (result.isPresent()) {
				setAllBowler(bowlerDialogController.getAllBowler());

				setStartBowler();
				initBowlingScorboard();
			}
		} catch (IOException e) {
			throw new BowlingGameException("" + e.getLocalizedMessage());
		}
	}

	private void stopPrevGame() {
		getSoloGames().clear();
		getAllBowlers().clear();
		getAttemptScoreDisable().set(false);
		setCurrentAttempt(Attempt.FIRST);
		setCurrentFrameIndex(0);
	}

	private void setCurrentFrameIndex(int currentFrameIndex) {
		this.currentFrameIndex = currentFrameIndex;
	}

	private ListView<TitledPane> getSoloGameView() {
		return soloGameView;
	}

	private ObservableList<TitledPane> getSoloGames() {
		return soloGames;
	}

}
