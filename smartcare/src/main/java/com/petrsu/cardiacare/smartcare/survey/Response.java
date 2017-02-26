package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by CardiaCareTeam on 20.04.2016.
 */
public class Response {
    private Integer answer_id;
    private String responseText;
    private String responseFile;
    LinkedList<ResponseItem> responseItems;

//    private String uri;
//    private String questionUri;

    public Response(Integer answer_id) {
        this.answer_id = answer_id;
        responseItems = new LinkedList<>();
    }

    public Integer getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(Integer answer_id) {
        this.answer_id = answer_id;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getResponseFile() {
        return responseFile;
    }

    public void setResponseFile(String responseFile) {
        this.responseFile = responseFile;
    }

    public LinkedList<ResponseItem> getResponseItems() {
        return responseItems;
    }

    public void addResponseItem(ResponseItem newResponseItem) {
        this.responseItems.addLast(newResponseItem);
    }

//    public Response(String uri, String questionUri) {
//        this.uri = uri;
//        this.questionUri = questionUri;
//        this.responseItems = new LinkedList<ResponseItem>();
//    }
//    public String getUri() {
//        return uri;
//    }
//    public void setUri(String uri) {
//        this.uri = uri;
//    }
//
//    public String getQuestionUri() {
//        return questionUri;
//    }
//
//    public void setQuestionUri(String questionUri) {
//        this.questionUri = questionUri;
//    }
//
//    public void setResponseItems(LinkedList<ResponseItem> responseItems) {
//        this.responseItems = responseItems;
//    }
}
