package teammates.logic.core;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;

public class QuestionInstructorStudentParameterObject {
    private final FeedbackQuestionAttributes question;
    private final InstructorAttributes instructorGiver;
    private final StudentAttributes studentGiver;

    public QuestionInstructorStudentParameterObject(FeedbackQuestionAttributes question, InstructorAttributes instructorGiver, StudentAttributes studentGiver) {
        this.question = question;
        this.instructorGiver = instructorGiver;
        this.studentGiver = studentGiver;
    }

    public FeedbackQuestionAttributes getQuestion() {
        return question;
    }

    public InstructorAttributes getInstructorGiver() {
        return instructorGiver;
    }

    public StudentAttributes getStudentGiver() {
        return studentGiver;
    }
}
