package com.example.lipreading;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class CaptionHandler extends DefaultHandler {

    private Subtitle sub = null;
    private StringBuilder data = null;
    private String id;

    public CaptionHandler(String id){
        this.id = id;
    }
    public void startElement(String uri, String localName, String qName, Attributes attributes){
       if(qName.equalsIgnoreCase("text")){
           sub = new Subtitle(id);
           sub.setStartTime((int)(Double.parseDouble(attributes.getValue("start"))*1000));
           sub.setDuration((int)(Double.parseDouble(attributes.getValue("dur"))*1000));
           sub.setAOff(300);
           sub.setZOff(300);
       }
       data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName){
        String t = data.toString().replaceAll("&#39;","'").replaceAll("&quot;","\"");//punctuation can be replaced with [\W]
        sub.setText(t);
        if (qName.equalsIgnoreCase("text"))
            MainActivity.captions.add(sub);
    }

    @Override
    public void characters(char[] ch, int start, int length){
        data.append(new String(ch, start, length));
    }
}
