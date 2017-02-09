package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 09.03.16.
 */
public class Answer {
    private String id;
    private String uri;
    private String type;

    LinkedList<AnswerItem> items;

    public Answer(String uri_from_sib,String type_from_sib){
        this.uri = uri_from_sib;
        this.type = type_from_sib;
        this.items = new LinkedList<AnswerItem>();
    }

    public Answer(Answer answer){
        this.uri = answer.getUri();
        this.type = answer.getType();
        this.items = new LinkedList<AnswerItem>();
    }

    // Получение идентификатора ответа из ИП
    public String getUri(){ return uri; }

    // Получение вариантов ответа
    public LinkedList<AnswerItem> getItems() {
        return items;
    }

    // Получение типа ответа
    public String getType() {
        int i = type.indexOf("#");
        String t = type.substring(i+1);
        return t;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Добавить вариант ответа в конец списка
    public void addAnswerItem(AnswerItem item){
        AnswerItem newItem = new AnswerItem(item);
        this.items.addLast(newItem);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
