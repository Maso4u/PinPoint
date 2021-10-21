
package com.pepeta.pinpoint.Model.Directions;
public class Duration
{
    private String text;

    private int value;

    public void setText(String text){
        this.text = text;
    }
    public String getText(){
        return this.text;
    }
    public void setValue(int value){
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
}

