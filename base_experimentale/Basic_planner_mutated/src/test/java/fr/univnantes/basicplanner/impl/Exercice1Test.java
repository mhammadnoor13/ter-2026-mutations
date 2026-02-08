package fr.univnantes.basicplanner.impl;

import fr.univnantes.basicplanner.Clock;
import fr.univnantes.basicplanner.Meeting;
import fr.univnantes.basicplanner.BasicPlanner;
import fr.univnantes.basicplanner.PlannerException;
import fr.univnantes.util.TimeManagement;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

class Exercice1Test {

    ZoneId timeZone;
    Instant teamMeetingStart;
    Instant teamMeetingEnd;
    Instant fakeMeetingStart;
    Duration threeMinutes;
    Duration fifteenMinutes;
    BasicPlanner basicPlanner;

    @BeforeEach
    public void setUp() {
        // Prepare the time zone
        timeZone = ZoneId.systemDefault();

        // Prepare timings
        teamMeetingStart = TimeManagement.createInstant("2024-07-10 16:00", timeZone);
        teamMeetingEnd = TimeManagement.createInstant("2024-07-10 17:00", timeZone);
        fakeMeetingStart = TimeManagement.createInstant("2024-07-10 19:00", timeZone);

        // Prepare timings
        threeMinutes = Duration.of(3, ChronoUnit.MINUTES);
        fifteenMinutes = Duration.of(15, ChronoUnit.MINUTES);

        // Prepare system
        Clock clock = new SystemClock();
        basicPlanner = new BasicPlannerImpl(clock);
    }

    @Nested
    @DisplayName("Méthodes de test du constructeur")
    class TestConstructor {

        @Test
        @DisplayName("Test du constructeur avec une date de début null")
        public void testConstructorStartDateNull() {
            assertThrows(IllegalArgumentException.class, () -> {
                Meeting meeting = new MeetingImpl(null, teamMeetingEnd, "No starting date");
            });
        }

        @Test
        @DisplayName("Test du constructeur avec une date de début supérieure à la date de fin")
        public void testConstructorStartDateGreaterThanEnding() {
            assertThrows(IllegalArgumentException.class, () -> {
                Meeting meeting = new MeetingImpl(fakeMeetingStart, teamMeetingEnd, "Greater starting date");
            });
        }

        @Test
        @DisplayName("Test du constructeur avec une titre vide")
        public void testConstructorTitleEmpty() {
            assertThrows(IllegalArgumentException.class, () -> {
                Meeting meeting = new MeetingImpl(teamMeetingStart, teamMeetingEnd, "");
            });
        }

        @Test
        @DisplayName("Test du constructeur OK")
        public void testConstructorOK() {
            Meeting meeting = new MeetingImpl(teamMeetingStart, teamMeetingEnd, "Team meeting");
            assertEquals(teamMeetingStart, meeting.getStartTime());
            assertEquals(teamMeetingEnd, meeting.getEndTime());
            assertEquals("Team meeting", meeting.getTitle());
        }
    }

    @Nested
    @DisplayName("Méthodes de test de la méthode 'setReminder'")
    class TestSetReminder {
        @Test
        @DisplayName("Test de la méthode 'setReminder' avec un reminder déjà configuré")
        void testSetReminderAlreadyConfigured() {
            Meeting meeting = new MeetingImpl(teamMeetingStart, teamMeetingEnd, "Team meeting");
            meeting.setReminder(fifteenMinutes);

            // On supprime le reminder => problème
            meeting.setReminder(null);

            assertNull(meeting.getReminderDuration());
        }

        @Test
        @DisplayName("Test de la méthode 'setReminder' pour ajouter un reminder null")
        void testSetReminderNull() {
            Meeting meeting = new MeetingImpl(teamMeetingStart, teamMeetingEnd, "Team meeting");
            meeting.setReminder(null);

            assertNull(meeting.getReminderDuration());
        }

        @Test
        @DisplayName("Test de la méthode 'setReminder' avec un reminder plus petit que 5 minutes")
        void testSetReminderLowerThanFiveMinutes() {
            Meeting meeting = new MeetingImpl(teamMeetingStart, teamMeetingEnd, "Team meeting");

            assertThrows(IllegalArgumentException.class, () -> {
                meeting.setReminder(threeMinutes);
            });
        }

        @Test
        @DisplayName("Test de la méthode 'setReminder' OK")
        void testSetReminderOK() {
            Meeting meeting = new MeetingImpl(teamMeetingStart, teamMeetingEnd, "Team meeting");
            meeting.setReminder(fifteenMinutes);

            assertEquals(Duration.ofMinutes(15), meeting.getReminderDuration());
        }
    }

    @Nested
    @DisplayName("Méthodes de test de la méthode 'addMeeting'")
    class TestAddMeeting {

        @BeforeEach
        public void setUpAddMeeting() throws PlannerException {
            Meeting existingMeeting = new MeetingImpl(teamMeetingStart, teamMeetingEnd, "Already existing meeting");
            basicPlanner.addMeeting(existingMeeting);
        }

