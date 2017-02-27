package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 04.03.16.
 */
public class Questionnaire {
    private Integer id;
    private Integer doctor_id;
    private String version;
    private String description;
    private String created_at;
    private String lang;
    private Integer emergency;
    LinkedList <Question> questions;

//    private String uri;
//    private String serverURI;

    public Questionnaire(Integer id, Integer doctor_id, String version, String description, String created_at, String lang, Integer emergency){
        this.id = id;
        this.doctor_id = doctor_id;
        this.version = version;
        this.description = description;
        this.created_at = created_at;
        this.lang = lang;
        this.emergency = emergency;
        questions = new LinkedList<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(Integer doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Integer isEmergency() {
        return emergency;
    }

    public void setEmergency(Integer emergency) {
        this.emergency = emergency;
    }

    public LinkedList <Question> getQuestions(){
        return questions;
    }

    public void addQuestion(Question question){
        questions.addLast(question);
    }

//    public Questionnaire(String uri_from_sib){
//        uri = uri_from_sib;
//        questions = new LinkedList<Question>();
//    }
//
//    public String getUri(){
//        return uri;
//    }
//
//    public void setUri(String uri) {
//        this.uri = uri;
//    }
//
//    public String getServerURI() {
//        return serverURI;
//    }
//
//    public void setServerURI(String serverURI) {
//        this.serverURI = serverURI;
//    }
}
