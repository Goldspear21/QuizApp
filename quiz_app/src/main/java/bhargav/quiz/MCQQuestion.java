package bhargav.quiz;

public class MCQQuestion implements Question<String[]> {
    private String question;
    private String[] answers;
    private String correctAnswer;

    public MCQQuestion(String question, String[] answers, String correctAnswer) {
        this.question = question;
        this.answers = answers;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() { 
        return question; }

    public String[] getAllAnswers() { 
        return answers; }  

    public String getCorrectAnswer() { 
        return correctAnswer; }

    @Override
    public String toString() {
        return question;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MCQQuestion) {
            MCQQuestion q = (MCQQuestion) o;
            if (q.getQuestion().equals(question) && q.getCorrectAnswer().equals(correctAnswer)) {
                for (int i = 0; i < answers.length; i++) {
                    if (!q.getAllAnswers()[i].equals(answers[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }



}
