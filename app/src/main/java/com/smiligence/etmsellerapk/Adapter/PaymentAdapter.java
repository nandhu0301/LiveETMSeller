package com.smiligence.etmsellerapk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smiligence.etmsellerapk.R;
import com.smiligence.etmsellerapk.bean.PaymentDetails;

import java.util.List;

public class PaymentAdapter extends BaseAdapter {

    private Context mcontext;
    private List<PaymentDetails> itemList;
    LayoutInflater inflater;
    String paymentType;
    private List<String> giftWrappingDetails;
    int Percentage;




    public PaymentAdapter(Context context, List<PaymentDetails> itemListŇew, String paymentType1, int Percentage1) {
        mcontext = context;
        itemList = itemListŇew;
        paymentType = paymentType1;
        inflater = (LayoutInflater.from(context));
        Percentage = Percentage1;
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
        TextView startDate,endDate,numberOfOrders,metrozServiceAmount,settelemtAmount,totalBilledAmount,settlementStatus,settlementStatusx,totaldeliveryAmount;
        ;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        PaymentAdapter.ViewHolder holder = new PaymentAdapter.ViewHolder();
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.payment_details_card, parent, false);
            holder.startDate = row.findViewById(R.id.startDate);
            holder.endDate = row.findViewById(R.id.endDate);
            holder.numberOfOrders = row.findViewById(R.id.numberOfOrders);
            holder.metrozServiceAmount = row.findViewById(R.id.metrozServiceCharge);
            holder.settelemtAmount = row.findViewById(R.id.settelementAmount);
            holder.totalBilledAmount = row.findViewById(R.id.totalBilledAmount);
            holder.settlementStatus=row.findViewById(R.id.settelementStatus);
            holder.settlementStatusx=row.findViewById(R.id.billNumberfromtxter);
            holder.totaldeliveryAmount= row.findViewById(R.id.totalDeliveryAmount);
            row.setTag(holder);
        } else {

            holder = (PaymentAdapter.ViewHolder) row.getTag();
        }


        PaymentDetails itemDetailsObj = itemList.get(position);

        holder.startDate.setText(itemDetailsObj.getStartDate());
        holder.endDate.setText(itemDetailsObj.getEndDate());
        holder.numberOfOrders.setText(itemDetailsObj.getOrderCount());
        holder.metrozServiceAmount.setText("₹ " + String.valueOf((((itemDetailsObj.getTotalBilledAmount())* Percentage / 100) +itemDetailsObj.getAdmindeliveryAmount())));
        holder.settelemtAmount.setText("₹ " + String.valueOf((itemDetailsObj.getTotalBilledAmount() - itemDetailsObj.getTotalBilledAmount() * Percentage / 100) + itemDetailsObj.getSellerdeliveryAmount() ));
        holder.totalBilledAmount.setText("₹ " + String.valueOf(itemDetailsObj.getTotalAmount()));
        holder.totaldeliveryAmount.setText("₹ " + String.valueOf(itemDetailsObj.getTotaldeliveryamount()));

        if (itemList.get(position).getPaymentStatus()!=null){

            holder.settlementStatus.setText(itemList.get(position).getPaymentStatus());

        }



        return row;
    }
}

