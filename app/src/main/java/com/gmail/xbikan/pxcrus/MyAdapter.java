package com.gmail.xbikan.pxcrus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    //static
    private static List<ItemPxc> itemPxcList;
    private static Integer selectedPos = -1;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView art, name, circle;
        public ImageView favStar;


        public MyViewHolder(View view) {
            super(view);
            art = (TextView) view.findViewById(R.id.art_fts);
            name = (TextView) view.findViewById(R.id.name_fts);
            circle = (TextView) view.findViewById(R.id.textCircle);
            favStar = (ImageView) view.findViewById(R.id.imageView_fav);

            View.OnClickListener oclBtn = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.art_fts:
                        case R.id.name_fts:
                            Intent intent = new Intent(v.getContext(), ItemDetails.class);
                            intent.putExtra("key_art", art.getText());
                            intent.putExtra("key_name", name.getText().toString());
                            intent.putExtra("key_art_web", itemPxcList.get(getAdapterPosition()).getArticul());
                            intent.putExtra("key_package", itemPxcList.get(getAdapterPosition()).getPackageMin());
                            v.getContext().startActivity(intent);
                            break;
                        case R.id.imageView_fav:

                            break;
                        case R.id.textCircle:

                            Intent intentFav = new Intent(v.getContext(), FavouritesActivity.class);
                            v.getContext().startActivity(intentFav);
                            break;
                    }
                }
            };

            art.setOnClickListener(oclBtn);
            name.setOnClickListener(oclBtn);
            favStar.setOnClickListener(oclBtn);
            circle.setOnClickListener(oclBtn);

        }
    }


    public MyAdapter(List<ItemPxc> itemPxcList) {
        this.itemPxcList = itemPxcList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fts, parent, false);
        MyViewHolder holder = new MyViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        String currency = " €";
        final ItemPxc item = itemPxcList.get(position);
        String upText = item.getArticul() + " " + item.getName() + " | " + item.getPriceGroup();
        holder.art.setText(upText);
        holder.circle.setText(item.getPriceGroup());
        String group = item.getPriceGroup();

        if (!item.getIsInEuro()) {
            currency = " \u20BD";
        }

        switch (group) {

            case "RUFIX":
                currency = " \u20BD";
                if (!item.getisSpecialPrice()) {
                    holder.name.setTextColor(Color.parseColor("#ff7961"));
                    holder.circle.setBackgroundResource(R.drawable.circle_text_red);
                    holder.circle.setTextColor(Color.parseColor("#fcfcfc"));
                } else {
                    holder.name.setTextColor(Color.parseColor("#757575"));
                    holder.circle.setBackgroundResource(R.drawable.circle_text_yellow);
                    holder.circle.setTextColor(Color.parseColor("#757575"));
                }
                break;

            case "BE1R":
            case "BE2R":
            case "CMPR":
            case "CK6R":
            case "CL2R":
                if (!item.getisSpecialPrice()) {
                    holder.circle.setBackgroundResource(R.drawable.circle_text_ru);
                    holder.name.setTextColor(Color.parseColor("#757575"));
                    holder.circle.setTextColor(Color.parseColor("#fcfcfc"));
                } else {
                    holder.name.setTextColor(Color.parseColor("#757575"));
                    holder.circle.setBackgroundResource(R.drawable.circle_text_yellow);
                    holder.circle.setTextColor(Color.parseColor("#757575"));
                }
                break;

            case "CK6G":
            case "CMPG":
            case "CK1G":
                if (!item.getisSpecialPrice()) {
                    holder.circle.setBackgroundResource(R.drawable.circle_text_green);
                    holder.name.setTextColor(Color.parseColor("#757575"));
                    holder.circle.setTextColor(Color.parseColor("#fcfcfc"));
                } else {
                    holder.name.setTextColor(Color.parseColor("#757575"));
                    holder.circle.setBackgroundResource(R.drawable.circle_text_yellow);
                    holder.circle.setTextColor(Color.parseColor("#757575"));
                }
                break;

            default:
                if (!item.getisSpecialPrice()) {
                    holder.circle.setBackgroundResource(R.drawable.circle_text);
                    holder.name.setTextColor(Color.parseColor("#757575"));
                    holder.circle.setTextColor(Color.parseColor("#fcfcfc"));
                } else {
                    holder.name.setTextColor(Color.parseColor("#757575"));
                    holder.circle.setBackgroundResource(R.drawable.circle_text_yellow);
                    holder.circle.setTextColor(Color.parseColor("#757575"));
                }
        }

        if (item.getIsFavourite()) {
            holder.favStar.setImageResource(R.drawable.ic_star_black_24dp);
        } else {
            holder.favStar.setImageResource(R.drawable.ic_star_border_black_24dp);
        }

//* форматирование реалов в строку с разделением триад и с точкой
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.FRANCE);
        dfs.setDecimalSeparator('.');
        String pattern = "0,000.00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, dfs);
        decimalFormat.setMinimumIntegerDigits(1);
        String priceAsString = decimalFormat.format(item.getBasicPrice());
        String discountPriceAsString = decimalFormat.format(item.getDiscountedPrice());
        String specialQuant = "";
        if (item.isHasSpecialQuant()) {
             specialQuant = "\nскидка при покупке: от " + item.getSpecialQuant() + " шт.";
        }

        String downText = priceAsString + currency +
                "| " + String.format("%.0f", item.getDiscount()) + "% " + item.getCustomerGroup()
                + "| " + discountPriceAsString + currency;

        SpannableString ss1=  new SpannableString(downText+specialQuant);
        ss1.setSpan(new RelativeSizeSpan(0.6f), downText.length(),ss1.length(), 0); // set size
       // ss1.setSpan(new ForegroundColorSpan(Color.RED), 0, 5, 0);
        holder.name.setText(ss1);


        holder.favStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBAdapterFTS db = new DBAdapterFTS(v.getContext());
                db.openDB();
                db.saveFavourite(item.getArticul());
                db.closeDB();
                if (item.getIsFavourite()) {
                    item.setIsFavourite(false);
                } else {
                    item.setIsFavourite(true);
                }

                notifyItemChanged(position);

            }
        });


    }

    public String makeStringFromFloat(float number) {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.FRANCE);
        dfs.setDecimalSeparator('.');
        String pattern = "0,000.00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, dfs);
        decimalFormat.setMinimumIntegerDigits(1);
        return decimalFormat.format(number);
    }

    @Override


    public int getItemCount() {
        return itemPxcList.size();
    }
}


