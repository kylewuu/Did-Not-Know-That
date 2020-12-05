package com.example.tfk.UI;

public class Cards {
    private String title;
    private String body;
    private String link;

    public Cards(String title, String body, String link){
        this.title = title;
        this.body = body;
        this.link = link;
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
