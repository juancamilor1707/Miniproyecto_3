package com.example.proyecto3_.model.Game;

import com.example.proyecto3_.model.Cards.Card;
import com.example.proyecto3_.model.Deck.Deck;
import com.example.proyecto3_.model.Player.Player;
import com.example.proyecto3_.model.Exceptions.*;
import java.util.*;

/**
 * Represents the game logic with Queue and Map structures
 */
public class GameModel {
    private Deck deck;
    private List<Player> players;
    private List<Card> tableCards;
    private int tableSum;
    private int currentPlayerIndex;

    // ESTRUCTURA 2: QUEUE para turnos
    private Queue<Player> turnQueue;

    // ESTRUCTURA 3: MAP para estadísticas por jugador
    private Map<String, PlayerGameStats> playerStatsMap;

    /**
     * Inner class to represent game statistics
     */
    public static class GameStats {
        private int totalTurns;
        private int cardsPlayed;
        private int playersEliminated;

        public GameStats() {
            this.totalTurns = 0;
            this.cardsPlayed = 0;
            this.playersEliminated = 0;
        }

        public void incrementTurns() { totalTurns++; }
        public void incrementCardsPlayed() { cardsPlayed++; }
        public void incrementPlayersEliminated() { playersEliminated++; }

        public int getTotalTurns() { return totalTurns; }
        public int getCardsPlayed() { return cardsPlayed; }
        public int getPlayersEliminated() { return playersEliminated; }
    }

    /**
     * Inner class for individual player statistics
     */
    public static class PlayerGameStats {
        private int cardsPlayed;
        private int turnsPlayed;
        private boolean isWinner;

        public PlayerGameStats() {
            this.cardsPlayed = 0;
            this.turnsPlayed = 0;
            this.isWinner = false;
        }

        public void incrementCardsPlayed() { cardsPlayed++; }
        public void incrementTurns() { turnsPlayed++; }
        public void setWinner(boolean winner) { isWinner = winner; }

        public int getCardsPlayed() { return cardsPlayed; }
        public int getTurnsPlayed() { return turnsPlayed; }
        public boolean isWinner() { return isWinner; }

        @Override
        public String toString() {
            return "Cards: " + cardsPlayed + ", Turns: " + turnsPlayed +
                    (isWinner ? " (WINNER)" : "");
        }
    }

    private GameStats stats;

    /**
     * Creates a new game
     * @param numBots number of machine players (1-3)
     */
    public GameModel(int numBots) {
        if (numBots < 1 || numBots > 3) {
            throw new IllegalArgumentException("Number of bots must be between 1 and 3");
        }

        this.deck = new Deck();
        this.players = new ArrayList<>();
        this.tableCards = new ArrayList<>();
        this.tableSum = 0;
        this.currentPlayerIndex = 0;
        this.stats = new GameStats();

        // INICIALIZAR QUEUE
        this.turnQueue = new LinkedList<>();

        // INICIALIZAR MAP
        this.playerStatsMap = new HashMap<>();

        // Create human player
        Player human = new Player("You", false);
        players.add(human);
        turnQueue.offer(human);
        playerStatsMap.put(human.getName(), new PlayerGameStats());

        // Create machine players
        for (int i = 1; i <= numBots; i++) {
            Player bot = new Player("Bot " + i, true);
            players.add(bot);
            turnQueue.offer(bot);
            playerStatsMap.put(bot.getName(), new PlayerGameStats());
        }
    }

    /**
     * Starts the game
     */
    public void start() {
        if (players.isEmpty()) {
            throw new InvalidGameStateException("Cannot start game: no players");
        }

        // Deal 4 cards to each player
        for (int i = 0; i < 4; i++) {
            for (Player player : players) {
                Card card = deck.drawCard();
                if (card != null) {
                    player.addCard(card);
                }
            }
        }

        // Place initial card on table
        Card initialCard = deck.drawCard();
        if (initialCard != null) {
            initialCard.setFaceUp(true);
            tableCards.add(initialCard);
            tableSum = initialCard.getValue(0);
        }
    }

    /**
     * Gets the game statistics
     * @return the game stats
     */
    public GameStats getStats() {
        return stats;
    }

    /**
     * Gets the current player
     * @return the current player
     */
    public Player getCurrentPlayer() {
        if (currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
            throw new InvalidGameStateException("Invalid player index");
        }
        return players.get(currentPlayerIndex);
    }

    /**
     * Gets all players
     * @return list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the current table sum
     * @return the sum on the table
     */
    public int getTableSum() {
        return tableSum;
    }

    /**
     * Gets the top card on the table
     * @return the top card
     */
    public Card getTopCard() {
        if (tableCards.isEmpty()) {
            return null;
        }
        return tableCards.get(tableCards.size() - 1);
    }

    /**
     * Plays a card
     * @param card the card to play
     * @throws InvalidMoveException if the move is invalid
     */
    public void playCard(Card card) throws InvalidMoveException {
        if (card == null) {
            throw new InvalidMoveException("Card cannot be null");
        }

        Player currentPlayer = getCurrentPlayer();

        // Check if player has the card
        if (!currentPlayer.getHand().contains(card)) {
            throw new InvalidMoveException("Player does not have this card");
        }

        // Check if card is valid
        int newSum = tableSum + card.getValue(tableSum);
        if (newSum > 50) {
            throw new InvalidMoveException(
                    "This card would exceed 50 (current: " + tableSum + ", card: " +
                            card.getValue(tableSum) + ")"
            );
        }

        // Remove card from player's hand
        if (!currentPlayer.removeCard(card)) {
            throw new InvalidMoveException("Failed to remove card from hand");
        }

        // Add card to table
        card.setFaceUp(true);
        tableCards.add(card);
        tableSum = newSum;
        stats.incrementCardsPlayed();

        // ACTUALIZAR MAP de estadísticas
        PlayerGameStats playerStats = playerStatsMap.get(currentPlayer.getName());
        if (playerStats != null) {
            playerStats.incrementCardsPlayed();
        }
    }

