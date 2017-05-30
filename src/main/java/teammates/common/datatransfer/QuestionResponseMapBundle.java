package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.util.Logger;

import java.util.*;

/**
 * Created by Rogier on 25-5-2017.
 */
public class QuestionResponseMapBundle {
    private static final Logger log = Logger.getLogger();


    /**
     * Gets the questions and responses in this bundle as a map.
     *
     * @return An ordered {@code Map} with keys as {@link FeedbackQuestionAttributes}
     *         sorted by questionNumber.
     *         The mapped values for each key are the corresponding
     *         {@link FeedbackResponseAttributes} as a {@code List}.
     * @param feedbackSessionResultsBundle
     */
    public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getQuestionResponseMap(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (feedbackSessionResultsBundle.questions == null || feedbackSessionResultsBundle.responses == null) {
            return null;
        }

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap =
                new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
        List<FeedbackQuestionAttributes> sortedQuestions = new ArrayList<>(feedbackSessionResultsBundle.questions.values());
        // sorts the questions by its natural ordering, which is by question number
        Collections.sort(sortedQuestions);
        for (FeedbackQuestionAttributes question : sortedQuestions) {
            sortedMap.put(question, new ArrayList<FeedbackResponseAttributes>());
        }

        for (FeedbackResponseAttributes response : feedbackSessionResultsBundle.responses) {
            FeedbackQuestionAttributes question = feedbackSessionResultsBundle.questions.get(response.feedbackQuestionId);
            List<FeedbackResponseAttributes> responsesForQuestion = sortedMap.get(question);
            responsesForQuestion.add(response);
        }

        for (List<FeedbackResponseAttributes> responsesForQuestion : sortedMap.values()) {
            Collections.sort(responsesForQuestion, feedbackSessionResultsBundle.compareByGiverRecipient);
        }

        return sortedMap;
    }

    public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getQuestionResponseMapSortedByRecipient(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        if (feedbackSessionResultsBundle.questions == null || feedbackSessionResultsBundle.responses == null) {
            return null;
        }

        Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap =
                new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();

        List<FeedbackQuestionAttributes> sortedQuestions = new ArrayList<>(feedbackSessionResultsBundle.questions.values());
        // sorts the questions by its natural ordering, which is by question number
        Collections.sort(sortedQuestions);
        for (FeedbackQuestionAttributes question : sortedQuestions) {
            sortedMap.put(question, new ArrayList<FeedbackResponseAttributes>());
        }

        for (FeedbackResponseAttributes response : feedbackSessionResultsBundle.responses) {
            FeedbackQuestionAttributes question = feedbackSessionResultsBundle.questions.get(response.feedbackQuestionId);
            List<FeedbackResponseAttributes> responsesForQuestion = sortedMap.get(question);
            responsesForQuestion.add(response);
        }

        for (List<FeedbackResponseAttributes> responsesForQuestion : sortedMap.values()) {
            Collections.sort(responsesForQuestion, feedbackSessionResultsBundle.compareByRecipientNameEmailGiverNameEmail);
        }

        return sortedMap;
    }

    public LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getQuestionResponseMapByRecipientTeam(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();

        Collections.sort(feedbackSessionResultsBundle.responses, feedbackSessionResultsBundle.compareByTeamQuestionRecipientTeamGiver);

        for (FeedbackResponseAttributes response : feedbackSessionResultsBundle.responses) {
            String recipientTeam = feedbackSessionResultsBundle.getTeamNameForEmail(response.recipient);
            if (recipientTeam.isEmpty()) {
                recipientTeam = feedbackSessionResultsBundle.getNameForEmail(response.recipient);
            }

            if (!sortedMap.containsKey(recipientTeam)) {
                sortedMap.put(recipientTeam,
                        new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>());
            }
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient =
                                            sortedMap.get(recipientTeam);

            FeedbackQuestionAttributes question = feedbackSessionResultsBundle.questions.get(response.feedbackQuestionId);
            if (!responsesForOneRecipient.containsKey(question)) {
                responsesForOneRecipient.put(question, new ArrayList<FeedbackResponseAttributes>());
            }

            List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion =
                                            responsesForOneRecipient.get(question);
            responsesForOneRecipientOneQuestion.add(response);
        }

        return sortedMap;
    }

    public LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getQuestionResponseMapByGiverTeam(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();

        Collections.sort(feedbackSessionResultsBundle.responses, feedbackSessionResultsBundle.compareByTeamQuestionGiverTeamRecipient);

        for (FeedbackResponseAttributes response : feedbackSessionResultsBundle.responses) {
            String giverTeam = feedbackSessionResultsBundle.getTeamNameForEmail(response.giver);
            if (giverTeam.isEmpty()) {
                giverTeam = feedbackSessionResultsBundle.getNameForEmail(response.giver);
            }

            if (!sortedMap.containsKey(giverTeam)) {
                sortedMap.put(giverTeam,
                        new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>());
            }
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesFromOneGiver =
                                            sortedMap.get(giverTeam);

            FeedbackQuestionAttributes question = feedbackSessionResultsBundle.questions.get(response.feedbackQuestionId);
            if (!responsesFromOneGiver.containsKey(question)) {
                responsesFromOneGiver.put(question, new ArrayList<FeedbackResponseAttributes>());
            }

            List<FeedbackResponseAttributes> responsesFromOneGiverOneQuestion = responsesFromOneGiver.get(question);
            responsesFromOneGiverOneQuestion.add(response);
        }

        return sortedMap;
    }

    /**
     * Returns responses as a {@code Map<recipientName, Map<question, List<response>>>}
     * Where the responses are sorted in the order of recipient, question, giver.
     * @return responses sorted by Recipient > Question > Giver
     * @param sortByTeam
     * @param feedbackSessionResultsBundle
     */
    public Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>
            getResponsesSortedByRecipientQuestionGiver(boolean sortByTeam, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        Map<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>> sortedMap =
                new LinkedHashMap<String, Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>>();

        if (sortByTeam) {
            Collections.sort(feedbackSessionResultsBundle.responses, feedbackSessionResultsBundle.compareByTeamRecipientQuestionTeamGiver);
        } else {
            Collections.sort(feedbackSessionResultsBundle.responses, feedbackSessionResultsBundle.compareByRecipientQuestionTeamGiver);
        }

        for (FeedbackResponseAttributes response : feedbackSessionResultsBundle.responses) {
            String recipientEmail = response.recipient;
            if (!sortedMap.containsKey(recipientEmail)) {
                sortedMap.put(recipientEmail,
                              new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>());
            }
            Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> responsesForOneRecipient =
                                            sortedMap.get(recipientEmail);

            FeedbackQuestionAttributes question = feedbackSessionResultsBundle.questions.get(response.feedbackQuestionId);
            if (!responsesForOneRecipient.containsKey(question)) {
                responsesForOneRecipient.put(question, new ArrayList<FeedbackResponseAttributes>());
            }
            List<FeedbackResponseAttributes> responsesForOneRecipientOneQuestion =
                                            responsesForOneRecipient.get(question);
            responsesForOneRecipientOneQuestion.add(response);
        }

        return sortedMap;
    }
}
