package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 03.02.17.
 */

public class Respond {
    private Integer question_id;
    LinkedList<Response> responses;

    public Respond(Integer question_id) {
        this.question_id = question_id;
        this.responses = new LinkedList<>();
    }

    public Integer getQuestionId() {
        return question_id;
    }

    public void setQuestionId(Integer question_id) {
        this.question_id = question_id;
    }

    public LinkedList<Response> getResponses() {
        return responses;
    }

    public void addResponse(Response newResponse) {
        this.responses.addLast(newResponse);
    }
}
