package fr.univnantes.basicplanner.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import fr.univnantes.basicplanner.BasicPlanner;
import fr.univnantes.basicplanner.Clock;
import fr.univnantes.basicplanner.Meeting;
import fr.univnantes.basicplanner.PlannerException;
import org.apache.commons.lang3.tuple.Pair;

/**
 * An implementation of {@link BasicPlanner}.
 */
public class BasicPlannerImpl implements BasicPlanner {

    private List<Meeting> meetings;
    private Clock clock;

    /**
     * Create an empty planner with no meetings.
     * Default constructor.
     */
    public BasicPlannerImpl(Clock clock) {
        this.meetings = new ArrayList<>();
        this.clock = clock;
    }

    public Pair<Boolean, Integer> methodTest(int i, boolean b) {
        System.out.println("#######################");

        int j= 0;
        System.out.println("Debut: i=" + i + ", b=" + b + ", j=" + j);

        if(b){
            j=i*2+1;
            System.out.println("Apres incrementation: j=" + j);
            if (j>0){
                System.out.println("Retourne (true, " + j + ")");
                System.out.println("#######################");

                return Pair.of(true, j);
            }else{
                System.out.println("Retourne (false, " + j + ")");
                System.out.println("#######################");

                return Pair.of(false, j);
            }
        }
        System.out.println("Retourne (false, " + j + ")");
        System.out.println("#######################");

        return Pair.of(false, j);
    }


    @Override
    public void addMeeting(Meeting newMeeting) throws PlannerException {
        for (Meeting existingMeeting : this.meetings) {

            if (!newMeeting.getStartTime().isAfter(existingMeeting.getStartTime()) &&
                    newMeeting.getStartTime().isBefore(existingMeeting.getEndTime())
                    ||
                    newMeeting.getEndTime().isAfter(existingMeeting.getStartTime()) &&
                            newMeeting.getEndTime().isBefore(existingMeeting.getEndTime())) {

                System.out.println("Nous sommes rentrés dans la conditionnelle");

                throw new PlannerException("The target start time conflicts with an existing meeting: \""
                        + existingMeeting.getTitle() + "\"");
            }
        }
        this.meetings.add(newMeeting);
    }

    @Override
    public void removeMeeting(Meeting meeting) {
        if (meeting == null) {
            throw new IllegalArgumentException("Meeting cannot be null.");
        }
        this.meetings.remove((meeting));
    }

    @Override
    public List<Meeting> getAllMeetings() {
        return List.copyOf(this.meetings);
    }

    @Override
    public Meeting seeCurrentMeeting() {
        for (Meeting existingMeeting : this.meetings) {
            if (!clock.getCurrentTime().isAfter(existingMeeting.getStartTime())
                    && clock.getCurrentTime().isBefore(existingMeeting.getEndTime())) {
                return existingMeeting;
            }
        }
        return null;
    }

    @Override
    public List<Meeting> seeActiveReminders() {

        List<Meeting> result = new ArrayList<>();
        for (Meeting existingMeeting : this.meetings) {
            if (existingMeeting.getReminderDuration() != null
                    && clock.getCurrentTime().isBefore(existingMeeting.getStartTime())
                    && clock.getCurrentTime().isAfter(
                            existingMeeting.getStartTime().minus(existingMeeting.getReminderDuration()))) {
                result.add(existingMeeting);
            }
        }
        return result;
    }

    public void moveMeeting(Meeting meeting, Instant newStartTime) throws PlannerException {

        if (meeting == null) {
            throw new IllegalArgumentException("Meeting cannot be null.");
        }

        if (newStartTime == null) {
            throw new IllegalArgumentException("New start time cannot be null.");
        }

        if (!this.meetings.contains(meeting)) {
            throw new PlannerException("Meeting must be part of the planner.");
        }

        Duration meetingDuration = Duration.between(meeting.getStartTime(), meeting.getEndTime());
        Instant newEndTime = newStartTime.plus(meetingDuration);
        for (Meeting existingMeeting : this.meetings) {
            if (existingMeeting != meeting) {
                if (newStartTime.isAfter(existingMeeting.getStartTime()) &&
                        newStartTime.isBefore(existingMeeting.getEndTime())
                        ||
                        newEndTime.isAfter(existingMeeting.getStartTime()) &&
                                newEndTime.isBefore(existingMeeting.getEndTime())) {
                    throw new PlannerException("The target start time conflicts with an existing meeting: \""
                            + existingMeeting.getTitle() + "\"");
                }
            }
        }
        meeting.changeStartTime(newStartTime);
        meeting.changeEndTime(newEndTime);
    }

}
