package com.gmail.xbikan.pxcrus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Created by xbika on 22-Nov-17.
 */

public class DBAdapterFTS {
    Context c;
    SQLiteDatabase db;
    DataBaseHelper helper;

    public DBAdapterFTS(Context c) {
        this.c = c;
        helper = new DataBaseHelper(c);
    }

    //OPEN DB
    public void openDB() {

        try {
            helper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            db = helper.getWritableDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //CLOSE
    public void closeDB() {
        try {
            helper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //SAVE OR INSERT
    public Cursor retrieve(String searchTerm) {


        String[] tableColumns = new String[]{"*"};
        String whereClause;
        String searchTextUpdate;
        String[] whereArgs;
        String[] myArray;

        Cursor c = null;
        String searchTrimmed = searchTerm.replace("%", "").replace("_", "");
        if (searchTerm != null && searchTrimmed.length() > 1) {

            Pattern p = Pattern.compile("\\b\\d{5,7}:?\\d{1,5}");
            Matcher m = p.matcher(searchTrimmed);
            List<String> a = new ArrayList<String>();


            while (m.find()) {
                String num = m.group();
                a.add(num);
            }
            myArray = new String[a.size()];
            a.toArray(myArray);
            String queryregex = " Art like ? ";
            String pattern = " Art like ? ";
            for (int i = 1; i < a.size(); i += 1) {
                queryregex = queryregex + "OR" + pattern;
            }

            if (myArray.length > 0) {
                whereClause = queryregex;
                searchTextUpdate = searchTerm.replaceAll(" ", "%").toLowerCase();
                whereArgs = myArray;
            } else {
                //if parsing is empty
                whereClause = "Name like ? or Art like ? or PriceGroup like ?";
                searchTextUpdate = searchTerm.replaceAll(" ", "%").toLowerCase();
                whereArgs = new String[]{searchTextUpdate + "%", searchTerm + "%", searchTerm};
                // end if empty
            }
            c = db.query("PRICE", tableColumns, whereClause, whereArgs,
                    null, null, "Name", "500");

            return c;
        }

        return c;
    }

    public Integer discountSearch(String priceGroup, String customerNumber) {
        String[] partners = {"KP", "SI", "MRP", "D", "P"};
        Integer discountInt = 0;
        String[] columns = {"ArtGroup", "Name"};
        Cursor c = null;
        if (priceGroup != null && priceGroup.length() > 0) {
            String[] distableColumns = new String[]{partners[Integer.parseInt(customerNumber)]};
            String diswhereClause = "ArtGroup = ?";
            String[] diswhereArgs = new String[]{priceGroup};
            c = db.query("DISCOUNT", distableColumns, diswhereClause, diswhereArgs,
                    null, null, null);

        }
        if (c != null && c.moveToFirst()) {
            discountInt = c.getInt(0);
            c.close();
        }
        return discountInt;
    }

    public void saveFavourite(String artFavourite) {

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);
        String[] diswhereArgs = new String[]{artFavourite};

        if (!checkFavourite(artFavourite)) {
            ContentValues values = new ContentValues();
            values.put("Art", artFavourite);
            values.put("Date", reportDate);
            db.insert("FAVOURITES", null, values);
        } else {
            db.delete("FAVOURITES", "Art = ?", diswhereArgs);
        }
    }

    public boolean checkFavourite(String articul) {
        String[] tableColumns = new String[]{"Art", "Date"};
        String[] diswhereArgs = new String[]{articul};
        Cursor c = null;
        c = db.query("FAVOURITES", tableColumns, "Art = ?", diswhereArgs, null, null, "Date");
        if (c != null && c.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public Cursor dbGetFavourites() {
        Cursor c = null;
        c = db.rawQuery("SELECT PRICE.* FROM PRICE  join FAVOURITES  ON FAVOURITES.Art=PRICE.Art order by FAVOURITES.Date desc", null);
        return c;

    }

    public Cursor dbGetSpecial(String articul) {
        Cursor c = null;
        c = db.rawQuery("SELECT SPECIALPRICE.PriceMrc, SPECIALPRICE.PriceSpecial, SPECIALPRICE.QuantSpecial \n" +
                " FROM  SPECIALPRICE    where  SPECIALPRICE.Art like ?", new String [] {articul});
        return c;

    }
}
