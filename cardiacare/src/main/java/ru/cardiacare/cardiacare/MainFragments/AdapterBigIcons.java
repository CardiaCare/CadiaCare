package ru.cardiacare.cardiacare.MainFragments;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ru.cardiacare.cardiacare.R;

// Адаптер для подстановки иконок меню на FragmentRegisteredScreenBigIcons

public class AdapterBigIcons extends BaseAdapter {
    public Context context;
    private final String[] textArrayValues;

    public AdapterBigIcons(Context context, String[] textArrayValues) {
        this.context = context;
        this.textArrayValues = textArrayValues;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        ImageView imageView;

        if (convertView == null) {
            gridView = inflater.inflate(R.layout.item_menu_icons_big, null);
            TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
            textView.setText(textArrayValues[position]);
//            textView.setTextSize(18);
            imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

            String textArray = textArrayValues[position];
            if (textArray.equals(FragmentRegisteredScreenBigIcons.resources.getText(R.string.pass_survey).toString())) {
                imageView.setImageResource(R.drawable.servey_white);
            } else if (textArray.equals(FragmentRegisteredScreenBigIcons.resources.getText(R.string.bp).toString())) {
                imageView.setImageResource(R.drawable.bpresure);
            } else if (textArray.equals(FragmentRegisteredScreenBigIcons.resources.getText(R.string.ecg).toString())) {
                imageView.setImageResource(R.drawable.monitor);
            } else {
                imageView.setImageResource(R.drawable.documents);
            }
        } else {
            gridView = (View) convertView;
        }
        return gridView;
    }

    @Override
    public int getCount() {
        return textArrayValues.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
