package com.example.tfk.user;

public class ParentWords {

    private String parent;
    private String word;

    public ParentWords(String word, String parent) {
         this.parent = parent;
         this.word = word;
    }

    public String getParent() {
        return parent;
    }

    public String getWord() {
        return word;
    }

}
