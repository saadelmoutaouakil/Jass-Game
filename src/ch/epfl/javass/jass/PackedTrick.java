package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Card.Color;

/**
 * Possède des méthodes permettant de manipuler des plis empaquetés dans des
 * valeurs de type int
 * 
 * @author Mohamed Saad Eddine El Moutaouakil (284843)
 * @author Taha Zakariya (288526)
 *
 */
public final class PackedTrick {

    /**
     * Représente un pli empaqueté invalide
     */
    public static final int INVALID = -1;

    private static final int CARDS_BITS = 6;
    private static final int CARDS_OF_SAME_COLOR = 16;
    private final static int INDEX_START = 24;
    private final static int INDEX_BITS = 4;
    private final static int TRUMP_START = 30;
    private final static int PLAYER_START = 28;
    private final static int TRUMP_PLAYER_BITS = 2;

    private PackedTrick() {
    }

    /**
     * Retourne vrai ssi l'entier donné représente un pli empaqueté valide,
     * c-à-d si l'index est compris entre 0 et 8 (inclus) et si les éventuelles
     * cartes invalides sont groupées dans les index supérieurs
     * 
     * @param pkTrick
     *            pli à tester donné
     * 
     * @return vrai si et seulement si l'entier donné représente un pli
     *         empaqueté valide
     */
    public static boolean isValid(int pkTrick) {
        if (Bits32.extract(pkTrick, INDEX_START, INDEX_BITS) <= 8
                && validCardsRightIndex(pkTrick)) {
            return true;
        } else
            return false;
    }

    /*
     * Vérifie que les cartes sont valides, ou si certaines ne le sont pas, la
     * méthode s'assurent qu'elles sont vers la fin
     */
    private static boolean validCardsRightIndex(int pkTrick) {
        int firstCard = Bits32.extract(pkTrick, 0, CARDS_BITS);
        int secondCard = Bits32.extract(pkTrick, 6, CARDS_BITS);
        int thirdCard = Bits32.extract(pkTrick, 12, CARDS_BITS);
        int forthCard = Bits32.extract(pkTrick, 18, CARDS_BITS);
        boolean firstCase = PackedCard.isValid(firstCard)
                && PackedCard.isValid(secondCard)
                && PackedCard.isValid(thirdCard)
                && PackedCard.isValid(forthCard);

        boolean secondCase = (forthCard == PackedCard.INVALID)
                && (PackedCard.isValid(firstCard))
                && PackedCard.isValid(secondCard)
                && PackedCard.isValid(thirdCard);
        boolean thirdCase = (thirdCard == PackedCard.INVALID)
                && (Bits32.extract(pkTrick, 18,
                        CARDS_BITS) == PackedCard.INVALID)
                && (PackedCard.isValid(firstCard))
                && PackedCard.isValid(Bits32.extract(pkTrick, 6, CARDS_BITS));
        boolean forthCase = (secondCard == PackedCard.INVALID)
                && (thirdCard == PackedCard.INVALID)
                && (forthCard == PackedCard.INVALID)
                && PackedCard.isValid(firstCard);
        boolean fifthCase = (firstCard == PackedCard.INVALID)
                && (secondCard == PackedCard.INVALID)
                && (thirdCard == PackedCard.INVALID)
                && (forthCard == PackedCard.INVALID);
        return firstCase || secondCase || thirdCase || forthCase || fifthCase;
    }

