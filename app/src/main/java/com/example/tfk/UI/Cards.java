package com.example.tfk.UI;

public class Cards {
    private String title;
    private String[] body;
    private String link;

    public Cards(String[] elements){
        this.title = elements[0];
//        this.body[0] = elements[1];
//        this.body[1] = "Test";
        this.body = new String[]{elements[1]};
        this.link = elements[2];
    }

    public String getTitle(){
        return this.title;
    }
    public String[] getBody(){
        return this.body;
    }
    public String getLink(){
        return this.link;
    }

    public void setBody(String[] bodyArray){
        this.body = bodyArray;
    }

}
