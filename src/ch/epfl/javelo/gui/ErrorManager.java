package ch.epfl.javelo.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Classe chargée de la gestion d'erreurs
 *
 * @author Thierry Sokhn (345880)
 * @author Sofia Taouhid (339880)
 */

public final class ErrorManager {

    private Pane pane;
    private Text text = new Text();

    public ErrorManager() {
        pane = new VBox();
        pane.getStylesheets().add("error.css");
        pane.setMouseTransparent(true);
        pane.getChildren().add(text);
    }

    public Pane pane() {
        return pane;
    }

    public void displayError(String msgErreur) {
        text.setText(msgErreur);

        java.awt.Toolkit.getDefaultToolkit().beep();

        FadeTransition ft1 = new FadeTransition(Duration.millis(200), pane);
        ft1.setFromValue(0);
        ft1.setToValue(0.8);

        PauseTransition pause = new PauseTransition();
        pause.durationProperty().setValue(Duration.millis(2000));

        FadeTransition ft2 = new FadeTransition(Duration.millis(500), pane);
        ft2.setFromValue(0.8);
        ft2.setToValue(0);

        SequentialTransition seqT = new SequentialTransition (pane, ft1, pause, ft2);

        seqT.stop();
        seqT.play();
    }
}