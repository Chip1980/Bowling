package controller;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Attempt;
import model.Bowler;
import model.Frame;

public class BowlingGameControllerTest {
	private static final int PERFECTGAME = 300;

	private ObservableList<Bowler> allBowlers;

	private BowlingGameController mockBowlingGameController;

	private Bowler bowler;

	@Before
	public void setUp() {
		bowler = new Bowler("Test-Bowler");
		allBowlers = FXCollections.observableArrayList();
		allBowlers.add(bowler);

		mockBowlingGameController = Mockito.mock(BowlingGameController.class);

		Mockito.when(mockBowlingGameController.getCurrentBowler()).thenReturn(bowler);
		Mockito.when(mockBowlingGameController.getAllBowlers()).thenReturn(allBowlers);
		Mockito.doNothing().when(mockBowlingGameController).markNextBowler(bowler);

		for (Attempt attempt : Attempt.values()) {
			Mockito.when(mockBowlingGameController.nextAttempt(attempt)).thenCallRealMethod();
		}
	}

	/*
	 * testet den "Normalfall" nach dem ersten Versuch folgt der zweite Versuch
	 * im selben Frame
	 */
	@Test
	public void nextAttemptSecond() {
		Mockito.when(mockBowlingGameController.isLastFrame()).thenReturn(false);
		Mockito.when(mockBowlingGameController.getCurrentFrame()).thenReturn(bowler.getFrames().get(0));
		Frame frame = bowler.getFrames().get(0);
		frame.setFirstAttempt(5);
		if (!frame.isLastFrame()) {
			Assert.assertEquals(Attempt.SECOND, mockBowlingGameController.nextAttempt(Attempt.FIRST));
		}
	}

	/*
	 * testet den Fall im ersten Versuch ein Strike, somit ist das Frame beendet
	 * und es geht im nächsten Frame im ersten Versuch weiter
	 */
	@Test
	public void nextAttemptFirstStrike() {
		Mockito.when(mockBowlingGameController.isLastFrame()).thenReturn(false);
		Mockito.when(mockBowlingGameController.getCurrentFrame()).thenReturn(bowler.getFrames().get(0));
		Frame frame = bowler.getFrames().get(0);
		frame.setFirstAttempt(10);
		if (!frame.isLastFrame()) {
			Assert.assertEquals(Attempt.FIRST, mockBowlingGameController.nextAttempt(Attempt.FIRST));
		}
	}

	/*
	 * testet den Fall im letzten Frame, zweiter Versuch ergibt einen Spare,
	 * somit ist das Frame nicht beendet und es gibt einen "Bonusversuch"
	 */
	@Test
	public void nextAttemptThird() {
		Frame frame = bowler.getLastframe();
		Mockito.when(mockBowlingGameController.isLastFrame()).thenReturn(true);
		Mockito.when(mockBowlingGameController.getCurrentFrame()).thenReturn(frame);
		frame.setFirstAttempt(5);
		frame.setSecondAttempt(5);
		Attempt nextAttempt = mockBowlingGameController.nextAttempt(Attempt.SECOND);
		Assert.assertEquals(Attempt.THIRD, nextAttempt);
	}

	/*
	 * testet den Fall des "Perfekten Spiels", das heißt alle Würfe sind Strikes
	 * , also ein maximal Total von 300 Punkten
	 */
	@Test
	public void perfectGame() throws BowlingTestException {
		Mockito.doNothing().when(mockBowlingGameController).clearAttemptScore();
		Mockito.when(mockBowlingGameController.getCurrentBowler()).thenReturn(bowler);
		Mockito.when(mockBowlingGameController.getAttemptScore()).thenReturn("10");

		Mockito.doCallRealMethod().when(mockBowlingGameController).getCurrentFrameIndex();
		Mockito.doCallRealMethod().when(mockBowlingGameController).getPrevFrame();
		Mockito.doCallRealMethod().when(mockBowlingGameController).getPre_PrevFrame();
		Mockito.doCallRealMethod().when(mockBowlingGameController).calculateAndSetScore();
		Mockito.doCallRealMethod().when(mockBowlingGameController).isLastFrame();
		Mockito.doCallRealMethod().when(mockBowlingGameController).convertInputToInteger();
		int convertedAttemptScore = mockBowlingGameController.convertInputToInteger();
		try {
			Mockito.doCallRealMethod().when(mockBowlingGameController).checkGutterball(convertedAttemptScore);
			Mockito.doCallRealMethod().when(mockBowlingGameController).hitPins();
			for (Frame frame : bowler.getFrames()) {
				Mockito.doCallRealMethod().when(mockBowlingGameController).setScoreForCurrentAttempt(Attempt.FIRST,
						frame);
				Mockito.doCallRealMethod().when(mockBowlingGameController).setScoreForCurrentAttempt(Attempt.SECOND,
						frame);
				Mockito.doCallRealMethod().when(mockBowlingGameController).setScoreForCurrentAttempt(Attempt.THIRD,
						frame);
				Mockito.when(mockBowlingGameController.getCurrentFrame()).thenReturn(frame);
				Mockito.when(mockBowlingGameController.getCurrentAttempt()).thenReturn(Attempt.FIRST);
				mockBowlingGameController.hitPins();
				if (frame.isLastFrame()) {
					Mockito.when(mockBowlingGameController.getCurrentAttempt()).thenReturn(Attempt.SECOND);
					mockBowlingGameController.hitPins();
					Mockito.when(mockBowlingGameController.getCurrentAttempt()).thenReturn(Attempt.THIRD);
					mockBowlingGameController.hitPins();
				}
			}
			Assert.assertEquals(PERFECTGAME, bowler.getTotalScore().get());
		} catch (Exception e) {
			throw new BowlingTestException(e);
		}
	}

