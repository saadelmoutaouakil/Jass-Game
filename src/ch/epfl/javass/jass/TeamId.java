package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * permet d'identifier chacune des deux équipes
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 */
public enum TeamId {
    TEAM_1, TEAM_2;

    /**
     * le nombre des identifiants des équipes
     */
    public static final int COUNT = 2;
    /**
     * liste immuable des identifiants des équipes
     */
    public static final List<TeamId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    /**
     * retourne l'autre équipe que celle à laquelle on l'applique
     * 
     * @return TEAM_2 pour TEAM_1, et inversement
     */
    public TeamId other() {
        if (this.equals(TEAM_1))
            return TEAM_2;
        else
            return TEAM_1;
    }
}