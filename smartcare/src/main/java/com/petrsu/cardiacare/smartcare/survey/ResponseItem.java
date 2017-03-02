package com.petrsu.cardiacare.smartcare.survey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 20.04.2016.
 */
public class ResponseItem {
    private String responseScore;
    private Integer linkedItems_id;
    LinkedList<Response> subResponses;

//    private String uri;
//    private LinkedList<AnswerItem> linkedItems;
//    private String textItem;
//    private String fileUri;

    public ResponseItem(Integer linkedItems_id) {
        this.linkedItems_id = linkedItems_id;
        subResponses = new LinkedList<>();
    }

    public ResponseItem(String responseScore) {
        this.responseScore = responseScore;
        subResponses = new LinkedList<>();
    }

    public String getResponseScore() {
        return responseScore;
    }

    public void setResponseScore(String responseScore) {
        this.responseScore = responseScore;
    }

    public Integer getLinkedItems_id() {
        return linkedItems_id;
    }

    public void setLinkedItems_id(Integer linkedItems_id) {
        this.linkedItems_id = linkedItems_id;
    }

    public LinkedList<Response> getSubResponses() {
        return subResponses;
    }

    public void addSubResponse(Response newResponse) {
        this.subResponses.addLast(newResponse);
    }

//    public ResponseItem(String uri,  String textItem, String fileUri) {
//        this.uri = uri;
//        this.textItem = textItem;
//        this.fileUri = fileUri;
//        this.linkedItems = new LinkedList<AnswerItem>();
//    }
//
//    public void setUri(String uri) {
//        this.uri = uri;
//    }
//
//    public void addLinkedAnswerItem(AnswerItem newAnswerItem) {
//        this.linkedItems.addLast(newAnswerItem);
//    }
//
//    public void setTextItem(String text) {
//        this.textItem = text;
//    }
//
//    public void setFileUri(String fileUri) {
//        this.fileUri = fileUri;
//    }
//
//    public String getUri() {
//        return uri;
//    }
//
//    public String getTextItem() {
//        return textItem;
//    }
//
//    public String getFileUri() {
//        return fileUri;
//    }
//
//    public LinkedList<AnswerItem> getLinkedItems() {
//        return linkedItems;
//    }
}
