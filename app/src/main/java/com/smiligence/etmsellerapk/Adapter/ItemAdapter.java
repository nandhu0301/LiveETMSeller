package com.smiligence.etmsellerapk.Adapter;

import static com.smiligence.etmsellerapk.Common.Constant.BOOLEAN_FALSE;
import static com.smiligence.etmsellerapk.Common.Constant.BOOLEAN_TRUE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.smiligence.etmsellerapk.R;
import com.smiligence.etmsellerapk.bean.ItemDetails;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ImageViewHolder> {


    private Context mcontext;
    private List<ItemDetails> itemDetailsList;
    private OnItemClicklistener mlistener;

    public ItemAdapter(Context context , List<ItemDetails> itemDetails ){

        mcontext = context;
        itemDetailsList = itemDetails;
    }


    public interface OnItemClicklistener {
        void Onitemclick(int Position);
    }

    public void setOnItemclickListener(OnItemClicklistener listener) {
        mlistener = listener;
    }

    @NonNull
    @Override
    public ItemAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from ( mcontext ).inflate ( R.layout.itemview, parent, BOOLEAN_FALSE );
        ImageViewHolder imageViewHolder = new ImageViewHolder ( v, mlistener );
        return imageViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.ImageViewHolder holder, int position) {
        ItemDetails itemDetails = itemDetailsList.get ( position );
        holder.setIsRecyclable(false);

        if (itemDetails.getItemStatus().equals("Inactive")) {
            holder.item_catagory.setText(itemDetails.getCategoryName());
            holder.itemName.setText((itemDetails.getItemName()));
            holder.itemPrice.setText(String.valueOf("₹" + itemDetails.getItemPrice()));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher_new);
            requestOptions.error(R.mipmap.ic_launcher_new);
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(itemDetails.getItemImage()).fitCenter().into(holder.itemImages);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.itemImages.setColorFilter(filter);
            holder.item_catagory.setTextColor(Color.GRAY);
            holder.itemPrice.setTextColor(Color.GRAY);
            holder.itemName.setTextColor(Color.GRAY);
            holder.item_status.setTextColor(Color.GRAY);
            holder.item_status.setText(itemDetails.getItemApprovalStatus());
        }
        if (itemDetails.getItemStatus().equals("Active")) {
            holder.item_catagory.setText(itemDetails.getCategoryName());
            holder.itemName.setText((itemDetails.getItemName()));
            holder.itemPrice.setText(String.valueOf("₹" + itemDetails.getItemPrice()));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.mipmap.ic_launcher_new);
            requestOptions.error(R.mipmap.ic_launcher_new);
            Glide.with(mcontext)
                    .setDefaultRequestOptions(requestOptions)
                    .load(itemDetails.getItemImage()).fitCenter().into(holder.itemImages);
            if (itemDetails.getItemApprovalStatus().equals("Waiting for approval")) {
                holder.item_status.setTextColor(mcontext.getColor(R.color.colorAccent));
            } else if (itemDetails.getItemApprovalStatus().equals("Approved")) {
                holder.item_status.setTextColor(mcontext.getColor(R.color.green));
            } else if (itemDetails.getItemApprovalStatus().equals("Declined")) {
                holder.item_status.setTextColor(mcontext.getColor(R.color.red));
            }
            holder.item_status.setText(itemDetails.getItemApprovalStatus());
        }

    }

    @Override
    public int getItemCount() {
        if (itemDetailsList == null) {
            return 0;
        } else {
            return itemDetailsList.size ();
        }
    }
    public  class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName, itemPrice, item_status, item_catagory;
        ImageView itemImages;

        public ImageViewHolder(@NonNull View itemView, final OnItemClicklistener itemClicklistener) {
            super ( itemView );
            item_catagory = itemView.findViewById ( R.id.text_itemcatagory );
            itemName = itemView.findViewById ( R.id.text_itemname );
            itemImages = itemView.findViewById ( R.id.Image_upload );
            itemPrice = itemView.findViewById ( R.id.text_itemPrice );
            item_status = itemView.findViewById ( R.id.item_status );
            itemName.setSelected ( BOOLEAN_TRUE );
            itemView.setOnClickListener ( new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    if (itemClicklistener != null) {
                        int Position = getAdapterPosition ();
                        if (Position != RecyclerView.NO_POSITION) {
                            itemClicklistener.Onitemclick ( Position );
                        }
                    }
                }
            });
        }
    }
}
