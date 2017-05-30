package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.comparators.*;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StringHelper;

/**
 * Represents detailed results for an feedback session.
 * <br> Contains:
 * <br> * The basic {@link FeedbackSessionAttributes}
 * <br> * {@link List} of viewable responses as {@link FeedbackResponseAttributes} objects.
 */
public class FeedbackSessionResultsBundle {

    private static final Logger log = Logger.getLogger();

    private PossibleGiversBundle possibleGiversBundle;
    private PossibleRecipientsBundle possibleRecipientsBundle;
    private SortedResponseBundle sortedResponseBundle;
    private QuestionResponseMapBundle questionResponseMapBundle;
    private RosterBundle rosterBundle;

    public FeedbackSessionAttributes feedbackSession;
    public List<FeedbackResponseAttributes> responses;
    public Map<String, FeedbackQuestionAttributes> questions;
    public Map<String, String> emailNameTable;
    public Map<String, String> emailLastNameTable;
    public Map<String, String> emailTeamNameTable;
    public Map<String, Set<String>> rosterTeamNameMembersTable;
    public Map<String, Set<String>> rosterSectionTeamNameTable;
    public Map<String, boolean[]> visibilityTable;
    public FeedbackSessionResponseStatus responseStatus;
    public CourseRoster roster;
    public Map<String, List<FeedbackResponseCommentAttributes>> responseComments;
    private boolean isComplete;

    /**
     * Responses with identities of giver/recipients NOT hidden.
     * To be used for anonymous result calculation only, and identities hidden before showing to users.
     */
    public List<FeedbackResponseAttributes> actualResponses;

    public PossibleGiversBundle getPossibleGiversBundle() {
        return possibleGiversBundle;
    }

    public void setPossibleGiversBundle(PossibleGiversBundle possibleGiversBundle) {
        this.possibleGiversBundle = possibleGiversBundle;
    }

    public PossibleRecipientsBundle getPossibleRecipientsBundle() {
        return possibleRecipientsBundle;
    }

    public void setPossibleRecipientsBundle(PossibleRecipientsBundle possibleRecipientsBundle) {
        this.possibleRecipientsBundle = possibleRecipientsBundle;
    }

    public QuestionResponseMapBundle getQuestionResponseMapBundle() {
        return questionResponseMapBundle;
    }

    public void setQuestionResponseMapBundle(QuestionResponseMapBundle questionResponseMapBundle) {
        this.questionResponseMapBundle = questionResponseMapBundle;
    }

    public SortedResponseBundle getSortedResponseBundle() {
        return sortedResponseBundle;
    }

    public void setSortedResponseBundle(SortedResponseBundle sortedResponseBundle) {
        this.sortedResponseBundle = sortedResponseBundle;
    }

    public RosterBundle getRosterBundle(){ return rosterBundle; };

    public void setRosterBundle(RosterBundle rosterBundle){
        this.rosterBundle = rosterBundle;
    }

    // For contribution questions.
    // Key is questionId, value is a map of student email to StudentResultSumary
    public Map<String, Map<String, StudentResultSummary>> contributionQuestionStudentResultSummary =
            new HashMap<String, Map<String, StudentResultSummary>>();
    // Key is questionId, value is a map of team name to TeamEvalResult
    public Map<String, Map<String, TeamEvalResult>> contributionQuestionTeamEvalResults =
            new HashMap<String, Map<String, TeamEvalResult>>();

    /*
     * sectionTeamNameTable takes into account the section viewing privileges of the logged-in instructor
     * and the selected section for viewing
     * whereas rosterSectionTeamNameTable doesn't.
     * As a result, sectionTeamNameTable only contains sections viewable to the logged-in instructor
     * whereas rosterSectionTeamNameTable contains all sections in the course.
     * As sectionTeamNameTable is dependent on instructor privileges,
     * it can only be used for instructor pages and not for student pages
    */



    public Map<String, Set<String>> sectionTeamNameTable;

    // Sorts by giverName > recipientName > qnNumber
    // General questions and team questions at the bottom.
    public Comparator<FeedbackResponseAttributes> compareByGiverRecipientQuestion =
            new CompareByGiverRecipientQuestionComparator(this);

