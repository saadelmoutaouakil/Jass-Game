package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizers.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizers.newRandom;
import static org.junit.jupiter.api.Assertions.*;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.TeamId;

interface TestValid {
    void test(int gamePoints1, int turnPoints1, int turnTricks1,
            int gamePoints2, int turnPoints2, int turnTricks2, long validCard,
            int trickPoints);
}

interface TestInvalid {
    void test(int gamePoints1, int turnPoints1, int turnTricks1,
            int gamePoints2, int turnPoints2, int turnTricks2, long rd,
            long invalidCard, int trickPoints);
}

class PackedScoreTestRename {

    void WorksForSomeValidScores(TestValid t) {
        SplittableRandom rng = newRandom();
        for (int turnTricks1 = 0; turnTricks1 <= 9; ++turnTricks1)
            for (int turnTricks2 = 0; turnTricks2 <= 9
                    - turnTricks1; ++turnTricks2)
                for (int turnPoints1 = turnTricks1 == 9 ? 257
                        : 0; turnPoints1 <= (turnTricks1 == 9 ? 257
                                : (turnTricks2 == 9 ? 0
                                        : 157)); turnPoints1 += rng.nextInt(50)
                                                + 1)
                    for (int turnPoints2 = turnTricks2 == 9 ? 257
                            : 0; turnPoints2 <= (turnTricks2 == 9 ? 257
                                    : (turnTricks1 == 9 ? 0
                                            : (157 - turnPoints1))); turnPoints2 += rng
                                                    .nextInt(50) + 1)
                        for (int gamePoints1 = 0; gamePoints1 <= 2000
                                - turnPoints1; gamePoints1 += rng.nextInt(500)
                                        + 1)
                            for (int gamePoints2 = 0; gamePoints2 <= 2000
                                    - turnPoints2; gamePoints2 += rng
                                            .nextInt(500) + 1)
                                t.test(gamePoints1, turnPoints1, turnTricks1,
                                        gamePoints2, turnPoints2, turnTricks2,
                                        ((((long) gamePoints2 << 9
                                                | (long) turnPoints2) << 4
                                                | (long) turnTricks2) << 32
                                                | (((long) gamePoints1 << 9
                                                        | (long) turnPoints1) << 4
                                                        | (long) turnTricks1)),
                                        rng.nextInt(10));
    }

    void WorksForSomeInvalidScores(TestInvalid t) {
        SplittableRandom rng = newRandom();
        for (int i = 0; i < RANDOM_ITERATIONS; ++i) {
            int gamePoints1 = rng.nextInt(1 << 11);
            int gamePoints2 = rng.nextInt(1 << 11);
            int turnPoints1 = rng.nextInt(1 << 9);
            int turnPoints2 = rng.nextInt(1 << 9);
            int turnTricks1 = rng.nextInt(1 << 4);
            int turnTricks2 = rng.nextInt(1 << 4);
            int trickPoints = rng.nextInt(10);
            long rd = 0;
            do
                rd = (rng.nextLong() & ~(Bits64.mask(0, 24)) << 32
                        | Bits64.mask(0, 24));
            while (rd == 0);
            long invalidCard = Bits64.pack(
                    (long) Bits32.pack(turnTricks1, 4, turnPoints1, 9,
                            gamePoints1, 11),
                    32, (long) Bits32.pack(turnTricks2, 4, turnPoints2, 9,
                            gamePoints2, 11),
                    32);
            invalidCard |= (gamePoints1 <= 2000 && gamePoints2 <= 2000
                    && turnPoints1 <= 257 && turnPoints2 <= 257
                    && turnTricks1 <= 9 && turnTricks2 <= 9) ? rd : 0L;
            t.test(gamePoints1, turnPoints1, turnTricks1, gamePoints2,
                    turnPoints2, turnTricks2, rd, invalidCard, trickPoints);
        }
    }

    @Test
    void packedScoreScoreConstantsAreCorrect() throws Exception {
        assertEquals(0L, PackedScore.INITIAL);
    }

    @Test
    void isValidWorksForSomeValidScores() {
        WorksForSomeValidScores((int gamePoints1, int turnPoints1,
                int turnTricks1, int gamePoints2, int turnPoints2,
                int turnTricks2, long validCard, int trickPoints) -> {
            assertTrue(
                    PackedScore
                            .isValid(Bits64.pack(
                                    (long) Bits32.pack(turnTricks1, 4,
                                            turnPoints1, 9, gamePoints1, 11),
                                    32,
                                    (long) Bits32.pack(turnTricks2, 4,
                                            turnPoints2, 9, gamePoints2, 11),
                                    32)));
        });
    }

