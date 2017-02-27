package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 09.03.16.
 */
public class Question {
    private Integer id;
    private String description;
    private String uri;
    LinkedList<Answer> answers;

//    Answer answer;

    public Question(Integer id, String description, String uri) {
        this.id = id;
        this.description = description;
        this.uri = uri;
        this.answers = new LinkedList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri(){
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public LinkedList<Answer> getAnswers() {
        return answers;
    }

    public void addAnswer(Answer answer){
        answers.addLast(answer);
    }

//    public Question(String uri_from_sib, String description_from_sib){
//        this.uri = uri_from_sib;
//        this.description = description_from_sib;
//        this.answers = new LinkedList<Answer>();
//    }

//    public void setAnswer(Answer answer_from_sib){
//        answer = new Answer(answer_from_sib);
//    }
//
//    public Answer getAnswer() {
//        return answer;
//    }
}
