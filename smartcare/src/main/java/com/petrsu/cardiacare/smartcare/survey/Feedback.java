package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by CardiaCareTeam on 20.04.2016.
 */
public class Feedback {
    private Integer questionnaire_id;
    private String lang;
    LinkedList<Respond> responds;

//    private String uri;
//    private String personUri;
//    private String questionnaireUri;
//    LinkedList<Response> responses;

    public Feedback(Integer questionnaire_id, String lang) {
        this.questionnaire_id = questionnaire_id;
        this.lang = lang;
        responds = new LinkedList<>();
    }

    public Integer getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(Integer questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public LinkedList<Respond> getResponds() {
        return responds;
    }

    public void addRespond(Respond newRespond) {
        this.responds.addLast(newRespond);
    }

//    public Feedback(String uri, String personUri, String questionnaireUri) {
//        this.uri = uri;
//        this.personUri = personUri;
//        this.questionnaireUri = questionnaireUri;
//        this.responses = new LinkedList<Response>();
//    }
//
//    public String getUri() {
//        return uri;
//    }
//
//    public void setUri(String uri) {
//        this.uri = uri;
//    }
//
//    public String getPersonUri() {
//        return personUri;
//    }
//
//    public void setPersonUri(String person_uri) {
//        this.personUri = personUri;
//    }
//
//    public String getQuestionnaireUri() {
//        return questionnaireUri;
//    }
//
//    public void setQuestionnaireUri(String questionnaire_uri) {
//        this.questionnaireUri = questionnaireUri;
//    }
//
//    public LinkedList<Response> getResponses() {
//        return responses;
//    }
//
//    public void setResponses(LinkedList<Response> responses) {
//        this.responses = responses;
//    }
//
//    public void addResponse(Response newResponse) {
//        this.responses.addLast(newResponse);
//    }
}
