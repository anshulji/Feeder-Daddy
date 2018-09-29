package com.dev.fd.feederdaddy.Database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;


import com.dev.fd.feederdaddy.model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "EatItDB";
    private static final int DB_VER=3;


    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts(){

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect={"ID","Foodid","Foodname","Quantity","Price","Image","Restaurantid","Menuid"};
        String sqlTable="OrderDetail";

        qb.setTables(sqlTable);
        Cursor c =qb.query(db,sqlSelect,null,null,null,null,null);

        final List<Order> result= new ArrayList<>();
        if(c.moveToFirst())
        {
            do{
                result.add(new Order(
                                        c.getInt(c.getColumnIndex("ID")),
                                        c.getString(c.getColumnIndex("Foodid")),
                                        c.getString(c.getColumnIndex("Foodname")),
                                        c.getString(c.getColumnIndex("Quantity")),
                                        c.getString(c.getColumnIndex("Price")),
                                        c.getString(c.getColumnIndex("Image")),
                                        c.getString(c.getColumnIndex("Restaurantid")),
                                                c.getString(c.getColumnIndex("Menuid"))));

            }while (c.moveToNext());

        }

        return result;
    }

    public void addToCart(Order order)
    {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("DefaultLocale") String query = String.format("INSERT INTO OrderDetail(ID,Foodid,Foodname,Quantity,Price,Image,Restaurantid,Menuid) VALUES('%d','%s','%s','%s','%s','%s','%s','%s');",
                order.getID(),
                order.getFoodid(),
                order.getFoodname(),
                order.getQuantity(),
                order.getPrice(),
                order.getImage(),
                order.getRestaurantid(),
                order.getMenuid()
        );
        db.execSQL(query);

    }
    public void cleanCart()
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail");
        db.execSQL(query);

    }

    public int getCountCart() {
        int count =0;
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
        Cursor cursor =  db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do{
                count= cursor.getInt(0);
            }while (cursor.moveToNext());

        }
        return count;
    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= %s WHERE ID = %d",order.getQuantity(),order.getID());
        db.execSQL(query);

    }
    public void updateFoodCart(String quantity, int ID) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= %s WHERE ID = %d",quantity,ID);
        db.execSQL(query);

    }

    public void removeFromCart(String Foodname) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE Foodname='%s'",Foodname);
        db.execSQL(query);

    }
    public void removeFoodFromCart(int ID) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE ID = %d",ID);
        db.execSQL(query);

    }
}
