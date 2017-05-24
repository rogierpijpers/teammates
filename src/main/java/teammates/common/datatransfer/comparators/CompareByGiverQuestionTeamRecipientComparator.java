package teammates.common.datatransfer.comparators;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

import java.util.Comparator;

/**
 * Created by Rogier on 24-5-2017.
 */
public class CompareByGiverQuestionTeamRecipientComparator implements Comparator<FeedbackResponseAttributes> {
    private FeedbackSessionResultsBundle feedbackSessionResultsBundle;

    public CompareByGiverQuestionTeamRecipientComparator(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        this.feedbackSessionResultsBundle = feedbackSessionResultsBundle;
    }

    @Override
    public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {
        String giverSection1 = o1.giverSection;
        String giverSection2 = o2.giverSection;
        int order = giverSection1.compareTo(giverSection2);
        if (order != 0) {
            return order;
        }

        boolean isGiverVisible1 = feedbackSessionResultsBundle.isGiverVisible(o1);
        boolean isGiverVisible2 = feedbackSessionResultsBundle.isGiverVisible(o2);

        String giverName1 = feedbackSessionResultsBundle.emailNameTable.get(o1.giver);
        String giverName2 = feedbackSessionResultsBundle.emailNameTable.get(o2.giver);
        order = feedbackSessionResultsBundle.compareByNames(giverName1, giverName2, isGiverVisible1, isGiverVisible2);
        if (order != 0) {
            return order;
        }

        order = feedbackSessionResultsBundle.compareByQuestionNumber(o1, o2);
        if (order != 0) {
            return order;
        }

        boolean isRecipientVisible1 = feedbackSessionResultsBundle.isRecipientVisible(o1);
        boolean isRecipientVisible2 = feedbackSessionResultsBundle.isRecipientVisible(o2);

        String t1 = feedbackSessionResultsBundle.getTeamNameForEmail(o1.recipient).isEmpty() ? feedbackSessionResultsBundle.getNameForEmail(o1.recipient)
                : feedbackSessionResultsBundle.getTeamNameForEmail(o1.recipient);
        String t2 = feedbackSessionResultsBundle.getTeamNameForEmail(o2.recipient).isEmpty() ? feedbackSessionResultsBundle.getNameForEmail(o2.recipient)
                : feedbackSessionResultsBundle.getTeamNameForEmail(o2.recipient);
        order = feedbackSessionResultsBundle.compareByNames(t1, t2, isRecipientVisible1, isRecipientVisible2);
        if (order != 0) {
            return order;
        }

        String recipientName1 = feedbackSessionResultsBundle.emailNameTable.get(o1.recipient);
        String recipientName2 = feedbackSessionResultsBundle.emailNameTable.get(o2.recipient);
        order = feedbackSessionResultsBundle.compareByNames(recipientName1, recipientName2, isRecipientVisible1, isRecipientVisible2);

        if (order != 0) {
            return order;
        }
        order = feedbackSessionResultsBundle.compareByResponseString(o1, o2);
        if (order != 0) {
            return order;
        }

        return o1.getId().compareTo(o2.getId());
    }
}