	/*
	 * testet die Berechnung für Frame 1 = Strike, Frame 2 = Strike, Frame 3 =
	 * 2/3, Frame 1 muss einen Score von 25, Frame 2 muss einen Score von
	 * 15,Frame 3 muss einen Score von 5 aufweisen
	 */
	@Test
	public void strikeSpecial() {
		Mockito.when(mockBowlingGameController.isLastFrame()).thenReturn(false);
		Mockito.doCallRealMethod().when(mockBowlingGameController).calculateAndSetScore();
		Mockito.doCallRealMethod().when(mockBowlingGameController).getPrevFrame();
		Mockito.doCallRealMethod().when(mockBowlingGameController).getPre_PrevFrame();
		Mockito.doCallRealMethod().when(mockBowlingGameController).getCurrentFrame();

		List<Frame> frames = bowler.getFrames();

		Frame pre_PrevFrame = frames.get(0);
		pre_PrevFrame.setFirstAttempt(10);
		Mockito.when(mockBowlingGameController.getCurrentFrameIndex()).thenReturn(0);
		mockBowlingGameController.calculateAndSetScore();

		Frame prevFrame = frames.get(1);
		prevFrame.setFirstAttempt(10);
		Mockito.when(mockBowlingGameController.getCurrentFrameIndex()).thenReturn(1);
		mockBowlingGameController.calculateAndSetScore();

		Frame currentFrame = frames.get(2);
		currentFrame.setFirstAttempt(2);
		currentFrame.setSecondAttempt(3);

		Mockito.when(mockBowlingGameController.getCurrentFrameIndex()).thenReturn(2);
		mockBowlingGameController.calculateAndSetScore();

		Assert.assertEquals(25, pre_PrevFrame.getScore().get());
		Assert.assertEquals(15, prevFrame.getScore().get());
		Assert.assertEquals(5, currentFrame.getScore().get());
	}

	/*
	 * testet die Berechnung für Frame 1 = Spare,Frame 2= 3/, Frame 1 muss einen
	 * Score von 13 aufweisen
	 */
	@Test
	public void spareSpecial() {
		Frame prevFrame = new Frame(1);
		prevFrame.setFirstAttempt(5);
		prevFrame.setSecondAttempt(5);

		Frame currentFrame = new Frame(2);
		currentFrame.setFirstAttempt(3);

		Mockito.doCallRealMethod().when(mockBowlingGameController).calculateAndSetScore();
		Mockito.when(mockBowlingGameController.getCurrentFrameIndex()).thenReturn(2);
		Mockito.when(mockBowlingGameController.isLastFrame()).thenReturn(false);
		Mockito.when(mockBowlingGameController.getPrevFrame()).thenReturn(prevFrame);
		Mockito.when(mockBowlingGameController.getCurrentFrame()).thenReturn(currentFrame);

		mockBowlingGameController.calculateAndSetScore();

		Assert.assertEquals(prevFrame.getScore().get(), 13);
	}

	/*
	 * testet den Fall eines Gutterball, falls eine 0 eingegeben wurde muss eine
	 * GutterballException geworfen werden
	 */
	@Test
	public void gutterball() {
		String attemptScore = mockBowlingGameController.getAttemptScore();
		Mockito.when(attemptScore).thenReturn("0");
		try {
			Mockito.doCallRealMethod().when(mockBowlingGameController).convertInputToInteger();
			int convertedAttemptScore = mockBowlingGameController.convertInputToInteger();
			Mockito.doCallRealMethod().when(mockBowlingGameController).checkGutterball(convertedAttemptScore);

			Mockito.doThrow(GutterballException.class).when(mockBowlingGameController)
					.checkGutterball(convertedAttemptScore);
		} catch (Exception ex) {
			throw new BowlingTestException(ex);
		}
	}
}
