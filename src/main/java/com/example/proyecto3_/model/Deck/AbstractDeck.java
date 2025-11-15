package com.example.proyecto3_.model.Deck;

import com.example.proyecto3_.model.Cards.Card;
import java.util.*;

/**
 * Abstract class that provides the common implementation for decks.
 * Implements IDeck and defines the base structure and stack management.
 */
public abstract class AbstractDeck implements IDeck {

    protected Stack<Card> cards;

    /**
     * Creates a new abstract deck.
     */
    public AbstractDeck() {
        cards = new Stack<>();
    }

    /**
     * Initializes the deck with 52 cards.
     * Must be implemented by subclasses.
     */
    protected abstract void initializeDeck();

    @Override
    public void shuffle() {
        List<Card> tempList = new ArrayList<>(cards);
        Collections.shuffle(tempList);
        cards.clear();
        cards.addAll(tempList);
    }

    @Override
    public Card drawCard() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.pop();
    }

    @Override
    public void addCard(Card card) {
        cards.push(card);
    }

    @Override
    public void addCards(List<Card> cardsToAdd) {
        for (Card card : cardsToAdd) {
            cards.push(card);
        }
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    @Override
    public int size() {
        return cards.size();
    }

    @Override
    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    /**
     * Inner class responsible for shuffling logic to demonstrate encapsulation.
     */
    protected class Shuffler {
        public void performShuffle() {
            shuffle();
        }
    }
}
