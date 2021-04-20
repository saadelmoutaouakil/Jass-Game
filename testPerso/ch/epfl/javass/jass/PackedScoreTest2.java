package ch.epfl.javass.jass;

//import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
//import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.*;

//import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

class PackedScoreTest2 {

    /**
     * Test isValid
     */
    @Test
    void isValidWorksForAllValidScores() {
        for (int r = 0; r <= 9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for (int j = 0; j <= 2000; ++j) {
                    assertTrue(PackedScore.isValid((long) j << 45
                            | (long) c << 36 | (long) r << 32 | (long) j << 13
                            | (long) c << 4 | (long) r));
                }
            }
        }
        assertTrue(PackedScore.isValid(2000L << 45 | 257L << 36 | 9L << 32
                | 2000L << 13 | 257L << 4 | 9L));
    }

    @Test
    void isValidWorksForSomeInvalidScores() {
        for (int r = 0; r <= 9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for (int j = 0; j <= 2000; ++j) {
                    assertFalse(PackedScore.isValid(1L << 56 | (long) j << 45
                            | (long) c << 36 | (long) r << 32 | (long) j << 13
                            | (long) c << 4 | (long) r));
                    assertFalse(PackedScore.isValid((long) j << 45
                            | (long) c << 36 | (long) r << 32 | 1L << 24
                            | (long) j << 13 | (long) c << 4 | (long) r));
                }
            }
        }
    }

    /**
     * pack test
     */
    @Test
    void packWorks() {
        for (int r = 0; r <= 9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for (int j = 0; j <= 2000; ++j) {
                    assertEquals(
                            (long) j << 45 | (long) c << 36 | (long) r << 32
                                    | (long) j << 13 | (long) c << 4 | (long) r,
                            PackedScore.pack(r, c, j, r, c, j));
                }
            }
        }
    }

    @Test
    void turnTricksWorks() {
        for (int r = 0; r <= 9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for (int j = 0; j <= 2000; ++j) {
                    assertEquals(r, PackedScore.turnTricks(
                            PackedScore.pack(r, c, j, r, c, j), TeamId.TEAM_1));
                    assertEquals(r, PackedScore.turnTricks(
                            PackedScore.pack(r, c, j, r, c, j), TeamId.TEAM_2));
                }
            }
        }
    }

    /*
     * turnPointsWorks
     */
    @Test
    void turnPointsWorks() {
        for (int r = 0; r <= 9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for (int j = 0; j <= 2000; ++j) {
                    assertEquals(c, PackedScore.turnPoints(
                            PackedScore.pack(r, c, j, r, c, j), TeamId.TEAM_1));
                    assertEquals(c, PackedScore.turnPoints(
                            PackedScore.pack(r, c, j, r, c, j), TeamId.TEAM_2));
                }
            }
        }
    }

    /*
     * gamePoints
     */
    @Test
    void gamePointsWorks() {
        for (int r = 0; r <= 9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for (int j = 0; j <= 2000; ++j) {
                    assertEquals(j, PackedScore.gamePoints(
                            PackedScore.pack(r, c, j, r, c, j), TeamId.TEAM_1));
                    assertEquals(j, PackedScore.gamePoints(
                            PackedScore.pack(r, c, j, r, c, j), TeamId.TEAM_2));
                }
            }
        }
    }

    /*
     * totalPoints
     */
    @Test
    void totalPointWorks() {
        for (int r = 0; r <= 9; ++r) {
            for (int c = 0; c <= 257; ++c) {
                for (int j = 0; j <= 2000; ++j) {
                    assertEquals(j + c, PackedScore.totalPoints(
                            PackedScore.pack(r, c, j, r, c, j), TeamId.TEAM_1));
                    assertEquals(j + c, PackedScore.totalPoints(
                            PackedScore.pack(r, c, j, r, c, j), TeamId.TEAM_2));
                }
            }
        }
    }

    /**
     * 
     */
    @Test
    void PackedScoreWorks() {
        long s = PackedScore.INITIAL;
        String score = PackedScore.toString(s);
        for (int i = 0; i < Jass.TRICKS_PER_TURN; ++i) {
            int p = (i == 0 ? 13 : 18);
            TeamId w = (i % 2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
            s = PackedScore.withAdditionalTrick(s, w, p);
            score += PackedScore.toString(s);
        }
        s = PackedScore.nextTurn(s);
        score += PackedScore.toString(s);
        assertEquals(
                "(0,0,0)/(0,0,0)(1,13,0)" + "/(0,0,0)(1,13,0)/(1,18,0)(2,31,0)"
                        + "/(1,18,0)(2,31,0)/(2,36,0)(3,49,0)"
                        + "/(2,36,0)(3,49,0)/(3,54,0)(4,67,0)"
                        + "/(3,54,0)(4,67,0)/(4,72,0)(5,85,0)"
                        + "/(4,72,0)(0,0,85)/(0,0,72)",
                score);
    }

}
