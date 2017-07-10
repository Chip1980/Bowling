package utils;

import org.controlsfx.control.Notifications;

import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;

public class NotificationHandler {

	private static final String GUTTERBALL = "gutterball";
	private static final String PERFECTGAME = "perfect game";

	public static void handle(Exception ex, Window owner, AlertType type) {
		Notifications notifications = Notifications.create();
		notifications.owner(owner);
		notifications.title(ex.getClass().getSimpleName());
		notifications.text(ex.getLocalizedMessage());

		if (type.equals(AlertType.INFORMATION)) {
			notifications.showInformation();
		}
		if (type.equals(AlertType.WARNING)) {
			notifications.showWarning();
		}
		if (type.equals(AlertType.ERROR)) {
			notifications.showError();
		}
		if (type.equals(AlertType.CONFIRMATION)) {
			notifications.showConfirm();
		}
	}

	public static void handleGutterball(Window owner) {
		Notifications notifications = Notifications.create();
		notifications.owner(owner);
		notifications.darkStyle();
		notifications.text(GUTTERBALL);
		notifications.showInformation();

	}

	public static void handlePerfectgame(Window owner) {
		Notifications notifications = Notifications.create();
		notifications.owner(owner);
		notifications.darkStyle();
		notifications.text(PERFECTGAME);
		notifications.showInformation();

	}

}
