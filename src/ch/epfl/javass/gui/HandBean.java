package ch.epfl.javass.gui;

import java.util.Collections;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Représente le bean JavaFx de la main , et contient 2 propriétés : main du
 * joueur et les cartes jouables
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class HandBean {
    // Propriété contenant la main du joueur
    private ObservableList<Card> handProperty = FXCollections
            .observableArrayList(Collections.nCopies(9, null));
    // Propriété contenant les cartes jouables
    private ObservableSet<Card> playableCardsProperty = FXCollections
            .observableSet();

    /**
     * Retourne la propriété contenant les cartes jouables sous forme d'un
     * ensemble observable non modifiable
     * 
     * @return la propriété contenant les cartes jouables
     */
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCardsProperty);
    }

    /**
     * Permet de modifier la propriété contenant les cartes jouables
     * 
     * @param newPlayableCards
     *            le nouvel ensemble de cartes jouables
     */
    public void setPlayableCards(CardSet newPlayableCards) {

        playableCardsProperty.clear();

        for (int i = 0; i < newPlayableCards.size(); ++i) {
            playableCardsProperty.add(newPlayableCards.get(i));
        }
    }

    /**
     * Retourne la propriété contenant la main du joueur
     * 
     * @return la propriété contenant la main du joueur
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(handProperty);
    }

    /**
     * Permet de modifier la propriété contenant la main du joueur
     * 
     * @param newHand
     *            La nouvelle main du joueur
     */
    public void setHand(CardSet newHand) {
        if (newHand.size() == Jass.HAND_SIZE) {
            for (int i = 0; i < Jass.HAND_SIZE; ++i) {
                handProperty.set(i, newHand.get(i));
            }
        } else {
            for (int i = 0; i < Jass.HAND_SIZE; ++i) {
                if (handProperty.get(i) != null
                        && !newHand.contains(handProperty.get(i))) {
                    handProperty.set(i, null);
                }
            }
        }
    }

}
