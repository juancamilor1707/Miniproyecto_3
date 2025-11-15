package com.example.proyecto3_.model.Deck;

import com.example.proyecto3_.model.Cards.Card;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * Unit tests for the Deck class.
 * Each test validates different deck behaviors following the professor's example style.
 */
class DeckTest {

    // ===== drawCard() =====

    @Test
    void drawCardShouldReturnNonNullAndReduceSize() {
        var deck = new Deck();
        int initialSize = deck.size();
        var card = deck.drawCard();
        assertNotNull(card);
        assertEquals(initialSize - 1, deck.size());
    }

    @Test
    void drawAllCardsShouldEmptyDeck() {
        var deck = new Deck();
        for (int i = 0; i < 52; i++) {
            deck.drawCard();
        }
        assertTrue(deck.isEmpty());
    }

    // ===== addCard() =====

    @Test
    void addCardShouldIncreaseDeckSize() {
        var deck = new Deck();
        int before = deck.size();
        deck.addCard(new Card("K", "Hearts"));
        assertEquals(before + 1, deck.size());
    }

    // ===== shuffle() =====

    @Test
    void shuffleShouldChangeCardOrder() {
        var deck = new Deck();
        List<Card> before = new ArrayList<>(deck.getCards());
        deck.shuffle();
        List<Card> after = new ArrayList<>(deck.getCards());
        assertNotEquals(before.toString(), after.toString());
    }

    // ===== isEmpty() =====

    @Test
    void isEmptyShouldReturnFalseWhenDeckHasCards() {
        var deck = new Deck();
        assertFalse(deck.isEmpty());
    }

    @Test
    void isEmptyShouldReturnTrueAfterDrawingAllCards() {
        var deck = new Deck();
        while (!deck.isEmpty()) {
            deck.drawCard();
        }
        assertTrue(deck.isEmpty());
    }

    // ===== size() =====

    @Test
    void sizeShouldMatchNumberOfCardsRemaining() {
        var deck = new Deck();
        int initial = deck.size();
        deck.drawCard();
        deck.drawCard();
        assertEquals(initial - 2, deck.size());
    }
}
