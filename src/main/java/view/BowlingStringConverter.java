package view;

import java.text.NumberFormat;
import java.text.ParseException;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.StringConverter;

public class BowlingStringConverter extends StringConverter<Number> {

	NumberFormat formatter = NumberFormat.getInstance();
	private SimpleIntegerProperty firstAttempt;
	private boolean isLastframe;

	public BowlingStringConverter(boolean isLastframe) {
		this.isLastframe = isLastframe;
	}

	public BowlingStringConverter(SimpleIntegerProperty firstAttempt, boolean isLastframe) {
		this.firstAttempt = firstAttempt;
		this.isLastframe = isLastframe;
	}

	@Override
	public String toString(Number value) {
		String retValue;
		if (firstAttempt == null || isLastframe) {
			if (value.intValue() == 10) {
				retValue = "X";
			} else {
				String format = formatter.format(value);
				retValue = format;
			}
		} else

		{
			if (firstAttempt.get() + value.intValue() == 10) {
				retValue = "/";
			} else if (firstAttempt.get() == 0 && value.intValue() == 10) {
				retValue = "X";
			} else {
				String format = formatter.format(value);
				retValue = format;
			}
		}
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
