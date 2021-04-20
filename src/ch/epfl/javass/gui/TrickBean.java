package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Représente le bean JavaFx , contenant le pli courant et doté de 3 propriétés:
 * propriété pour l'atout , propriété pour le pli courant , propriété pour le
 * joueur menant le pli courant
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 */
public final class TrickBean {

    /**
     * Propriété contenant l'atout
     */
    private ObjectProperty<Color> trumpProperty = new SimpleObjectProperty<>();
    /**
     * Propriété contenant le pli courant
     */
    private ObservableMap<PlayerId, Card> trickProperty = FXCollections
            .observableHashMap();
    /**
     * Propriété contenant le joueur menant le pli courant
     */
    private ObjectProperty<PlayerId> winningPlayerProperty = new SimpleObjectProperty<>();

    /**
     * Retourne la propriété contenant le joueur menant le pli courant
     * 
     * @return la propriété contenant le joueur menant le pli courant
     */
    public ObjectProperty<PlayerId> WinningPlayerProperty() {
        return winningPlayerProperty;
    }

    /**
     * Permet de modifier la propriété contenant l'atout
     * 
     * @param trump
     *            l'atout
     */
    public void setTrump(Color trump) {
        trumpProperty.setValue(trump);
    }

    /**
     * Retourne la propriété contenant l'atout
     * 
     * @return la propriété contenant l'atout
     */
    public ObjectProperty<Color> TrumpProperty() {
        return trumpProperty;
    }

    /**
     * Retourne la propriété contenant le pli courant sous forme d'une table
     * associative observable et non modifiable
     * 
     * @return la propriété contenant le pli courant
     */
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trickProperty);
    }

    /**
     * Permet de modifier la propriété contenant le pli courant
     * 
     * @param newTrick
     *            le nouveau pli
     */
    public void setTrick(Trick newTrick) {
        for (int i = 0; i < newTrick.size(); ++i) {
            trickProperty.put(newTrick.player(i), newTrick.card(i));
        }
        for (int i = newTrick.size(); i < PlayerId.COUNT; ++i) {
            trickProperty.put(newTrick.player(i), null);
        }
        if (newTrick.isEmpty())
            winningPlayerProperty.setValue(null);
        else
            winningPlayerProperty.setValue(newTrick.winningPlayer());
    }

}
