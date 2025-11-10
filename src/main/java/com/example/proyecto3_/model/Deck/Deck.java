package com.example.proyecto3_.model.Deck;

import com.example.proyecto3_.model.Cards.Card;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of cards
 */
public class Deck {
    private List<Card> cards;

    /**
     * Creates a new deck with 52 cards
     */
    public Deck() {
        cards = new ArrayList<>();
        initializeDeck();
        shuffle();
    }

    /**
     * Initializes the deck with all 52 cards
     */
    private void initializeDeck() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    /**
     * Shuffles the deck
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws a card from the deck
     * @return the drawn card, or null if deck is empty
     */
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.remove(0);
    }

    /**
     * Adds a card to the bottom of the deck
     * @param card the card to add
     */
    public void addCard(Card card) {
        cards.add(card);
    }

    /**
     * Adds multiple cards to the deck
     * @param cardsToAdd list of cards to add
     */
    public void addCards(List<Card> cardsToAdd) {
        cards.addAll(cardsToAdd);
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