package bhargav.quiz;

/**
 * Represents a generic question interface with methods to retrieve
 * the question text, possible answers, and the correct answer.
 *
 * @param <T> The type of answer options (could be a list, array, or other
 *            collection).
 */
interface Question<T> {

    /**
     * Retrieves the text of the question.
     *
     * @return The question as a String.
     */
    String getQuestion();

    /**
     * Retrieves all possible answers for the question.
     *
     * @return A collection of answers of type T.
     */
    T getAllAnswers();

    /**
     * Retrieves the correct answer for the question.
     *
     * @return The correct answer as a String.
     */
    String getCorrectAnswer();
}