package bhargav.quiz;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class represents a quiz game.
 * It loads questions from a file, asks the user to answer them,
 * and calculates the final score.
 */
public class Quiz {
    private QuizQuestions quizQuestions = new QuizQuestions();
    private int score = 0;

    /**
     * Starts the quiz by asking the user for their name and then presenting
     * a set of selected quiz questions.
     *
     * @param filename The name of the file that contains quiz questions.
     */
    public void startQuiz(String filename) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

        try {
            quizQuestions.load(filename);
            quizQuestions.select(10); // Ensure questions are selected
        } catch (IOException e) {
            System.out.println("Error loading questions file.");
            scanner.close();
            return;
        }

        if (quizQuestions.getSelectedQuestions() == null || quizQuestions.getSelectedQuestions().isEmpty()) {
            System.out.println("No questions available to display.");
            scanner.close();
            return;
        }

        for (Question<?> q : quizQuestions.getSelectedQuestions()) {
            System.out.println("\n" + q.getQuestion());
            Object answers = q.getAllAnswers();
            String[] options;

            // Check if answers are in the correct format (String array)
            if (answers instanceof String[]) {
                options = (String[]) answers;
            } else {
                System.out.println("Error: Invalid answer format.");
                continue;
            }

            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ". " + options[i]);
            }

            // Get the user's answer
            System.out.print("Enter the number of your answer: ");
            int userChoice = scanner.nextInt();

            // Validate the answer
            if (userChoice < 1 || userChoice > options.length) {
                System.out.println("Invalid choice! Please select a valid option.");
                continue;
            }

            // Check if the answer is correct
            if (options[userChoice - 1].equalsIgnoreCase(q.getCorrectAnswer())) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Wrong answer!");
            }
        }

        // Show the final score
        System.out.println("\n" + username + "'s final score: "
                + score + "/" + quizQuestions.getSelectedQuestions().size());

        scanner.close();
    }
}
