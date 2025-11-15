package com.example.proyecto3_.model.Game;

import com.example.proyecto3_.model.Cards.Card;
import com.example.proyecto3_.model.Deck.Deck;
import com.example.proyecto3_.model.Player.Player;
import com.example.proyecto3_.model.Exceptions.*;
import java.util.*;

/**
 * Represents the game logic with Queue and Map structures.
 * Manages players, deck, table cards, and game state.
 */
public class GameModel {
    private Deck deck;
    private List<Player> players;
    private List<Card> tableCards;
    private int tableSum;
    private int currentPlayerIndex;

    private Queue<Player> turnQueue;

    private Map<String, PlayerGameStats> playerStatsMap;

    /**
     * Inner class to represent overall game statistics.
     */
    public static class GameStats {
        private int totalTurns;
        private int cardsPlayed;
        private int playersEliminated;

        /**
         * Creates a new GameStats instance with zero values.
         */
        public GameStats() {
            this.totalTurns = 0;
            this.cardsPlayed = 0;
            this.playersEliminated = 0;
        }

        /**
         * Increments the total turns counter.
         */
        public void incrementTurns() { totalTurns++; }

        /**
         * Increments the cards played counter.
         */
        public void incrementCardsPlayed() { cardsPlayed++; }

        /**
         * Increments the players eliminated counter.
         */
        public void incrementPlayersEliminated() { playersEliminated++; }

        /**
         * Gets the total number of turns played.
         * @return total turns
         */
        public int getTotalTurns() { return totalTurns; }

        /**
         * Gets the total number of cards played.
         * @return cards played
         */
        public int getCardsPlayed() { return cardsPlayed; }

        /**
         * Gets the number of players eliminated.
         * @return players eliminated
         */
        public int getPlayersEliminated() { return playersEliminated; }
    }

    /**
     * Inner class for individual player statistics.
     */
    public static class PlayerGameStats {
        private int cardsPlayed;
        private int turnsPlayed;
        private boolean isWinner;

        /**
         * Creates a new PlayerGameStats instance with zero values.
         */
        public PlayerGameStats() {
            this.cardsPlayed = 0;
            this.turnsPlayed = 0;
            this.isWinner = false;
        }

        /**
         * Increments the cards played counter for this player.
         */
        public void incrementCardsPlayed() { cardsPlayed++; }

        /**
         * Increments the turns played counter for this player.
         */
        public void incrementTurns() { turnsPlayed++; }

        /**
         * Sets the winner status for this player.
         * @param winner true if player won, false otherwise
         */
        public void setWinner(boolean winner) { isWinner = winner; }

        /**
         * Gets the number of cards played by this player.
         * @return cards played
         */
        public int getCardsPlayed() { return cardsPlayed; }

        /**
         * Gets the number of turns played by this player.
         * @return turns played
         */
        public int getTurnsPlayed() { return turnsPlayed; }

        /**
         * Checks if this player is the winner.
         * @return true if winner, false otherwise
         */
        public boolean isWinner() { return isWinner; }

        /**
         * Returns a string representation of the player statistics.
         * @return formatted statistics string
         */
        @Override
        public String toString() {
            return "Cards: " + cardsPlayed + ", Turns: " + turnsPlayed +
                    (isWinner ? " (WINNER)" : "");
        }
    }

    private GameStats stats;

    /**
     * Creates a new game with specified number of bots.
     * Initializes deck, players, and game structures.
     * @param numBots number of machine players (1-3)
     * @throws IllegalArgumentException if numBots is not between 1 and 3
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

        this.turnQueue = new LinkedList<>();

        this.playerStatsMap = new HashMap<>();

        // Create human player
        Player human = new Player("Tú", false);
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
     * Starts the game by dealing cards and placing initial card.
     * Deals 4 cards to each player and places one card on the table.
     * @throws InvalidGameStateException if no players exist
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
     * Gets the game statistics.
     * @return the game stats
     */
    public GameStats getStats() {
        return stats;
    }

    /**
     * Gets the current player whose turn it is.
     * @return the current player
     * @throws InvalidGameStateException if player index is invalid
     */
    public Player getCurrentPlayer() {
        if (currentPlayerIndex < 0 || currentPlayerIndex >= players.size()) {
            throw new InvalidGameStateException("Invalid player index");
        }
        return players.get(currentPlayerIndex);
    }

    /**
     * Gets all players in the game.
     * @return list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the current sum of card values on the table.
     * @return the sum on the table
     */
    public int getTableSum() {
        return tableSum;
    }

    /**
     * Gets the top card on the table (last played card).
     * @return the top card, or null if table is empty
     */
    public Card getTopCard() {
        if (tableCards.isEmpty()) {
            return null;
        }
        return tableCards.get(tableCards.size() - 1);
    }

    /**
     * Plays a card from current player's hand to the table.
     * Validates the move and updates game state.
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

        PlayerGameStats playerStats = playerStatsMap.get(currentPlayer.getName());
        if (playerStats != null) {
            playerStats.incrementCardsPlayed();
        }
    }

    /**
     * Draws a card for the current player from the deck.
     * Automatically recycles table cards if deck is empty.
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
     * Recycles cards from table back to deck (except the last one).
     * Called automatically when deck is empty.
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
            card.setFaceUp(false);
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
     * Moves to the next player's turn.
     * Skips eliminated players and updates turn queue.
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
     * Eliminates the current player from the game.
     * Returns player's cards to deck and marks them as eliminated.
     * @throws NoValidCardException if player has no valid cards
     * @throws InvalidGameStateException if player can still play
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
     * Checks if the game is over.
     * Game ends when only one player remains active.
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
     * Gets the winner of the game.
     * @return the winning player, or null if game is not over
     */
    public Player getWinner() {
        if (!isGameOver()) {
            return null;
        }

        for (Player player : players) {
            if (!player.isEliminated()) {
                PlayerGameStats stats = playerStatsMap.get(player.getName());
                if (stats != null) {
                    stats.setWinner(true);
                }
                return player;
            }
        }
        return null;
    }

    /**
     * Gets the current size of the deck.
     * @return number of cards in deck
     */
    public int getDeckSize() {
        return deck.size();
    }

    /**
     * Checks if the deck is empty.
     * @return true if deck has no cards, false otherwise
     */
    public boolean isDeckEmpty() {
        return deck.isEmpty();
    }

    /**
     * Gets statistics for a specific player.
     * @param playerName the player's name
     * @return the player's statistics, or null if player not found
     */
    public PlayerGameStats getPlayerStats(String playerName) {
        return playerStatsMap.get(playerName);
    }

    /**
     * Gets all player statistics.
     * @return map of all player statistics (copy)
     */
    public Map<String, PlayerGameStats> getAllPlayerStats() {
        return new HashMap<>(playerStatsMap);
    }

    /**
     * Prints all player statistics to console.
     * Displays cards played, turns played, and winner status for each player.
     */
    public void printPlayerStats() {
        System.out.println("\n=== ESTADÍSTICAS POR JUGADOR ===");
        for (Map.Entry<String, PlayerGameStats> entry : playerStatsMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("================================\n");
    }
}