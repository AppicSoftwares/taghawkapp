package com.taghawk.util;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.taghawk.R;
import com.taghawk.constants.AppConstants;
import com.taghawk.ui.home.product_details.ProductDetailsActivity;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Appinventiv on 24-01-2019.
 */

public class BindingUtils {

    @BindingAdapter("bindImage")
    public static void bindImage(ImageView view, String imgUrl) {
        if (view.getContext() != null)
            Glide.with(view.getContext()).asBitmap().load(imgUrl).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(view);
    }


    @BindingAdapter("bindCardImage")
    public static void bindImage(ImageView view, ArrayList<String> imgUrl) {
        if (view.getContext() != null && imgUrl != null && imgUrl.size() > 0)
            Glide.with(view.getContext()).asBitmap().load(imgUrl.get(0)).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(view);
        else {
            view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_home_placeholder));
        }
    }

    @BindingAdapter("bindCurrency")
    public static void bindCurrency(AppCompatTextView view, String amont) {
        view.setText("$ " + amont);
    }

    public @BindingAdapter("bindRewardsPoints")
    static void bindRewardsPoints(AppCompatTextView view, String rewards) {
        view.setText(rewards);
    }

    @BindingAdapter("bindPromotionDays")
    public static void bindPromotionDays(AppCompatTextView view, int promotionDays) {
        view.setText(promotionDays + view.getContext().getString(R.string.days_promotion));
    }
//    @BindingAdapter("bind:showTimeAgo")
//    public static void showTimeAgo(AppCompatTextView view, long timeStamp) {
//        view.setText("" + new TimeAgo().getTimeAgo(AppUtils.timeStampToDate(timeStamp), view.getContext()));
//
//    }

    public static void onClickAction(CardView view, String id) {
        Intent intent = new Intent(view.getContext(), ProductDetailsActivity.class);
        intent.putExtra(AppConstants.NOTIFICATION_ACTION.ENTITY_ID, id);
    }

    @BindingAdapter("shippingType")
    public static void shippingType(AppCompatTextView view, int shipping) {
        String shiping = "";
        switch (shipping) {
            case 1:
                shiping = view.getContext().getString(R.string.pickup);
                break;
            case 2:
                shiping = view.getContext().getString(R.string.deliver);
                break;
            case 3:
                shiping = view.getContext().getString(R.string.shipping);
                break;

        }
        view.setText(shiping);
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("followFollowersCount")
    public static void followFollowersCount(AppCompatTextView view, int followerCount, int followingCount) {

        view.setText("Followers: " + followerCount + " Following: " + followingCount);
    }

    @BindingAdapter("readUnreadImag")
    public static void readUnreadImag(AppCompatImageView view, int status) {
        if (status == 0)
            view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_notification_unread));
        else {
            view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_notification_read));
        }
    }

    @BindingAdapter("followFollowingImage")
    public static void followFollowingImage(CircleImageView view, String url) {
        if (url != null && url.length() > 0) {
            Glide.with(view.getContext()).asBitmap().load(url).apply(RequestOptions.placeholderOf(R.drawable.ic_detail_user_placeholder)).into(view);
        } else {
            view.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.ic_detail_user_placeholder));
        }
    }

    @BindingAdapter("calculateDate")
    public static void calculateDate(AppCompatTextView view, long timeStamp) {

        view.setText("" + new PrettyTime().format(AppUtils.timeStampToDate(timeStamp)));
    }

}
