package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 09.03.16.
 */
public class AnswerItem {
    private Integer id;
    private String itemText;
    private String itemScore;
    private String uri;
    LinkedList<Answer> subAnswers;

    public AnswerItem(AnswerItem item){
        this.id = item.id;
        this.itemText = item.itemText;
        this.itemScore = item.itemScore;
        this.uri = item.uri;
        this.subAnswers = item.subAnswers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public String getItemScore() {
        return itemScore;
    }

    public void setItemScore(String itemScore) {
        this.itemScore = itemScore;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public LinkedList<Answer> getSubAnswers() {
        return subAnswers;
    }

    public void addSubAnswer(Answer answer){
        Answer subAnswer = new Answer(answer);
        subAnswers.addLast(subAnswer);
    }

    //    public AnswerItem(String uri_from_sib,String itemScore_from_sib, String itemText_from_sib){
//        uri = uri_from_sib;
//        itemScore = itemScore_from_sib;
//        itemText = itemText_from_sib;
//        subAnswers = new LinkedList<Answer>();
//    }
}
