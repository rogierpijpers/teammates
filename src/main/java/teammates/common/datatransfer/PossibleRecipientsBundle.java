package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Rogier on 24-5-2017.
 */
public class PossibleRecipientsBundle {
    private static final Logger log = Logger.getLogger();

    /**
     * Get the possible recipients for a giver for the question specified.
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the participantIdentifier
     * @param fqa
     * @param giverParticipantIdentifier
     * @param feedbackSessionResultsBundle
     */
    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa,
                                              String giverParticipantIdentifier, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (giverParticipantIdentifier.contains("@@")) {
            return new ArrayList<String>();
        }

        if (feedbackSessionResultsBundle.isParticipantIdentifierStudent(giverParticipantIdentifier)) {
            StudentAttributes student = feedbackSessionResultsBundle.roster.getStudentForEmail(giverParticipantIdentifier);
            return getPossibleRecipients(fqa, student, feedbackSessionResultsBundle);
        } else if (feedbackSessionResultsBundle.isParticipantIdentifierInstructor(giverParticipantIdentifier)) {
            InstructorAttributes instructor = feedbackSessionResultsBundle.roster.getInstructorForEmail(giverParticipantIdentifier);
            return getPossibleRecipients(fqa, instructor, feedbackSessionResultsBundle);
        } else {
            return getPossibleRecipientsForTeam(fqa, giverParticipantIdentifier, feedbackSessionResultsBundle);
        }
    }

    /**
     * Get the possible recipients for a INSTRUCTOR giver for the question specified.
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the instructorGiver
     * @param fqa
     * @param instructorGiver
     * @param feedbackSessionResultsBundle
     */
    private List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa,
                                               InstructorAttributes instructorGiver, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();

        switch (recipientType) {
        case STUDENTS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfStudentEmails(feedbackSessionResultsBundle);
            break;
        case INSTRUCTORS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfInstructorEmails(feedbackSessionResultsBundle);
            possibleRecipients.remove(instructorGiver.email);
            break;
        case TEAMS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfTeams(feedbackSessionResultsBundle);
            break;
        case SELF:
            possibleRecipients.add(instructorGiver.email);
            break;
        case OWN_TEAM:
            possibleRecipients.add(Const.USER_TEAM_FOR_INSTRUCTOR);
            break;
        case NONE:
            possibleRecipients.add(Const.GENERAL_QUESTION);
            break;
        default:
            log.severe("Invalid recipient type specified");
            break;
        }

        return possibleRecipients;
    }

    /**
     * Get the possible recipients for a STUDENT giver for the question specified.
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the studentGiver
     * @param fqa
     * @param studentGiver
     * @param feedbackSessionResultsBundle
     */
    private List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa,
                                               StudentAttributes studentGiver, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();

        switch (recipientType) {
        case STUDENTS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfStudentEmails(feedbackSessionResultsBundle);
            possibleRecipients.remove(studentGiver.email);
            break;
        case OWN_TEAM_MEMBERS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfTeamMembersEmailsExcludingSelf(studentGiver, feedbackSessionResultsBundle);
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfTeamMembersEmails(studentGiver, feedbackSessionResultsBundle);
            break;
        case INSTRUCTORS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfInstructorEmails(feedbackSessionResultsBundle);
            break;
        case TEAMS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfTeamsExcludingOwnTeam(studentGiver, feedbackSessionResultsBundle);
            break;
        case OWN_TEAM:
            possibleRecipients.add(studentGiver.team);
            break;
        case SELF:
            possibleRecipients.add(studentGiver.email);
            break;
        case NONE:
            possibleRecipients.add(Const.GENERAL_QUESTION);
            break;
        default:
            log.severe("Invalid recipient type specified");
            break;
        }

        return possibleRecipients;
    }

    /**
     * Get the possible recipients for a TEAM giver for the question specified.
     * @return a list of possible recipients that can receive a response from giver specified by
     *         the givingTeam
     * @param fqa
     * @param givingTeam
     * @param feedbackSessionResultsBundle
     */
    private List<String> getPossibleRecipientsForTeam(FeedbackQuestionAttributes fqa,
                                                      String givingTeam, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = new ArrayList<String>();

        switch (recipientType) {
        case TEAMS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfTeams(feedbackSessionResultsBundle);
            possibleRecipients.remove(givingTeam);
            break;
        case SELF:
        case OWN_TEAM:
            possibleRecipients.add(givingTeam);
            break;
        case INSTRUCTORS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfInstructorEmails(feedbackSessionResultsBundle);
            break;
        case STUDENTS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfStudentEmails(feedbackSessionResultsBundle);
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            if (feedbackSessionResultsBundle.rosterTeamNameMembersTable.containsKey(givingTeam)) {
                Set<String> studentEmailsToNames = feedbackSessionResultsBundle.rosterTeamNameMembersTable.get(givingTeam);
                possibleRecipients = new ArrayList<String>(studentEmailsToNames);
                Collections.sort(possibleRecipients);
            }
            break;
        case NONE:
            possibleRecipients.add(Const.GENERAL_QUESTION);
            break;
        default:
            log.severe("Invalid recipient type specified");
            break;
        }

        return possibleRecipients;
    }

    public List<String> getPossibleRecipients(FeedbackQuestionAttributes fqa, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleRecipients = null;

        // use giver type to determine recipients if recipient is "self"
        if (fqa.recipientType == FeedbackParticipantType.SELF) {
            recipientType = fqa.giverType;
        }

        switch (recipientType) {
        case STUDENTS:
        case OWN_TEAM_MEMBERS:
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfStudentEmails(feedbackSessionResultsBundle);
            break;
        case INSTRUCTORS:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfInstructorEmails(feedbackSessionResultsBundle);
            break;
        case TEAMS:
        case OWN_TEAM:
            possibleRecipients = feedbackSessionResultsBundle.getSortedResponseBundle().getSortedListOfTeams(feedbackSessionResultsBundle);
            break;
        case NONE:
            possibleRecipients = new ArrayList<String>();
            possibleRecipients.add(Const.USER_NOBODY_TEXT);
            break;
        default:
            log.severe("Invalid recipient type specified");
            break;
        }

        return possibleRecipients;
    }
}
