package fr.univnantes.basicplanner.impl;

import fr.univnantes.basicplanner.BasicPlanner;
import fr.univnantes.basicplanner.Clock;
import fr.univnantes.basicplanner.Meeting;
import fr.univnantes.basicplanner.PlannerException;
import fr.univnantes.util.TimeManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Exercice2Test {

    Clock mockClock;
    ZoneId timeZone;
    Meeting meeting;
    BasicPlanner basicPlanner;

    @BeforeEach
    public void setUp() throws PlannerException {
        // Déclaration doublure
        mockClock = mock(Clock.class);

        // Création du meeting
        timeZone = ZoneId.systemDefault();
        Instant startMeeting = TimeManagement.createInstant("2024-07-10 11:00", timeZone);
        Instant endMeeting = TimeManagement.createInstant("2024-07-10 12:00", timeZone);
        meeting = new MeetingImpl(startMeeting, endMeeting, "Meeting");
        basicPlanner = new BasicPlannerImpl(mockClock);
        basicPlanner.addMeeting(meeting);
    }

    @Nested
    @DisplayName("Méthodes de test de la méthode 'seeCurrentMeeting'")
    class TestSeeCurrentMeeting {

        @Test
        @DisplayName("Test de la méthode 'seeCurrentMeeting' avec un meeting déjà passé")
        void testSeeCurrentMeetingMeetingAlreadyDone() {
            when(mockClock.getCurrentTime()).thenReturn(TimeManagement.createInstant("2024-07-10 13:00", timeZone));

            assertNull(basicPlanner.seeCurrentMeeting());
        }

        @Test
        @DisplayName("Test de la méthode 'seeCurrentMeeting' avec un meeting qui est en train de se dérouler")
        void testSeeCurrentMeetingCurrently() {
            when(mockClock.getCurrentTime()).thenReturn(TimeManagement.createInstant("2024-07-10 11:30", timeZone));

            assertEquals(meeting, basicPlanner.seeCurrentMeeting());
        }
    }

    @Nested
    @DisplayName("Méthodes de test de la méthode 'seeActiveReminders'")
    class TestSeeActiveReminders {

        @BeforeEach
        public void setUpActiveReminders() {
            when(mockClock.getCurrentTime()).thenReturn(TimeManagement.createInstant("2024-07-10 10:30", timeZone));
        }

        @Test
        @DisplayName("Test de la méthode 'seeActiveReminders' avec un reminder en cours")
        void testSeeActiveRemindersOneReminder() {
            meeting.setReminder(Duration.of(1, ChronoUnit.HOURS));
            List<Meeting> listMeeting = new ArrayList<Meeting>();
            listMeeting.add(meeting);

            assertEquals(listMeeting, basicPlanner.seeActiveReminders());
        }

        @Test
        @DisplayName("Test de la méthode 'seeActiveReminders' sans reminder en cours")
        void testSeeActiveRemindersNoReminders() {
            meeting.setReminder(Duration.of(15, ChronoUnit.MINUTES));
            List<Meeting> listMeeting = new ArrayList<Meeting>();

            assertEquals(listMeeting, basicPlanner.seeActiveReminders());
        }
    }
}