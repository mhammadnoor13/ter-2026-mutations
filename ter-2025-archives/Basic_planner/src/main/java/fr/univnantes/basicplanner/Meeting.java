package fr.univnantes.basicplanner;

import java.time.Duration;
import java.time.Instant;

/**
 * A meeting stored in a {@link Planner}.
 * 
 */
public interface Meeting {

    /**
     * The start time of the meeting.
     * 
     * @return the start time of the meeting.
     */
    Instant getStartTime();

    /**
     * The end time of the meeting.
     * 
     * @return the end time of the meeting.
     */
    Instant getEndTime();

    /**
     * How long before the meeting should a reminder be triggered.
     * 
     * For example, if a meeting is at 12:00 and the reminder duration is 15
     * minutes, then the reminder should be triggered at 11:45.
     * 
     * @return the reminder duration.
     */
    Duration getReminderDuration();

    /**
     * The meeting title.
     * 
     * @return the meeting title?
     */
    String getTitle();

    /**
     * Sets a reminder to the meeting.
     * 
     * If null, removes the reminder from the Meeting.
     * 
     * @param reminderDuration The reminder duration.
     * @throws IllegalArgumentException if reminderDuration is _not_ null *and*
     *                                  smaller than 5 minutes.
     */
    public void setReminder(Duration reminderDuration);

    /**
     * Change the start time.
     * 
     * CAUTION: does not check that the new start time is valid.
     * 
     * @param newStartTime The new start time of the meeting.
     */
    void changeStartTime(Instant newStartTime);

    /**
     * 
     * Change the end time.
     * 
     * CAUTION: does not check that the new end time is valid.
     * 
     * @param newStartTime The new end time of the meeting.
     */
    void changeEndTime(Instant newEndTime);

}