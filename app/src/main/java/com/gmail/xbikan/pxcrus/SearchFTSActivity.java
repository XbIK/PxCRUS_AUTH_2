package com.gmail.xbikan.pxcrus;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.xbikan.pxcrus.retrofit.CbClient;
import com.gmail.xbikan.pxcrus.retrofit.RequestGenerator;
import com.gmail.xbikan.pxcrus.retrofit.ValCurs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

import retrofit2.Call;


public class SearchFTSActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private List<ItemPxc> itemPxcList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private String queryTop = "";
    private SharedPreferences sp;
    private String customerNumber;
    private String currency_rate;
    private String strDate;
    private boolean currencyPref, saveHistoryPref, updateEuroPref;
    private LinearLayout linearLayout;
    private static final String TAG = "MyActivity";
    private SearchView searchView;
    private FirebaseAuth mAuth;
    RecyclerView.LayoutManager mLayoutManager;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static final String EURO_RUBBLE_KEY = "euro_rubble";
    private static final String RATE_FIXED_KEY = "rate_fixed";
    private static final String DATE_FIXED_KEY = "date_of_fixed";
    TextView rateText;
    ImageView renewImage;
    FloatingActionButton fab;
    Menu opMenu;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fts);

        rateText = findViewById(R.id.valute_value_text);
        renewImage = findViewById(R.id.ic_renew_valute);
        linearLayout = (LinearLayout) findViewById(R.id.empty_layout);
        fab = findViewById(R.id.floatingActionButton);


        sp = PreferenceManager.getDefaultSharedPreferences(this);
        customerNumber = sp.getString("customer_type", "0");
        currency_rate = sp.getString("currency_rate_number", "68");
        currencyPref = sp.getBoolean("currency_rate_pref", false);
        saveHistoryPref = sp.getBoolean("pref_save_histoy", false);
        queryTop = sp.getString("prefQueryTop", "");
        updateEuroPref = sp.getBoolean("update_euro", true);

        checkAuthFirebase();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new MyAdapter(itemPxcList);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        hideRecycle();
        createRecycleFromHistory();
        handleIntent(getIntent());
        checkEuroUpdateStatus();


        //update listener
        renewImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                renewImage.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.anim_item));
                changeCurrencyRate();
            }

        });

        fab.setOnClickListener(fabListener);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    fab.hide();
                else if (dy < 0)
                    fab.show();
            }
        });


    }

    private void checkEuroUpdateStatus() {
        if (updateEuroPref) {
            renewImage.setVisibility(View.VISIBLE);
            remoteConfig();

        } else {
            String sourceString = "установлен курс евро: " + "<b>" + currency_rate + "</b> " + " \u20BD";
            rateText.setText(Html.fromHtml(sourceString));
            renewImage.setVisibility(View.GONE);
        }
    }

    private void checkAuthFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            fillUserViews();
        } else {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("customer_type", "2");
            editor.commit();
        }
    }

    private String formatDate(Date date) {

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        return formatter.format(date);
    }

    private void updateEuroCB(final String date) {

        RequestGenerator requestGenerator = new RequestGenerator();
        requestGenerator.updateEuroCB(date, new CbClient() {
            @Override
            public Call<ValCurs> getCbValuteOnDate(String url) {
                return null;
            }

            @Override
            public void onSuccess(@NonNull String value) {

                String sourceString = "курс евро ЦБ на " + date.replace("/", ".") +
                        ": " + "<b>" + value + "</b> " + " \u20BD";
                rateText.setText(Html.fromHtml(sourceString));

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("currency_rate_number", value);
                //save last successful CBRF rate
                editor.putString("currency_rate_cb_history", value);
                editor.putString("last_currency_date", date.replace("/", "."));
                editor.apply();

                currency_rate = value;
                changeRecycle();

            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                Log.v("Test", throwable.getMessage());
                Toast.makeText(getApplicationContext(), "не удалось обновить курс евро", Toast.LENGTH_LONG).show();

                //get the last successful CBRF rate

                SharedPreferences.Editor editor = sp.edit();
                editor.putString("currency_rate_number", sp.getString("currency_rate_cb_history", "68.8668"));
                editor.apply();

                String valueSP = sp.getString("currency_rate_cb_history", "68.8668");

                String sourceString = "курс евро ЦБ на " + sp.getString("last_currency_date", "01.01.2018")
                        + ": " + "<b>" + valueSP + "</b> " + " \u20BD";


                rateText.setText(Html.fromHtml(sourceString));
                currency_rate = valueSP;
                changeRecycle();

            }
        });


    }

    private void remoteConfig() {
        renewImage.setVisibility(View.VISIBLE);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(false)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchWelcome();
    }

    private void fetchWelcome() {

        long cacheExpiration = 43200; // 12 hours in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                            changeCurrencyRate();
                        }
                    }
                });
    }

    private void changeCurrencyRate() {
        // [START get_config_values]
        String current_value = mFirebaseRemoteConfig.getString(EURO_RUBBLE_KEY);
        Boolean rate_fixed = mFirebaseRemoteConfig.getBoolean(RATE_FIXED_KEY);
        String date_fixed = mFirebaseRemoteConfig.getString(DATE_FIXED_KEY);

        //if rate FIXED by PxC RUS, put number to SharedPref

        if (!sp.getString("currency_rate_number", "68").equals(current_value) && rate_fixed) {
            Toast.makeText(this, "компанией с " + date_fixed + " установлен фиксированный курс евро: " + current_value + " \u20BD",
                    Toast.LENGTH_LONG).show();

            String sourceString = "компанией с " + date_fixed + " установлен фиксированный курс евро: "
                    + "<b>" + current_value + "</b> " + " \u20BD";
            rateText.setText(Html.fromHtml(sourceString));

            SharedPreferences.Editor editor = sp.edit();
            editor.putString("currency_rate_number", current_value);
            editor.apply();
            currency_rate = current_value;
            changeRecycle();

        }
//if rate not FIXED, update EURO
        if (!rate_fixed) {
            Calendar calendar = Calendar.getInstance();
            Date dateToday = calendar.getTime();
            strDate = formatDate(dateToday);
            updateEuroCB(strDate);
        }


    }

    private void createRecycleFromHistory() {
        if (!queryTop.equals("")) {
            getItems(queryTop);
            // handleIntent(getIntent());
            adapter.notifyDataSetChanged();
            hideRecycle();

        }
    }

    private void fillUserViews() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.getCurrentUser()
                .reload()
                .addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(Exception e) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("customer_type", "2");
                        editor.apply();

                    }
                });


    }


    public boolean onCreateOptionsMenu(Menu menu) {

        opMenu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(true);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
                cursor.moveToPosition(position);
                String suggestion = cursor.getString(2);//2 is the index of col containing suggestion name.
                searchView.setQuery(suggestion, true);//setting suggestion
                return true;
            }
        });


        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_search_black_24dp));
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPxcGreen)));
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                fab.setImageDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_clear_white_24dp));
                fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorFabGrey)));
                return true;  // Return true to expand action view
            }
        };
        MenuItem actionMenuItem = menu.findItem(R.id.search);
        MenuItemCompat.setOnActionExpandListener(actionMenuItem, expandListener);

        //   searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

        //   @Override
        //       public boolean onQueryTextSubmit(String query) {
        // collapse the view ?
        //menu.findItem(R.id.menu_search).collapseActionView();
        //    Log.e("queryText", query);
        //        return false;
        //    }

        //    @Override
        //     public boolean onQueryTextChange(String newText) {
        //   if (newText.length() > 2) {
        //     Log.e("queryText", newText);
        //    getItems(newText);
        //    adapter.notifyDataSetChanged();
        //  hideRecycle();
        //  searchResultSnackbar();
        //   }
        //         return false;
        //      }


        //   });

        return true;
    }

    private View.OnClickListener fabListener = new View.OnClickListener() {
        public void onClick(View v) {
            MenuItem search = opMenu.findItem(R.id.search);
            if (search.isActionViewExpanded()) {
                search.collapseActionView();

            } else {
                search.expandActionView();
            }

        }
    };

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            queryTop = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
            if (saveHistoryPref) {
                suggestions.saveRecentQuery(queryTop, null);

            }

            getItems(queryTop);
            adapter.notifyDataSetChanged();
            searchView.clearFocus();
            hideRecycle();
            searchResultSnackbar();
        }
    }

    private void hideRecycle() {
        if (itemPxcList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        } else {

            recyclerView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);

        }
    }

    private void searchResultSnackbar() {
        int count;
        if (adapter != null) {
            count = adapter.getItemCount();
            Snackbar snack = Snackbar.make(findViewById(R.id.myCoordinatorLayout), "Артикулов найдено: " + Integer.toString(count), Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else {
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
            }
            snack.show();
        }
    }

    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void getItems(String searchTerm) {
        itemPxcList.clear();
        DBAdapterFTS db = new DBAdapterFTS(this);
        db.openDB();
        ItemPxc p;
        Cursor c = db.retrieve(searchTerm);
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
                itemPxcList.add(p);
            }

            c.close();
        }
        db.closeDB();
        recyclerView.setAdapter(adapter);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
    }

    protected void onPause() {

        super.onPause();


    }

    protected void onStop() {
        super.onStop();
        if (!itemPxcList.isEmpty() && !queryTop.equals("")) {
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("prefQueryTop", queryTop);
            editor.commit();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        if (!itemPxcList.isEmpty() && !queryTop.equals("")) {
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("prefQueryTop", queryTop);
            editor.commit();

        }

    }

    protected void onRestart() {
        super.onRestart();

    }

    protected void onResume() {
        super.onResume();

        saveHistoryPref = sp.getBoolean("pref_save_histoy", false);
/*проверка изменений
        1. Тип заказчика
        2. По евро или по рублю
        3. Изменение курса евро
        4. Изменение онлайн обновления

                */
        if (!sp.getString("customer_type", "0").equals(customerNumber)
                || currencyPref != sp.getBoolean("currency_rate_pref", false)
                || !currency_rate.equals(sp.getString("currency_rate_number", "68"))
                || updateEuroPref != sp.getBoolean("update_euro", true)) {

            updateEuroPref = sp.getBoolean("update_euro", true);
            currencyPref = sp.getBoolean("currency_rate_pref", false);
            currency_rate = sp.getString("currency_rate_number", "65");
            customerNumber = sp.getString("customer_type", "0");

            if (updateEuroPref) {
                renewImage.setVisibility(View.VISIBLE);
                changeCurrencyRate();
            } else {
                String sourceString = "установлен курс евро: " + "<b>" + currency_rate + "</b> " + " \u20BD";
                rateText.setText(Html.fromHtml(sourceString));
                renewImage.setVisibility(View.GONE);
                changeRecycle();
            }

        }
    }

    private void changeRecycle() {
        itemPxcList.clear();
        getItems(queryTop);
        adapter.notifyDataSetChanged();
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);


    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

    }


}
