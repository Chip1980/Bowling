package model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/*
 * Object das einen einzelenen Mitspieler repräsentiert
 * */
public class Bowler {

	private List<Frame> frames = new ArrayList<>(Frame.MAX_FRAMES);

	private SimpleStringProperty name = new SimpleStringProperty();

	private SimpleIntegerProperty totalScore = new SimpleIntegerProperty();

	public Bowler(String name) {
		setName(name);
		for (int index = 0; index < Frame.MAX_FRAMES; index++) {
			Frame frame = new Frame(index);
			frames.add(frame);
		}

	}

	public List<Frame> getFrames() {
		return frames;
	}

	public SimpleStringProperty getName() {
		return name;
	}

	public SimpleIntegerProperty getTotalScore() {
		return totalScore;
	}

	public void calculateTotalScore() {
		int total = 0;
		for (Frame frame : frames) {
			total += frame.getScore().get();
		}
		totalScore.set(total);
	}

	public Frame getLastframe() {
		return frames.get(Frame.MAX_FRAMES - 1);
	}

	public void setName(String name) {
		this.name.set(name);
	}
}