    /**
     * retourne le pli empaqueté vide — c-à-d sans aucune carte — d'index 0 avec
     * l'atout et le premier joueur donnés
     * 
     * @param trump
     *            atout du pli donné
     * @param firstPlayer
     *            le premier joueur du pli
     * @return Retourne le pli empaqueté vide 
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        int firstEmptyTrick = Bits32.pack(PackedCard.INVALID, CARDS_BITS,
                PackedCard.INVALID, CARDS_BITS, PackedCard.INVALID, CARDS_BITS,
                PackedCard.INVALID, CARDS_BITS, 0, INDEX_BITS,
                firstPlayer.ordinal(), TRUMP_PLAYER_BITS, trump.ordinal(),
                TRUMP_PLAYER_BITS);
        return firstEmptyTrick;
    }

    /**
     * qui retourne le pli empaqueté vide suivant celui donné (supposé plein),
     * c-à-d le pli vide dont l'atout est identique à celui du pli donné,
     * l'index est le successeur de celui du pli donné et le premier joueur est
     * le vainqueur du pli donné ; si le pli donné est le dernier du tour, alors
     * le pli invalide (INVALID ci-dessus) est retourné
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne qui retourne le pli empaqueté vide suivant celui donné
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);
        if (Bits32.extract(pkTrick, INDEX_START, INDEX_BITS) == 8) {
            return PackedTrick.INVALID;
        } else {
            int nextEmptyTrick = Bits32.pack(PackedCard.INVALID, CARDS_BITS,
                    PackedCard.INVALID, CARDS_BITS, PackedCard.INVALID,
                    CARDS_BITS, PackedCard.INVALID, CARDS_BITS,
                    Bits32.extract(pkTrick, INDEX_START, INDEX_BITS) + 1,
                    INDEX_BITS, winningPlayer(pkTrick).ordinal(),
                    TRUMP_PLAYER_BITS,
                    Bits32.extract(pkTrick, TRUMP_START, TRUMP_PLAYER_BITS),
                    TRUMP_PLAYER_BITS);
            return nextEmptyTrick;
        }
    }

    /**
     * Retourne vrai ssi le pli est le dernier du tour, c-à-d si son index vaut
     * 8
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne vrai ssi le pli est le dernier du tour
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        int trickIndex = Bits32.extract(pkTrick, INDEX_START, INDEX_BITS);
        return trickIndex == 8;

    }

    /**
     * Retourne vrai ssi le pli est vide, c-à-d s'il ne contient aucune carte
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne vrai ssi le pli est vide
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        boolean isEmptyCond = Bits32.extract(pkTrick, 0,
                CARDS_BITS) == PackedCard.INVALID
                && Bits32.extract(pkTrick, 6, CARDS_BITS) == PackedCard.INVALID
                && Bits32.extract(pkTrick, 12, CARDS_BITS) == PackedCard.INVALID
                && Bits32.extract(pkTrick, 18,
                        CARDS_BITS) == PackedCard.INVALID;

        return isEmptyCond;
    }

    /**
     * Retourne vrai ssi le pli est plein, c-à-d s'il contient 4 cartes
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne vrai ssi le pli est plein
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        boolean isFullCond = PackedCard
                .isValid(Bits32.extract(pkTrick, 0, CARDS_BITS))
                && PackedCard.isValid(Bits32.extract(pkTrick, 6, CARDS_BITS))
                && PackedCard.isValid(Bits32.extract(pkTrick, 12, CARDS_BITS))
                && PackedCard.isValid(Bits32.extract(pkTrick, 18, CARDS_BITS));
        return isFullCond;

    }

    /**
     * Retourne la taille du pli, c-à-d le nombre de cartes qu'il contient
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne la taille du pli
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);
        int size = 0;
        int cardIndexCounter = 0;
        while (Bits32.extract(pkTrick, cardIndexCounter,
                CARDS_BITS) != PackedCard.INVALID && cardIndexCounter < 19) {
            cardIndexCounter = cardIndexCounter + 6;
            ++size;
        }

        return size;
    }

    /**
     * Retourne l'atout du pli
     * 
     * @param pkTrick
     * @return Retourne l'atout du pli
     */
    public static Color trump(int pkTrick) {
        assert isValid(pkTrick);
        Color trump = Card.Color.ALL
                .get(Bits32.extract(pkTrick, TRUMP_START, TRUMP_PLAYER_BITS));
        return trump;
    }

    /**
     * Retourne le joueur d'index donné dans le pli, le joueur d'index 0 étant
     * le premier du pli
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @param index
     *            index du joueur voulu, le joueur d'index 0 étant le premier du
     *            pli
     * @return Retourne le joueur d'index donné dans le pli
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick) && index >= 0 && index < 4;
        PlayerId player = PlayerId.ALL.get((index
                + Bits32.extract(pkTrick, PLAYER_START, TRUMP_PLAYER_BITS))
                % 4);
        return player;
    }

    /**
     * Retourne l'index du pli
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne l'index du pli
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);
        int index = Bits32.extract(pkTrick, INDEX_START, INDEX_BITS);
        return index;
    }

    /**
     * Retourne la version empaquetée de la carte du pli à l'index donné
     * (supposée avoir été posée)
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @param index
     *            index de la carte voulue
     * @return Retourne la version empaquetée de la carte du pli à l'index donné
     */
    public static int card(int pkTrick, int index) {
        assert isValid(pkTrick) && index >= 0 && index < 4;
        int card = Bits32.extract(pkTrick, index * CARDS_BITS, CARDS_BITS);
        return card;
    }

