package com.example.proyecto3_.model.Deck;

import com.example.proyecto3_.model.Cards.Card;
import java.util.*;

/**
 * Represents a deck of cards using Stack (LIFO)
 */
public class Deck {
    private Stack<Card> cards;

    /**
     * Creates a new deck with 52 cards
     */
    public Deck() {
        cards = new Stack<>();
        initializeDeck();
        shuffle();
    }

    /**
     * Initializes the deck with all 52 cards
     */
    private void initializeDeck() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};

        List<Card> tempCards = new ArrayList<>();
        for (String suit : suits) {
            for (String rank : ranks) {
                tempCards.add(new Card(rank, suit));
            }
        }

        // Agregar todas al stack
        cards.addAll(tempCards);
    }

    /**
     * Shuffles the deck
     */
    public void shuffle() {
        List<Card> tempList = new ArrayList<>(cards);
        Collections.shuffle(tempList);
        cards.clear();
        cards.addAll(tempList);
    }

    /**
     * Draws a card from the top of the deck (Stack pop)
     * @return the drawn card, or null if deck is empty
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.pop();  // LIFO - Saca del tope
    }

    /**
     * Adds a card to the deck
     * @param card the card to add
     */
    public void addCard(Card card) {
        cards.push(card);  // Push al stack
    }

    /**
     * Adds multiple cards to the deck
     * @param cardsToAdd list of cards to add
     */
    public void addCards(List<Card> cardsToAdd) {
        for (Card card : cardsToAdd) {
            cards.push(card);
        }
    }

    /**
     * Checks if the deck is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Gets the number of cards remaining
     * @return number of cards in deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Gets the cards in the deck
     * @return list of cards
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }
}