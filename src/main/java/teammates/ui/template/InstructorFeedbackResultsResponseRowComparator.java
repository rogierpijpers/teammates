package teammates.ui.template;

import java.util.Comparator;

/**
 * Created by Rogier on 1-6-2017.
 */
public class InstructorFeedbackResultsResponseRowComparator implements Comparator<InstructorFeedbackResultsResponseRow> {
    @Override
    public int compare(InstructorFeedbackResultsResponseRow o1,
                       InstructorFeedbackResultsResponseRow o2) {

        if (!o1.getGiverTeam().equals(o2.getGiverTeam())) {
            return o1.getGiverTeam().compareTo(o2.getGiverTeam());
        }

        if (!o1.getGiverDisplayableIdentifier().equals(o2.getGiverDisplayableIdentifier())) {
            return o1.getGiverDisplayableIdentifier().compareTo(o2.getGiverDisplayableIdentifier());
        }

        if (!o1.getDisplayableResponse().equals(o2.getDisplayableResponse())) {
            return o1.getDisplayableResponse().compareTo(o2.getDisplayableResponse());
        }

        if (!o1.getRecipientTeam().equals(o2.getRecipientTeam())) {
            return o1.getRecipientTeam().compareTo(o2.getRecipientTeam());
        }

        return o1.getRecipientDisplayableIdentifier().compareTo(o2.getRecipientDisplayableIdentifier());
    }
}