    /**
     * Retourne un pli identique à celui donné (supposé non plein), mais à
     * laquelle la carte donnée a été ajoutée
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @param pkCard
     *            la carte à ajouter au pli
     * @return Retourne un pli identique à celui donné (supposé non plein), mais
     *         à laquelle la carte donnée a été ajoutée
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick) && PackedCard.isValid(pkCard);
        int index = size(pkTrick);
        int trickWithAddedCard = pkTrick
                & (~Bits32.mask(index * CARDS_BITS, CARDS_BITS)
                        | pkCard << (index * 6));
        return trickWithAddedCard;
    }

    /**
     * Retourne la couleur de base du pli, c-à-d la couleur de sa première carte
     * (supposée avoir été jouée)
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne la couleur de base du pli
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        Color baseColor = Card.Color.ALL.get(Bits32.extract(pkTrick, 4, 2));
        return baseColor;
    }

    /**
     * Retourne le sous-ensemble (empaqueté) des cartes de la main pkHand qui
     * peuvent être jouées comme prochaine carte du pli pkTrick (supposé non
     * plein)
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @param pkHand
     *            version empaquetée de la main du joueur en question
     * @return l'ensemble des cartes jouables sous forme empaquetée
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick) && PackedCardSet.isValid(pkHand);
        Card.Color baseColor = baseColor(pkTrick);
        // Pli vide : le joueur peut joueur n'importe quelle carte
        if (isEmpty(pkTrick))
            return pkHand;

        // Si le pli n'est pas vide et que la première carte n'est pas un atout
        if (baseColor != trump(pkTrick)) {

            // Premier cas : si quelqu'un a déja coupé
            if (hasCut(pkTrick)) {

                // Si le joueur a une meilleure carte d'atout
                if (haveBetterTrump(pkTrick, pkHand)) {

                    // S'il ne peut pas suivre il peut soit sous-couper, soit
                    // jouer une carte quelconque
                    if (cantFollow(pkTrick, pkHand))
                        return underCut(pkTrick, pkHand)
                                | anyCard(pkHand, trump(pkTrick).ordinal());

                    // Autrement il peut sous-couper ou suivre
                    return underCut(pkTrick, pkHand)
                            | follow(pkHand, baseColor);

                    // S'il n'a pas de meilleure carte d'atout, on vérifie
                    // d'abord qu'il n'a pas d'autres cartes autre que des
                    // atouts
                } else if (OtherThanTrumpExists(pkTrick, pkHand)) {
                    // Dans ce cas, s'il peut suivre il fait, sinon il joue une
                    // carte quelconque hors atout
                    if (cantFollow(pkTrick, pkHand))
                        return anyCard(pkHand, trump(pkTrick).ordinal());
                    else
                        return follow(pkHand, baseColor);
                } else
                    // S'il n'a pas de meilleure carte d'atout, mais qu'il n'a
                    // pas non plus une carte autre que des cartes d'atout, il
                    // peut quand meme sous-couper
                    return cut(pkHand, trump(pkTrick).ordinal());
            }
            // Deuxième cas : si aucun joueur n'a coupé
            // Si le joueur ne peut pas suivre il peut jouer une carte
            // quelconque ou couper
            else if (cantFollow(pkTrick, pkHand))
                return anyCard(pkHand, trump(pkTrick).ordinal())
                        | cut(pkHand, trump(pkTrick).ordinal());
            else
                // Autrement le joueur peut suivre ou couper
                return follow(pkHand, baseColor)
                        | cut(pkHand, trump(pkTrick).ordinal());

            // Dans le cas ou la première carte est un atout
            // Si le joueur n'a que le valet ou s'il n'a pas de carte d'atout,
            // il peut jouer n'importe quelle carte
        } else if (noTrumpOrOnlyJack(pkTrick, pkHand))
            return pkHand;
        // Autrement il peut suivre en jouant une carte d'atout

        else
            return follow(pkHand, baseColor);
    }

    /**
     * Retourne la valeur du pli, en tenant compte des « 5 de der »
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne la valeur du pli,
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);
        int nbCards = 0;
        int points = 0;
        while (nbCards < size(pkTrick)) {
            points += PackedCard.points(trump(pkTrick), card(pkTrick, nbCards));
            ++nbCards;
        }
        int pointsGained = isLast(pkTrick)
                ? points + Jass.LAST_TRICK_ADDITIONAL_POINTS
                : points;
        return pointsGained;
    }

    /**
     * Retourne l'identité du joueur menant le pli (supposé non vide)
     * 
     * @param pkTrick
     *            entier représentant le pli donné
     * @return Retourne l'identité du joueur menant le pli
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);

        int counter = 0;
        int index = 0;
        int winningCard = Bits32.extract(pkTrick, 0, CARDS_BITS);
        while (counter < size(pkTrick)) {
            if (PackedCard.isBetter(trump(pkTrick), card(pkTrick, counter),
                    winningCard)) {
                winningCard = card(pkTrick, counter);
                index = counter;

            }
            ++counter;
        }

        return player(pkTrick, index);

    }

    /*
     * @see java.lang.Enum#toString()
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);
        StringBuilder trick = new StringBuilder();
        for (int i = 0; i < size(pkTrick); ++i) {
            trick.append(PackedCard.toString(card(pkTrick, i))).append(", ");
        }
        return trick.toString();
    }

    /*
     * Retourne la version empaquetée représentant l'ensemble de cartes avec
     * lesquelles le joueur peut suivre, c-à-d les cartes de la meme couleur
     */
    private static long follow(long pkHand, Card.Color color) {
        return pkHand & Bits64.mask(color.ordinal() * CARDS_OF_SAME_COLOR,
                CARDS_OF_SAME_COLOR);
    }

