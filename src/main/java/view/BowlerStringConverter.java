package view;

import javafx.scene.control.ListCell;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.util.StringConverter;
import model.Bowler;

public class BowlerStringConverter extends StringConverter<Bowler> {
	private final ListCell<Bowler> cell;

	public BowlerStringConverter(TextFieldListCell<Bowler> cell) {
		this.cell = cell;
	}

	@Override
	public Bowler fromString(String string) {
		Bowler client = cell.getItem();
		client.setName(string);
		return client;
	}

	@Override
	public String toString(Bowler bowler) {
		return bowler.getName().get();
	}

}
