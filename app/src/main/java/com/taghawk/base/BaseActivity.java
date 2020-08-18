package com.taghawk.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.taghawk.R;
import com.taghawk.custom_dialog.CustomCommonDialog;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.ui.chat.GroupDetailActivity;
import com.taghawk.ui.chat.MessagesDetailActivity;
import com.taghawk.ui.onboard.login.LoginActivity;
import com.taghawk.util.AppUtils;
import com.taghawk.util.DialogUtil;
import com.taghawk.util.ResourceUtils;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    private Observer<Throwable> errorObserver;
    private Observer<FailureResponse> failureResponseObserver;
    private Observer<Boolean> loadingStateObserver;
    private RelativeLayout baseContainer;
    private ProgressDialog mProgressDialog;
    private CustomCommonDialog mLogout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        baseContainer = findViewById(R.id.rl_base_container);
        setLayout();
        ButterKnife.bind(this);
        initObservers();
    }

    protected void setHeader(String title) {
        if (findViewById(R.id.tv_title) != null) {
            ((AppCompatTextView) findViewById(R.id.tv_title)).setVisibility(View.VISIBLE);
            ((AppCompatTextView) findViewById(R.id.tv_title)).setText(title);
        }
    }

    private void initObservers() {
        errorObserver = new Observer<Throwable>() {
            @Override
            public void onChanged(@Nullable Throwable throwable) {
                onErrorOccurred(throwable);
            }
        };

        failureResponseObserver = new Observer<FailureResponse>() {
            @Override
            public void onChanged(@Nullable FailureResponse failureResponse) {
                onFailure(failureResponse);
            }
        };

        /**
         * experimental
         * */
        loadingStateObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                onLoadingStateChanged(aBoolean);
            }
        };
    }

    public Observer<Throwable> getErrorObserver() {
        return errorObserver;
    }

    public Observer<FailureResponse> getFailureResponseObserver() {
        return failureResponseObserver;
    }

    public Observer<Boolean> getLoadingStateObserver() {
        return loadingStateObserver;
    }

    protected void onLoadingStateChanged(boolean aBoolean) {
        if (aBoolean) {
            showProgressDialog();
        } else {
            hideProgressDialog();
        }
    }

    protected void onFailure(FailureResponse failureResponse) {
        getLoadingStateObserver().onChanged(false);
        if (failureResponse.getErrorCode() == 401) {
            if (mLogout != null && !mLogout.isShowing()) {
                mLogout = new CustomCommonDialog(this, getString(R.string.invalid), getString(R.string.logout_msg), getString(R.string.logout), getString(R.string.cencel), new DialogCallback() {
                    @Override
                    public void submit(String data) {
                        DataManager.getInstance().clearPreferences();
                        startActivity(new Intent(BaseActivity.this, LoginActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }

                    @Override
                    public void cancel() {

                    }
                });
                mLogout.show();
            }
        } else {
            showToastShort("failure:" + failureResponse.getErrorMessage());
            Log.e("onFailure: ", failureResponse.getErrorMessage() + "   " + failureResponse.getErrorCode());
        }
    }

    protected void onErrorOccurred(Throwable throwable) {
        getLoadingStateObserver().onChanged(false);
        showToastShort("error");
        Log.e("onErrorOccurred: ", throwable.getMessage());
    }

    /**
     * Method is used to set the layout in the Base Activity.
     * Layout params of the inserted child is match parent
     */
    private void setLayout() {
        if (getResourceId() != -1) {
            removeLayout();
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT
                    , RelativeLayout.LayoutParams.MATCH_PARENT);

            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            if (layoutInflater != null) {
                View view = layoutInflater.inflate(getResourceId(), null);
                baseContainer.addView(view, layoutParams);
            }
        }
    }


    /**
     * hides keyboard onClick anywhere besides edit text
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!(this instanceof MessagesDetailActivity) && !(this instanceof GroupDetailActivity)) {
            View view = getCurrentFocus();
            if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
                int scrcoords[] = new int[2];
                view.getLocationOnScreen(scrcoords);
                float x = ev.getRawX() + view.getLeft() - scrcoords[0];
                float y = ev.getRawY() + view.getTop() - scrcoords[1];
                if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                    ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Method used to get unique device id
     */
    public String getDeviceId() {
        return AppUtils.getUniqueDeviceId(this);
    }


    /**
     * Method is used by the sub class for passing the id of the layout ot be inflated in the relative layout
     *
     * @return id of the resource to be inflated
     */
    protected abstract int getResourceId();


    public void addFragment(int layoutResId, BaseFragment fragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null)
            getSupportFragmentManager().beginTransaction()
                    .add(layoutResId, fragment, tag)
                    .commit();
    }

    public void addFragmentWithBackstack(int layoutResId, BaseFragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .add(layoutResId, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }


    public void replaceFragment(int layoutResId, BaseFragment fragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null)
            getSupportFragmentManager().beginTransaction()

                    .replace(layoutResId, fragment, tag)
                    .commit();
    }

    public void replaceFragment(int layoutResId, Fragment fragment, String tag) {
        if (getSupportFragmentManager().findFragmentByTag(tag) == null)
            getSupportFragmentManager().beginTransaction()

                    .replace(layoutResId, fragment, tag)
                    .commit();
    }

    public void replaceFragmentWithBackstack(int layoutResId, BaseFragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(layoutResId, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    public void replaceFragmentWithBackstackWithStateLoss(int layoutResId, BaseFragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(layoutResId, fragment, tag)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }


    /**
     * This method is used to remove the view already present as a child in relative layout.
     */
    private void removeLayout() {
        if (baseContainer.getChildCount() >= 1)
            baseContainer.removeAllViews();
    }

    public void showToastLong(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public void showToastShort(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * This function is used to show a progress dialog
     *
     * @param -context of the activity
     */
    public void showProgressDialog() {
        try {
            if (mProgressDialog != null)
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            mProgressDialog = ProgressDialog.show(this, null, null, true);
            mProgressDialog.setContentView(R.layout.dialog_view);

            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            mProgressDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function is used to dismiss the dialog
     */
    public void hideProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void showUnknownRetrofitError() {
        hideProgressDialog();
        showToastLong(ResourceUtils.getInstance().getString(R.string.something_went_wrong));
    }

    public void showNoNetworkError() {
        try {
            hideProgressDialog();
            showToastLong(ResourceUtils.getInstance().getString(R.string.no_internet));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void hideKeyboard() {
            AppUtils.hideKeyboard(this);
    }


    public void popFragment() {
        if (getSupportFragmentManager() != null) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    public void logout() {
        // Logout UI changes
    }

    public Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Fragment currentFragment = fragmentManager.findFragmentByTag(fragmentTag);
        return currentFragment;
    }

    public int getBackStackSize() {
        return getSupportFragmentManager().getBackStackEntryCount();
    }

    public void CustomBottomDialog(String title, String message, OnDialogItemClickListener listener) {
        DialogUtil.getInstance().CustomBottomSheetDialog(this, title, message, listener);
    }

    public void performShareAction(String url) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(shareIntent, "Share link using"));
    }

    public void CustomShippingBottomDialog(Integer[] shipping, OnDialogViewClickListener listener) {
        DialogUtil.getInstance().CustomShippingTypeBottomSheetDialog(this, shipping, listener);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
