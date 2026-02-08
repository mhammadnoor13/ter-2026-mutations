package fr.univnantes.basicplanner.impl;

import fr.univnantes.basicplanner.Clock;
import fr.univnantes.basicplanner.BasicPlanner;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class MutationTest {

    BasicPlanner basicPlanner;

    @BeforeEach
    public void setUp() {
        // Prepare system
        Clock clock = new SystemClock();
        basicPlanner = new BasicPlannerImpl(clock);
    }

    @Nested
    @DisplayName("Méthodes de test mutation")
    class TestMutation {
        @Test
        @DisplayName("Test de la méthode")
        void testMethode1() {

            Pair<Boolean, Integer> answer = basicPlanner.methodTest(1, false);
            System.out.println("Answer: " + answer);
            assertEquals(false, answer.getLeft());
        }

        @Test
        @DisplayName("Test de la méthode")
        void testMethode2() {
            Pair<Boolean, Integer> answer = basicPlanner.methodTest(0, true);
            System.out.println("Answer: " + answer);
            assertEquals(true, answer.getLeft());
        }
        @Test
        @DisplayName("Test de la méthode")
        void testMethode3() {
            Pair<Boolean, Integer> answer = basicPlanner.methodTest(-1, true);
            System.out.println("Answer: " + answer);
            assertEquals(true, answer.getLeft());
        }
    }
}