    @Test
    void isValidWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1,
                        int gamePoints2, int turnPoints2, int turnTricks2,
                        long rd, long invalidCard, int trickPoints) -> {
                    assertFalse(PackedScore.isValid(invalidCard));
                });
    }

    @Test
    void packWorksForSomeValidScores() {
        WorksForSomeValidScores((int gamePoints1, int turnPoints1,
                int turnTricks1, int gamePoints2, int turnPoints2,
                int turnTricks2, long validCard, int trickPoints) -> {
            assertEquals(validCard, PackedScore.pack(turnTricks1, turnPoints1,
                    gamePoints1, turnTricks2, turnPoints2, gamePoints2));
        });
    }

    @Test
    void turnTricksWorksForSomeValidScores() {
        WorksForSomeValidScores((int gamePoints1, int turnPoints1,
                int turnTricks1, int gamePoints2, int turnPoints2,
                int turnTricks2, long validCard, int trickPoints) -> {
            assertEquals(turnTricks1,
                    PackedScore.turnTricks(validCard, TeamId.TEAM_1));
            assertEquals(turnTricks2,
                    PackedScore.turnTricks(validCard, TeamId.TEAM_2));
        });
    }

    @Test
    void turnTricksWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1,
                        int gamePoints2, int turnPoints2, int turnTricks2,
                        long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.turnTricks(invalidCard, TeamId.TEAM_1);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.turnTricks(invalidCard, TeamId.TEAM_2);
                    });
                });
    }

    @Test
    void turnPointsWorksForSomeValidScores() {
        WorksForSomeValidScores((int gamePoints1, int turnPoints1,
                int turnTricks1, int gamePoints2, int turnPoints2,
                int turnTricks2, long validCard, int trickPoints) -> {
            assertEquals(turnPoints1,
                    PackedScore.turnPoints(validCard, TeamId.TEAM_1));
            assertEquals(turnPoints2,
                    PackedScore.turnPoints(validCard, TeamId.TEAM_2));
        });
    }

    @Test
    void turnPointsWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1,
                        int gamePoints2, int turnPoints2, int turnTricks2,
                        long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.turnPoints(invalidCard, TeamId.TEAM_1);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.turnPoints(invalidCard, TeamId.TEAM_2);
                    });
                });
    }

    @Test
    void gamePointsWorksForSomeValidScores() {
        WorksForSomeValidScores((int gamePoints1, int turnPoints1,
                int turnTricks1, int gamePoints2, int turnPoints2,
                int turnTricks2, long validCard, int trickPoints) -> {
            assertEquals(gamePoints1,
                    PackedScore.gamePoints(validCard, TeamId.TEAM_1));

            assertEquals(gamePoints2,
                    PackedScore.gamePoints(validCard, TeamId.TEAM_2));
        });
    }

    @Test
    void gamePointsWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1,
                        int gamePoints2, int turnPoints2, int turnTricks2,
                        long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.gamePoints(invalidCard, TeamId.TEAM_1);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.gamePoints(invalidCard, TeamId.TEAM_2);
                    });
                });
    }

    @Test
    void totalPointsWorksForSomeValidScores() {
        WorksForSomeValidScores((int gamePoints1, int turnPoints1,
                int turnTricks1, int gamePoints2, int turnPoints2,
                int turnTricks2, long validCard, int trickPoints) -> {
            assertEquals(gamePoints1 + turnPoints1,
                    PackedScore.totalPoints(validCard, TeamId.TEAM_1));
            assertEquals(gamePoints2 + turnPoints2,
                    PackedScore.totalPoints(validCard, TeamId.TEAM_2));
        });
    }

    @Test
    void totalPointsWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1,
                        int gamePoints2, int turnPoints2, int turnTricks2,
                        long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.totalPoints(invalidCard, TeamId.TEAM_1);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.totalPoints(invalidCard, TeamId.TEAM_2);
                    });
                });
    }

    @Test
    void withAdditionalTrickWorksForSomeValidScores() {
        WorksForSomeValidScores((int gamePoints1, int turnPoints1,
                int turnTricks1, int gamePoints2, int turnPoints2,
                int turnTricks2, long validCard, int trickPoints) -> {
            if (turnTricks1 != 9 && turnPoints1 + trickPoints
                    + (turnTricks1 == 8 ? 100 : 0) <= 257)
                assertEquals(
                        PackedScore.pack(turnTricks1 + 1,
                                turnPoints1 + trickPoints
                                        + (turnTricks1 == 8 ? 100 : 0),
                                gamePoints1, turnTricks2, turnPoints2,
                                gamePoints2),
                        PackedScore.withAdditionalTrick(validCard,
                                TeamId.TEAM_1, trickPoints));
            if (turnTricks2 != 9 && turnPoints2 + trickPoints
                    + (turnTricks2 == 8 ? 100 : 0) <= 257)
                assertEquals(
                        PackedScore.pack(turnTricks1, turnPoints1, gamePoints1,
                                turnTricks2 + 1,
                                turnPoints2 + trickPoints
                                        + (turnTricks2 == 8 ? 100 : 0),
                                gamePoints2),
                        PackedScore.withAdditionalTrick(validCard,
                                TeamId.TEAM_2, trickPoints));
        });
    }

    @Test
    void withAdditionalTrickWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1,
                        int gamePoints2, int turnPoints2, int turnTricks2,
                        long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.withAdditionalTrick(invalidCard,
                                TeamId.TEAM_1, trickPoints);
                    });
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.withAdditionalTrick(invalidCard,
                                TeamId.TEAM_2, trickPoints);
                    });
                });
    }

    @Test
    void nextTurnWorksForSomeValidScores() {
        WorksForSomeValidScores((int gamePoints1, int turnPoints1,
                int turnTricks1, int gamePoints2, int turnPoints2,
                int turnTricks2, long validCard, int trickPoints) -> {
            if (gamePoints1 + turnPoints1 <= 2000
                    && gamePoints2 + turnPoints2 <= 2000) {
                long l = PackedScore.pack(0, 0, gamePoints1 + turnPoints1, 0, 0,
                        gamePoints2 + turnPoints2);
                long l2 = PackedScore.nextTurn(validCard);
                assertEquals(l, l2);
            }
        });
    }

    @Test
    void nextTurnWorksForSomeInvalidScores() {
        WorksForSomeInvalidScores(
                (int gamePoints1, int turnPoints1, int turnTricks1,
                        int gamePoints2, int turnPoints2, int turnTricks2,
                        long rd, long invalidCard, int trickPoints) -> {
                    assertThrows(AssertionError.class, () -> {
                        PackedScore.nextTurn(invalidCard);
                    });
                });
    }
}
