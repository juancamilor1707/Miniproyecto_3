package com.example.proyecto3_.model.Deck;

import com.example.proyecto3_.model.Cards.Card;
import java.util.*;

/**
 * Represents a deck of cards using Stack (LIFO).
 */
public class Deck extends AbstractDeck {

    /**
     * Creates a new deck with 52 cards.
     */
    public Deck() {
        super();
        initializeDeck();
        shuffle();
    }

    /**
     * Initializes the deck with all 52 cards.
     */
    @Override
    protected void initializeDeck() {
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] suits = {"Hearts", "Diamonds", "Clubs", "Spades"};

        List<Card> tempCards = new ArrayList<>();
        for (String suit : suits) {
            for (String rank : ranks) {
                tempCards.add(new Card(rank, suit));
            }
        }

        cards.addAll(tempCards);
    }

    /**
     * Shuffles the deck.
     */
    @Override
    public void shuffle() {
        super.shuffle();
    }

    /**
     * Draws a card from the top of the deck (Stack pop).
     * @return the drawn card, or null if deck is empty
     */
    @Override
    public Card drawCard() {
        return super.drawCard();
    }

    /**
     * Adds a card to the deck.
     * @param card the card to add
     */
    @Override
    public void addCard(Card card) {
        super.addCard(card);
    }

    /**
     * Adds multiple cards to the deck.
     * @param cardsToAdd list of cards to add
     */
    @Override
    public void addCards(List<Card> cardsToAdd) {
        super.addCards(cardsToAdd);
    }

    /**
     * Checks if the deck is empty.
     * @return true if empty, false otherwise
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }

    /**
     * Gets the number of cards remaining.
     * @return number of cards in deck
     */
    @Override
    public int size() {
        return super.size();
    }

    /**
     * Gets the cards in the deck.
     * @return list of cards
     */
    @Override
    public List<Card> getCards() {
        return super.getCards();
    }
}
