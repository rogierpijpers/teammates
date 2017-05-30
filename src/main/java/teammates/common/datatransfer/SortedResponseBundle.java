package teammates.common.datatransfer;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Rogier on 25-5-2017.
 */
public class SortedResponseBundle {
    List<String> getSortedListOfTeamsExcludingOwnTeam(StudentAttributes student, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        String studentTeam = student.team;
        List<String> listOfTeams = getSortedListOfTeams(feedbackSessionResultsBundle);
        listOfTeams.remove(studentTeam);
        return listOfTeams;
    }

    /**
     * Get a sorted list of teams for the feedback session.<br>
     * Instructors are not present as a team.
     * @param feedbackSessionResultsBundle
     */
    List<String> getSortedListOfTeams(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        List<String> teams = new ArrayList<String>(feedbackSessionResultsBundle.rosterTeamNameMembersTable.keySet());
        teams.remove(Const.USER_TEAM_FOR_INSTRUCTOR);
        Collections.sort(teams);
        return teams;
    }

    /**
     * Get a sorted list of team members, who are in the same team as the student.<br>
     * This list includes the student.
     *
     * @return a list of team members, including the original student
     * @see #getSortedListOfTeamMembersEmailsExcludingSelf
     * @param student
     * @param feedbackSessionResultsBundle
     */
    public List<String> getSortedListOfTeamMembersEmails(StudentAttributes student, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        String teamName = student.team;
        Set<String> teamMembersEmailsToNames = feedbackSessionResultsBundle.rosterTeamNameMembersTable.get(teamName);
        List<String> teamMembers = new ArrayList<String>(teamMembersEmailsToNames);
        Collections.sort(teamMembers);
        return teamMembers;
    }

    /**
     * Get a sorted list of team members, who are in the same team as the student,
     * EXCLUDING the student.
     *
     * @return a list of team members, excluding the original student
     * @see SortedResponseBundle#getSortedListOfTeamMembersEmails
     * @param student
     * @param feedbackSessionResultsBundle
     */
    List<String> getSortedListOfTeamMembersEmailsExcludingSelf(StudentAttributes student, FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        List<String> teamMembers = getSortedListOfTeamMembersEmails(student, feedbackSessionResultsBundle);
        String currentStudentEmail = student.email;
        teamMembers.remove(currentStudentEmail);
        return teamMembers;
    }

    /**
     * Returns a list of student emails, sorted by section name.
     * @param feedbackSessionResultsBundle
     */
    List<String> getSortedListOfStudentEmails(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        List<String> emailList = new ArrayList<String>();
        List<StudentAttributes> students = feedbackSessionResultsBundle.roster.getStudents();
        StudentAttributes.sortBySectionName(students);
        for (StudentAttributes student : students) {
            emailList.add(student.email);
        }
        return emailList;
    }

    /**
     * Returns a list of instructor emails, sorted alphabetically.
     * @param feedbackSessionResultsBundle
     */
    List<String> getSortedListOfInstructorEmails(FeedbackSessionResultsBundle feedbackSessionResultsBundle) {
        List<String> emailList = new ArrayList<String>();
        List<InstructorAttributes> instructors = feedbackSessionResultsBundle.roster.getInstructors();
        for (InstructorAttributes instructor : instructors) {
            emailList.add(instructor.email);
        }
        Collections.sort(emailList);
        return emailList;
    }
}
