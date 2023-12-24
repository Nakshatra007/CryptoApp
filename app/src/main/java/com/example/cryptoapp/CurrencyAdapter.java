package com.example.cryptoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CurrencyAdapter extends BaseAdapter {
    private Context context;
    private List<Currency> currencyList;

    public CurrencyAdapter(Context context, List<Currency> currencyList) {
        this.context = context;
        this.currencyList = currencyList;
    }

    @Override
    public int getCount() {
        return currencyList.size();
    }

    @Override
    public Object getItem(int position) {
        return currencyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // In a more complex scenario, this might return the item's unique ID.
        // For simplicity, we'll use the position as the ID.
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Implement the logic to create and return a View for each item in the list
        // Inflate a layout resource, bind data to views, etc.
        // Use ViewHolder pattern for better performance

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_currency, parent, false);
            // Inflate your custom list item layout (list_item_currency.xml)
        }

        // Bind data to views here
        Currency currency = (Currency) getItem(position);
        TextView nameTextView = convertView.findViewById(R.id.nameTextView);
        TextView rateTextView = convertView.findViewById(R.id.rateTextView);
        ImageView iconImageView = convertView.findViewById(R.id.iconImageView);

        nameTextView.setText(currency.getName());
        rateTextView.setText(String.format("%.6f", currency.getExchangeRate()));

        // Use a library like Picasso to load the icon image asynchronously
        Picasso.get().load(currency.getIconUrl()).into(iconImageView);

        return convertView;
    }

    // Clear the adapter's data
    public void clear() {
        currencyList.clear();
        notifyDataSetChanged();
    }

    // Add a list of items to the adapter
    public void addAll(List<Currency> currencies) {
        currencyList.addAll(currencies);
        notifyDataSetChanged();
    }
}

