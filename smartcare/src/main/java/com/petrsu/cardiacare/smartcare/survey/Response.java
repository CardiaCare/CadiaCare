package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by CardiaCareTeam on 20.04.2016.
 */
public class Response {

    private Integer id;
    private Integer answerId;
    private String textItem;
    private String fileUri;
    LinkedList<ResponseItem> responseItems;

    private String uri;
    private String questionUri;

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Response(String uri, String questionUri) {
        this.uri = uri;
        this.questionUri = questionUri;
        this.responseItems = new LinkedList<ResponseItem>();
    }

    public void addResponseItem(ResponseItem newResponseItem) {
        this.responseItems.addLast(newResponseItem);
    }

    public void setQuestionUri(String questionUri) {
        this.questionUri = questionUri;
    }

    public void setResponseItems(LinkedList<ResponseItem> responseItems) {
        this.responseItems = responseItems;
    }

    public LinkedList<ResponseItem> getResponseItems() {
        return responseItems;
    }

    public String getUri() {
        return uri;
    }

    public String getQuestionUri() {
        return questionUri;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public String getTextItem() {
        return textItem;
    }

    public void setTextItem(String textItem) {
        this.textItem = textItem;
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }
}
