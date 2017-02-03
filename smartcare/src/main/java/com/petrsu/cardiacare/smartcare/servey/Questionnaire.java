package com.petrsu.cardiacare.smartcare.servey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 04.03.16.
 */
public class Questionnaire {
    private Integer id;
    private String uri;
    private String serverURI;
    private String version;
    private String lang;
    private String description;
    private boolean emergency;
    // questions - связный список элементов типа Question
    LinkedList <Question> questions;

    public Questionnaire(String uri_from_sib){
        uri = uri_from_sib;
        questions = new LinkedList<Question>();
    }
    // Получение идентификатора вопросника из ИП
    public String getUri(){
        return uri;
    }

    // Добавление элемента в конец списка
    public void addQuestion(Question question){
        questions.addLast(question);
    }

    public String getVersion() {
        return version;
    }

    public void setServerURI(String serverURI) {
        this.serverURI = serverURI;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    // Получение списка вопросов
    public LinkedList <Question> getQuestions(){
         return questions;
     }

    public String getServerURI() {
        return serverURI;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
