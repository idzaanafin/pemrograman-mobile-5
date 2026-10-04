package com.example.pertemuan5;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MahasiswaAdapter extends ArrayAdapter<Mahasiswa> {
    private static class ViewHolder {
        TextView nama;
        TextView noHp;
    }

    public MahasiswaAdapter(Context context, List<Mahasiswa> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_mahasiswa, parent, false);
            holder = new ViewHolder();
            holder.nama = convertView.findViewById(R.id.itemNama);
            holder.noHp = convertView.findViewById(R.id.itemNoHp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Mahasiswa mahasiswa = getItem(position);
        if (mahasiswa != null) {
            holder.nama.setText(mahasiswa.getNama());
            holder.noHp.setText(mahasiswa.getNoHp());
        }
        return convertView;
    }
}
