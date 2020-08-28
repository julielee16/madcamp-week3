package com.example.madcamp_game.UI.Game;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class PlayerAdapter extends BaseAdapter {

    private LayoutInflater inflate;
    private ArrayList<Player> players;
    private int layout;
    public static Context context;

    String Nickname;
    String Win;
    String Lose;

    public PlayerAdapter(Context context, int layout, ArrayList<Player> players) {
        this.inflate = LayoutInflater.from(context);
        this.layout = layout;
        this.players = players;
        this.context = context;
    }

    @Override
    public int getCount() {
        return players.size();//array size
    }

    @Override
    public Player getItem(int pos) {
        Player item = (Player) players.get(pos);
        return item;
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {

        PlayerView view = null;

        if(convertView == null) {view = new PlayerView(context,null);}
        else { view = (PlayerView) convertView;}

        Player item = (Player) players.get(pos);

        view.setNickname(item.getNickname());
        view.setWinLose("W : " + item.getWin() + " / " + "L : " + item.getLose());

        PlayerClickListener playerclicklistener = new PlayerClickListener(context, item.getNickname(), item.getWin(), item.getLose());
        view.setOnClickListener(playerclicklistener);

        return view;
    }
}
