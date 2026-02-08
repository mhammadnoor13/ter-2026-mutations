package fr.univnantes.basicplanner.impl;

import java.time.Duration;
import java.time.Instant;

import fr.univnantes.basicplanner.Meeting;

/**
 * An implementation of {@link Meeting}.
 */
public class MeetingImpl implements Meeting {

    private Instant startTime;
    private Instant endTime;
    private String title;
    private Duration reminderDuration;

    /**
     * Create an MeetingImpl.
     * 
     * @throws IllegalArgumentException If startTime is null, or if endTime is null,
     *                                  or if startTime is before endTime, or
     *                                  startTime equals endTime, or if title is
     *                                  null or empty.
     */
    public MeetingImpl(Instant startTime, Instant endTime, String title) {

        if (startTime == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }

        if (endTime == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time cannot be after end time.");
        }

        if (startTime.equals(endTime)) {
            throw new IllegalArgumentException("Start time cannot be equal to end time.");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty.");
        }


        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
    }

  

    @Override
    public void setReminder(Duration reminderDuration) {
        if (reminderDuration != null) {
            if (reminderDuration.compareTo(Duration.ofMinutes(5)) < 0) {
                throw new IllegalArgumentException("Reminder duration cannot be smaller than 5 minutes.");
            }
            this.reminderDuration = reminderDuration;
        } else {
            // Correction apportée
            this.reminderDuration = null;
        }
    }

    @Override
    public Instant getStartTime() {
        return this.startTime;
    }

    @Override
    public Instant getEndTime() {
        return this.endTime;
    }

    @Override
    public Duration getReminderDuration() {
        return this.reminderDuration;
    }

    @Override
    public String getTitle() {
        return this.title;
    }



    @Override
    public void changeStartTime(Instant newStartTime) {
        this.startTime = newStartTime;
    }

    @Override
    public void changeEndTime(Instant newEndTime) {
        this.endTime = newEndTime;
    }

}
