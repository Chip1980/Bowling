package model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Frame {
	public static final int MAX_FRAMES = 10;
	private SimpleIntegerProperty score = new SimpleIntegerProperty();
	private SimpleIntegerProperty firstAttempt = new SimpleIntegerProperty();

	private SimpleIntegerProperty secondAttempt = new SimpleIntegerProperty();
	private SimpleIntegerProperty thirdAttempt = new SimpleIntegerProperty();
	private Integer index;
	private Attempt currentAttempt = Attempt.FIRST;
	private SimpleBooleanProperty hasBonusAttempt = new SimpleBooleanProperty();

	public Frame(int index) {
		this.index = index;
	}

	public SimpleIntegerProperty getScore() {
		return score;
	}

	public void setScore(Integer value) {
		score.set(value);
	}

	public SimpleIntegerProperty getFirstAttempt() {
		return firstAttempt;
	}

	public SimpleIntegerProperty getSecondAttempt() {
		return secondAttempt;
	}

	public SimpleIntegerProperty getThirdAttempt() {
		return thirdAttempt;
	}

	public void setFirstAttempt(Integer value) {
		this.firstAttempt.set(value);
	}

	public void setSecondAttempt(Integer value) {
		this.secondAttempt.set(value);
	}

	public void setThirdAttempt(Integer value) {
		this.thirdAttempt.set(value);
	}

	public SimpleBooleanProperty hasBonusAttempt() {
		hasBonusAttempt
				.set((getIndex() == MAX_FRAMES - 1) && (isSpare() || isStrike() || getSecondAttempt().get() == 10));
		return hasBonusAttempt;
	}

	public boolean isSpare() {
		return !isStrike() && getFirstAttempt().get() + getSecondAttempt().get() == 10;
	}

	public boolean isStrike() {
		return getFirstAttempt().get() == 10;
	}

	public Integer getIndex() {
		return index;
	}

	public void score() {
		int scoreValue = getFirstAttempt().get() + getSecondAttempt().get();
		setScore(scoreValue);
	}

	public void scoreBonusFrame() {
		int scoreValue = getScore().get() + getThirdAttempt().get();
		setScore(scoreValue);
	}

	public boolean isLastFrame() {
		return getIndex().equals(MAX_FRAMES - 1);
	}

	public Attempt getCurrentAttempt() {
		return currentAttempt;
	}

	public void setCurrentAttempt(Attempt currentAttempt) {
		this.currentAttempt = currentAttempt;
	}
}
