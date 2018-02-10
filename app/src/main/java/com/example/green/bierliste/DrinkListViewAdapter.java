package com.example.green.bierliste;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by green on 10.02.2018.
 */

public class DrinkListViewAdapter extends BaseAdapter {

    private ArrayList<Drink> drinks;
    private Activity activity;

    private TextView tvDrinkBarcode;
    private TextView tvDrinkName;
    private TextView tvDrinkPrice;

    public DrinkListViewAdapter(Activity activity,ArrayList<Drink> drinks){
        super();

        this.drinks=drinks;
        this.activity=activity;


    }

    @Override
    public int getCount() {
        return drinks.size();
    }

    @Override
    public Object getItem(int i) {
        if(drinks==null || getCount()<=i)
            return null;

        return drinks.get(i);
    }

    @Override
    public long getItemId(int i) {
        Drink drink = (Drink)getItem(i);

        if(drink==null)
            return -1;

        return drink.Id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = activity.getLayoutInflater();

        if(view == null){

            view = inflater.inflate(R.layout.add_drinkgrid_layout, null);

            tvDrinkBarcode = view.findViewById(R.id.tvDrinkBarcode);
            tvDrinkName = view.findViewById(R.id.tvDrinkName);
            tvDrinkPrice = view.findViewById(R.id.tvDrinkPrice);

        }

        Drink ctDrink = (Drink) getItem(i);

        if(ctDrink!=null){

            tvDrinkName.setText(ctDrink.Name);
            tvDrinkPrice.setText(""+ctDrink.Price);
            tvDrinkBarcode.setText(ctDrink.Barcode);
        }

        return view;
    }

    public boolean addItem(Drink drink){
        if(drink==null)
            return false;

        long idHighest = 0;
        for (Drink ctDrink: drinks) {
            if(ctDrink.Barcode==drink.Barcode){
                return false;
            }

            if(idHighest<ctDrink.Id){
                idHighest=ctDrink.Id;
            }
        }

        drink.Id = idHighest+1;
        drinks.add(drink);
        notifyDataSetChanged();

        return true;
    }
}
