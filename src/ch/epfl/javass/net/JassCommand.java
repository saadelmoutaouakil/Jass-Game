package ch.epfl.javass.net;

/**
 * Enumère les 7 types de messages échangés par le client et le serveur
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 */
public enum JassCommand {
    // Représente la méthode setPlayers
    PLRS,
    // Représente la méthode setTrump
    TRMP,
    // Représente la méthode updateHand
    HAND,
    // Représente la méthode updateTrick
    TRCK,
    // Représente la méthode cardToPlay
    CARD,
    // Représente la méthode updateScore
    SCOR,
    // Représente la méthode setWinningTeam
    WINR;

}
