package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rogier on 24-5-2017.
 */
public class PossibleGiversBundle {
    private static final Logger log = Logger.getLogger();

    /**
     * Get the possible givers for a TEAM recipient for the question specified.
     * @return a list of possible givers that can give a response to the team
     *         specified as the recipient
     * @param fqa
     * @param recipientTeam
     * @param feedbackSessionResultsBundle
     */
    List<String> getPossibleGiversForTeam(FeedbackQuestionAttributes fqa,
                                          String recipientTeam, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        FeedbackParticipantType giverType = fqa.giverType;
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleGivers = new ArrayList<String>();

        if (recipientType == FeedbackParticipantType.TEAMS) {
            switch (giverType) {
            case TEAMS:
                possibleGivers = feedbackSessionResultsBundle.getSortedListOfTeams();
                break;
            case STUDENTS:
                possibleGivers = feedbackSessionResultsBundle.getSortedListOfStudentEmails();
                break;
            case INSTRUCTORS:
                possibleGivers = feedbackSessionResultsBundle.getSortedListOfInstructorEmails();
                break;
            case SELF:
                possibleGivers.add(fqa.creatorEmail);
                break;
            default:
                log.severe("Invalid giver type specified");
                break;
            }
        } else if (recipientType == FeedbackParticipantType.OWN_TEAM) {
            if (giverType == FeedbackParticipantType.TEAMS) {
                possibleGivers.add(recipientTeam);
            } else {
                possibleGivers = new ArrayList<String>(feedbackSessionResultsBundle.getTeamMembersFromRoster(recipientTeam));
            }
        }
        return possibleGivers;
    }


    /**
     * Get the possible givers for a STUDENT recipient for the question specified.
     * @return a list of possible givers that can give a response to the student
     *         specified as the recipient
     * @param fqa
     * @param studentRecipient
     * @param feedbackSessionResultsBundle
     */
    List<String> getPossibleGivers(FeedbackQuestionAttributes fqa,
                                   StudentAttributes studentRecipient, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        FeedbackParticipantType giverType = fqa.giverType;
        FeedbackParticipantType recipientType = fqa.recipientType;
        List<String> possibleGivers = new ArrayList<String>();

        switch (giverType) {
        case STUDENTS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfStudentEmails();
            break;
        case INSTRUCTORS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfTeams();
            break;
        case SELF:
            possibleGivers.add(fqa.creatorEmail);
            break;
        default:
            log.severe("Invalid giver type specified");
            break;
        }

        switch (recipientType) {
        case STUDENTS:
        case TEAMS:
            break;
        case SELF:
            possibleGivers = new ArrayList<String>();
            possibleGivers.add(studentRecipient.email);
            break;
        case OWN_TEAM_MEMBERS:
            possibleGivers.retainAll(feedbackSessionResultsBundle.getSortedListOfTeamMembersEmailsExcludingSelf(studentRecipient));
            break;
        case OWN_TEAM_MEMBERS_INCLUDING_SELF:
            possibleGivers.retainAll(feedbackSessionResultsBundle.getSortedListOfTeamMembersEmails(studentRecipient));
            break;
        default:
            break;
        }

        return possibleGivers;
    }

    /**
     * Get the possible givers for a recipient specified by its participant identifier for
     * a question.
     *
     * @return a list of participant identifiers that can give a response to the recipient specified
     * @param fqa
     * @param recipientParticipantIdentifier
     * @param feedbackSessionResultsBundle
     */
    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa,
                                          String recipientParticipantIdentifier, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (recipientParticipantIdentifier.contains("@@")) {
            return new ArrayList<String>();
        }

        if (feedbackSessionResultsBundle.isParticipantIdentifierStudent(recipientParticipantIdentifier)) {
            StudentAttributes student = feedbackSessionResultsBundle.roster.getStudentForEmail(recipientParticipantIdentifier);
            return new PossibleGiversBundle().getPossibleGivers(fqa, student, feedbackSessionResultsBundle);
        } else if (feedbackSessionResultsBundle.isParticipantIdentifierInstructor(recipientParticipantIdentifier)) {
            return new PossibleGiversBundle().getPossibleGiversForInstructor(fqa, feedbackSessionResultsBundle);
        } else if (recipientParticipantIdentifier.equals(Const.GENERAL_QUESTION)) {
            switch (fqa.giverType) {
            case STUDENTS:
                return feedbackSessionResultsBundle.getSortedListOfStudentEmails();
            case TEAMS:
                return feedbackSessionResultsBundle.getSortedListOfTeams();
            case INSTRUCTORS:
                return feedbackSessionResultsBundle.getSortedListOfInstructorEmails();
            case SELF:
                List<String> creatorEmail = new ArrayList<String>();
                creatorEmail.add(fqa.creatorEmail);
                return creatorEmail;
            default:
                log.severe("Invalid giver type specified");
                return new ArrayList<String>();
            }
        } else {
            return new PossibleGiversBundle().getPossibleGiversForTeam(fqa, recipientParticipantIdentifier, feedbackSessionResultsBundle);
        }
    }

    /**
     * Get the possible givers for a INSTRUCTOR recipient for the question specified.
     * @return a list of possible givers that can give a response to the instructor
     *         specified as the recipient
     * @param fqa
     * @param feedbackSessionResultsBundle
     */
    public List<String> getPossibleGiversForInstructor(FeedbackQuestionAttributes fqa, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        FeedbackParticipantType giverType = fqa.giverType;
        List<String> possibleGivers = new ArrayList<String>();

        switch (giverType) {
        case STUDENTS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfStudentEmails();
            break;
        case INSTRUCTORS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfTeams();
            break;
        case SELF:
            possibleGivers.add(fqa.creatorEmail);
            break;
        default:
            log.severe("Invalid giver type specified");
            break;
        }

        return possibleGivers;
    }

    public List<String> getPossibleGivers(FeedbackQuestionAttributes fqa, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        FeedbackParticipantType giverType = fqa.giverType;
        List<String> possibleGivers = new ArrayList<String>();

        switch (giverType) {
        case STUDENTS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfStudentEmails();
            break;
        case INSTRUCTORS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfInstructorEmails();
            break;
        case TEAMS:
            possibleGivers = feedbackSessionResultsBundle.getSortedListOfTeams();
            break;
        case SELF:
            possibleGivers = new ArrayList<String>();
            possibleGivers.add(fqa.creatorEmail);
            break;
        default:
            log.severe("Invalid giver type specified");
            break;
        }

        return possibleGivers;
    }
}
