package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;

    public PrintingPlayer(Player underlyingPlayer) {
        this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        System.out.print("C'est à moi de jouer... Je joue : ");
        Card c = underlyingPlayer.cardToPlay(state, hand);
        System.out.println(c);
        return c;
    }

    // … autres méthodes de Player (à écrire)

    @Override

    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        System.out.println("Les joueurs sont : ");
        for (PlayerId key : playerNames.keySet()) {
            if (key == ownId)
                System.out.println(playerNames.get(key) + "(moi)");
            else
                System.out.println(playerNames.get(key));
        }
    }

    @Override

    public void updateHand(CardSet newHand) {
        System.out.println("Ma nouvelle main : " + newHand);
    }

    @Override

    public void setTrump(Color trump) {
        System.out.println("Atout : " + trump);
    }

    @Override
    public void updateScore(Score score) {
        System.out.println("Scores : " + score);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        // if(newTrick.index()==0)
        // System.out.println("Pli " + newTrick.index()+" ,commencé par "+
        // newTrick.player(0) + " : " + newTrick);
        // else {
        // System.out.println("Pli " + newTrick.index()+" ,commencé par "+
        // newTrick.winningPlayer() + " : " + newTrick);
        // }
        System.out.println(newTrick);

    }

    @Override

    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("L'équipe gagnante est " + winningTeam);
    }
}
