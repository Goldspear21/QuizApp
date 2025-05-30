package bhargav.quiz;

import java.io.*;
import java.util.*;

/**
 * This class manages a collection of quiz questions.
 * It can add new questions, choose a few for a quiz,
 * and load questions from a file.
 */
class QuizQuestions {
    private List<Question<?>> allQuestions = new ArrayList<>();
    private List<Question<?>> selectedQuestions;

    /**
     * Adds a question to the list of all available questions.
     *
     * @param q The question to add.
     */
    public void addQuestion(Question<?> q) {
        allQuestions.add(q);
    }

    /**
     * Gets the full list of questions.
     *
     * @return A list of all questions.
     */
    public List<Question<?>> getAllQuestions() {
        return allQuestions;
    }

    /**
     * Selects a certain number of questions for the quiz.
     * If there are fewer questions than requested, all available ones will be selected.
     *
     * @param numberOfQuestions The number of questions to select.
     */
    public void select(int numberOfQuestions) {
        selectedQuestions = allQuestions.subList(0, Math.min(numberOfQuestions, allQuestions.size()));
    }

    /**
     * Gets the list of selected questions.
     *
     * @return A list of the selected questions.
     */
    public List<Question<?>> getSelectedQuestions() {
        return selectedQuestions;
    }

    /**
     * Reads quiz questions from a file.
     * The file should follow this format:
     * 1. The question text (one line)
     * 2. The possible answers (one line, separated by commas)
     * 3. The correct answer (one line)
     *
     * If the question has only two answers, it is treated as a True/False question.
     * Otherwise, it is stored as a multiple-choice question.
     *
     * @param filename The name of the file to read questions from.
     * @throws IOException If there is an error while reading the file.
     */
    public void load(String filename) throws IOException {
        // This method remains for compatibility if you ever need to load from a direct file path.
        // It will still require the file to be present on the file system.
        try (Scanner scanner = new Scanner(new File(filename))) {
            loadFromScanner(scanner);
        }
    }

    /**
     * Reads quiz questions from an InputStream.
     * This method is suitable for loading resources from within a JAR file.
     * The input stream content should follow the same format as the file:
     * 1. The question text (one line)
     * 2. The possible answers (one line, separated by commas)
     * 3. The correct answer (one line)
     *
     * @param inputStream The InputStream to read questions from.
     * @throws IOException If there is an error while reading the stream.
     */
    public void load(InputStream inputStream) throws IOException {
        // Use InputStreamReader to correctly handle character encoding from the stream
        try (Scanner scanner = new Scanner(new InputStreamReader(inputStream))) {
            loadFromScanner(scanner);
        }
    }

    // Helper method to consolidate the scanning logic
    private void loadFromScanner(Scanner scanner) {
        while (scanner.hasNextLine()) {
            String question = scanner.nextLine().trim();
            if (question.isEmpty()) continue;

            String[] answers = scanner.nextLine().trim().split(",");
            String correctAnswer = scanner.nextLine().trim();

            // If there are only two answers, it's a True/False question
            if (answers.length == 2) {
                allQuestions.add(new TFQQuestion(question, correctAnswer));
            }
            // Otherwise, it's a multiple-choice question
            else {
                allQuestions.add(new MCQQuestion(question, answers, correctAnswer));
            }
        }
    }
}