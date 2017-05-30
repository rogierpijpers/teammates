package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Rogier on 25-5-2017.
 */
public class RosterBundle {
    String getNameFromRoster(String participantIdentifier, boolean isFullName, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (participantIdentifier.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }

        // return person name if participant is a student
        if (feedbackSessionResultsBundle.isParticipantIdentifierStudent(participantIdentifier)) {
            StudentAttributes student = feedbackSessionResultsBundle.roster.getStudentForEmail(participantIdentifier);
            if (isFullName) {
                return student.name;
            }
            return student.lastName;
        }

        // return person name if participant is an instructor
        if (feedbackSessionResultsBundle.isParticipantIdentifierInstructor(participantIdentifier)) {
            return feedbackSessionResultsBundle.roster.getInstructorForEmail(participantIdentifier)
                         .name;
        }

        // return team name if participantIdentifier is a team name
        boolean isTeamName = feedbackSessionResultsBundle.rosterTeamNameMembersTable.containsKey(participantIdentifier);
        if (isTeamName) {
            return participantIdentifier;
        }

        // return team name if participant is team identified by a member
        boolean isNameRepresentingStudentsTeam = participantIdentifier.contains(Const.TEAM_OF_EMAIL_OWNER);
        if (isNameRepresentingStudentsTeam) {
            int index = participantIdentifier.indexOf(Const.TEAM_OF_EMAIL_OWNER);
            return getTeamNameFromRoster(participantIdentifier.substring(0, index), feedbackSessionResultsBundle);
        }

        return "";
    }

    /**
     * Get the displayable full name from an email.
     *
     * <p>This function is different from {@link #getNameForEmail} as it obtains the name
     * using the class roster, instead of from the responses.
     * @return the full name of a student, if participantIdentifier is the email of a student, <br>
     *         the name of an instructor, if participantIdentifier is the email of an instructor, <br>
     *         or the team name, if participantIdentifier represents a team. <br>
     *         Otherwise, return an empty string
     * @param participantIdentifier
     * @param feedbackSessionResultsBundle
     */
    public String getFullNameFromRoster(String participantIdentifier, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getNameFromRoster(participantIdentifier, true, feedbackSessionResultsBundle);
    }

    /**
     * Get the displayable last name from an email.
     *
     * <p>This function is different from {@link #getLastNameForEmail} as it obtains the name
     * using the class roster, instead of from the responses.
     * @return the last name of a student, if participantIdentifier is the email of a student, <br>
     *         the name of an instructor, if participantIdentifier is the email of an instructor, <br>
     *         or the team name, if participantIdentifier represents a team. <br>
     *         Otherwise, return an empty string
     * @param participantIdentifier
     * @param feedbackSessionResultsBundle
     */
    public String getLastNameFromRoster(String participantIdentifier, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        return getNameFromRoster(participantIdentifier, false, feedbackSessionResultsBundle);
    }

    /**
     * Return true if the participantIdentifier is an email of either a student
     * or instructor in the course roster. Otherwise, return false.
     *
     * @return true if the participantIdentifier is an email of either a student
     *         or instructor in the course roster, false otherwise.
     * @param participantIdentifier
     * @param feedbackSessionResultsBundle
     */
    public boolean isEmailOfPersonFromRoster(String participantIdentifier, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        boolean isStudent = feedbackSessionResultsBundle.isParticipantIdentifierStudent(participantIdentifier);
        boolean isInstructor = feedbackSessionResultsBundle.isParticipantIdentifierInstructor(participantIdentifier);
        return isStudent || isInstructor;
    }

    /**
     * If the participantIdentifier identifies a student or instructor,
     * the participantIdentifier is returned.
     *
     * <p>Otherwise, Const.USER_NOBODY_TEXT is returned.
     * @see #getDisplayableEmail
     * @param participantIdentifier
     * @param feedbackSessionResultsBundle
     */
    public String getDisplayableEmailFromRoster(String participantIdentifier, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (isEmailOfPersonFromRoster(participantIdentifier, feedbackSessionResultsBundle)) {
            return participantIdentifier;
        }
        return Const.USER_NOBODY_TEXT;
    }

    /**
     * Get the displayable team name from an email.
     * If the email is not an email of someone in the class roster, an empty string is returned.
     *
     * <p>This function is different from {@link #getTeamNameForEmail} as it obtains the name
     * using the class roster, instead of from the responses.
     * @param participantIdentifier
     * @param feedbackSessionResultsBundle
     */
    public String getTeamNameFromRoster(String participantIdentifier, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (participantIdentifier.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }
        if (feedbackSessionResultsBundle.isParticipantIdentifierStudent(participantIdentifier)) {
            return feedbackSessionResultsBundle.roster.getStudentForEmail(participantIdentifier).team;
        } else if (feedbackSessionResultsBundle.isParticipantIdentifierInstructor(participantIdentifier)) {
            return Const.USER_TEAM_FOR_INSTRUCTOR;
        } else {
            return "";
        }
    }

    /**
     * Get the displayable section name from an email.
     *
     * <p>If the email is not an email of someone in the class roster, an empty string is returned.
     *
     * <p>If the email of an instructor or "%GENERAL%" is passed in, "No specific recipient" is returned.
     * @param participantIdentifier
     * @param feedbackSessionResultsBundle
     */
    public String getSectionFromRoster(String participantIdentifier, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        boolean isStudent = feedbackSessionResultsBundle.isParticipantIdentifierStudent(participantIdentifier);
        boolean isInstructor = feedbackSessionResultsBundle.isParticipantIdentifierInstructor(participantIdentifier);
        boolean participantIsGeneral = participantIdentifier.equals(Const.GENERAL_QUESTION);

        if (isStudent) {
            return feedbackSessionResultsBundle.roster.getStudentForEmail(participantIdentifier)
                         .section;
        } else if (isInstructor || participantIsGeneral) {
            return Const.NO_SPECIFIC_RECIPIENT;
        } else {
            return "";
        }
    }

    /**
     * Get the emails of the students given a teamName,
     * if teamName is "Instructors", returns the list of instructors.
     * @return a set of emails of the students in the team
     * @param teamName
     * @param feedbackSessionResultsBundle
     */
    public Set<String> getTeamMembersFromRoster(String teamName, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (!feedbackSessionResultsBundle.rosterTeamNameMembersTable.containsKey(teamName)) {
            return new HashSet<String>();
        }

        return new HashSet<String>(feedbackSessionResultsBundle.rosterTeamNameMembersTable.get(teamName));
    }

    /**
     * Get the team names in a section. <br>
     *
     * <p>Instructors are not contained in any section.
     * @return a set of team names of the teams in the section
     * @param sectionName
     * @param feedbackSessionResultsBundle
     */
    public Set<String> getTeamsInSectionFromRoster(String sectionName, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (feedbackSessionResultsBundle.rosterSectionTeamNameTable.containsKey(sectionName)) {
            return new HashSet<String>(feedbackSessionResultsBundle.rosterSectionTeamNameTable.get(sectionName));
        }
        return new HashSet<String>();
    }
}
