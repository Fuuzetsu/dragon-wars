package com.group7.dragonwars;
import com.group7.dragonwars.engine.Player;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class IsAiAdapter extends ArrayAdapter<Player> implements OnCheckedChangeListener {
    Context context;
    int layoutResourceId;   
    Player data[] = null;
   
    public IsAiAdapter(Context context, int layoutResourceId, Player[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        IsAiHolder holder = null;
       
        if(row == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
                    //((Activity)context).getLayoutInflater();
            
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new IsAiHolder();
            holder.playerName = (TextView)row.findViewById(R.id.playerName);
            holder.toggleAi = (ToggleButton)row.findViewById(R.id.toggleAI);
            holder.toggleAi.setOnCheckedChangeListener(this);
           
            row.setTag(holder);
        }
        else
        {
            holder = (IsAiHolder) row.getTag();
        }
       
        Player player = data[position];
        holder.playerName.setText(player.getName());
        player.setIsAi(holder.toggleAi.isChecked());
        
        return row;
    }
   
    static class IsAiHolder
    {
        TextView playerName;
        ToggleButton toggleAi;
    }

    @Override
    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
        this.notifyDataSetChanged();
    }
}