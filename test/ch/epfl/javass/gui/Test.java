package ch.epfl.javass.gui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Test {
    public static void main(String[] args) {
        // IntegerProperty i = new SimpleIntegerProperty(2);
        // System.out.println(Bindings.format("%s = %d %o", "joe", 35, i));

        ObservableList<Integer> l = FXCollections.observableArrayList();
        l.add(1);
        l.add(2);
        l.add(3);
        BooleanProperty isPlayable = new SimpleBooleanProperty();
        isPlayable.bind(Bindings.createBooleanBinding(() -> {
            System.out.println("List has changed");

            return l.contains(2);
        }, l));
        l.remove(1);
        if (isPlayable.getValue())
            l.add(1);
        // l.add(2);
        IntegerProperty i = new SimpleIntegerProperty(9);
        i.bind(Bindings.when(isPlayable).then(2).otherwise(3));
        // l.add(2);
        // System.out.println(l);
        // l.add(2);
        // System.out.println(isPlayable.getValue());
    }
}
