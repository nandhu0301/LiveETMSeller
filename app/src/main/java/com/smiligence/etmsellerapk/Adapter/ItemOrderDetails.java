package com.smiligence.etmsellerapk.Adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmsellerapk.R;
import com.smiligence.etmsellerapk.bean.ItemDetails;

import java.util.List;

public class ItemOrderDetails extends BaseAdapter {

    private Context mcontext;
    private List<ItemDetails> itemList;
    LayoutInflater inflater;
 //   private List<String> giftWrappingDetails;

    public ItemOrderDetails(Context context, List<ItemDetails> itemListŇew) {
        mcontext = context;
        itemList = itemListŇew;
        inflater = (LayoutInflater.from(context));
       // giftWrappingDetails=orderDetails1;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView images;
        TextView t_name, t_price_percent, t_total_amount;
        RelativeLayout itemViewLayout ;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_details_layout, parent, false);
            holder.t_name = row.findViewById(R.id.itemName);
            holder.t_price_percent = row.findViewById(R.id.item_qty);
            holder.t_total_amount = row.findViewById(R.id.itemTotal);
            holder.itemViewLayout = row.findViewById(R.id.ItemViewLayout);

            holder.images = (ImageView) row.findViewById(R.id.itemImage);
            row.setTag(holder);
        } else {

            holder = (ViewHolder) row.getTag();
        }

        ItemDetails itemDetailsObj = itemList.get(position);



            holder.t_name.setText((itemDetailsObj.getItemName()));



        holder.t_price_percent.setText(itemDetailsObj.getItemPrice() + " * " + itemDetailsObj.getItemBuyQuantity());
        holder.t_total_amount.setText("₹" + String.valueOf(itemDetailsObj.getTotalItemQtyPrice()));

        if (itemDetailsObj.getOrderStatus() !=null) {
            if (itemDetailsObj.getOrderStatus().equals("Your Order is Shipped")) {

                holder.itemViewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext,R.style.CustomAlertDialog);
                        final View customLayout = ((LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.viewtrackingdetails_layout, null);

                        builder.setView(customLayout);
                        final AlertDialog dialog = builder.create();
                        dialog.show();

                        TextView    deliverystatus = customLayout.findViewById(R.id.delivery_status);
                        TextView    courierstatus = customLayout.findViewById(R.id.courierdelivery_status);
                        TextView    trackingid = customLayout.findViewById(R.id.couriertrackingdelivery_status);
                        ImageView   trackingimage = customLayout.findViewById(R.id.trackingimage);

                        deliverystatus.setText(itemDetailsObj.getOrderStatus());
                        courierstatus.setText(itemDetailsObj.getCourierName());
                        trackingid.setText(itemDetailsObj.getTrackingId());
                        Glide.with(mcontext).load(itemDetailsObj.getTrackingimage()).into(trackingimage);


                    }
                });




            } else if (itemDetailsObj.getOrderStatus().equals("Delivered")) {



                holder.itemViewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mcontext,R.style.CustomAlertDialog);
                        final View customLayout = ((LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.viewtrackingdetails_layout, null);

                        builder.setView(customLayout);
                        final AlertDialog dialog = builder.create();
                        dialog.show();



                        TextView    deliverystatus = customLayout.findViewById(R.id.delivery_status);
                        TextView    courierstatus = customLayout.findViewById(R.id.courierdelivery_status);
                        TextView    trackingid = customLayout.findViewById(R.id.couriertrackingdelivery_status);
                        ImageView   trackingimage = customLayout.findViewById(R.id.trackingimage);

                        deliverystatus.setText(itemDetailsObj.getOrderStatus());
                        courierstatus.setText(itemDetailsObj.getCourierName());
                        trackingid.setText(itemDetailsObj.getTrackingId());
                        Glide.with(mcontext).load(itemDetailsObj.getTrackingimage()).into(trackingimage);
                    }
                });
            }

        }


        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.mipmap.ic_launcher_new);
        requestOptions.error(R.mipmap.ic_launcher_new);
        if (!((Activity) mcontext).isFinishing ()) {
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(itemDetailsObj.getItemImage()).fitCenter().into(holder.images);
        }







        return row;


    }
}

