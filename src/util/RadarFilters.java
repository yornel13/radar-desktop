package util;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class RadarFilters {


    public static EventHandler<KeyEvent> numberLetterFilter() {

        EventHandler<KeyEvent> aux = (KeyEvent keyEvent) -> {
            if (!"0123456789abcdefghijklmopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
                    .contains(keyEvent.getCharacter())) {
                keyEvent.consume();

            }
        };
        return aux;
    }


    public static EventHandler<KeyEvent> numberFilter() {

        EventHandler<KeyEvent> aux = (KeyEvent keyEvent) -> {
            if (!"0123456789"
                    .contains(keyEvent.getCharacter())) {
                keyEvent.consume();

            }
        };
        return aux;
    }
}
