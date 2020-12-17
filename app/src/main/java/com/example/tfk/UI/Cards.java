package com.example.tfk.UI;

public class Cards {
    private String title;
    private String[] body;
    private String link;
    private String word;
    private int currentCard;

    public Cards(String[] elements){
        this.title = elements[0];
//        this.body[0] = elements[1];
//        this.body[1] = "Test";
        this.body = new String[]{elements[1]};
//        this.body = new String[]{
//                "Bee Movie Script - Dialogue Transcript\n" +
//                        "\n" +
//                        "  \n" +
//                        "According to all known laws\n" +
//                        "of aviation,\n" +
//                        "\n" +
//                        "  \n" +
//                        "there is no way a bee\n" +
//                        "should be able to fly.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Its wings are too small to get\n" +
//                        "its fat little body off the ground.\n" +
//                        "\n" +
//                        "  \n" +
//                        "The bee, of course, flies anyway\n" +
//                        "\n" +
//                        "  \n" +
//                        "because bees don't care\n" +
//                        "what humans think is impossible.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Yellow, black. Yellow, black.\n" +
//                        "Yellow, black. Yellow, black.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Ooh, black and yellow!\n" +
//                        "Let's shake it up a little.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Barry! Breakfast is ready!\n" +
//                        "\n" +
//                        "  \n" +
//                        "Ooming!\n" +
//                        "\n" +
//                        "  \n" +
//                        "Hang on a second.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Hello?\n" +
//                        "\n" +
//                        "  \n" +
//                        "- Barry?\n" +
//                        "- Adam?\n" +
//                        "\n" +
//                        "  \n" +
//                        "- Oan you believe this is happening?\n" +
//                        "- I can't. I'll pick you up.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Looking sharp.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Use the stairs. Your father\n" +
//                        "paid good money for those.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Sorry. I'm excited.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Here's the graduate.\n" +
//                        "We're very proud of you, son.\n" +
//                        "\n" +
//                        "  \n" +
//                        "A perfect report card, all B's.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Very proud.\n" +
//                        "\n" +
//                        "  \n" +
//                        "Ma! I got a thing going here.\n" +
//                        "\n" +
//                        "  \n" +
//                        "- You got lint on your fuzz.\n" +
//                        "- Ow! That's me!\n" +
//                        "\n" +
//                        "  \n" +
//                        "- Wave to us! We'll be in row 118,000.\n" +
//                        "- Bye!\n" +
//                        "\n" +
//                        "  \n" +
//                        "Barry, I told you,\n" +
//                        "stop flying in the house!\n" +
//                        "\n" +
//                        "  \n" +
//                        "- Hey, Adam.\n" +
//                        "- Hey, Barry.\n" +
//                        "\n" +
//                        "  \n" +
//                        "- Is that fuzz gel?\n"
//
//        };
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
