package com.example.proyecto3_.model.Cards;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Card class.
 * Each test validates a specific card behavior with clear case examples.
 */
class CardTest {

    // ===== getValue() =====

    @Test
    void twoOfHeartsShouldReturnTwo() {
        var card = new Card("2", "Hearts");
        assertEquals(2, card.getValue(0));
    }

    @Test
    void tenOfSpadesShouldReturnTen() {
        var card = new Card("10", "Spades");
        assertEquals(10, card.getValue(0));
    }

    @Test
    void nineOfClubsShouldReturnZero() {
        var card = new Card("9", "Clubs");
        assertEquals(0, card.getValue(20));
    }

    @Test
    void jackShouldReturnMinusTen() {
        var card = new Card("J", "Diamonds");
        assertEquals(-10, card.getValue(30));
    }

    @Test
    void aceShouldBeTenWhenUnderFifty() {
        var card = new Card("A", "Hearts");
        assertEquals(10, card.getValue(39)); // 39 + 10 = 49 ok
    }

    @Test
    void aceShouldBeOneWhenOverFifty() {
        var card = new Card("A", "Hearts");
        assertEquals(1, card.getValue(45)); // 45 + 10 > 50
    }

    // ===== toString() =====

    @Test
    void toStringShouldShowRankAndSuit() {
        var card = new Card("7", "Clubs");
        assertEquals("7 of Clubs", card.toString());
    }

    // ===== setFaceUp() / isFaceUp() =====

    @Test
    void faceUpShouldChangeStateCorrectly() {
        var card = new Card("K", "Spades");
        assertFalse(card.isFaceUp()); // Default should be false
        card.setFaceUp(true);
        assertTrue(card.isFaceUp());
    }
}
