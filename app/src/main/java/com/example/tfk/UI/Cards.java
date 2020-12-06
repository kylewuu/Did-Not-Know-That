package com.example.tfk.UI;

public class Cards {
    private String title;
    private String body;
    private String link;

    public Cards(String[] elements){
        this.title = elements[0];
        this.body = elements[1];
        this.link = elements[2];
    }

    public String getTitle(){
        return this.title;
    }
    public String getBody(){
        return this.body;
    }
    public String getLink(){
        return this.link;
    }
}