    /**
     * Draws a card for the current player
     * @return the drawn card
     * @throws DeckEmptyException if deck is empty and cannot be recycled
     */
    public Card drawCard() throws DeckEmptyException {
        // Check if deck is empty
        if (deck.isEmpty()) {
            System.out.println("⚠️ Deck vacío. Reciclando cartas de la mesa...");
            recycleDeck();

            // If still empty after recycling, throw exception
            if (deck.isEmpty()) {
                System.out.println("❌ No hay cartas para reciclar. Deck sigue vacío.");
                throw new DeckEmptyException("Cannot draw card: deck is empty even after recycling");
            }
            System.out.println("✅ Reciclaje exitoso. Cartas en deck: " + deck.size());
        }

        Card card = deck.drawCard();
        if (card != null) {
            getCurrentPlayer().addCard(card);
            System.out.println(getCurrentPlayer().getName() + " robó una carta. Cartas en mano: " + getCurrentPlayer().getHand().size());
        }
        return card;
    }

    /**
     * Recycles cards from table to deck (except the last one)
     */
    private void recycleDeck() {
        System.out.println("🔄 Iniciando reciclaje. Cartas en mesa: " + tableCards.size());

        if (tableCards.size() <= 1) {
            System.out.println("⚠️ Solo hay " + tableCards.size() + " carta(s) en la mesa. No se puede reciclar.");
            return;
        }

        // Take all cards except the last one
        List<Card> cardsToRecycle = new ArrayList<>();
        for (int i = 0; i < tableCards.size() - 1; i++) {
            Card card = tableCards.get(i);
            card.setFaceUp(false); // Voltear cartas boca abajo
            cardsToRecycle.add(card);
        }

        // Keep only the last card on table
        Card lastCard = tableCards.get(tableCards.size() - 1);
        tableCards.clear();
        tableCards.add(lastCard);

        // Add cards to deck and shuffle
        deck.addCards(cardsToRecycle);
        deck.shuffle();

        System.out.println("✅ Reciclaje completado. " + cardsToRecycle.size() + " cartas agregadas al deck.");
        System.out.println("📊 Estado: Mesa=" + tableCards.size() + " carta(s), Deck=" + deck.size() + " cartas");
    }

    /**
     * Moves to the next player's turn using Queue
     */
    public void nextTurn() {
        stats.incrementTurns();

        Player current = getCurrentPlayer();
        PlayerGameStats playerStats = playerStatsMap.get(current.getName());
        if (playerStats != null) {
            playerStats.incrementTurns();
        }

        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (getCurrentPlayer().isEliminated() && !isGameOver());

        turnQueue.clear();
        for (int i = 0; i < players.size(); i++) {
            int index = (currentPlayerIndex + i) % players.size();
            Player p = players.get(index);
            if (!p.isEliminated()) {
                turnQueue.offer(p);
            }
        }
    }

    /**
     * Eliminates the current player
     * @throws NoValidCardException if player has no valid cards
     */
    public void eliminateCurrentPlayer() throws NoValidCardException {
        Player player = getCurrentPlayer();

        if (player.canPlay(tableSum)) {
            throw new InvalidGameStateException("Cannot eliminate player that can still play");
        }

        // Return cards to deck
        List<Card> cards = player.clearHand();
        deck.addCards(cards);

        // Mark as eliminated
        player.eliminate();
        stats.incrementPlayersEliminated();

        // Throw exception to notify
        throw new NoValidCardException(player.getName());
    }

    /**
     * Checks if the game is over
     * @return true if only one player remains, false otherwise
     */
    public boolean isGameOver() {
        int activePlayers = 0;
        for (Player player : players) {
            if (!player.isEliminated()) {
                activePlayers++;
            }
        }
        return activePlayers <= 1;
    }

    /**
     * Gets the winner
     * @return the winning player, or null if game is not over
     */
    public Player getWinner() {
        if (!isGameOver()) {
            return null;
        }

        for (Player player : players) {
            if (!player.isEliminated()) {
                // Marcar como ganador en el MAP
                PlayerGameStats stats = playerStatsMap.get(player.getName());
                if (stats != null) {
                    stats.setWinner(true);
                }
                return player;
            }
        }
        return null;
    }

    public int getDeckSize() {
        return deck.size();
    }

    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }

    /**
     * Gets statistics for a specific player
     * @param playerName the player's name
     * @return the player's statistics
     */
    public PlayerGameStats getPlayerStats(String playerName) {
        return playerStatsMap.get(playerName);
    }

    /**
     * Gets all player statistics
     * @return map of all player statistics
     */
    public Map<String, PlayerGameStats> getAllPlayerStats() {
        return new HashMap<>(playerStatsMap);
    }

    /**
     * Prints all player statistics to console
     */
    public void printPlayerStats() {
        System.out.println("\n=== ESTADÍSTICAS POR JUGADOR ===");
        for (Map.Entry<String, PlayerGameStats> entry : playerStatsMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("================================\n");
    }
}