    // Sorts by giverName > recipientName
    Comparator<FeedbackResponseAttributes> compareByGiverRecipient =
            new CompareByGiverRecipientComparator(this);

    // Sorts by teamName > giverName > recipientName > qnNumber
    private Comparator<FeedbackResponseAttributes> compareByTeamGiverRecipientQuestion =
            new CompareByTeamGiverRecipientQuestionComparator(this);

    // Sorts by recipientName > giverName > qnNumber
    private Comparator<FeedbackResponseAttributes> compareByRecipientGiverQuestion =
            new CompareByRecipientGiverQuestionComparator(this);

    // Sorts by teamName > recipientName > giverName > qnNumber
    private Comparator<FeedbackResponseAttributes> compareByTeamRecipientGiverQuestion =
            new CompareByTeamRecipientGiverQuestionComparator(this);

    // Sorts by giverName > question > recipientTeam > recipientName
    private Comparator<FeedbackResponseAttributes> compareByGiverQuestionTeamRecipient =
            new CompareByGiverQuestionTeamRecipientComparator(this);

    // Sorts by giverTeam > giverName > question > recipientTeam > recipientName
    private Comparator<FeedbackResponseAttributes> compareByTeamGiverQuestionTeamRecipient =
            new CompareByTeamGiverQuestionTeamRecipientComparator(this);

    // Sorts by recipientName > question > giverTeam > giverName
    final Comparator<FeedbackResponseAttributes> compareByRecipientQuestionTeamGiver =
            new CompareByRecipientQuestionTeamGiverComparator(this);

    // Sorts by recipientTeam > recipientName > question > giverTeam > giverName
    Comparator<FeedbackResponseAttributes> compareByTeamRecipientQuestionTeamGiver =
            new CompareByTeamRecipientQuestionTeamGiverComparator(this);

    // Sorts by recipientTeam > question > recipientName > giverTeam > giverName
    Comparator<FeedbackResponseAttributes> compareByTeamQuestionRecipientTeamGiver =
            new CompareByTeamQuestionRecipientTeamGiverComparator(this);

    // Sorts by giverTeam > question > giverName > recipientTeam > recipientName
    Comparator<FeedbackResponseAttributes> compareByTeamQuestionGiverTeamRecipient =
            new CompareByTeamQuestionGiverTeamRecipientComparator(this);

    // Sorts by recipientName > recipientEmail > giverName > giverEmail
    Comparator<FeedbackResponseAttributes> compareByRecipientNameEmailGiverNameEmail =
            new CompareByRecipientNameEmailGiverNameEmailComparator(this);

    public FeedbackSessionResultsBundle(FeedbackSessionAttributes feedbackSession,
            Map<String, FeedbackQuestionAttributes> questions, CourseRoster roster) {
        this(feedbackSession, new ArrayList<FeedbackResponseAttributes>(), questions, new HashMap<String, String>(),
             new HashMap<String, String>(), new HashMap<String, String>(), new HashMap<String, Set<String>>(),
             new HashMap<String, boolean[]>(), new FeedbackSessionResponseStatus(), roster,
             new HashMap<String, List<FeedbackResponseCommentAttributes>>());
        createBundles();
    }

    public FeedbackSessionResultsBundle(FeedbackSessionAttributes feedbackSession,
                                        List<FeedbackResponseAttributes> responses,
                                        Map<String, FeedbackQuestionAttributes> questions,
                                        Map<String, String> emailNameTable,
                                        Map<String, String> emailLastNameTable,
                                        Map<String, String> emailTeamNameTable,
                                        Map<String, Set<String>> sectionTeamNameTable,
                                        Map<String, boolean[]> visibilityTable,
                                        FeedbackSessionResponseStatus responseStatus,
                                        CourseRoster roster,
                                        Map<String, List<FeedbackResponseCommentAttributes>> responseComments) {
        this(feedbackSession, responses, questions, emailNameTable, emailLastNameTable,
             emailTeamNameTable, sectionTeamNameTable, visibilityTable, responseStatus, roster, responseComments, true);
        createBundles();
    }

