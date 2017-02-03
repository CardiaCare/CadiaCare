package com.petrsu.cardiacare.smartcare.servey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 03.02.17.
 */

public class Respond {

    private Integer id;
    private Integer questionId;
    LinkedList<Response> responses;


    public Respond(Integer id, Integer questionId, LinkedList<Response> responses) {
        this.id = id;
        this.questionId = questionId;
        this.responses = new LinkedList<Response>();
    }

    public void addResponse(Response newResponse) {
        this.responses.addLast(newResponse);
    }
    public LinkedList<Response> getResponses() {
        return responses;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }
}
