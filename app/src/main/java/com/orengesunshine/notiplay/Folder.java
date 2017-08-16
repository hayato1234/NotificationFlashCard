package com.orengesunshine.notiplay;

import java.sql.Time;

/**
 * Created by hayatomoritani on 8/2/17.
 */

public class Folder {

    private String title;
    private int numberOfCards;
    private Time lastEdited;

    public Folder(String title) {
        this.title = title;
    }

    public Folder(String title, int numberOfCards, Time lastEdited) {
        this.title = title;
        this.numberOfCards = numberOfCards;
        this.lastEdited = lastEdited;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumberOfCards() {
        return numberOfCards;
    }

    public void setNumberOfCards(int numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public Time getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Time lastEdited) {
        this.lastEdited = lastEdited;
    }
}
