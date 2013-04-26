package com.group7.dragonwars;
import com.group7.dragonwars.engine.Player;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class IsAiAdapter extends ArrayAdapter<Player>
    implements OnCheckedChangeListener {
    private Context context;
    private int layoutResourceId;
    private Player[] data = null;

    public IsAiAdapter(final Context ctext, final int layoutResId,
                       final Player[] pData) {
        super(ctext, layoutResId, pData);
        this.layoutResourceId = layoutResId;
        this.context = ctext;
        this.data = pData;
    }

    @Override
    public final View getView(final int position, final View convertView,
                              final ViewGroup parent) {
        View row = convertView;
        IsAiHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new IsAiHolder();
            holder.playerName = (TextView) row.findViewById(R.id.playerName);
            holder.toggleAi = (ToggleButton) row.findViewById(R.id.toggleAI);
            holder.toggleAi.setOnCheckedChangeListener(this);

            row.setTag(holder);
        } else {
            holder = (IsAiHolder) row.getTag();
        }

        Player player = data[position];
        holder.playerName.setText(player.getName());
        player.setIsAi(holder.toggleAi.isChecked());

        return row;
    }

    static class IsAiHolder {
        private TextView playerName;
        private ToggleButton toggleAi;
    }

    @Override
    public final void onCheckedChanged(final CompoundButton arg0,
                                       final boolean arg1) {
        this.notifyDataSetChanged();
    }
}
