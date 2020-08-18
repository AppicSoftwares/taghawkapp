package com.taghawk.ui.walkthrough;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.taghawk.R;
import com.taghawk.Video_youtube.VideoScreen;
import com.taghawk.adapters.WalkThroughPagerAdapter;
import com.taghawk.databinding.LayoutTutorialsBinding;
import com.taghawk.ui.onboard.login.LoginActivity;

import java.util.Timer;
import java.util.TimerTask;

public class WalkThroughActivity extends AppCompatActivity implements View.OnClickListener {

    final int size = 3;
    private LayoutTutorialsBinding mTutorialBinding;
    private WalkThroughPagerAdapter adapter;
    private Runnable Update;
    private int currentPage = 0;
    private Timer swipeTimer;
    private RelativeLayout rlplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTutorialBinding = DataBindingUtil.setContentView(this, R.layout.layout_tutorials);
        initView();
        loadPager();
//        imageAutoChange();
    }

    private void initView() {
        mTutorialBinding.tvNext.setOnClickListener(this);
        mTutorialBinding.tvSkip.setOnClickListener(this);
        mTutorialBinding.rlplay.setOnClickListener(this);
    }

    public void loadPager() {
        ViewGroup.LayoutParams p = mTutorialBinding.llBottom.getLayoutParams();
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) p;
        ViewGroup.LayoutParams layoutParams = mTutorialBinding.tvNext.getLayoutParams();
        lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
        mTutorialBinding.llBottom.setLayoutParams(lp);


        adapter = new WalkThroughPagerAdapter(getSupportFragmentManager(), 4);
        mTutorialBinding.vpTutorials.setAdapter(adapter);
        mTutorialBinding.circleIndicator.setViewPager(mTutorialBinding.vpTutorials);
        mTutorialBinding.vpTutorials.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {

            }
        });
        mTutorialBinding.vpTutorials.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                ViewGroup.LayoutParams p = mTutorialBinding.llBottom.getLayoutParams();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) p;
                ViewGroup.LayoutParams layoutParams = mTutorialBinding.tvNext.getLayoutParams();

                currentPage = i;
                if (i == 0) {
                    mTutorialBinding.rlplay.setVisibility(View.VISIBLE);
                    lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
                    mTutorialBinding.tvNext.setText("Start");
                } else if (i == 3) {
                    mTutorialBinding.rlplay.setVisibility(View.GONE);
                    layoutParams.width = (int) getResources().getDimension(R.dimen._110sdp);
                    mTutorialBinding.tvNext.setLayoutParams(layoutParams);
                    lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
                    mTutorialBinding.tvNext.setText(getString(R.string.lets_hawk));
                    mTutorialBinding.tvSkip.setVisibility(View.GONE);
                } else {
//                    mTutorialBinding.tvNext.setWidth((int) getResources().getDimension(R.dimen._70sdp));
                    layoutParams.width = (int) getResources().getDimension(R.dimen._70sdp);
                    mTutorialBinding.tvNext.setLayoutParams(layoutParams);
                    if (i == 0) {
                        mTutorialBinding.rlplay.setVisibility(View.VISIBLE);
                        lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
                    } else if (i == 1) {
                        mTutorialBinding.rlplay.setVisibility(View.GONE);
                        lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
                    } else if (i == 2) {
                        mTutorialBinding.rlplay.setVisibility(View.GONE);
                        lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));

                    }
                    mTutorialBinding.llBottom.setLayoutParams(lp);
                    mTutorialBinding.tvNext.setText("Next");
                    mTutorialBinding.tvSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int i) {
                ViewGroup.LayoutParams p = mTutorialBinding.llBottom.getLayoutParams();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) p;
                ViewGroup.LayoutParams layoutParams = mTutorialBinding.tvNext.getLayoutParams();

                currentPage = i;
                if (i == 0) {
                    lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
                    mTutorialBinding.tvNext.setText("Start");
                } else if (i == 3) {
                    mTutorialBinding.rlplay.setVisibility(View.GONE);
                    layoutParams.width = (int) getResources().getDimension(R.dimen._110sdp);
                    mTutorialBinding.tvNext.setLayoutParams(layoutParams);
                    lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
                    mTutorialBinding.tvNext.setText(getString(R.string.lets_hawk));
                    mTutorialBinding.tvSkip.setVisibility(View.GONE);
                } else {
//                    mTutorialBinding.tvNext.setWidth((int) getResources().getDimension(R.dimen._70sdp));
                    layoutParams.width = (int) getResources().getDimension(R.dimen._70sdp);
                    mTutorialBinding.tvNext.setLayoutParams(layoutParams);
                    if (i == 0) {
                        mTutorialBinding.rlplay.setVisibility(View.VISIBLE);
                        lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
                    } else if (i == 1) {
                        mTutorialBinding.rlplay.setVisibility(View.GONE);
                        lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));
                    } else if (i == 2) {
                        mTutorialBinding.rlplay.setVisibility(View.GONE);
                        lp.setMargins(0, 0, 0, (int) getResources().getDimension(R.dimen._150sdp));

                    }
                    mTutorialBinding.llBottom.setLayoutParams(lp);
                    mTutorialBinding.tvNext.setText("Next");
                    mTutorialBinding.tvSkip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mTutorialBinding.vpTutorials.setOffscreenPageLimit(3);
    }

    private void imageAutoChange() {

        final Handler handler = new Handler();
        Update = new Runnable() {
            public void run() {
                if (currentPage == size) {
                    currentPage = 0;
                }
                mTutorialBinding.vpTutorials.setCurrentItem(currentPage++);
            }
        };

        swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            //
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 1500);
    }

    private void moveToNextActivity() {
        Intent intent = new Intent(WalkThroughActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_skip:
                moveToNextActivity();
                break;
            case R.id.tv_next:
                if (currentPage < 3)
                    mTutorialBinding.vpTutorials.setCurrentItem(++currentPage);

                else
                    moveToNextActivity();
                break;
            case R.id.rlplay:
                startActivity(new Intent(WalkThroughActivity.this, VideoScreen.class));
                  /*  final Dialog dialog = new Dialog(CelebDashBoard.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setCancelable(true);
                    dialog.setContentView(R.layout.custom_dialog_logout);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    btnpay_now = dialog.findViewById(R.id.btnpay_now);

                    btnpay_now.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            startActivity(new Intent(CelebDashBoard.this, WhoWeAre.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    });
                    dialog.show();*/
                Log.d("vihal", "kjdshf");
                break;
        }
    }
}
