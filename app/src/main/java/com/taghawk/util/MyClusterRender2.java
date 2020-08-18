package com.taghawk.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.taghawk.Jointag.Jointag;
import com.taghawk.R;
import com.taghawk.model.tag.MyItem;
import com.taghawk.ui.home.HomeActivity;

import de.hdodenhof.circleimageview.CircleImageView;


public class MyClusterRender2 extends DefaultClusterRenderer<MyItem> {


    private final float mDensity;
    Context context;

    private final IconGenerator mClusterIconGenerator;

    public MyClusterRender2(Context context, GoogleMap map,
                            ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        mClusterIconGenerator = new IconGenerator(context);
        mDensity = context.getResources().getDisplayMetrics().density;

    }

    private void setupIconGen(IconGenerator generator, Drawable drawable, Context context, int size) {
        TextView textView = new TextView(context);

        textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        if (size >= 100) {
            textView.setText("99+");
        } else if (size >= 200) {
            textView.setText("199+");
        } else if (size >= 200) {
            textView.setText("299");
        } else if (size >= 200) {
            textView.setText("399+");
        } else {
            textView.setText("" + size);
        }
        if (size < 10) {
            mClusterIconGenerator.setContentPadding(5, 5, 5, 5);
        } else {
            mClusterIconGenerator.setContentPadding(40, 40, 40, 40);
        }
        textView.setTextSize(15);
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setTextColor(context.getResources().getColor(R.color.White));
        textView.setLayoutParams(new FrameLayout.LayoutParams(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
        generator.setContentView(textView);
//        generator.setBackground(drawable);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
        final Drawable clusterIcon = context.getResources().getDrawable(R.drawable.map_cluster_circle);
        mClusterIconGenerator.setBackground(clusterIcon);
        mClusterIconGenerator.makeIcon(getClusterText(context.getResources().getColor(R.color.White)));
        //modify padding for one or two digit numbers

        Bitmap icon;
        setupIconGen(mClusterIconGenerator, clusterIcon, context, cluster.getSize());
        icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
//        }
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

    }

    BitmapDescriptor bitmapDescriptor;
    BitmapDescriptor bitmapDescriptorMember;

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {


        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_info_window, null);
        final RelativeLayout llMain = (RelativeLayout) customMarkerView.findViewById(R.id.ll_main);
        final AppCompatImageView background = (AppCompatImageView) customMarkerView.findViewById(R.id.ic_map_pin);
        CircleImageView markerImageView = (CircleImageView) customMarkerView.findViewById(R.id.iv_tag);
        AppCompatTextView text = (AppCompatTextView) customMarkerView.findViewById(R.id.tv_tag_name);
        if (item.getMember()) {
            background.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_community_green));
        } else {
            background.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_pin));
        }

        llMain.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        llMain.layout(0, 0, llMain.getMeasuredWidth(), llMain.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(llMain.getMeasuredWidth(), llMain.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = llMain.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        llMain.draw(canvas);
        text.setText(item.getTagName());
        if (item.getMember()) {
            if (bitmapDescriptorMember == null) {
                bitmapDescriptorMember = BitmapDescriptorFactory.fromBitmap(returnedBitmap);
                markerOptions.icon(bitmapDescriptorMember);
            } else {
                markerOptions.icon(bitmapDescriptorMember);
            }
        } else {
            if (bitmapDescriptor == null) {
                bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(returnedBitmap);
                markerOptions.icon(bitmapDescriptor);
            } else {
                markerOptions.icon(bitmapDescriptor);
            }
        }
    }


    @Override
    protected void onClusterItemRendered(MyItem item, Marker marker) {
//        super.onClusterItemRendered(clusterItem, marker);
        createBitmapForMarker(item.getImageUrl(), item.getTagName(), item.getMember(), marker);
    }

    private void createBitmapForMarker(String url, String name, final boolean isMember, final Marker markerOptions) {
        final View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_info_window, null);
        final RelativeLayout llMain = (RelativeLayout) customMarkerView.findViewById(R.id.ll_main);
        final AppCompatImageView background = (AppCompatImageView) customMarkerView.findViewById(R.id.ic_map_pin);

        final CircleImageView markerImageView = (CircleImageView) customMarkerView.findViewById(R.id.iv_tag);
        ((AppCompatTextView) customMarkerView.findViewById(R.id.tv_tag_name)).setText(name);

        if (!((Jointag) context).isDestroyed()) {

            Glide.with(context).load(url).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//                    Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
//                            R.drawable.ic_profile_email);
//                    markerOptions.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
                    if (isMember) {
                        background.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_community_green));
                    } else {
                        background.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_map_pin));
                    }
                    markerImageView.setImageDrawable(resource);
                    llMain.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    llMain.layout(0, 0, llMain.getMeasuredWidth(), llMain.getMeasuredHeight());

                    Bitmap returnedBitmap = Bitmap.createBitmap(llMain.getMeasuredWidth(), llMain.getMeasuredHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(returnedBitmap);
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
                    Drawable drawable = llMain.getBackground();
                    if (drawable != null)
                        drawable.draw(canvas);
                    llMain.draw(canvas);
                    if (markerOptions.getTag() == null) {
                        markerOptions.setTag("anything");
                    }

                    try {
                        markerOptions.setIcon(BitmapDescriptorFactory.fromBitmap(returnedBitmap));
                    } catch (Exception e) {
                    }

                    return true;
                }
            }).apply(RequestOptions.placeholderOf(R.drawable.ic_home_placeholder)).into(markerImageView);
        }

    }

}