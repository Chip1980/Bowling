package view;

import java.text.NumberFormat;
import java.text.ParseException;

import javafx.util.StringConverter;

public class BowlingScoreStringConverter extends StringConverter<Number> {
	NumberFormat formatter = NumberFormat.getInstance();

	@Override
	public String toString(Number value) {
		String retValue;
		String format = formatter.format(value);
		retValue = format;
		return retValue;
	}

	@Override
	public Number fromString(String text) {

		try {

			// we don't have to check for the symbol because the NumberFormat is
			// lenient, ie 123abc would result in the value 123
			return formatter.parse(text);

		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

	}

}
