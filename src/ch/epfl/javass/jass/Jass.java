package ch.epfl.javass.jass;

/**
 * Contient un certain nombre de constantes entières de type int, liées au jeu
 * de Jass
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public interface Jass {

    /**
     * Le nombre de cartes dans une main au début d'un tour
     */
    public static final int HAND_SIZE = 9;
    /**
     * Le nombre de plis dans un tour de jeu
     */
    public static final int TRICKS_PER_TURN = 9;
    /**
     * Le nombre de points nécessaire à une victoire
     */
    public static final int WINNING_POINTS = 1000;
    /**
     * Le nombre de points additionnels obtenus par une équipe remportant la
     * totalité des plis d'un tour
     */
    public static final int MATCH_ADDITIONAL_POINTS = 100;
    /**
     * Le nombre de points additionnels obtenu par l'équipe remportant le
     * dernier pli
     */
    public static final int LAST_TRICK_ADDITIONAL_POINTS = 5;

}
