package Assignment2Quiz;

/**
 * Represents a True/False question, implementing the Question interface.
 */
public class TFQQuestion implements Question<String[]> {
    private String question;
    private String[] answers = new String[]{"True", "False"};
    private String correctAnswer;

    /**
     * Constructs a new True/False question.
     *
     * @param question      The question text.
     * @param correctAnswer The correct answer (either "True" or "False").
     */
    public TFQQuestion(String question, String correctAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
    }

    /**
     * Retrieves the text of the question.
     *
     * @return The question as a String.
     */
    public String getQuestion() {
        return question;
    }

    /**
     * Retrieves the possible answers for the True/False question.
     *
     * @return An array containing "True" and "False".
     */
    public String[] getAllAnswers() {
        return answers;
    }

    /**
     * Retrieves the correct answer.
     *
     * @return The correct answer as a String.
     */
    public String getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Checks whether two TFQQuestion objects are equal.
     * They are considered equal if they have the same question text and correct answer.
     *
     * @param o The object to compare.
     * @return {@code true} if the questions are the same, {@code false} otherwise.
     */
    public boolean equals(Object o) {
        if (o instanceof TFQQuestion) {
            TFQQuestion q = (TFQQuestion) o;
            return q.getQuestion().equals(question) && q.getCorrectAnswer().equals(correctAnswer);
        }
        return false;
    }

    /**
     * Returns a string representation of the True/False question.
     *
     * @return The formatted question and answer choices.
     */
    public String toString() {
        return question + "\n1. True\n2. False\n";
    }
}