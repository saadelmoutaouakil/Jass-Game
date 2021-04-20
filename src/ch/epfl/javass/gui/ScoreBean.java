package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Représente le bean JavaFX des Scores et doté de propriétés contenant les
 * points du tour ,les points de la partie et le total des points (en 2
 * versions, chacune pour une équipe) et une propriété contenant l'identité de
 * l'équipe ayant gagné la partie
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class ScoreBean {
    /**
     * Propriété contenant les points du tour de l'équipe 1
     */
    private IntegerProperty turnPoints_TEAM1 = new SimpleIntegerProperty();
    /**
     * Propriété contenant les points du tour de l'équipe 2
     */
    private IntegerProperty turnPoints_TEAM2 = new SimpleIntegerProperty();
    /**
     * Propriété contenant les points de la partie de l'équipe 1
     */
    private IntegerProperty gamePoints_TEAM1 = new SimpleIntegerProperty();
    /**
     * Propriété contenant les points de la partie de l'équipe 2
     */
    private IntegerProperty gamePoints_TEAM2 = new SimpleIntegerProperty();
    /**
     * Propriété contenant le total des points de l'équipe 1
     */
    private IntegerProperty totalPoints_TEAM1 = new SimpleIntegerProperty();
    /**
     * Propriété contenant le total des points de l'équipe 2
     */
    private IntegerProperty totalPoints_TEAM2 = new SimpleIntegerProperty();
    /**
     * Propriété contenant l'identité de l'équipe ayant gagné la partie
     */
    private ObjectProperty<TeamId> winningTeam = new SimpleObjectProperty<>();

    /**
     * Retourne la propriété contenant les points du tour de l'équipe team
     * 
     * @param team
     *            l'identité de l'équipe
     * @return la propriété contenant les points du tour de l'équipe team
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        if (team == TeamId.TEAM_1)
            return turnPoints_TEAM1;
        else
            return turnPoints_TEAM2;
    }

    /**
     * Permet de modifier la propriété contenant les points du tour de l'équipe
     * team
     * 
     * @param team
     *            l'identité de l'équipe
     * @param newTurnPoints
     *            les nouveaux points du tour
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team == TeamId.TEAM_1)
            turnPoints_TEAM1.set(newTurnPoints);
        else
            turnPoints_TEAM2.set(newTurnPoints);
    }

    /**
     * Retourne la propriété contenant les points de la partie de l'équipe team
     * 
     * @param team
     *            l'identité de l'équipe
     * @return la propriété contenant les points de la partie de l'équipe team
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        if (team == TeamId.TEAM_1)
            return gamePoints_TEAM1;
        else
            return gamePoints_TEAM2;
    }

    /**
     * Permet de modifier la propriété contenant les points de la partie de
     * l'équipe team
     * 
     * @param team
     *            l'identité de l'équipe
     * @param newGamePoints
     *            les nouveaux points de la partie
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team == TeamId.TEAM_1)
            gamePoints_TEAM1.set(newGamePoints);
        else
            gamePoints_TEAM2.set(newGamePoints);
    }

    /**
     * Retourne la propriété contenant le total des points l'équipe team
     * 
     * @param team
     *            l'identité de l'équipe
     * @return la propriété contenant les points du tour de l'équipe team
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        if (team == TeamId.TEAM_1)
            return totalPoints_TEAM1;
        else
            return totalPoints_TEAM2;
    }

    /**
     * Permet de modifier la propriété contenant le total des points de l'équipe
     * team
     * 
     * @param team
     *            l'identité de l'équipe
     * @param newTotalPoints
     *            le nouveau total des points
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team == TeamId.TEAM_1)
            totalPoints_TEAM1.set(newTotalPoints);
        else
            totalPoints_TEAM2.set(newTotalPoints);
    }

    /**
     * Retourne la propriété contenant l'identité de l'équipe ayant gagné la
     * partie
     * 
     * @return la propriété contenant l'identité de l'équipe ayant gagné la
     *         partie
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    /**
     * Permet de modifier la propriété contenant l'identité de l'équipe ayant
     * gagné la partie
     * 
     * @param winningTeam
     *            l'identité de l'équipe ayant gagné la partie
     */
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeam.setValue(winningTeam);
    }
}
