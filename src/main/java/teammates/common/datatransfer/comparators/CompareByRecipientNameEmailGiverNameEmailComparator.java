package teammates.common.datatransfer.comparators;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;

import java.util.Comparator;

/**
 * Created by Rogier on 24-5-2017.
 */
public class CompareByRecipientNameEmailGiverNameEmailComparator implements Comparator<FeedbackResponseAttributes> {
    private FeedbackSessionResultsBundle feedbackSessionResultsBundle;

    public CompareByRecipientNameEmailGiverNameEmailComparator(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        this.feedbackSessionResultsBundle = feedbackSessionResultsBundle;
    }

    @Override
    public int compare(FeedbackResponseAttributes o1, FeedbackResponseAttributes o2) {

        boolean isRecipientVisible1 = feedbackSessionResultsBundle.isRecipientVisible(o1);
        boolean isRecipientVisible2 = feedbackSessionResultsBundle.isRecipientVisible(o2);
        // Compare by Recipient Name
        int recipientNameCompareResult = feedbackSessionResultsBundle.compareByNames(feedbackSessionResultsBundle.getNameForEmail(o1.recipient),
                feedbackSessionResultsBundle.getNameForEmail(o2.recipient),
                isRecipientVisible1, isRecipientVisible2);
        if (recipientNameCompareResult != 0) {
            return recipientNameCompareResult;
        }

        // Compare by Recipient Email
        int recipientEmailCompareResult = feedbackSessionResultsBundle.compareByNames(o1.recipient, o2.recipient,
                isRecipientVisible1, isRecipientVisible2);
        if (recipientEmailCompareResult != 0) {
            return recipientEmailCompareResult;
        }

        boolean isGiverVisible1 = feedbackSessionResultsBundle.isGiverVisible(o1);
        boolean isGiverVisible2 = feedbackSessionResultsBundle.isGiverVisible(o2);
        // Compare by Giver Name
        int giverNameCompareResult = feedbackSessionResultsBundle.compareByNames(feedbackSessionResultsBundle.getNameForEmail(o1.giver),
                feedbackSessionResultsBundle.getNameForEmail(o2.giver),
                isGiverVisible1, isGiverVisible2);
        if (giverNameCompareResult != 0) {
            return giverNameCompareResult;
        }

        // Compare by Giver Email
        int giverEmailCompareResult = feedbackSessionResultsBundle.compareByNames(o1.giver, o2.giver,
                isGiverVisible1, isGiverVisible2);
        if (giverEmailCompareResult != 0) {
            return giverEmailCompareResult;
        }

        int responseStringResult = feedbackSessionResultsBundle.compareByResponseString(o1, o2);
        if (responseStringResult != 0) {
            return responseStringResult;
        }

        return o1.getId().compareTo(o2.getId());
    }
}
