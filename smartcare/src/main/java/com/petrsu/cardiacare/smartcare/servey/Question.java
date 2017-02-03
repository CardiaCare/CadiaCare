package com.petrsu.cardiacare.smartcare.servey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 09.03.16.
 */
public class Question {
    private Integer id;
    private String uri;
    private String description;
    Answer answer;

    LinkedList<Answer> answers;

    public Question(String uri_from_sib, String description_from_sib){
         this.uri = uri_from_sib;
         this.description = description_from_sib;
        this.answers = new LinkedList<Answer>();
    }

    public Question(Integer id, String uri, String description) {
        this.id = id;
        this.uri = uri;
        this.description = description;
        this.answers = new LinkedList<Answer>();
    }

    public void addAnswer(Answer answer){
        answers.addLast(answer);
    }

    public LinkedList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswer(Answer answer_from_sib){
        answer = new Answer(answer_from_sib);
    }
    // Получение ответа
    public Answer getAnswer() {
        return answer;
    }


    // Получение идентификатора вопроса из ИП
    public String getUri(){
        return uri;
    }
    // Получение вопроса
    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
