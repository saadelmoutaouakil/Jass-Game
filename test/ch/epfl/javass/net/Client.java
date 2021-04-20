/*
 * package ch.epfl.javass.net;
 * 
 * import java.util.HashMap; import java.util.Map;
 * 
 * import ch.epfl.javass.jass.JassGame; import ch.epfl.javass.jass.MctsPlayer;
 * import ch.epfl.javass.jass.Player; import ch.epfl.javass.jass.PlayerId;
 * 
 * class Client { public static void main(String[] args) { int seed = 2018;
 * Map<PlayerId, Player> players = new HashMap<>(); Map<PlayerId, String>
 * playerNames = new HashMap<>(); Player player; RemotePlayerClient playerHost=
 * new RemotePlayerClient("128.179.140.52");
 * 
 * for (PlayerId pId: PlayerId.ALL) { if (pId == PlayerId.PLAYER_1) {
 * players.put(pId, playerHost); playerNames.put(pId, pId.name()); } else {
 * 
 * player = new MctsPlayer(pId, seed, 10_000); players.put(pId, player);
 * playerNames.put(pId, pId.name()); } } JassGame g = new JassGame(seed,
 * players, playerNames); while (!g.isGameOver()) { g.advanceToEndOfNextTrick();
 * } try{ playerHost.close(); } catch(Exception e) {
 * System.out.println(e.getMessage()); }
 * 
 * 
 * } }
 */