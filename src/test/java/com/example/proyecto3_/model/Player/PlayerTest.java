package com.example.proyecto3_.model.Player;

import com.example.proyecto3_.model.Cards.Card;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Unit tests for the Player class.
 * Each test validates a specific behavior following the professor's example style.
 */
class PlayerTest {

    // ===== addCard() =====

    @Test
    void addCardShouldAddCardToHand() {
        var player = new Player("Human", false);
        var card = new Card("7", "Hearts");
        player.addCard(card);
        assertTrue(player.getHand().contains(card));
    }

    @Test
    void addCardShouldTurnFaceUpForHuman() {
        var player = new Player("Human", false);
        var card = new Card("K", "Spades");
        player.addCard(card);
        assertTrue(card.isFaceUp());
    }

    @Test
    void addCardShouldNotTurnFaceUpForMachine() {
        var player = new Player("Bot", true);
        var card = new Card("K", "Hearts");
        player.addCard(card);
        assertFalse(card.isFaceUp());
    }

    // ===== removeCard() =====

    @Test
    void removeCardShouldRemoveIfExists() {
        var player = new Player("Tester", false);
        var card = new Card("5", "Clubs");
        player.addCard(card);
        assertTrue(player.removeCard(card));
        assertFalse(player.getHand().contains(card));
    }

    @Test
    void removeCardShouldReturnFalseIfNotInHand() {
        var player = new Player("Tester", false);
        var card = new Card("Q", "Spades");
        assertFalse(player.removeCard(card));
    }

    // ===== canPlay() =====

    @Test
    void canPlayShouldReturnTrueIfAnyCardValid() {
        var player = new Player("Bot", true);
        player.addCard(new Card("3", "Hearts"));
        player.addCard(new Card("K", "Spades"));
        assertTrue(player.canPlay(40)); // puede jugar (3 o K)
    }

    @Test
    void canPlayShouldReturnFalseIfAllCardsOverLimit() {
        var player = new Player("Bot", true);
        player.addCard(new Card("10", "Hearts"));
        player.addCard(new Card("8", "Diamonds"));
        assertFalse(player.canPlay(45)); // 45 + 10 = 55, 45 + 8 = 53 -> over 50
    }

    @Test
    void canPlayShouldReturnTrueIfCardDecreasesSum() {
        var player = new Player("Bot", true);
        player.addCard(new Card("K", "Spades")); // K = -10
        assertTrue(player.canPlay(45)); // 45 - 10 = 35 <= 50
    }

    // ===== selectCard() =====

    @Test
    void selectCardShouldReturnValidCardForMachine() {
        var bot = new Player("Bot", true);
        bot.addCard(new Card("2", "Hearts"));
        bot.addCard(new Card("K", "Clubs"));
        var chosen = bot.selectCard(40);
        assertNotNull(chosen);
        assertTrue(chosen.getValue(40) + 40 <= 50);
    }

    @Test
    void selectCardShouldReturnNullForHuman() {
        var human = new Player("Human", false);
        human.addCard(new Card("4", "Hearts"));
        assertNull(human.selectCard(20));
    }

    // ===== clearHand() =====

    @Test
    void clearHandShouldReturnAllCardsAndEmptyHand() {
        var player = new Player("Human", false);
        player.addCard(new Card("8", "Diamonds"));
        player.addCard(new Card("Q", "Spades"));
        List<Card> cleared = player.clearHand();

        assertEquals(2, cleared.size());
        assertTrue(player.getHand().isEmpty());
    }
}

