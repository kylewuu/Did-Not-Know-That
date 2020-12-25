package com.example.tfk.UI;

public class Cards {
    private String title;
    public String[] body;
    private String link;
    private String word;
    public int currentCard;

    public Cards(String[] elements){
        this.title = elements[0];
        this.body = new String[]{elements[1]};
        this.link = elements[2];
        this.word = elements[3];

        currentCard = 0;
    }

    public String getTitle(){
        return this.title;
    }
    public String[] getBody(){
        return this.body;
    }
    public String getWord() { return this.word;}
    public String getNextCard() {
        if(currentCard < this.body.length - 1) {
            currentCard ++;
        }
//        else currentCard = 0;
        return this.body[currentCard];
    }

    public String getPreviousCard(){
        if(currentCard > 0) {
            currentCard --;
        }
//        else currentCard = this.body.length - 1;
        return this.body[currentCard];
    }


    public String getLink(){
        return this.link;
    }


    public void setBody(String[] body) {
        this.body = body;
    }

}
