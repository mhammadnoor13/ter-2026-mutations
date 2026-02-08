package fr.univnantes.basicplanner;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jdt.internal.compiler.util.Tuples;

import java.time.Instant;
import java.util.List;

/**
 * A BasicPlanner is a timetable containing a collection of {@link Meeting}s.
 */
public interface BasicPlanner {

    /**
     * Adds a meeting to the planner.
     * 
     * A meeting cannot overlap an existing meeting.
     * 
     * @param meeting The meeting to add.
     * @throws IllegalArgumentException if meeting is null.
     * @throws PlannerException         if the meeting overlaps an existing meeting.
     */
    void addMeeting(Meeting meeting) throws PlannerException;

    /**
     * Remove a meeting from the planner.
     * 
     * If the meeting is not part of the planner, nothing happens.
     * 
     * @param meeting The meeting to remove.
     * @throws IllegalArgumentException if meeting is null.
     */
    void removeMeeting(Meeting meeting);

    /**
     * Retrieve all meetings currently in the planner.
     * 
     * @return all meetings currently in the planner.
     */
    List<Meeting> getAllMeetings();

    /**
     * Move a meeting to a new start time.
     * When moved, the duration of the meeting does not change,
     * nor the other attributes of the meeting (title, etc.).
     * 
     * @param newStartTime The new start time.
     * @throws IllegalArgumentException if meeting or newStartTime is null.
     * @throws PlannerException         if the meeting is not part of the planner,
     *                                  or if the meeting overlaps another existing
     *                                  meeting.
     * 
     */
    void moveMeeting(Meeting meeting, Instant newStartTime) throws PlannerException;

    /**
     * Retrieve the meeting that is *currently* taking place, which means the
     * current time is between the meeting start and end times.
     * 
     * @return The meeting that is currently taking place, or null if no meeting is
     *         taking place.
     */
    Meeting seeCurrentMeeting();

    /**
     * Retrieve the list of all active reminders.
     * 
     * A reminder is considered active if the current time is before the meeting
     * start time, but no farther than the reminder duration.
     * 
     * @return The list of all active reminders.
     */
    List<Meeting> seeActiveReminders();

    Pair<Boolean, Integer> methodTest(int i, boolean b);

}