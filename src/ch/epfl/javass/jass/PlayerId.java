package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Permet d'identifier chacun des quatre joueurs
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 */
public enum PlayerId {

    PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4;

    /**
     * le nombre des identifiants des joueurs
     */
    public static final int COUNT = 4;
    /**
     * liste immuable de tous les identifiants des joueurs
     */
    public static final List<PlayerId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    /**
     * retourne l'identité de l'équipe à laquelle appartient le joueur auquel on
     * l'applique
     * 
     * @return TEAM_1 pour les joueurs 1 et 3, et TEAM_2 pour les joueurs 2 et 4
     */
    public TeamId team() {
        if (this.equals(PLAYER_1) || this.equals(PLAYER_3))
            return TeamId.TEAM_1;
        else
            return TeamId.TEAM_2;
    }
}