    /*
     * Retourne la version empaquetée représentant l'ensemble de cartes avec
     * lesquelles le joueur peut couper en jouant un trump
     */
    private static long cut(long pkHand, int trump) {
        return pkHand
                & Bits64.mask(trump * CARDS_OF_SAME_COLOR, CARDS_OF_SAME_COLOR);
    }

    /*
     * Retourne la version empaquetée représentant l'ensemble de cartes que le
     * joueur peut jouer d'une manière quelconque (s'il n'a pas de carte pour
     * suivre par exemple)
     */
    private static long anyCard(long pkHand, int trump) {
        return pkHand & (~cut(pkHand, trump));
    }

    /*
     * Retourne la version empaquetée représentant l'ensemble de cartes avec
     * lesquelles le joueur peut sous-couper, si un joueur avant lui a déja
     * coupé
     */
    private static long underCut(int pkTrick, long pkHand) {
        int bigCard = biggestTrump(pkTrick);
        return pkHand & PackedCardSet.trumpAbove(bigCard);

    }

    /*
     * Retourne vrai si un joueur a déja coupé, faux sinon
     */
    private static boolean hasCut(int pkTrick) {
        boolean cut = false;
        for (int i = 1; i < 4 && card(pkTrick, i) != PackedCard.INVALID; ++i) {
            if (PackedCard.color(card(pkTrick, i)) == trump(pkTrick))
                cut = true;
        }
        return cut;
    }

    /*
     * Retourne une version empaquetée de la meilleur carte d'atout posée(il y
     * en a forcément une minimum vu la condition dans playableCard)
     */
    private static int biggestTrump(int pkTrick) {
        Card.Color trump = trump(pkTrick);
        int bestTrumpCard = Bits32.extract(pkTrick, 0, CARDS_BITS);
        for (int i = 0; i < 4 && card(pkTrick, i) != PackedCard.INVALID; ++i) {
            if (PackedCard.isBetter(trump, card(pkTrick, i), bestTrumpCard))
                bestTrumpCard = card(pkTrick, i);
        }
        return bestTrumpCard;
    }

    /*
     * Retourne vrai si le joueur a une meilleure carte d'atout, faux sinon
     */
    private static boolean haveBetterTrump(int pkTrick, long pkHand) {
        int bestTrumpCard = biggestTrump(pkTrick);
        long trumpAbove = PackedCardSet.trumpAbove(bestTrumpCard);
        return (pkHand & trumpAbove) != 0;
    }

    /*
     * Retourne vrai si le joueur peut jouer une carte quelconque, c-à-d s'il ne
     * peut pas suivre, faux sinon
     */
    private static boolean cantFollow(int pkTrick, long pkHand) {
        return (pkHand & Bits64.mask(
                baseColor(pkTrick).ordinal() * CARDS_OF_SAME_COLOR,
                CARDS_OF_SAME_COLOR)) == 0;
    }

    /*
     * Retourne vrai si le joueur n'a pas de carte d'atout ou s'il a seulement
     * le valet en atout, faux sinon
     */
    private static boolean noTrumpOrOnlyJack(int pkTrick, long pkHand) {
        boolean noTrump = (pkHand
                & Bits64.mask(trump(pkTrick).ordinal() * CARDS_OF_SAME_COLOR,
                        CARDS_OF_SAME_COLOR)) == 0;
        boolean onlyJack = ((pkHand & Bits64.mask(
                trump(pkTrick).ordinal() * CARDS_OF_SAME_COLOR + 5, 1)) != 0)
                && ((pkHand & Bits64.mask(
                        trump(pkTrick).ordinal() * CARDS_OF_SAME_COLOR,
                        5)) == 0)
                && ((pkHand & Bits64.mask(
                        trump(pkTrick).ordinal() * CARDS_OF_SAME_COLOR + 6,
                        3)) == 0);
        return noTrump || onlyJack;
    }

    /*
     * Retourne vrai si le joueur possède une carte autre que des cartes atouts
     */
    private static boolean OtherThanTrumpExists(int pkTrick, long pkHand) {
        return (pkHand
                & ~Bits64.mask(trump(pkTrick).ordinal() * CARDS_OF_SAME_COLOR,
                        CARDS_OF_SAME_COLOR)) != 0;
    }

}
