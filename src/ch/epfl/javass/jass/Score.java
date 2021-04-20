package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;

/**
 * représente les scores d'une partie de Jass
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class Score {

    /**
     * contient des scores dont les six composantes valent 0
     */
    public static final Score INITIAL = new Score(0L);
    private final long packedScore;

    private Score(long packedScore) {
        this.packedScore = packedScore;
    }

    /**
     * retourne les scores dont packed est la version empaquetée
     * 
     * @param packed
     *            scores empaquetés
     * @throws IllegalArgumentException
     *             si packed ne représente pas des scores empaquetés valides
     *             d'après la méthode isValid de PackedScore
     * @return les scores dont packed est la version empaquetée
     */
    public static Score ofPacked(long packed) {
        Preconditions.checkArgument(PackedScore.isValid(packed));
        return new Score(packed);
    }

    /**
     * retourne la version empaquetée des scores
     * 
     * @return la version empaquetée des scores
     */
    public long packed() {
        return packedScore;
    }

    /**
     * retourne le nombre de plis remportés par l'équipe donnée dans le tour
     * courant du récepteur
     * 
     * @param t
     *            l'équipe donnée
     * @return le nombre de plis remportés par l'équipe donnée dans le tour
     *         courant du récepteur
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(packedScore, t);
    }

    /**
     * retourne le nombre de points remportés par l'équipe donnée dans le tour
     * courant du récepteur
     * 
     * @param t
     *            l'équipe donnée
     * @return le nombre de points remportés par l'équipe donnée dans le tour
     *         courant du récepteur
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(packedScore, t);
    }

    /**
     * retourne le nombre de points reportés par l'équipe donnée dans les tours
     * précédents (sans inclure le tour courant) du récepteur
     * 
     * @param t
     *            l'équipe donnée
     * @return le nombre de points reportés par l'équipe donnée dans les tours
     *         précédents du récepteur
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(packedScore, t);
    }

    /**
     * retourne le nombre total de points remportés par l'équipe donnée dans la
     * partie courante du récepteur
     * 
     * @param t
     *            l'équipe donnée
     * @return le nombre total de points remportés par l'équipe donnée dans la
     *         partie courante du récepteur
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(packedScore, t);
    }

    /**
     * retourne les scores mis à jour pour tenir compte du fait que l'équipe
     * winningTeam a remporté un pli valant trickPoints points
     * 
     * @param winningTeam
     *            l'équipe qui a remporté le pli
     * @param trickPoints
     *            points que rapporte le pli
     * @throws IllegalArgumentException
     *             si le nombre de points donné est inférieur à 0
     * @return les scores mis à jour
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        Preconditions.checkArgument(trickPoints >= 0);
        return ofPacked(PackedScore.withAdditionalTrick(packedScore,
                winningTeam, trickPoints));
    }

    /**
     * retourne les scores empaquetés donnés mis à jour pour le tour prochain
     * 
     * @return les scores empaquetés donnés mis à jour pour le tour prochain
     */
    public Score nextTurn() {
        return ofPacked(PackedScore.nextTurn(packedScore));
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return PackedScore.toString(packedScore);
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Long.hashCode(packedScore);
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object thatO) {
        if (thatO != null && thatO.getClass() == Score.class) {
            return (((Score) thatO).packedScore == this.packedScore);

        } else
            return false;
    }
}