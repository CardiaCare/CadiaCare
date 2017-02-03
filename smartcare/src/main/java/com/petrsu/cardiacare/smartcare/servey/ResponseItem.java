package com.petrsu.cardiacare.smartcare.servey;

import java.util.LinkedList;

/**
 * Created by Iuliia Zavialova on 20.04.2016.
 */
public class ResponseItem {

    private Integer id;
    private Integer linkedItemId;
    private Integer responseScore;
    LinkedList<Response> subResponses;

    public ResponseItem(Integer id, Integer linkedItemId, Integer responseScore) {
        this.id = id;
        this.linkedItemId = linkedItemId;
        this.responseScore = responseScore;
        this.subResponses = new LinkedList<Response>();
    }

    public Integer getId() {return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getLinkedItemId() {  return linkedItemId;  }
    public void setLinkedItemId(Integer linkedItemId) { this.linkedItemId = linkedItemId; }

    public Integer getResponseScore() { return responseScore; }
    public void setResponseScore(Integer responseScore) { this.responseScore = responseScore; }

    public void addSubResponse(Response newResponse) {
        this.subResponses.addLast(newResponse);
    }
    public LinkedList<Response> getSubResponses() {
        return subResponses;
    }


    private String uri;
    private LinkedList<AnswerItem> linkedItems;
    private String textItem;
    private String fileUri;


    public ResponseItem(String uri,  String textItem, String fileUri) {
        this.uri = uri;
        this.textItem = textItem;
        this.fileUri = fileUri;
        this.linkedItems = new LinkedList<AnswerItem>();
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void addLinkedAnswerItem(AnswerItem newAnswerItem) {
        this.linkedItems.addLast(newAnswerItem);
    }

    public void setTextItem(String text) {
        this.textItem = text;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getUri() {
        return uri;
    }

    public String getTextItem() {
        return textItem;
    }

    public String getFileUri() {
        return fileUri;
    }

    public LinkedList<AnswerItem> getLinkedItems() {
        return linkedItems;
    }
}
