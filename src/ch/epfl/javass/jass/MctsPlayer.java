package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

import ch.epfl.javass.Preconditions;

/**
 * Représente un joueur simulé au moyen de l'algorithme MCTS
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class MctsPlayer implements Player {

    private PlayerId ownId;
    private SplittableRandom rng;
    private int iterations;
    private static final int CONSTANTE = 40;

    /**
     * Construit un joueur simulé avec l'identité, la graine aléatoire et le
     * nombre d'itérations donnés
     * 
     * @param ownId
     *            l'identité du joueur
     * @param rngSeed
     *            la graine
     * @param iterations
     *            le nombre d'itérations
     * 
     * @throws IllegalArgumentException
     *             si le nombre d'itérations est inférieur à 9
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        Preconditions.checkArgument(iterations >= 9);
        this.ownId = ownId;
        this.rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }

    /*
     * @see ch.epfl.javass.jass.Player#cardToPlay(ch.epfl.javass.jass.TurnState,
     * ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // S'il ne reste qu'une carte dans la main, la jouer
        if (hand.size() == 1)
            return hand.get(0);
        // Initialiser le compteur d'itération
        int iterationCounter = 0;

        // Déterminer les cartes jouables
        CardSet playable = nodePlayableCards(state, hand, this.ownId);

        // Créer un tableau avec comme taille le nombre de cartes jouables afin
        // d'avoir plusieurs branches
        CardSet[] hands = new CardSet[playable.size()];

        // Créer le node dont les fils sont les cartes jouables
        Node initial = new Node(state, new Node[playable.size()], playable, 0,
                0, null, ownId.team().other());

        // Créer les noeuds correspondants aux cartes jouables avec comme état
        // l'état donné plus cette carte jouée
        for (int i = 0; i < playable.size(); ++i) {
            TurnState newState = state
                    .withNewCardPlayedAndTrickCollected(playable.get(i));
            hands[i] = hand.remove(playable.get(i));
            initial.childNodes[i] = new Node(newState,
                    new Node[(nodePlayableCards(newState, hands[i], this.ownId))
                            .size()],
                    nodePlayableCards(newState, hands[i], this.ownId), 0, 0,
                    initial, ownId.team());
            ++iterationCounter;
        }

        // Simuler un tour pour chaque carte jouable afin de trouver le meilleur
        // fils
        for (int i = 0; i < initial.childNodes.length; ++i) {
            initial.childNodes[i].totalPoints_S = scoreTurnSimulated(
                    initial.childNodes[i], hands[i]);
            ++initial.childNodes[i].turns_N;
            ++initial.turns_N;
            initial.totalPoints_S += 157 - initial.childNodes[i].totalPoints_S;
        }

        initial.totalPoints_S = initial.totalPoints_S / initial.turns_N;
        // Origin représente le noeud à partir duquel on commence la simulation
        Node origin = initial;
        boolean newIteration = true;
        int indexBestChildOfInitial = initial.bestChildNodeIndex(CONSTANTE);
        while (iterationCounter < iterations) {
            // A chaque nouvelle itération, on remet l'origin à initial, et on
            // retrouve le meilleur candidat à partir duquel on
            // développe les branches
            if (newIteration) {
                origin = initial;
                indexBestChildOfInitial = initial.bestChildNodeIndex(CONSTANTE);
            }
            int indexBestChildOfOrigin = origin.bestChildNodeIndex(CONSTANTE);

            // Si le tableau du meilleur fils est plein, on doit trouver son
            // meilleur fils pour développer sa branche en l'assignant à origin
            if (isTabFull(
                    origin.childNodes[indexBestChildOfOrigin].childNodes)) {
                origin = origin.childNodes[origin
                        .bestChildNodeIndex(CONSTANTE)];
                newIteration = false;

            } else {
                // On ajoute un nouveau noeud si possible, et on simule le tour
                // lui correspondant
                List<Node> generated = origin.addNode(
                        origin.childNodes[indexBestChildOfOrigin],
                        hands[indexBestChildOfInitial], ownId);
                int childScore = scoreTurnSimulated(generated.get(0),
                        hands[indexBestChildOfInitial]);
                // On met à jour les scores des différents noeuds de la branche
                // développée
                generated.get(0).totalPoints_S = childScore;
                ++generated.get(0).turns_N;
                for (int i = 1; i < generated.size(); ++i) {
                    generated.get(i).totalPoints_S *= generated.get(i).turns_N;
                    if (generated.get(i).ownTeam == generated.get(0).ownTeam) {
                        generated.get(i).totalPoints_S += childScore;
                    } else {
                        generated.get(i).totalPoints_S += 157 - childScore;
                    }
                    ++generated.get(i).turns_N;
                    generated.get(i).totalPoints_S /= generated.get(i).turns_N;
                }

                ++iterationCounter;
                newIteration = true;
            }
        }

        int indexBest = initial.bestChildNodeIndex(0);
        return playable.get(indexBest);

    }

    private static boolean isTabFull(Node[] tab) {
        for (int i = 0; i < tab.length; ++i) {
            if (tab[i] == null)
                return false;
        }
        return true;
    }

    private static CardSet nodePlayableCards(TurnState turnState, CardSet hand,
            PlayerId ownId) {
        if (turnState.nextPlayer() == ownId) {

            return turnState.trick().playableCards(
                    hand.intersection(turnState.unplayedCards()));
        } else {
            CardSet otherCards = turnState.unplayedCards().difference(hand);

            return turnState.trick().playableCards(otherCards);
        }
    }

    private int scoreTurnSimulated(Node node, CardSet hand) {
        TurnState turnStateCopy = TurnState.ofPackedComponents(
                node.turnState.packedScore(),
                node.turnState.packedUnplayedCards(),
                node.turnState.packedTrick());
        CardSet newHand = CardSet.ofPacked(hand.packed());

        while (!turnStateCopy.isTerminal()) {

            CardSet playable = nodePlayableCards(turnStateCopy, newHand,
                    this.ownId);
            Card played = playable.get(rng.nextInt(playable.size()));
            turnStateCopy = turnStateCopy
                    .withNewCardPlayedAndTrickCollected(played);
            newHand = newHand.remove(played);
        }
        return turnStateCopy.score().totalPoints(node.ownTeam);
    }

    private static class Node {
        private TurnState turnState;
        private Node[] childNodes;
        private CardSet unexistingNodesCards;
        private int totalPoints_S;
        private int turns_N;
        private int childCounter = 0;
        private Node parentNode;
        private TeamId ownTeam;

        private Node(TurnState turnState, Node[] childNodes,
                CardSet unexistingNodesCards, int total, int turns,
                Node parentNode, TeamId ownTeam) {
            this.turnState = turnState;
            this.childNodes = childNodes;
            this.unexistingNodesCards = unexistingNodesCards;
            this.totalPoints_S = total;
            this.turns_N = turns;
            this.parentNode = parentNode;
            this.ownTeam = ownTeam;
        }

        private int bestChildNodeIndex(int c) {
            int bestChildIndex = 0;
            double[] vScores = new double[childNodes.length];
            for (int i = 0; i < childNodes.length; ++i) {
                double v;
                if (childNodes[i].turns_N > 0) {
                    v = childNodes[i].totalPoints_S
                            / (double) childNodes[i].turns_N
                            + c * Math.sqrt((2 * Math.log(this.turns_N))
                                    / childNodes[i].turns_N);
                } else {
                    v = Double.POSITIVE_INFINITY;
                }
                vScores[i] = v;
            }
            double temp = vScores[0];
            for (int i = 0; i < vScores.length; ++i) {
                if (vScores[i] > temp) {
                    bestChildIndex = i;
                    temp = vScores[i];
                }
            }
            return bestChildIndex;
        }

        private List<Node> addNode(Node BestChild, CardSet hand,
                PlayerId ownId) {

            // Si le tour n'est pas fini, on ajoute une carte au pli en le
            // ramassant si nécessaire
            if (!BestChild.turnState.isTerminal()) {

                TurnState newState = BestChild.turnState
                        .withNewCardPlayedAndTrickCollected(
                                BestChild.unexistingNodesCards.get(0));
                // Si après avoir joué la carte l'état ne devient pas invalide,
                // on crée le noeud au bon endroit et on met à jour les cartes
                // non jouées
                if (!newState.isTerminal()) {
                    BestChild.childNodes[BestChild.childCounter] = new Node(
                            newState,
                            new Node[nodePlayableCards(newState, hand, ownId)
                                    .size()],
                            nodePlayableCards(newState, hand, ownId).remove(
                                    BestChild.unexistingNodesCards.get(0)),
                            0, 0, BestChild, BestChild.ownTeam.other());

                    Node temp = BestChild.childNodes[BestChild.childCounter];
                    ++BestChild.childCounter;
                    BestChild.unexistingNodesCards = BestChild.unexistingNodesCards
                            .remove(BestChild.unexistingNodesCards.get(0));

                    List<Node> hierarchy = new ArrayList<Node>();
                    while (temp != null) {
                        hierarchy.add(temp);
                        temp = temp.parentNode;
                    }
                    return hierarchy;
                }

            }
            // Dans tous les cas, retourne la hiérarchie des noeuds constituant
            // la branche
            Node temp = BestChild;
            List<Node> hierarchy = new ArrayList<Node>();
            while (temp != null) {
                hierarchy.add(temp);
                temp = temp.parentNode;
            }
            return hierarchy;

        }

    }

}
