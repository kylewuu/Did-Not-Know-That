package com.app.tfk.user;

public class ArticleWords {
    private String word;
    private String url;

    public ArticleWords(String url, String word) {
        this.url = url;
        this.word = word;
    }

    public String getUrl(){
        return url;
    }

    public String getWord() {
        return word;
    }
}
