package com.example.map;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.map.model.Facet;

import java.util.List;

public class ActiviteAdapterSpinner extends ArrayAdapter<Facet> {
    int resId;
    String colors[];
    public ActiviteAdapterSpinner(Context context, int resource, List<Facet> objects, String[] colors) {
        super(context, resource, objects);
        resId = resource;
        this.colors = colors;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = LayoutInflater.from(getContext()).inflate(resId, null);

        View viewColor = convertView.findViewById(R.id.viewColor);
        TextView textViewActivite = convertView.findViewById(R.id.textViewActivite);

        Facet item = getItem(position);

        viewColor.setBackgroundColor(Color.parseColor(colors[position]));
        textViewActivite.setText(item.name);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(resId, null);

        View viewColor = convertView.findViewById(R.id.viewColor);
        TextView textViewActivite = convertView.findViewById(R.id.textViewActivite);

        Facet item = getItem(position);

        viewColor.setBackgroundColor(Color.parseColor(colors[position]));
        textViewActivite.setText(item.name);

        return convertView;
    }
}
