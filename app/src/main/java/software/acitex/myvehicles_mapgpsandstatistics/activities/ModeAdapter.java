package software.acitex.myvehicles_mapgpsandstatistics.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import software.acitex.myvehicles_mapgpsandstatistics.R;
import software.acitex.myvehicles_mapgpsandstatistics.Tools.ModeItem;

public class ModeAdapter extends ArrayAdapter<ModeItem> {

    public ModeAdapter(Context context, ArrayList<ModeItem> modeList){
        super(context,0,modeList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }
    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.mode_spinner_row,parent, false
            );
        }

        ImageView imageViewMode = convertView.findViewById(R.id.image_view_mode);
        TextView textViewMode = convertView.findViewById(R.id.text_view_mode);

        ModeItem currentMode = getItem(position);

        if(currentMode != null) {
            imageViewMode.setImageResource(currentMode.getModeImage());
            textViewMode.setText(currentMode.getModeName());
        }

        return convertView;
    }
}
