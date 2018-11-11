package com.gmail.xbikan.pxcrus;


import android.content.Context;
import android.database.Cursor;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.io.IOException;
import java.util.Locale;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class itemFragment extends Fragment {

    public interface OnDataPass {
        public void onDataPass(String data);
    }

    OnDataPass dataPasser;
    private ItemPxc itemPxc;
    private TextView articulText;
    private TextView priceText;
    private DataBaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    public String customerText;
    private String currency = " €";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    public void passData(String data) {
        dataPasser.onDataPass(data);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemPxc = new ItemPxc();
        mDBHelper = new DataBaseHelper(getActivity());

        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.itempxc, container, false);
        articulText = v.findViewById(R.id.articulText);
        priceText = v.findViewById(R.id.itemPrice);
        String strtext = getArguments().getString("query");
        customerText = getArguments().getString("customer");
        sqlRequest(strtext, Integer.parseInt(customerText), itemPxc);
        textForFragment();

        return v;
    }

    public void sqlRequest(String searchText, Integer customerGroup, ItemPxc item) {

        Cursor c = null;
        String[] partners = {"KP", "SI", "MRP", "D", "P"};
        float costUnitCoeff;

        try {
            //запрос в таблицу прайс-листа
            String[] tableColumns = new String[]{"*"};
            String  whereClause = "Name like ? or Name like ? or Art like ?";
            String searchTextUpdate = searchText.replaceAll(" ", "%").toLowerCase();
             String[] whereArgs = new String[]{searchText,searchTextUpdate+"%",searchText};
            c = mDb.query("Price", tableColumns, whereClause, whereArgs,
                    null, null, "Name");
            c.moveToFirst();
            //заполнение объекта Item
            item.setArticul(c.getString(0));
            item.setName(c.getString(1));
            item.setCostUnit(c.getString(2));
            item.setPackageMin(Integer.valueOf(c.getString(3)));
            item.setPriceGroup(c.getString(4));
            item.setBasicPrice(Float.parseFloat(c.getString(5)));

            //запрос в таблицу скидок
            String[] distableColumns = new String[]{partners[customerGroup]};
            String diswhereClause = "ArtGroup = ?";
            String[] diswhereArgs = new String[]{item.getPriceGroup()};
            c = mDb.query("DISCOUNT", distableColumns, diswhereClause, diswhereArgs,
                    null, null, null);
            c.moveToFirst();
            item.setDiscount(Float.parseFloat(c.getString(0)));
            c.close();

//расчитать и добавить цену со скидкой
            if (item.getCostUnit().equals("H")) {
                costUnitCoeff = 0.01f;
                item.setBasicPrice(item.getBasicPrice() * costUnitCoeff);
            }
            item.setDiscountedPrice((100 - item.getDiscount()) * 0.01f * (item.getBasicPrice()));

        } catch (Exception e) {
            item.setArticul("артикул не найден");
            item.setName("");
            item.setPriceGroup("");
            item.setBasicPrice(0);
            item.setDiscount(0);
            item.setDiscountedPrice(0);
            c.close();
        }
    }

    public void onResume() {
        super.onResume();
        String strtext = getArguments().getString("query");
        customerText = getArguments().getString("customer");
        if (!strtext.equals("")) {
            sqlRequest(strtext, Integer.valueOf(customerText), itemPxc);
            textForFragment();
        }
        passData(itemPxc.getArticul());
    }

    public void textForFragment() {
         String[] partnersRussian = {"КП", "СИ", "МРЦ", "Д", "П"};
          int customerInt = Integer.valueOf(getArguments().getString("customer"));
        if (itemPxc.getPriceGroup().equals("RUFIX")){
            priceText.setTextColor(Color.parseColor("#DE0D20"));
            currency=" \u20BD";
        }

        String priceTextString = String.format("%.2f",itemPxc.getBasicPrice()) +currency+ " | " +
                String.format("%.0f",itemPxc.getDiscount()) + "% " + partnersRussian[customerInt] +" | "+
                String.format(Locale.ENGLISH,"%.2f",itemPxc.getDiscountedPrice())+currency;
        articulText.setText(itemPxc.getArticul() + " " + itemPxc.getName()+" "+ itemPxc.getPriceGroup());
        priceText.setText(priceTextString);
    }

}
