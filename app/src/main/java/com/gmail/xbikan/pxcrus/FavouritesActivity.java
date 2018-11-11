package com.gmail.xbikan.pxcrus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class FavouritesActivity extends AppCompatActivity {
    private List<ItemPxc> itemPxcFavourites = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapterFav adapter;
    private String customerNumber;
    private String currency_rate;
    private boolean currencyPref;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        customerNumber = sp.getString("customer_type", "0");
        currency_rate = sp.getString("currency_rate_number", "65");
        currencyPref = sp.getBoolean("currency_rate_pref", false);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_favourites);
        adapter = new MyAdapterFav(itemPxcFavourites);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        getFavourites();
        adapter.notifyDataSetChanged();
    }

    private void getFavourites() {

        itemPxcFavourites.clear();

        DBAdapterFTS db = new DBAdapterFTS(this);
        db.openDB();
        ItemPxc p;
        Cursor c =  db.dbGetFavourites();
        Cursor cursorSpecial;
        Float specialMrc = null;
        Float specialDiscount = null;
        Float specialDiscountAsCustomer = null;
        String[] partnersRussian = {"КП", "СИ", "МРЦ", "Д", "П"};
        Float discountedPriceFloat;

        if (c != null && c.getCount() > 0) {
            while (c.moveToNext()) {
                String art = c.getString(0);
                String name = c.getString(1);
                String costUnit = c.getString(2);
                Integer packageUnit = c.getInt(3);
                String priceGroup = c.getString(4);
                Float basicPriceFloat = c.getFloat(5);
                Integer discount = db.discountSearch(priceGroup, customerNumber);
                boolean isFavourite = db.checkFavourite(art);

                p = new ItemPxc();

                p.setArticul(art);
                p.setName(name);
                p.setCostUnit(costUnit);
                p.setPackageMin(packageUnit);
                p.setPriceGroup(priceGroup);
                p.setIsFavourite(isFavourite);
                p.setisSpecialPrice(false);
                p.setHasSpecialQuant(false);
                p.setSpecialQuant(0);

                cursorSpecial = db.dbGetSpecial(art);
                while (cursorSpecial.moveToNext()) {
                    if (cursorSpecial != null && cursorSpecial.getCount() > 0) {
                        p.setisSpecialPrice(true);
                        if (cursorSpecial.getInt(2) > 0 &&(Integer.parseInt(customerNumber) > 2)){
                            p.setHasSpecialQuant(true);
                            p.setSpecialQuant(cursorSpecial.getInt(2));
                        }

                        specialMrc = (1 - (cursorSpecial.getFloat(0)) / basicPriceFloat);
                        specialDiscount = (1 - (cursorSpecial.getFloat(1)) / basicPriceFloat);

                        if (Integer.parseInt(customerNumber) > 2) {
                            specialDiscountAsCustomer = specialDiscount;
                        } else {
                            specialDiscountAsCustomer = specialMrc;
                        }
                    }
                }
                cursorSpecial.close();

                if (costUnit != null && costUnit.equals("H")) {
                    basicPriceFloat = basicPriceFloat / 100;
                }
                p.setIsInEuro(!currencyPref);


                if (priceGroup.equals("RUFIX")) {
                    p.setIsInEuro(false);
                }

                if (!p.getIsInEuro() && !priceGroup.equals("RUFIX")) {
                    basicPriceFloat = basicPriceFloat * Float.parseFloat(currency_rate);
                }
                p.setBasicPrice(basicPriceFloat);

                if (p.getisSpecialPrice()) {
                    p.setDiscount(Math.round(specialDiscountAsCustomer * 100));
                    discountedPriceFloat = p.getBasicPrice() * (1 - specialDiscountAsCustomer);

                } else {
                    p.setDiscount(discount);
                    discountedPriceFloat = p.getBasicPrice() * (1 - (p.getDiscount() / 100));
                }

                float df = Math.round(discountedPriceFloat * 100);
                p.setDiscountedPrice(df / 100);

                p.setCustomerGroup(partnersRussian[Integer.parseInt(customerNumber)]);
                itemPxcFavourites.add(p);
            }

            c.close();
        }
        db.closeDB();

        recyclerView.setAdapter(adapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
