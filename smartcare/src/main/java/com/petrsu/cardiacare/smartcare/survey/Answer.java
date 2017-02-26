package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 09.03.16.
 */
public class Answer {
    private Integer id;
    private String uri;
    private String type;
    LinkedList<AnswerItem> items;

    public Answer(Answer answer){
        this.id = answer.getId();
        this.uri = answer.getUri();
        this.type = answer.getType();
        this.items = new LinkedList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getType() {
        int i = type.indexOf("#");
        String t = type.substring(i+1);
        return t;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LinkedList<AnswerItem> getItems() {
        return items;
    }

    public void addAnswerItem(AnswerItem item){
        AnswerItem newItem = new AnswerItem(item);
        this.items.addLast(newItem);
    }

//    public Answer(String uri_from_sib,String type_from_sib){
//        this.uri = uri_from_sib;
//        this.type = type_from_sib;
//        this.items = new LinkedList<AnswerItem>();
//    }
}
