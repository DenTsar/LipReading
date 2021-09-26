package com.example.lipreading;

public class Subtitle {
    private String text;
    private int startTime;
    private int duration;
    private String videoId;
    private int aOff;
    private int zOff;
    public Subtitle(String text, int startTime, int duration, String videoId, int aOff, int zOff){
        this.text = text;
        this.startTime = startTime;
        this.duration = duration;
        this.videoId = videoId;
        this.aOff = aOff;
        this.zOff = zOff;
    }
    public Subtitle(String id){
        videoId = id;
        aOff = 300;
        zOff = 300;
    }
    public Subtitle(){

    }

    public int getAOff() {
        return aOff;
    }

    public int getZOff() {
        return zOff;
    }

    public void setAOff(int aOff) {
        this.aOff = aOff;
    }

    public void setZOff(int zOff) {
        this.zOff = zOff;
    }

    public void setText(String text){
        this.text = text;
    }
    public void setStartTime(int startTime){
        this.startTime = startTime;
    }
    public void setDuration(int duration){
        this.duration = duration;
    }
    public void setVideoId(String videoId){
        this.videoId = videoId;
    }
    public String getText() {
        return text;
    }
    public int getDuration() {
        return duration;
    }

    public int getStartTime() {
        return startTime;
    }
    public String getVideoId(){
        return videoId;
    }
    public String toString(){
        return "The video "+videoId+" says \""+text+"\" from "+startTime+" for "+duration;
    }
}

