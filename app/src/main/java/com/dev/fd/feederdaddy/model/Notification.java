package com.dev.fd.feederdaddy.model;

public class Notification {

    public String body,title,click_action,sound,android_channel_id;

    public Notification(String body, String title, String click_action, String sound, String android_channel_id) {
        this.body = body;
        this.title = title;
        this.click_action = click_action;
        this.sound = sound;
        this.android_channel_id = android_channel_id;
    }

    public String getAndroid_channel_id() {
        return android_channel_id;
    }

    public void setAndroid_channel_id(String android_channel_id) {
        this.android_channel_id = android_channel_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getClick_action() {
        return click_action;
    }

    public void setClick_action(String click_action) {
        this.click_action = click_action;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
}