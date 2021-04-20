package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.bits.Bits64;

/**
 * Contient des méthodes statiques permettant de manipuler les scores d'une
 * partie de Jass empaquetés dans un entier de type long
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class PackedScore {
    /**
     * contient le score initial d'une partie, dont les six composantes valent 0
     */
    public static final long INITIAL = 0L;
    private static final int TRICK_START_INDEX = 0;
    private static final int TRICK_SIZE = 4;
    private static final int TURNPOINTS_START_INDEX = 4;
    private static final int TURNPOINTS_SIZE = 9;
    private static final int GAMEPOINTS_START_INDEX = 13;
    private static final int GAMEPOINTS_SIZE = 11;
    private static final int ZEROS_START_INDEX = 24;
    private static final int ZEROS_SIZE = 8;
    private static final int TEAM1_TO_TEAM2_CONSTANTE = 32;
    private static final int MAX_TURNPOINTS = 257;
    private static final int MAX_GAMEPOINTS = 2000;

    private PackedScore() {
    }

    private static boolean hasTheRightbounds(long number, int a, int b) {
        return number >= a && number <= b;
    }

    /**
     * Vérifie que le score empaqueté est valide
     * 
     * @param pkScore
     *            le score empaqueté
     * @return retourne vrai ssi la valeur donnée est un score empaqueté valide
     */
    public static boolean isValid(long pkScore) {
        long teamOneTrick = Bits64.extract(pkScore, TRICK_START_INDEX,
                TRICK_SIZE);
        long teamOneTurnpoints = Bits64.extract(pkScore, TURNPOINTS_START_INDEX,
                TURNPOINTS_SIZE);
        long teamOneGamepoints = Bits64.extract(pkScore, GAMEPOINTS_START_INDEX,
                GAMEPOINTS_SIZE);
        long teamOneZeros = Bits64.extract(pkScore, ZEROS_START_INDEX,
                ZEROS_SIZE);
        long teamTwoTrick = Bits64.extract(pkScore,
                TRICK_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE, TRICK_SIZE);
        long teamTwoTurnpoints = Bits64.extract(pkScore,
                TURNPOINTS_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE,
                TURNPOINTS_SIZE);
        long teamTwoGamepoints = Bits64.extract(pkScore,
                GAMEPOINTS_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE,
                GAMEPOINTS_SIZE);
        long teamTwoZeros = Bits64.extract(pkScore,
                ZEROS_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE, ZEROS_SIZE);

        if (hasTheRightbounds(teamOneTrick, 0, Jass.TRICKS_PER_TURN)
                && hasTheRightbounds(teamOneTurnpoints, 0, MAX_TURNPOINTS)
                && hasTheRightbounds(teamOneGamepoints, 0, MAX_GAMEPOINTS)
                && teamOneZeros == 0
                && hasTheRightbounds(teamTwoTrick, 0, Jass.TRICKS_PER_TURN)
                && hasTheRightbounds(teamTwoTurnpoints, 0, MAX_TURNPOINTS)
                && hasTheRightbounds(teamTwoGamepoints, 0, MAX_GAMEPOINTS)
                && teamTwoZeros == 0)
            return true;
        else
            return false;
    }

    /**
     * Empaquette les six composantes des scores dans un entier de type long
     * 
     * @param turnTricks1
     *            le nombre de plis remportés par l'équipe 1 dans le tour
     *            courant
     * @param turnPoints1
     *            le nombre de points remportés par l'équipe 1 dans le tour
     *            courant
     * @param gamePoints1
     *            le nombre de points remportés par l'équipe 1 dans la partie
     *            courante
     * @param turnTricks2
     *            le nombre de plis remportés par l'équipe 2 dans le tour
     *            courant
     * @param turnPoints2
     *            le nombre de points remportés par l'équipe 2 dans le tour
     *            courant
     * @param gamePoints2
     *            le nombre de points remportés par l'équipe 2 dans la partie
     *            courante
     * @return les six composantes des scores empaquetés dans un entier de type
     *         long
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {
        Preconditions.checkArgument(
                hasTheRightbounds(turnTricks1, 0, Jass.TRICKS_PER_TURN)
                        && hasTheRightbounds(turnPoints1, 0, MAX_TURNPOINTS)
                        && hasTheRightbounds(gamePoints1, 0, MAX_GAMEPOINTS)
                        && hasTheRightbounds(turnTricks2, 0,
                                Jass.TRICKS_PER_TURN)
                        && hasTheRightbounds(turnPoints2, 0, MAX_TURNPOINTS)
                        && hasTheRightbounds(gamePoints2, 0, MAX_GAMEPOINTS));

        long t1TurnTricksAndTurnpoints = Bits64.pack(turnTricks1, TRICK_SIZE,
                turnPoints1, TURNPOINTS_SIZE);
        long t1AllThreeComposants = Bits64.pack(t1TurnTricksAndTurnpoints,
                TRICK_SIZE + TURNPOINTS_SIZE, gamePoints1, GAMEPOINTS_SIZE);
        long scoreTeamOne = Bits64.pack(t1AllThreeComposants,
                TRICK_SIZE + TURNPOINTS_SIZE + GAMEPOINTS_SIZE, 0,
                Integer.SIZE - TRICK_SIZE - TURNPOINTS_SIZE - GAMEPOINTS_SIZE);

        long t2TurnTricksAndTurnpoints = Bits64.pack(turnTricks2, TRICK_SIZE,
                turnPoints2, TURNPOINTS_SIZE);
        long t2AllThreeComposants = Bits64.pack(t2TurnTricksAndTurnpoints,
                TRICK_SIZE + TURNPOINTS_SIZE, gamePoints2, GAMEPOINTS_SIZE);

        long scoreTeamTwo = Bits64.pack(t2AllThreeComposants,
                TRICK_SIZE + TURNPOINTS_SIZE + GAMEPOINTS_SIZE, 0,
                Integer.SIZE - TRICK_SIZE - TURNPOINTS_SIZE - GAMEPOINTS_SIZE);

        return Bits64.pack(scoreTeamOne, Integer.SIZE, scoreTeamTwo,
                Integer.SIZE);

    }

    /**
     * Retourne le nombre de plis remportés par l'équipe donnée dans le tour
     * courant des scores empaquetés donnés
     * 
     * @param pkScore
     *            scores empaquetés donnés
     * @param t
     *            l'équipe donnée
     * @return le nombre de plis remportés par l'équipe donnée dans le tour
     *         courant des scores empaquetés donnés
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        int teamOneturnTricks = (int) Bits64.extract(pkScore, TRICK_START_INDEX,
                TRICK_SIZE);
        int teamTwoTurnTricks = (int) Bits64.extract(pkScore,
                TRICK_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE, TRICK_SIZE);
        if (t.equals(TeamId.TEAM_1))
            return teamOneturnTricks;
        else
            return teamTwoTurnTricks;
    }

    /**
     * Retourne le nombre de points remportés par l'équipe donnée dans le tour
     * courant des scores empaquetés donnés
     * 
     * @param pkScore
     *            scores empaquetés donnés
     * @param t
     *            l'équipe donnée
     * @return le nombre de points remportés par l'équipe donnée dans le tour
     *         courant des scores empaquetés donnés
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        int teamOneTurnPoints = (int) Bits64.extract(pkScore,
                TURNPOINTS_START_INDEX, TURNPOINTS_SIZE);
        int teamTwoTurnPoints = (int) Bits64.extract(pkScore,
                TURNPOINTS_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE,
                TURNPOINTS_SIZE);
        return t.equals(TeamId.TEAM_1) ? teamOneTurnPoints : teamTwoTurnPoints;

    }

    /**
     * Retourne le nombre de points reportés par l'équipe donnée dans les tours
     * précédents (sans inclure le tour courant) des scores empaquetés donnés
     * 
     * @param pkScore
     *            scores empaquetés donnés
     * @param t
     *            l'équipe donnée
     * @return le nombre de points reportés par l'équipe donnée dans les tours
     *         précédents (sans inclure le tour courant) des scores empaquetés
     *         donnés
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        int teamOneGamePoints = (int) Bits64.extract(pkScore,
                GAMEPOINTS_START_INDEX, GAMEPOINTS_SIZE);
        int teamTwoGamePoints = (int) Bits64.extract(pkScore,
                GAMEPOINTS_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE,
                GAMEPOINTS_SIZE);
        return t.equals(TeamId.TEAM_1) ? teamOneGamePoints : teamTwoGamePoints;

    }

    /**
     * Retourne le nombre total de points remportés par l'équipe donnée dans la
     * partie courante des scores empaquetés donnés, c-à-d la somme des points
     * remportés dans les tours précédents et ceux remportés dans le tour
     * courant
     * 
     * @param pkScore
     *            scores empaquetés donnés
     * @param t
     *            l'équipe donnée
     * @return retourne le nombre total de points remportés par l'équipe donnée
     *         dans la partie courante des scores empaquetés donnés
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return turnPoints(pkScore, t) + gamePoints(pkScore, t);
    }

    /**
     * Retourne les scores empaquetés donnés mis à jour pour tenir compte du
     * fait que l'équipe winningTeam a remporté un pli valant trickPoints points
     * 
     * @param pkScore
     *            scores empaquetés donnés
     * @param winningTeam
     *            l'équipe qui a remporté le pli
     * @param trickPoints
     *            points que rapporte le pli
     * @return retourne les scores empaquetés donnés mis à jour
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam,
            int trickPoints) {
        assert (isValid(pkScore));
        int teamOneturnTricks = (int) Bits64.extract(pkScore, TRICK_START_INDEX,
                TRICK_SIZE);
        int teamTwoTurnTricks = (int) Bits64.extract(pkScore,
                TRICK_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE, TRICK_SIZE);
        int teamOneTurnPoints = (int) Bits64.extract(pkScore,
                TURNPOINTS_START_INDEX, TURNPOINTS_SIZE);
        int teamTwoTurnPoints = (int) Bits64.extract(pkScore,
                TURNPOINTS_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE,
                TURNPOINTS_SIZE);
        int teamOneGamePoints = (int) Bits64.extract(pkScore,
                GAMEPOINTS_START_INDEX, GAMEPOINTS_SIZE);
        int teamTwoGamePoints = (int) Bits64.extract(pkScore,
                GAMEPOINTS_START_INDEX + TEAM1_TO_TEAM2_CONSTANTE,
                GAMEPOINTS_SIZE);
        if (winningTeam.equals(TeamId.TEAM_1)) {
            if (turnTricks(pkScore, winningTeam) == 8) {
                return pack(teamOneturnTricks + 1,
                        teamOneTurnPoints + trickPoints
                                + Jass.MATCH_ADDITIONAL_POINTS,
                        teamOneGamePoints, teamTwoTurnTricks, teamTwoTurnPoints,
                        teamTwoGamePoints);
            } else
                return pack(teamOneturnTricks + 1,
                        teamOneTurnPoints + trickPoints, teamOneGamePoints,
                        teamTwoTurnTricks, teamTwoTurnPoints,
                        teamTwoGamePoints);
        } else if (turnTricks(pkScore, winningTeam) == 8) {
            return pack(teamOneturnTricks, teamOneTurnPoints, teamOneGamePoints,
                    teamTwoTurnTricks + 1, teamTwoTurnPoints + trickPoints
                            + Jass.MATCH_ADDITIONAL_POINTS,
                    teamTwoGamePoints);
        } else
            return pack(teamOneturnTricks, teamOneTurnPoints, teamOneGamePoints,
                    teamTwoTurnTricks + 1, teamTwoTurnPoints + trickPoints,
                    teamTwoGamePoints);
    }

    /**
     * Retourne les scores empaquetés donnés mis à jour pour le tour prochain,
     * c-à-d avec les points obtenus par chaque équipe dans le tour courant
     * ajoutés à leur nombre de points remportés lors de la partie, et les deux
     * autres composantes remises à 0
     * 
     * @param pkScore
     *            scores empaquetés donnés
     * @return retourne les scores empaquetés donnés mis à jour pour le tour
     *         prochain
     */
    public static long nextTurn(long pkScore) {
        assert (isValid(pkScore));
        return pack(0, 0, totalPoints(pkScore, TeamId.TEAM_1), 0, 0,
                totalPoints(pkScore, TeamId.TEAM_2));
    }

    /**
     * Retourne la représentation textuelle des scores sous la forme
     * (turnTricks,TurnPoints,GamePoints) pour chaque équipe avec une barre
     * oblique séparant les deux équipes
     * 
     * @param pkScore
     *            scores empaquetés donnés
     * @return retourne la représentation textuelle des scores
     */
    public static String toString(long pkScore) {
        assert (isValid(pkScore));
        StringBuilder b = new StringBuilder("(");
        b.append(turnTricks(pkScore, TeamId.TEAM_1)).append(",")
                .append(turnPoints(pkScore, TeamId.TEAM_1)).append(",")
                .append(gamePoints(pkScore, TeamId.TEAM_1)).append("/")
                .append(turnTricks(pkScore, TeamId.TEAM_2)).append(",")
                .append(turnPoints(pkScore, TeamId.TEAM_2)).append(",")
                .append(gamePoints(pkScore, TeamId.TEAM_2)).append(")");

        return b.toString();

    }

}