    public FeedbackSessionResultsBundle(FeedbackSessionAttributes feedbackSession,
                                        List<FeedbackResponseAttributes> responses,
                                        Map<String, FeedbackQuestionAttributes> questions,
                                        Map<String, String> emailNameTable,
                                        Map<String, String> emailLastNameTable,
                                        Map<String, String> emailTeamNameTable,
                                        Map<String, Set<String>> sectionTeamNameTable,
                                        Map<String, boolean[]> visibilityTable,
                                        FeedbackSessionResponseStatus responseStatus,
                                        CourseRoster roster,
                                        Map<String, List<FeedbackResponseCommentAttributes>> responseComments,
                                        boolean isComplete) {
        this.feedbackSession = feedbackSession;
        this.questions = questions;
        this.responses = responses;
        this.emailNameTable = emailNameTable;
        this.emailLastNameTable = emailLastNameTable;
        this.emailTeamNameTable = emailTeamNameTable;
        this.sectionTeamNameTable = sectionTeamNameTable;
        this.visibilityTable = visibilityTable;
        this.responseStatus = responseStatus;
        this.roster = roster;
        this.responseComments = responseComments;
        this.actualResponses = new ArrayList<FeedbackResponseAttributes>();

        // We change user email to team name here for display purposes.
        for (FeedbackResponseAttributes response : responses) {
            if (questions.get(response.feedbackQuestionId).giverType == FeedbackParticipantType.TEAMS
                    && roster.isStudentInCourse(response.giver)) {
                // for TEAMS giver type, for older responses,
                // the giverEmail is stored as the student giver's email in the database
                // so we convert it to the team name for use in FeedbackSessionResultsBundle
                response.giver = emailNameTable.get(response.giver + Const.TEAM_OF_EMAIL_OWNER);
            }
            // Copy the data before hiding response recipient and giver.
            FeedbackResponseAttributes fraCopy = new FeedbackResponseAttributes(response);
            actualResponses.add(fraCopy);
        }
        this.isComplete = isComplete;

        hideResponsesGiverRecipient();
        // unlike emailTeamNameTable, emailLastNameTable and emailTeamNameTable,
        // roster.*Table is populated using the CourseRoster data directly
        this.rosterTeamNameMembersTable = getTeamNameToEmailsTableFromRoster(roster);
        this.rosterSectionTeamNameTable = getSectionToTeamNamesFromRoster(roster);
        createBundles();
    }

    private void createBundles(){
        this.possibleGiversBundle = new PossibleGiversBundle();
        this.possibleRecipientsBundle = new PossibleRecipientsBundle();
        this.questionResponseMapBundle = new QuestionResponseMapBundle();
        this.sortedResponseBundle = new SortedResponseBundle();
        this.rosterBundle = new RosterBundle();
    }

