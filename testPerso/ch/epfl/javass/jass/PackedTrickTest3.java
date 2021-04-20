package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Random;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static ch.epfl.javass.jass.PackedTrick.isValid;
import static ch.epfl.javass.jass.PackedScore.pack;
import static ch.epfl.test.TestRandomizers.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizers.newRandom;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
import ch.epfl.javass.jass.TeamId;

public class PackedTrickTest3 {

    @Test
    void winningPlayerTest() {
        int playerId = 0;
        int counter = 0;
        ArrayList<Integer> cards = new ArrayList<>();
        Color trump;
        int noIdea = 0;
        boolean winnter = false;
        boolean is = false;
        boolean comming = false;
        int winningCard;
        int nbCard = -1;
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)
                    && ((Bits32.extract(iterations, 0, 6) != 0b111111)
                            || Bits32.extract(iterations, 6, 6) != 0b111111
                            || Bits32.extract(iterations, 12, 6) != 0b111111
                            || Bits32.extract(iterations, 18, 6) != 0b111111)) {
                cards.clear();
                ;
                counter = 0;
                playerId = Bits32.extract(iterations, 28, 2);
                trump = Card.Color.ALL.get(Bits32.extract(iterations, 30, 2));
                for (int j = 0; j < 4; ++j) {
                    if (Bits32.extract(iterations, 6 * j, 6) != 0b111111) {
                        cards.add(Bits32.extract(iterations, 6 * j, 6));
                        counter += 1;
                    }
                }
                winningCard = cards.get(0);
                for (int k = 0; k < counter - 1; ++k) {
                    if (PackedCard.isBetter(trump, cards.get(k + 1),
                            winningCard)) {
                        winningCard = cards.get(k + 1);
                    }
                }
                noIdea = cards.indexOf(winningCard);
                assertEquals((noIdea + playerId) % 4,
                        PackedTrick.winningPlayer(iterations).ordinal());

            }
        }
    }

    @Test
    void playableCardTestUnit2() {
        int pkTrick1 = PackedTrick.firstEmpty(Card.Color.SPADE,
                PlayerId.PLAYER_1);
        long pkHand1 = 0b0000_0000_0010_0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000L;
        int pkTrick2 = Bits32.pack(0b110, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand2 = 0b0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000_1010_0000_0000_0010_0000L;
        int pkTrick3 = Bits32.pack(0b110, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand3 = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_1010_0000L;
        int pkTrick4 = Bits32.pack(0b10_0110, 6, 0b0, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand4 = 0b0000_0000_0010_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000_0010L;
        int pkTrick5 = Bits32.pack(0b11_0001, 6, 0b1_0011, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b1,
                2);
        long pkHand5 = 0b0000_0000_0000_0000_0000_0000_1000_0000_0000_0000_0000_0100_0000_0000_1000_0001L;
        int pkTrick6 = Bits32.pack(0b1_0101, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand6 = 0b0000_0000_0000_0001_0000_0000_0001_0000_0000_0000_1000_0000_0000_0000_0000_1000L;
        int pkTrick7 = Bits32.pack(0b11_0011, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b11,
                2);
        long pkHand7 = 0b0000_0000_0000_0000_0000_0000_1000_1000_0000_0000_0100_0000_0000_0001_0000_0000L;
        int pkTrick8 = Bits32.pack(0b1000, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b10,
                2);
        long pkHand8 = 0b0000_0000_0000_0000_0000_0000_0010_0010_0000_0000_0100_0000_0000_0000_1000_0000L;
        int pkTrick9 = Bits32.pack(0b11_0100, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b10,
                2);
        long pkHand9 = 0b0000_0000_0000_0010_0000_0000_0000_1000_0000_0000_0001_0001_0000_0000_0000_0000L;
        int pkTrick10 = Bits32.pack(0b10_0100, 6, 0b0100, 6, PackedCard.INVALID,
                6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand10 = 0b0000_0000_1000_0000_0000_0000_1010_0000_0000_0000_0000_0001_0000_0000_0000_0000L;
        int pkTrick11 = Bits32.pack(0b10_0100, 6, 0b0100, 6, PackedCard.INVALID,
                6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand11 = 0b0000_0000_0000_0000_0000_0000_1010_0000_0000_0000_0000_0001_0000_0000_0000_0100L;
        System.out.println("{\u266110 \u2663J} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick1, pkHand1)));
        System.out.println();
        System.out.println("{\u2660J \u26617 \u26619 \u266310} expected\n"
                + PackedCardSet.toString(
                        PackedTrick.playableCards(pkTrick2, pkHand2)));
        System.out.println();
        System.out.println("{\u2660J \u2660K} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick3, pkHand3)));
        System.out.println();
        System.out.println("{\u26607 \u266210} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick4, pkHand4)));
        System.out.println();
        System.out.println(
                "{\u26606 \u2660K \u2662K} expected\n" + PackedCardSet.toString(
                        PackedTrick.playableCards(pkTrick5, pkHand5)));
        System.out.println();
        System.out.println("{\u26609 \u2661K} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick6, pkHand6)));
        System.out.println();
        System.out.println("{\u2660A \u2661Q \u26629 \u2662K} expected\n"
                + PackedCardSet.toString(
                        PackedTrick.playableCards(pkTrick7, pkHand7)));
        System.out.println();
        System.out.println(
                "{\u2660K \u26627 \u2662J} expected\n" + PackedCardSet.toString(
                        PackedTrick.playableCards(pkTrick8, pkHand8)));
        System.out.println();
        System.out.println("{\u26629 \u26637} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick9, pkHand9)));
        System.out.println();
        System.out.println("{\u2662J \u2662K} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick10, pkHand10)));
        System.out.println();
        System.out.println("{\u2662J \u2662K} expected\n" + PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick11, pkHand11)));
    }

    @Test
    void playableCardsWorks() {
        // deux cartes jouables: valet de pique, as de coeur
        // en main: 8 de pique, valet de pique, as de coeur, 7 de trèfle
        // premiere carte jouée: 6 de coeur, as de pique, 6 de trèfle, atout
        // pique
        assertEquals(0b100000000_0000000000100000L, PackedTrick.playableCards(
                0b00_11_0000_111111_110000_001000_010000,
                0b10_0000000000000000_0000000100000000_0000000000100100L));

        // en main: 9 de pique, valet de pique, 6 de coeur, 7 coeur;
        // premiere carte jouée: 10 de coeur, atout carreau
        assertEquals(0b11_0000000000000000L, PackedTrick.playableCards(
                0b10_11_0000_111111_111111_111111_010100,
                0b00_0000000000000000_0000000000000011_0000000000101000L));

        // en main: 6 de pique, 7 de pique, 8 de pique, 6 de coeur
        // premiere carte jouée: 9 de pique, 2nd: 10 de pique, atout pique
        assertEquals(0b0_0000000000000111L, PackedTrick.playableCards(
                0b00_11_0000_111111_111111_000100_000011,
                0b00_0000000000000000_0000000000000001_0000000000000111L));

        // en main: 6 de pique, 7 de pique, 8 de pique, 6 de coeur
        // premiere carte jouée: 9 de carreau, 2nd: 10 de pique, atout carreau
        assertEquals(0b1_0000000000000111L, PackedTrick.playableCards(
                0b10_11_0000_111111_111111_000100_100011,
                0b00_0000000000000000_0000000000000001_0000000000000111L));

        // en main: 6 de pique, 7 de pique, 8 de pique, as de pique
        // premiere carte jouée: 9 de carreau, 2nd: 10 de pique, atout pique
        assertEquals(0b0_0000000100000000L, PackedTrick.playableCards(
                0b00_11_0000_111111_111111_000100_100011,
                0b00_0000000000000000_0000000000000000_0000000100000111L));

        // atout pas demandé, mais pas coupé, et peut pas suivre
        assertEquals(0b1000000000000000000000001000000000000000000100100L,
                PackedTrick.playableCards(
                        0b00_11_0000_111111_110001_010000_100000,
                        0b1000000000000000000000001000000000000000000100100L));
        assertEquals(0b1000000000000000000100000L,
                PackedTrick.playableCards(
                        0b00_10_0000_111111_111111_001000_010000,
                        0b1000000000000000000000001000000000000000000100100L));

        // que des atouts dans la main, pas atout demandé, mais qqn coupe et pas
        // moyen de surcouper
        // en main: 6, 7 et 8 de pique
        // première carte jouée: 9 de carreau, deuxieme carte jouée: 10 de
        // pique, atout pique
        assertEquals(0b0_0000000000000111L, PackedTrick.playableCards(
                0b00_11_0000_111111_111111_000100_100011,
                0b00_0000000000000000_0000000000000000_0000000000000111L));

        // quand qqn a couper, tu peux pas sous couper, tu peux pas suivre, et
        // t'as une carte qui n'est pas d'atout
        // 6, 7 ,8 de pique et 6 de coeur, atout pique,
        // premiere carte jouee 9 de carreau, deuxieme carte 10 de pique
        assertEquals(0b1_0000000000000000L, PackedTrick.playableCards(
                0b00_11_0000_111111_111111_000100_100011,
                0b00_0000000000000000_0000000000000001_0000000000000111L));

        // qqn a coupé, peut surcouper, peut pas suivre
        // 6, 7 ,9 de pique et 6 de coeur, atout pique,
        // premiere carte jouee 9 de carreau, deuxieme carte 10 de pique
        assertEquals(0b1_0000000000001000L, PackedTrick.playableCards(
                0b00_11_0000_111111_111111_000100_100011,
                0b00_0000000000000000_0000000000000001_0000000000001011L));

        // qqn a coupé, pas surcouper, peut suivre
        // 6, 7 ,8 de pique et 6 de coeur, atout pique,
        // premiere carte jouee 9 de coeur, deuxieme carte 10 de pique
        assertEquals(0b1_0000000000000000L, PackedTrick.playableCards(
                0b00_11_0000_111111_111111_000100_010011,
                0b00_0000000000000000_0000000000000001_0000000000000111L));

        // on est le quatrieme joueur, les deux joueurs precedents ont coupe
        // 6, 7 ,8 de pique et 6 de coeur, atout pique,
        // premiere carte jouee 9 de coeur, deuxieme carte 10 de pique,
        // troisième carte bourre
        assertEquals(0b1_0000000000000000L, PackedTrick.playableCards(
                0b00_11_0000_111111_000101_000100_010011,
                0b00_0000000000000000_0000000000000001_0000000000000111L));

        // on est le quatrieme joueur, les deux joueurs precedents ont coupe
        // 6, 7 ,9 de pique et 6 de coeur, atout pique,
        // premiere carte jouee 9 de coeur, deuxieme carte 10 de pique,
        // troisième carte bourre
        assertEquals(0b1_0000000000000000L, PackedTrick.playableCards(
                0b00_11_0000_111111_000101_000100_010011,
                0b00_0000000000000000_0000000000000001_0000000000001011L));

        // qqn a coupé, peut surcouper, peut suivre
        // 6, 7 ,9 de pique et 6 de coeur, atout pique
        // premiere carte jouee 9 de coeur, deuxieme carte 10 de pique
        assertEquals(0b00_0000000000000000_0000000000000001_0000000000001000L,
                PackedTrick.playableCards(
                        0b00_11_0000_111111_111111_000100_010011,
                        0b00_0000000000000000_0000000000000001_0000000000001011L));

        // qqn a coupé, pas d'atout plus haut, soit autre couleur, pas de
        // couleur de base
        // 6, 7 ,8 de pique et 6 de coeur, atout pique
        // premiere carte jouee 9 de carreau, deuxieme carte 10 de pique
        assertEquals(0b1_0000000000000000L, PackedTrick.playableCards(
                0b00_11_0000_111111_111111_000100_100011,
                0b00_0000000000000000_0000000000000001_0000000000000111L));

        // cas normal: atout demandé, tout le monde a atout, atout pique
        // en main: 6, 7, 8 de pique, 6 de coeur
        // première carte jouée: 9 de pique, après 10 de pique, après bourre
        assertEquals(0b0_0000000000000111L, PackedTrick.playableCards(
                0b00_11_0000_111111_000101_000100_000011,
                0b00_0000000000000000_0000000000000001_0000000000000111L));

        // personne n'a coupé, on a de l'atout
        // en main: 6, 7, 8 de pique
        // première carte jouée: 9 de coeur, après 10 de coeur, après valet de
        // coeur
        assertEquals(0b0_0000000000000111L, PackedTrick.playableCards(
                0b00_11_0000_111111_010101_010100_010011,
                0b00_0000000000000000_0000000000000000_0000000000000111L));

        // peut couper mais peut pas suivre
        // en main: 6, 7, 8 de pique, 6 de coeur
        // première carte jouée: 9 de coeur, après 10 de coeur, après valet de
        // coeur
        assertEquals(0b1_0000000000000111L, PackedTrick.playableCards(
                0b00_11_0000_111111_010101_010100_010011,
                0b00_0000000000000000_0000000000000001_0000000000000111L));

        // cas de la dernière pli, on peut jouer tout ce qu'on a en main
        // en main: 6 de pique
        // première carte jouée: 7 de coeur, atout coeur
        assertEquals(0b0_0000000000000001L, PackedTrick.playableCards(
                0b01_11_0000_111111_111111_111111_010001,
                0b00_0000000000000000_0000000000000000_0000000000000001L));

        // cas de la dernière pli, on peut jouer tout ce qu'on a en main
        // en main: 6 de pique
        // première carte jouée: 7 de coeur, deuxieme carte: 8 de carreau atout
        // coeur
        assertEquals(0b0_0000000000000001L, PackedTrick.playableCards(
                0b01_11_0000_111111_111111_100011_010001,
                0b00_0000000000000000_0000000000000000_0000000000000001L));

        // cas de la dernière pli, on peut jouer tout ce qu'on a en main
        // en main: 6 de pique
        // première carte jouée: 7 de coeur, deuxieme carte: 8 de carreau,
        // troisieme carte: as de trefle, atout coeur
        assertEquals(0b0_0000000000000001L, PackedTrick.playableCards(
                0b01_11_0000_111111_111000_100011_010001,
                0b00_0000000000000000_0000000000000000_0000000000000001L));

        // jamais une main vide, set retourné toujours un subset de la main
        // donnée

        ArrayList<Integer> cards = new ArrayList<>();
        for (int i = 0; i < Card.Rank.COUNT; ++i) {
            for (int j = 0; j < Card.Color.COUNT; ++j) {
                cards.add(PackedCard.pack(Color.ALL.get(j), Rank.ALL.get(i)));
            }
        }
        for (int i = 1; i < PackedCardSet.subsetOfColor(PackedCardSet.ALL_CARDS,
                Color.SPADE); ++i) {
            for (int j = 0; j < cards.size(); ++j) {
                for (int m = 0; m < cards.size(); ++m) {
                    assertTrue(PackedTrick.playableCards(
                            (0b00_11_0000_111111_111111 << 12)
                                    | (cards.get(j) << 6) | cards.get(m),
                            ((i << 48) | (i << 32) | (i << 16) | i)) > 0);
                    assertTrue((PackedTrick.playableCards(
                            (0b00_11_0000_111111_111111 << 12)
                                    | (cards.get(j) << 6) | cards.get(m),
                            ((i << 48) | (i << 32) | (i << 16) | i))
                            & ((i << 48) | (i << 32) | (i << 16) | i)) > 0);
                }
            }
        }
    }

    @Test
    void playableCardTestUnit() {
        int pkTrick1 = PackedTrick.firstEmpty(Card.Color.SPADE,
                PlayerId.PLAYER_1);
        long pkHand1 = 0b0000_0000_0010_0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_0000_0000L;
        int pkTrick2 = Bits32.pack(0b110, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand2 = 0b0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000_1010_0000_0000_0010_0000L;
        int pkTrick3 = Bits32.pack(0b110, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand3 = 0b0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0001_0000_0000_0000_1010_0000L;
        int pkTrick4 = Bits32.pack(0b10_0110, 6, 0b0, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand4 = 0b0000_0000_0010_0000_0000_0000_0001_0000_0000_0000_0000_0000_0000_0000_0000_0010L;
        int pkTrick5 = Bits32.pack(0b11_0001, 6, 0b1_0011, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b1,
                2);
        long pkHand5 = 0b0000_0000_0000_0000_0000_0000_1000_0000_0000_0000_0000_0100_0000_0000_1000_0001L;
        int pkTrick6 = Bits32.pack(0b1_0101, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand6 = 0b0000_0000_0000_0001_0000_0000_0001_0000_0000_0000_1000_0000_0000_0000_0000_1000L;
        int pkTrick7 = Bits32.pack(0b11_0011, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b11,
                2);
        long pkHand7 = 0b0000_0000_0000_0000_0000_0000_1000_1000_0000_0000_0100_0000_0000_0001_0000_0000L;
        int pkTrick8 = Bits32.pack(0b1000, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b10,
                2);
        long pkHand8 = 0b0000_0000_0000_0000_0000_0000_0010_0010_0000_0000_0100_0000_0000_0000_1000_0000L;
        int pkTrick9 = Bits32.pack(0b11_0100, 6, PackedCard.INVALID, 6,
                PackedCard.INVALID, 6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0b10,
                2);
        long pkHand9 = 0b0000_0000_0000_0010_0000_0000_0000_1000_0000_0000_0001_0001_0000_0000_0000_0000L;
        int pkTrick10 = Bits32.pack(0b10_0100, 6, 0b0100, 6, PackedCard.INVALID,
                6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand10 = 0b0000_0000_1000_0000_0000_0000_1010_0000_0000_0000_0000_0001_0000_0000_0000_0000L;
        int pkTrick11 = Bits32.pack(0b10_0100, 6, 0b0100, 6, PackedCard.INVALID,
                6, PackedCard.INVALID, 6, 0, 4, 0, 2, 0, 2);
        long pkHand11 = 0b0000_0000_0000_0000_0000_0000_1010_0000_0000_0000_0000_0001_0000_0000_0000_0100L;
        assertEquals("{\u266110,\u2663J}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick1, pkHand1)));
        assertEquals("{\u2660J,\u26617,\u26619,\u266310}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick2, pkHand2)));
        assertEquals("{\u2660J,\u2660K}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick3, pkHand3)));
        assertEquals("{\u26607,\u266210}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick4, pkHand4)));
        assertEquals("{\u26606,\u2660K,\u2662K}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick5, pkHand5)));
        assertEquals("{\u26609,\u2661K}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick6, pkHand6)));
        assertEquals("{\u2660A,\u2661Q,\u26629,\u2662K}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick7, pkHand7)));
        assertEquals("{\u2660K,\u26627,\u2662J}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick8, pkHand8)));
        assertEquals("{\u26629,\u26637}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick9, pkHand9)));
        assertEquals("{\u2662J,\u2662K}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick10, pkHand10)));
        assertEquals("{\u2662J,\u2662K}", PackedCardSet
                .toString(PackedTrick.playableCards(pkTrick11, pkHand11)));
    }

    @Test
    void isValidWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            assertEquals(IsValidTest(iterations),
                    PackedTrick.isValid(iterations));
        }
    }

    private boolean IsValidTest(int pkTrick) {
        if ((Bits32.extract(pkTrick, 24, 4) < 9)) {
            int Card0 = Bits32.extract(pkTrick, 0, 6);
            int Card1 = Bits32.extract(pkTrick, 6, 6);
            int Card2 = Bits32.extract(pkTrick, 12, 6);
            int Card3 = Bits32.extract(pkTrick, 18, 6);
            if ((PackedCard.isValid(Card0) && PackedCard.isValid(Card1)
                    && PackedCard.isValid(Card2)
                    && PackedCard.isValid(Card3))) {
                return true;
            }
            if ((PackedCard.isValid(Card0) && PackedCard.isValid(Card1)
                    && PackedCard.isValid(Card2) && (Card3 == 0b111111))) {
                return true;
            }
            if ((PackedCard.isValid(Card0) && PackedCard.isValid(Card1)
                    && (Card2 == 0b111111) && (Card3 == 0b111111))) {
                return true;
            }
            if ((PackedCard.isValid(Card0) && (Card1 == 0b111111)
                    && (Card2 == 0b111111)) && (Card3 == 0b111111)) {
                return true;
            }
            if ((Card0 == 0b111111) && (Card1 == 0b111111)
                    && (Card2 == 0b111111) && (Card3 == 0b111111)) {
                return true;
            }
        }
        return false;
    }

    @Test
    void firstEmptyWorks() {
        for (int j = 0; j < 4; ++j) {
            for (int i = 0; i < 4; ++i) {
                int trick = PackedTrick.firstEmpty(Color.ALL.get(j),
                        PlayerId.ALL.get(i));
                int shouldBe1 = Bits32.extract(trick, 0, 24);
                int shouldBe0 = Bits32.extract(trick, 24, 4);
                int shouldBePlayer = Bits32.extract(trick, 28, 2);
                int shouldBeColor = Bits32.extract(trick, 30, 2);
                assertTrue((shouldBeColor == (j)));
                assertTrue((shouldBePlayer == (i)));
                assertTrue(shouldBe0 == 0);
                assertTrue(shouldBe1 == 0b111111111111111111111111);
            }
        }
    }

    @Test
    void nextEmptyWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)
                    && (Bits32.extract(iterations, 0, 6) != 0b111111)
                    && (Bits32.extract(iterations, 6, 6) != 0b111111)
                    && (Bits32.extract(iterations, 12, 6) != 0b111111)
                    && (Bits32.extract(iterations, 18, 6) != 0b111111)) {
                if (Bits32.extract(iterations, 24, 4) == 8) {
                    assertEquals(PackedTrick.INVALID,
                            PackedTrick.nextEmpty(iterations));
                } else {
                    int nextTrick = PackedTrick.nextEmpty(iterations);

                    assertEquals(Bits32.extract(nextTrick, 0, 24),
                            0b111111111111111111111111);
                    assertEquals(Bits32.extract(iterations, 30, 2),
                            Bits32.extract(nextTrick, 30, 2));
                    assertEquals(PackedTrick.winningPlayer(iterations),
                            PlayerId.ALL.get(Bits32.extract(nextTrick, 28, 2)));
                    assertEquals(Bits32.extract(iterations, 24, 4) + 1,
                            Bits32.extract(nextTrick, 24, 4));
                }
            }
        }
    }

    @Test
    void sizeWorks() {
        int shouldBeThisSize;
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            shouldBeThisSize = 0;
            if (PackedTrick.isValid(iterations)) {
                for (int j = 0; j < 4; ++j) {
                    if (Bits32.extract(iterations, 6 * j, 6) != 0b111111) {
                        shouldBeThisSize += 1;
                    }
                }
                assertEquals(shouldBeThisSize, PackedTrick.size(iterations));
            }
        }
    }

    @Test
    void cardWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                for (int j = 0; j < 4; ++j) {
                    int shouldBeThatCard = Bits32.extract(iterations, 6 * j, 6);
                    if (PackedCard.isValid(shouldBeThatCard)) {
                        assertEquals(shouldBeThatCard,
                                PackedTrick.card(iterations, j));

                    }
                }
            }
        }
    }

    @Test
    void withAddedCardWorks() {
        int k;
        int index;
        int ones = 0b111111;
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations) && ((iterations
                    & 0b111111000000000000000000) == (0b111111 << 18))) {
                for (int j = 0; j < 64; ++j) {
                    if (PackedCard.isValid(j)) {
                        k = 0;
                        index = 0;
                        while (k < 4) {
                            if (Bits32.extract(iterations, k * 6,
                                    6) == 0b111111) {
                                index = k;
                                k = 5;
                            } else {
                                k++;
                            }
                        }

                        assertEquals(
                                (iterations - (ones << 6 * index))
                                        + (j << 6 * index),
                                PackedTrick.withAddedCard(iterations, j));
                    }
                }
            }
        }
    }

    @Test
    void isLastWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                if (Bits32.extract(iterations, 24, 4) == 8) {
                    assertTrue(PackedTrick.isLast(iterations));
                } else {
                    assertFalse(PackedTrick.isLast(iterations));
                }
            }
        }
    }

    @Test
    void isEmptyWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                if (Bits32.extract(iterations, 0,
                        24) == 0b111111111111111111111111) {
                    assertTrue(PackedTrick.isEmpty(iterations));
                } else {
                    assertFalse(PackedTrick.isEmpty(iterations));
                }
            }
        }
    }

    @Test
    void isFullWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                if ((Bits32.extract(iterations, 0, 4) < 9)
                        && (Bits32.extract(iterations, 6, 4) < 9)
                        && (Bits32.extract(iterations, 12, 4) < 9)
                        && (Bits32.extract(iterations, 18, 4) < 9)) {
                    assertTrue(PackedTrick.isFull(iterations));
                }

                else {
                    assertFalse(PackedTrick.isFull(iterations));
                }
            }
        }
    }

    @Test
    void trumpWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                int colorIndex = Bits32.extract(iterations, 30, 2);
                assertEquals(Color.ALL.get(colorIndex),
                        PackedTrick.trump(iterations));
            }
        }
    }

    @Test
    void playerWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                int playerIndex = Bits32.extract(iterations, 28, 2);
                for (int j = 0; j < 4; ++j) {
                    assertEquals(PlayerId.ALL.get((playerIndex + j) % 4),
                            PackedTrick.player(iterations, j));

                }
            }
        }
    }

    @Test
    void indexWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                int index = Bits32.extract(iterations, 24, 4);
                assertEquals(index, PackedTrick.index(iterations));
            }
        }
    }

    @Test
    void baseColorWorks() {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                int shouldBeThatColor = Bits32.extract(iterations, 4, 2);
                assertEquals(shouldBeThatColor,
                        PackedTrick.baseColor(iterations).ordinal());
            }
        }
    }

    @Test
    void pointsWorks() {
        int Card0, Card1, Card2, Card3, trump, points0, points1, points2,
                points3, pointBonus;
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int iterations = rng.nextInt();
            if (PackedTrick.isValid(iterations)) {
                Card0 = Bits32.extract(iterations, 0, 6);
                Card1 = Bits32.extract(iterations, 6, 6);
                Card2 = Bits32.extract(iterations, 12, 6);
                Card3 = Bits32.extract(iterations, 18, 6);
                trump = Bits32.extract(iterations, 30, 2);
                pointBonus = 0;
                points0 = 0;
                points1 = 0;
                points2 = 0;
                points3 = 0;
                if (PackedCard.isValid(Card0)) {
                    points0 = PackedCard.points(Color.ALL.get(trump), Card0);
                }
                if (PackedCard.isValid(Card1)) {
                    points1 = PackedCard.points(Color.ALL.get(trump), Card1);
                }
                if (PackedCard.isValid(Card2)) {
                    points2 = PackedCard.points(Color.ALL.get(trump), Card2);
                }
                if (PackedCard.isValid(Card3)) {
                    points3 = PackedCard.points(Color.ALL.get(trump), Card3);
                }
                if (Bits32.extract(iterations, 24, 4) == 8) {
                    pointBonus += 5;
                }
                // les cartes entrée dans points sont toutes valides
                // si on entre des mauvaises cartes , assertError
                if (PackedCard.isValid(Card0) && PackedCard.isValid(Card1)
                        && PackedCard.isValid(Card2)
                        && PackedCard.isValid(Card3)) {
                    int sum = points0 + points1 + points2 + points3
                            + pointBonus;
                    assertEquals(sum, PackedTrick.points(iterations));
                }
            }
        }
    }
}
