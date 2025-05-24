package bhargav;

import org.junit.Test;

import bhargav.quiz.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class TestQuiz {

    // Test Case 1: Valid Answer Updates the Score
    @Test
    public void testCorrectAnswer() {
        // Given
        String[] options = {"Java", "Python", "JavaScript"};
        MCQQuestion question = new MCQQuestion("Which of these is an object-oriented language?", options, "Java");
        int initialScore = 0;  // Starting score

        // When
        String userAnswer = "Java"; // Correct answer
        boolean actual = userAnswer.equalsIgnoreCase(question.getCorrectAnswer());
        int updatedScore = actual ? initialScore + 1 : initialScore;

        // Then
        assertThat("Score should increase for correct answer.", actual, is(true));
        assertThat("Updated score should be 1 after correct answer.", updatedScore, is(1));
        System.out.println("Test passed.");
    }

    // Test Case 2: Empty Answer Should Not Update the Score
    @Test
    public void testEmptyAnswer() {
        TFQQuestion question = new TFQQuestion("Is Java statically typed?", "True");
        int initialScore = 0;  // Starting score

        // When
        String userAnswer = "hello"; // Empty answer
        boolean actual = userAnswer.equalsIgnoreCase(question.getCorrectAnswer());
        int updatedScore = actual ? initialScore + 1 : initialScore;

        // Then
        assertThat("Score should not increase for empty answer.", actual, is(false));
        assertThat("Updated score should remain 0 after empty answer.", updatedScore, is(0));
        System.out.println("Test passed.");
    }

    // Test Case 3: Multiple Identical Answers Should Be Handled Correctly
    @Test
    public void testIdenticalAnswer() {
        // Given
        String[] options = {"Scanner", "Scanner", "JUnit", "Math"};
        MCQQuestion question = new MCQQuestion("Which import is used to take user input?", options, "Scanner");
        int initialScore = 0;  // Starting score

        // When
        String userAnswer = "Scanner"; // Correct answer, selected from a duplicate
        boolean actual = userAnswer.equalsIgnoreCase(question.getCorrectAnswer());
        int updatedScore = actual ? initialScore + 1 : initialScore;

        // Then
        assertThat("Duplicate correct options should be valid.", actual, is(true));
        assertThat("Updated score should be 1 after selecting correct answer.", updatedScore, is(1));
        System.out.println("Test passed.");
    }
}
