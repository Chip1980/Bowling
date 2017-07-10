package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import model.Bowler;
import view.BowlerStringConverter;

/*
 * BowlerDialogController, Verwaltung des DialogContents um vor einem neuen Spiel, die Mitspieler festzulegen
 * */
public class BowlerDialogController implements Initializable {

	@FXML
	private ListView<Bowler> bowlerListView;

	@FXML
	private TextField bowlerName;
	@FXML
	private DialogPane bowlersDialogPane;

	private ObservableList<Bowler> bowlerItems = FXCollections.observableArrayList();
	private SimpleBooleanProperty applyDisable = new SimpleBooleanProperty(true);

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		getBowlerListView().setItems(bowlerItems);
		getBowlerListView().setEditable(true);

		getBowlerListView().setCellFactory(lv -> {
			TextFieldListCell<Bowler> cell = new TextFieldListCell<>();
			cell.setConverter(new BowlerStringConverter(cell));
			return cell;
		});

		final EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
			@Override
			public void handle(final KeyEvent keyEvent) {
				if (keyEvent.getCode() == KeyCode.ENTER) {
					addBowler();
				}
			}
		};
		getBowlerName().addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);

		getBowlerItems().addListener((ListChangeListener.Change<? extends Bowler> c) -> {
			getApplyDisable().set(getBowlerItems().isEmpty());
		});

		final Button apply = (Button) getBowlersDialogPane().lookupButton(ButtonType.APPLY);
		apply.disableProperty().bind(getApplyDisable());
	}

	@FXML
	private void addBowler() {
		Bowler bowler = new Bowler(getBowlerName().getText());
		getBowlerItems().add(bowler);
		getBowlerName().clear();
	}

	public ObservableList<Bowler> getAllBowler() {
		return bowlerItems;
	}

	private ListView<Bowler> getBowlerListView() {
		return bowlerListView;
	}

	private TextField getBowlerName() {
		return bowlerName;
	}

	private DialogPane getBowlersDialogPane() {
		return bowlersDialogPane;
	}

	private ObservableList<Bowler> getBowlerItems() {
		return bowlerItems;
	}

	private SimpleBooleanProperty getApplyDisable() {
		return applyDisable;
	}
}
