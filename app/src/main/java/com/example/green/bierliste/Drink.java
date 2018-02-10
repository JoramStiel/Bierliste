package com.example.green.bierliste;

/**
 * Created by green on 10.02.2018.
 */

public class Drink {

    public Drink(long id, String barcode, String name, double price){
        Id=id;
        Barcode = barcode;
        Name = name;
        Price = price;
    }

    public long Id;

    public String Barcode;

    public String Name;

    public double Price;

}