    /**
     * Hides response names/emails and teams that are not visible to the current user.
     * Replaces the giver/recipient email in responses to an email with two "@@"s
     * to indicate it is invalid and should not be displayed.
     */
    private void hideResponsesGiverRecipient() {
        for (FeedbackResponseAttributes response : responses) {
            // Hide recipient details if its not visible to the current user
            String name = emailNameTable.get(response.recipient);
            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            FeedbackParticipantType participantType = question.recipientType;

            if (!isRecipientVisible(response)) {
                String anonEmail = getAnonEmail(participantType, name);
                name = getAnonName(participantType, name);

                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);

                response.recipient = anonEmail;
            }

            // Hide giver details if its not visible to the current user
            name = emailNameTable.get(response.giver);
            participantType = question.giverType;

            if (!isGiverVisible(response)) {
                String anonEmail = getAnonEmail(participantType, name);
                name = getAnonName(participantType, name);

                emailNameTable.put(anonEmail, name);
                emailTeamNameTable.put(anonEmail, name + Const.TEAM_OF_EMAIL_OWNER);
                if (participantType == FeedbackParticipantType.TEAMS) {
                    emailTeamNameTable.put(anonEmail, name);
                }
                response.giver = anonEmail;
            }
        }
    }

    /**
     * Checks if the giver/recipient for a response is visible/hidden from the current user.
     */
    public boolean isFeedbackParticipantVisible(boolean isGiver, FeedbackResponseAttributes response) {
        FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
        FeedbackParticipantType participantType;
        String responseId = response.getId();

        boolean isVisible;
        if (isGiver) {
            isVisible = visibilityTable.get(responseId)[Const.VISIBILITY_TABLE_GIVER];
            participantType = question.giverType;
        } else {
            isVisible = visibilityTable.get(responseId)[Const.VISIBILITY_TABLE_RECIPIENT];
            participantType = question.recipientType;
        }
        boolean isTypeSelf = participantType == FeedbackParticipantType.SELF;
        boolean isTypeNone = participantType == FeedbackParticipantType.NONE;

        return isVisible || isTypeSelf || isTypeNone;
    }

    /**
     * Returns true if the recipient from a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isRecipientVisible(FeedbackResponseAttributes response) {
        return isFeedbackParticipantVisible(false, response);
    }

    /**
     * Returns true if the giver from a response is visible to the current user.
     * Returns false otherwise.
     */
    public boolean isGiverVisible(FeedbackResponseAttributes response) {
        return isFeedbackParticipantVisible(true, response);
    }

    public static String getAnonEmail(FeedbackParticipantType type, String name) {
        String anonName = getAnonName(type, name);
        return anonName + "@@" + anonName + ".com";
    }

    public String getAnonEmailFromStudentEmail(String studentEmail) {
        String name = roster.getStudentForEmail(studentEmail).name;
        return getAnonEmail(FeedbackParticipantType.STUDENTS, name);
    }

    public String getAnonNameWithoutNumericalId(FeedbackParticipantType type) {
        return "Anonymous " + type.toSingularFormString();
    }

    public static String getAnonName(FeedbackParticipantType type, String name) {
        String hashedEncryptedName = getHashOfName(getEncryptedName(name));
        String participantType = type.toSingularFormString();
        return String.format("Anonymous %s %s", participantType, hashedEncryptedName);
    }

    private static String getEncryptedName(String name) {
        return StringHelper.encrypt(name);
    }

    private static String getHashOfName(String name) {
        return Long.toString(Math.abs((long) name.hashCode()));
    }

    public boolean isParticipantIdentifierStudent(String participantIdentifier) {
        StudentAttributes student = roster.getStudentForEmail(participantIdentifier);
        return student != null;
    }

    public boolean isParticipantIdentifierInstructor(String participantIdentifier) {
        InstructorAttributes instructor = roster.getInstructorForEmail(participantIdentifier);
        return instructor != null;
    }

    // TODO code duplication between this function and in FeedbackQuestionsLogic getRecipientsForQuestion

    /**
     * Used for instructor feedback results views.
     */
    public String getResponseAnswerHtml(FeedbackResponseAttributes response,
                                        FeedbackQuestionAttributes question) {
        return response.getResponseDetails().getAnswerHtml(response, question, this);
    }

    public String getResponseAnswerCsv(FeedbackResponseAttributes response,
                                       FeedbackQuestionAttributes question) {
        return response.getResponseDetails().getAnswerCsv(response, question, this);
    }

    public FeedbackResponseAttributes getActualResponse(FeedbackResponseAttributes response) {
        FeedbackResponseAttributes actualResponse = null;
        for (FeedbackResponseAttributes resp : actualResponses) {
            if (resp.getId().equals(response.getId())) {
                actualResponse = resp;
                break;
            }
        }
        return actualResponse;
    }

    public String getNameForEmail(String email) {
        String name = emailNameTable.get(email);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else if (name.equals(Const.USER_IS_TEAM)) {
            return getTeamNameForEmail(email);
        } else {
            return name;
        }
    }

    public String getLastNameForEmail(String email) {
        String name = emailLastNameTable.get(email);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else if (name.equals(Const.USER_IS_TEAM)) {
            return getTeamNameForEmail(email);
        } else {
            return name;
        }
    }

    public String getTeamNameForEmail(String email) {
        String teamName = emailTeamNameTable.get(email);
        if (teamName == null || email.equals(Const.GENERAL_QUESTION)) {
            return Const.USER_NOBODY_TEXT;
        }
        return teamName;
    }

    /**
     * Returns displayable email if the email of a giver/recipient in the course
     * and it is allowed to be displayed.
     * Returns Const.USER_NOBODY_TEXT otherwise.
     */
    public String getDisplayableEmail(boolean isGiver, FeedbackResponseAttributes response) {
        String participantIdentifier;
        if (isGiver) {
            participantIdentifier = response.giver;
        } else {
            participantIdentifier = response.recipient;
        }

        if (isEmailOfPerson(participantIdentifier) && isFeedbackParticipantVisible(isGiver, response)) {
            return participantIdentifier;
        }
        return Const.USER_NOBODY_TEXT;
    }

    /**
     * Returns displayable email if the email of a recipient in the course
     * and it is allowed to be displayed.
     * Returns Const.USER_NOBODY_TEXT otherwise.
     */
    public String getDisplayableEmailRecipient(FeedbackResponseAttributes response) {
        return getDisplayableEmail(false, response);
    }

    /**
     * Returns displayable email if the email of a giver in the course
     * and it is allowed to be displayed.
     * Returns Const.USER_NOBODY_TEXT otherwise.
     */
    public String getDisplayableEmailGiver(FeedbackResponseAttributes response) {
        return getDisplayableEmail(true, response);
    }

    /**
     * Returns true if the given identifier is an email of a person in the course.
     * Returns false otherwise.
     */
    public boolean isEmailOfPerson(String participantIdentifier) {
        // An email must at least contains '@' character
        boolean isIdentifierEmail = participantIdentifier.contains("@");

        /*
         * However, a team name may also contains '@'
         * To differentiate a team name and an email of a person,
         * we check against the name & team name associated by the participant identifier
         */
        String name = emailNameTable.get(participantIdentifier);
        boolean isIdentifierName = name != null && name.equals(participantIdentifier);
        boolean isIdentifierTeam = name != null && name.equals(Const.USER_IS_TEAM);

        String teamName = emailTeamNameTable.get(participantIdentifier);
        boolean isIdentifierTeamName = teamName != null && teamName.equals(participantIdentifier);
        return isIdentifierEmail && !(isIdentifierName || isIdentifierTeamName || isIdentifierTeam);
    }

    public String getRecipientNameForResponse(FeedbackResponseAttributes response) {
        String name = emailNameTable.get(response.recipient);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return name;
        }
    }

    public String getGiverNameForResponse(FeedbackResponseAttributes response) {
        String name = emailNameTable.get(response.giver);
        if (name == null || name.equals(Const.USER_IS_MISSING)) {
            return Const.USER_UNKNOWN_TEXT;
        } else if (name.equals(Const.USER_IS_NOBODY)) {
            return Const.USER_NOBODY_TEXT;
        } else {
            return name;
        }
    }

    public String appendTeamNameToName(String name, String teamName) {
        String outputName;
        if (name.contains("Anonymous") || name.equals(Const.USER_UNKNOWN_TEXT)
                || name.equals(Const.USER_NOBODY_TEXT) || teamName.isEmpty()) {
            outputName = name;
        } else {
            outputName = name + " (" + teamName + ")";
        }
        return outputName;
    }

    // TODO consider removing this to increase cohesion
    public String getQuestionText(String feedbackQuestionId) {
        return SanitizationHelper.sanitizeForHtml(questions.get(feedbackQuestionId)
                                                  .getQuestionDetails()
                                                  .getQuestionText());
    }

    // TODO: make responses to the student calling this method always on top.

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node
     * using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by recipientName > giverName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String recipientName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String giverName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by recipient's name > giver's name > question number.
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByRecipient() {
        return getResponsesSortedByRecipient(false);
    }

    public Map<String, Map<String, List<FeedbackResponseAttributes>>>
            getResponsesSortedByRecipient(boolean sortByTeam) {
        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamRecipientGiverQuestion);
        } else {
            Collections.sort(responses, compareByRecipientGiverQuestion);
        }

        for (FeedbackResponseAttributes response : responses) {
            String recipientName = this.getRecipientNameForResponse(response);
            String recipientTeamName = this.getTeamNameForEmail(response.recipient);
            String recipientNameWithTeam = this.appendTeamNameToName(recipientName, recipientTeamName);
            if (!sortedMap.containsKey(recipientNameWithTeam)) {
                sortedMap.put(recipientNameWithTeam,
                        new LinkedHashMap<String, List<FeedbackResponseAttributes>>());
            }
            Map<String, List<FeedbackResponseAttributes>> responsesToOneRecipient =
                                            sortedMap.get(recipientNameWithTeam);

            String giverName = this.getGiverNameForResponse(response);
            String giverTeamName = this.getTeamNameForEmail(response.giver);
            String giverNameWithTeam = this.appendTeamNameToName(giverName, giverTeamName);
            if (!responsesToOneRecipient.containsKey(giverNameWithTeam)) {
                responsesToOneRecipient.put(giverNameWithTeam, new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                                            responsesToOneRecipient.get(giverNameWithTeam);
            responsesFromOneGiverToOneRecipient.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node
     * using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by recipientName > giverName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String recipientName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String giverName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by recipient identifier > giver identifier > question number.
     * @see #getResponsesSortedByRecipient
     */
    public LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>
            getResponsesSortedByRecipientGiverQuestion(boolean sortByTeam) {

        LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamRecipientGiverQuestion);
        } else {
            Collections.sort(responses, compareByRecipientGiverQuestion);
        }

        for (FeedbackResponseAttributes response : responses) {
            String recipientEmail = response.recipient;
            if (!sortedMap.containsKey(recipientEmail)) {
                sortedMap.put(recipientEmail,
                              new LinkedHashMap<String, List<FeedbackResponseAttributes>>());
            }
            Map<String, List<FeedbackResponseAttributes>> responsesToOneRecipient =
                                            sortedMap.get(recipientEmail);

            String giverEmail = response.giver;
            if (!responsesToOneRecipient.containsKey(giverEmail)) {
                responsesToOneRecipient.put(giverEmail, new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                                            responsesToOneRecipient.get(giverEmail);
            responsesFromOneGiverToOneRecipient.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns responses as a {@code Map<giverName, Map<question, List<response>>>}
     * Where the responses are sorted in the order of giver, question, recipient.
     * @return responses sorted by Giver > Question > Recipient
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
                getResponsesSortedByGiverQuestionRecipient(boolean sortByTeam) {
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamGiverQuestionTeamRecipient);
        } else {
            Collections.sort(responses, compareByGiverQuestionTeamRecipient);
        }

        for (FeedbackResponseAttributes response : responses) {
            String giverEmail = response.giver;
            if (!sortedMap.containsKey(giverEmail)) {
                sortedMap.put(giverEmail,
                        new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>());
            }
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromOneGiver =
                                            sortedMap.get(giverEmail);

            FeedbackQuestionAttributes question = questions.get(response.feedbackQuestionId);
            if (!responsesFromOneGiver.containsKey(question)) {
                responsesFromOneGiver.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverOneQuestion =
                                            responsesFromOneGiver.get(question);
            responsesFromOneGiverOneQuestion.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node
     * using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by giverName > recipientName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String giverName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String recipientName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by giver's name > recipient's name > question number.
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByGiver() {
        return getResponsesSortedByGiver(false);
    }

    public Map<String, Map<String, List<FeedbackResponseAttributes>>>
            getResponsesSortedByGiver(boolean sortByTeam) {
        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

        if (sortByTeam) {
            Collections.sort(responses, compareByTeamGiverRecipientQuestion);
        } else {
            Collections.sort(responses, compareByGiverRecipientQuestion);
        }

        for (FeedbackResponseAttributes response : responses) {
            String giverName = this.getGiverNameForResponse(response);
            String giverTeamName = this.getTeamNameForEmail(response.giver);
            String giverNameWithTeam = this.appendTeamNameToName(giverName, giverTeamName);
            if (!sortedMap.containsKey(giverNameWithTeam)) {
                sortedMap.put(giverNameWithTeam,
                              new LinkedHashMap<String, List<FeedbackResponseAttributes>>());
            }
            Map<String, List<FeedbackResponseAttributes>> responsesFromOneGiver = sortedMap.get(giverNameWithTeam);

            String recipientName = this.getRecipientNameForResponse(response);
            String recipientTeamName = this.getTeamNameForEmail(response.recipient);
            String recipientNameWithTeam = this.appendTeamNameToName(recipientName, recipientTeamName);
            if (!responsesFromOneGiver.containsKey(recipientNameWithTeam)) {
                responsesFromOneGiver.put(recipientNameWithTeam,
                                          new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                    responsesFromOneGiver.get(recipientNameWithTeam);
            responsesFromOneGiverToOneRecipient.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns the responses in this bundle as a {@code Tree} structure with no base node
     * using a {@code LinkedHashMap} implementation.
     * <br>The tree is sorted by giverName > recipientName > questionNumber.
     * <br>The key of each map represents the parent node, while the value represents the leaf.
     * <br>The top-most parent {@code String giverName} is the recipient's name of all it's leafs.
     * <br>The inner parent {@code String recipientName} is the giver's name of all it's leafs.
     * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
     * <br>with attributes corresponding to it's parents.
     * @return The responses in this bundle sorted by giver's identifier > recipient's identifier > question number.
     * @see #getResponsesSortedByGiver
     */
    public Map<String, Map<String, List<FeedbackResponseAttributes>>>
                getResponsesSortedByGiverRecipientQuestion(boolean sortByTeam) {
        if (sortByTeam) {
            Collections.sort(responses, compareByTeamGiverRecipientQuestion);
        } else {
            Collections.sort(responses, compareByGiverRecipientQuestion);
        }

        Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
                                        new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

        for (FeedbackResponseAttributes response : responses) {
            String giverEmail = response.giver;
            if (!sortedMap.containsKey(giverEmail)) {
                sortedMap.put(giverEmail,
                              new LinkedHashMap<String, List<FeedbackResponseAttributes>>());
            }
            Map<String, List<FeedbackResponseAttributes>> responsesFromOneGiver = sortedMap.get(giverEmail);

            String recipientEmail = response.recipient;
            if (!responsesFromOneGiver.containsKey(recipientEmail)) {
                responsesFromOneGiver.put(recipientEmail,
                                          new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
                                            responsesFromOneGiver.get(recipientEmail);
            responsesFromOneGiverToOneRecipient.add(response);
        }

        return sortedMap;
    }

    public boolean isStudentHasSomethingNewToSee(StudentAttributes student) {
        for (FeedbackResponseAttributes response : responses) {
            // There is a response not written by the student
            // which is visible to the student
            if (!response.giver.equals(student.email)) {
                return true;
            }
            // There is a response comment visible to the student
            if (responseComments.containsKey(response.getId())) {
                return true;
            }
        }
        return false;
    }

    private Map<String, Set<String>> getTeamNameToEmailsTableFromRoster(CourseRoster courseroster) {
        List<StudentAttributes> students = courseroster.getStudents();
        Map<String, Set<String>> teamNameToEmails = new HashMap<String, Set<String>>();

        for (StudentAttributes student : students) {
            String studentTeam = student.team;
            Set<String> studentEmails;

            if (teamNameToEmails.containsKey(studentTeam)) {
                studentEmails = teamNameToEmails.get(studentTeam);
            } else {
                studentEmails = new TreeSet<String>();
            }

            studentEmails.add(student.email);
            teamNameToEmails.put(studentTeam, studentEmails);
        }

        List<InstructorAttributes> instructors = courseroster.getInstructors();
        String instructorsTeam = Const.USER_TEAM_FOR_INSTRUCTOR;
        Set<String> instructorEmails = new HashSet<String>();

        for (InstructorAttributes instructor : instructors) {
            instructorEmails.add(instructor.email);
            teamNameToEmails.put(instructorsTeam, instructorEmails);
        }

        return teamNameToEmails;
    }

    private Map<String, Set<String>> getSectionToTeamNamesFromRoster(CourseRoster courseroster) {
        List<StudentAttributes> students = courseroster.getStudents();
        Map<String, Set<String>> sectionToTeam = new HashMap<String, Set<String>>();

        for (StudentAttributes student : students) {
            String studentSection = student.section;
            String studentTeam = student.team;
            Set<String> teamNames;

            if (sectionToTeam.containsKey(studentSection)) {
                teamNames = sectionToTeam.get(studentSection);
            } else {
                teamNames = new HashSet<String>();
            }

            teamNames.add(studentTeam);
            sectionToTeam.put(studentSection, teamNames);
        }

        return sectionToTeam;
    }

    public int compareByQuestionNumber(FeedbackResponseAttributes r1,
                                        FeedbackResponseAttributes r2) {
        FeedbackQuestionAttributes q1 = questions.get(r1.feedbackQuestionId);
        FeedbackQuestionAttributes q2 = questions.get(r2.feedbackQuestionId);
        if (q1 == null || q2 == null) {
            return 0;
        }
        return q1.compareTo(q2);
    }

    /**
     * Compares the values of {@code name1} and {@code name2}.
     * Anonymous names are ordered later than non-anonymous names.
     * @param isFirstNameVisible  true if the first name should be visible to the user
     * @param isSecondNameVisible true if the second name should be visible to the user
     */
    public int compareByNames(String name1, String name2,
                               boolean isFirstNameVisible, boolean isSecondNameVisible) {
        if (!isFirstNameVisible && !isSecondNameVisible) {
            return 0;
        }
        if (!isFirstNameVisible && isSecondNameVisible) {
            return 1;
        } else if (isFirstNameVisible && !isSecondNameVisible) {
            return -1;
        }

        // Make class feedback always appear on top, and team responses at bottom.
        int n1Priority = 0;
        int n2Priority = 0;

        if (name1.equals(Const.USER_IS_NOBODY)) {
            n1Priority = -1;
        } else if (name1.equals(Const.USER_IS_TEAM)) {
            n1Priority = 1;
        }
        if (name2.equals(Const.USER_IS_NOBODY)) {
            n2Priority = -1;
        } else if (name2.equals(Const.USER_IS_TEAM)) {
            n2Priority = 1;
        }

        int order = Integer.compare(n1Priority, n2Priority);
        return order == 0 ? name1.compareTo(name2) : order;
    }

    public int compareByResponseString(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
        String responseAnswer1 = o1.getResponseDetails().getAnswerString();

        String responseAnswer2 = o2.getResponseDetails().getAnswerString();

        return responseAnswer1.compareTo(responseAnswer2);
    }

    public FeedbackSessionAttributes getFeedbackSession() {
        return feedbackSession;
    }

    public List<FeedbackResponseAttributes> getResponses() {
        return responses;
    }

    public Map<String, FeedbackQuestionAttributes> getQuestions() {
        return questions;
    }

    public Map<String, String> getEmailNameTable() {
        return emailNameTable;
    }

    public Map<String, String> getEmailLastNameTable() {
        return emailLastNameTable;
    }

    public Map<String, String> getEmailTeamNameTable() {
        return emailTeamNameTable;
    }

    public Map<String, Set<String>> getRosterTeamNameMembersTable() {
        return rosterTeamNameMembersTable;
    }

    public Set<String> sectionsInCourse() {
        return new HashSet<>(rosterSectionTeamNameTable.keySet());
    }

    public Map<String, Set<String>> getRosterSectionTeamNameTable() {
        return rosterSectionTeamNameTable;
    }

    public Map<String, boolean[]> getVisibilityTable() {
        return visibilityTable;
    }

    public FeedbackSessionResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public CourseRoster getRoster() {
        return roster;
    }

    public Map<String, List<FeedbackResponseCommentAttributes>> getResponseComments() {
        return responseComments;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void complete(){
        this.isComplete = true;
    }

}
