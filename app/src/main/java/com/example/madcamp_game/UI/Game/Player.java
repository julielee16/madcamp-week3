package com.example.madcamp_game.UI.Game;

public class Player {

    String Nickname;
    String Win;
    String Lose;

    public Player(String nickname, String win, String lose) {
        Nickname = nickname;
        Win = win;
        Lose = lose;
    }

    public String getNickname() {
        return Nickname;
    }
    public void setNickname(String nickname) {
        Nickname = nickname;
    }
    public String getWin() {
        return Win;
    }
    public void setWin(String win) {
        Win = win;
    }
    public String getLose() {
        return Lose;
    }
    public void setLose(String lose) {
        Lose = lose;
    }
}