        @Test
        @DisplayName("Test de la méthode 'addMeeting' pour ajouter un meeting qui se termine pendant un meeting déjà existant")
        public void testAddMeetingEndingOnAnExistingMeeting() {
            Instant newMeetingStart = TimeManagement.createInstant("2024-07-10 15:30", timeZone);
            Instant newMeetingEnd = TimeManagement.createInstant("2024-07-10 16:30", timeZone);
            Meeting newMeeting = new MeetingImpl(newMeetingStart, newMeetingEnd, "New meeting");

            assertThrows(PlannerException.class, () -> {
                basicPlanner.addMeeting(newMeeting);
            });
        }

        @Test
        @DisplayName("Test de la méthode 'addMeeting' pour ajouter un meeting qui commence pendant un meeting déjà existant")
        public void testAddMeetingStartingOnAnExistingMeeting() {
            Instant newMeetingStart = TimeManagement.createInstant("2024-07-10 16:30", timeZone);
            Instant newMeetingEnd = TimeManagement.createInstant("2024-07-10 17:30", timeZone);
            Meeting newMeeting = new MeetingImpl(newMeetingStart, newMeetingEnd, "New meeting");

            assertThrows(PlannerException.class, () -> {
                basicPlanner.addMeeting(newMeeting);
            });
        }

        @Test
        @DisplayName("Test de la méthode 'addMeeting' OK")
        public void testAddMeetingOK() {
            Instant newMeetingStart = TimeManagement.createInstant("2024-07-10 14:30", timeZone);
            Instant newMeetingEnd = TimeManagement.createInstant("2024-07-10 15:15", timeZone);
            Meeting newMeeting = new MeetingImpl(newMeetingStart, newMeetingEnd, "New meeting");

            assertDoesNotThrow(() -> {
                basicPlanner.addMeeting(newMeeting);
            });
        }
    }

    @Nested
    @DisplayName("Méthodes de test de la méthode 'moveMeeting'")
    class TestMoveMeeting {

        Instant newStart;
        Instant startDuringExistingMeeting;
        Instant endDuringExistingMeeting;

        @BeforeEach
        public void setUpMoveMeeting() throws PlannerException {
            // Already in the agenda
            Meeting existingMeeting = new MeetingImpl(teamMeetingStart, teamMeetingEnd, "Already existing meeting");
            basicPlanner.addMeeting(existingMeeting);

            // New dates
            newStart = TimeManagement.createInstant("2024-07-11 10:00", timeZone);
            startDuringExistingMeeting = TimeManagement.createInstant("2024-07-10 16:30", timeZone);
            endDuringExistingMeeting = TimeManagement.createInstant("2024-07-10 15:30", timeZone);
        }

        @Test
        @DisplayName("Test de la méthode 'moveMeeting' avec un meeting null")
        public void testMoveMeetingMoveNullMeeting() {
            assertThrows(IllegalArgumentException.class, () -> {
                basicPlanner.moveMeeting(null, newStart);
            });
        }

        @Test
        @DisplayName("Test de la méthode 'moveMeeting' avec un meeting qui n'est pas dans l'agenda")
        public void testMoveMeetingNotInAgenda() {
            Instant startNotExisting = TimeManagement.createInstant("2024-07-10 10:00", timeZone);
            Instant endNotExisting = TimeManagement.createInstant("2024-07-10 11:00", timeZone);
            Meeting notExisting = new MeetingImpl(startNotExisting, endNotExisting, "Non-existing");

            assertThrows(PlannerException.class, () -> {
                basicPlanner.moveMeeting(notExisting, newStart);
            });
        }

        @Test
        @DisplayName("Test de la méthode 'moveMeeting' avec une nouvelle date qui fait débuter pendant un meeting")
        public void testMoveMeetingNewStartingDuringExistingMeeting() {
            Instant startNotExisting = TimeManagement.createInstant("2024-07-10 10:00", timeZone);
            Instant endNotExisting = TimeManagement.createInstant("2024-07-10 11:00", timeZone);
            Meeting meeting = new MeetingImpl(startNotExisting, endNotExisting, "Movable meeting");

            assertThrows(PlannerException.class, () -> {
                basicPlanner.moveMeeting(meeting, startDuringExistingMeeting);
            });
        }

        @Test
        @DisplayName("Test de la méthode 'moveMeeting' avec une nouvelle date qui fait terminer pendant un meeting")
        public void testMoveMeetingNewEndingDuringExistingMeeting() {
            Instant startNotExisting = TimeManagement.createInstant("2024-07-10 10:00", timeZone);
            Instant endNotExisting = TimeManagement.createInstant("2024-07-10 11:00", timeZone);
            Meeting meeting = new MeetingImpl(startNotExisting, endNotExisting, "Movable meeting");

            assertThrows(PlannerException.class, () -> {
                basicPlanner.moveMeeting(meeting, endDuringExistingMeeting);
            });
        }
    }
}