package com.example.proyecto3_.model.Deck;

import com.example.proyecto3_.model.Cards.Card;
import java.util.List;

/**
 * Represents the base contract for all deck types.
 */
public interface IDeck {

    /**
     * Shuffles the deck.
     */
    void shuffle();

    /**
     * Draws a card from the top of the deck (Stack pop).
     * @return the drawn card, or null if deck is empty
     */
    Card drawCard();

    /**
     * Adds a single card to the deck.
     * @param card the card to add
     */
    void addCard(Card card);

    /**
     * Adds multiple cards to the deck.
     * @param cardsToAdd list of cards to add
     */
    void addCards(List<Card> cardsToAdd);

    /**
     * Checks if the deck is empty.
     * @return true if empty, false otherwise
     */
    boolean isEmpty();

    /**
     * Gets the number of cards remaining.
     * @return number of cards in deck
     */
    int size();

    /**
     * Gets the list of cards currently in the deck.
     * @return list of cards
     */
    List<Card> getCards();
}
