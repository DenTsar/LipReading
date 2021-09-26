package com.example.lipreading;

public class Issue {
    private String title;
    private String text;
    private boolean extra;
    public Issue(String title, String text, boolean extra){
        this.text = text;
        this.title = title;
        this.extra = extra;
    }

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public boolean isExtra() {
        return extra;
    }
}
