package com.example.main_screen.adapter;

public class Reviews1 {

    int id;
    String nickname, mainText, ava;


    public Reviews1(int id, String nickname, String mainText, String ava) {
        this.id = id;
        this.nickname = nickname;
        this.mainText = mainText;
        this.ava = ava;
    }

    public String getAva() {
        return ava;
    }

    public void setAva(String ava) {
        this.ava = ava;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }
}

