package com.taghawk.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.taghawk.R;
import com.taghawk.custom_dialog.CustomCommonDialog;
import com.taghawk.custom_dialog.DialogCallback;
import com.taghawk.data.DataManager;
import com.taghawk.interfaces.OnDialogItemClickListener;
import com.taghawk.interfaces.OnDialogViewClickListener;
import com.taghawk.model.FailureResponse;
import com.taghawk.ui.onboard.login.LoginActivity;

import java.util.Objects;


public class BaseFragment extends Fragment {


    private Observer<Throwable> errorObserver;
    private Observer<FailureResponse> failureResponseObserver;
    private Observer<Boolean> loadingStateObserver;
    private CustomCommonDialog mLogout;
    private static boolean isLogoutShowing = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initObservers();
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

        loadingStateObserver = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean != null)
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
        hideProgressDialog();
        if (failureResponse.getErrorCode() == 401) {

            mLogout = new CustomCommonDialog(getActivity(), getString(R.string.invalid), getString(R.string.logout_msg), getString(R.string.logout), getString(R.string.cencel), new DialogCallback() {
                @Override
                public void submit(String data) {
                    isLogoutShowing = false;
                    DataManager.getInstance().clearPreferences();
                    startActivity(new Intent(getActivity(), LoginActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    getActivity().finish();
                }

                @Override
                public void cancel() {
                    isLogoutShowing = false;
                }
            });
            if (!isLogoutShowing) {
                isLogoutShowing = true;
                mLogout.show();
            }


        } else {
            if (failureResponse != null && failureResponse.getErrorMessage() != null) {
               if (failureResponse.getErrorCode()==404) {
                   getCustomBottomDialog(getString(R.string.oops), failureResponse.getErrorMessage().toString(), new OnDialogItemClickListener() {
                       @Override
                       public void onPositiveBtnClick() {
                           if (getActivity() != null && !getActivity().isFinishing())
                               getActivity().finish();
                       }

                       @Override
                       public void onNegativeBtnClick() {

                       }
                   });
               }else {
//                   showToastShort(failureResponse.getErrorMessage());
                   Log.e("onErrorOccurred: ", failureResponse.getMessage()+"");

               }
            } else {
                if (failureResponse != null && failureResponse.getMessage() != null)
//                    showToastShort(failureResponse.getMessage());
                    Log.e("onErrorOccurred: ", failureResponse.getMessage()+"");
                else {
                    showToastShort("Your account have been blocked by admin.");
                }
            }
            Log.e("onFailure: ", failureResponse.getErrorMessage() + "   " + failureResponse.getErrorCode());
        }

    }

    protected void onErrorOccurred(Throwable throwable) {
        hideProgressDialog();
        showToastShort(throwable.getMessage());
        Log.e("onErrorOccurred: ", throwable.getMessage());
    }

    public void showToastLong(CharSequence message) {
        try {
            if (getActivity() != null && !getActivity().isFinishing())
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void showToastShort(CharSequence message) {
        try {
            if (getActivity() != null && !getActivity().isFinishing())
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProgressDialog() {
        try {
            if (getActivity() != null && !getActivity().isFinishing())
                ((BaseActivity) getActivity()).showProgressDialog();
        } catch (Exception e) {

        }
    }

    public void hideProgressDialog() {
        try {
            if (getActivity() != null && !getActivity().isFinishing())
                ((BaseActivity) (getActivity())).hideProgressDialog();
        } catch (Exception e) {
        }
    }


    public String getDeviceId() {

        return ((BaseActivity) Objects.requireNonNull(getActivity())).getDeviceId();
    }

    public void getCustomBottomDialog(String title, String message, OnDialogItemClickListener listener) {
        if (getActivity() != null && !getActivity().isFinishing())
            ((BaseActivity) Objects.requireNonNull(getActivity())).CustomBottomDialog(title, message, listener);
    }

    public void getCustomShippingBottomDialog(Integer[] shipping, OnDialogViewClickListener listener) {
        if (getActivity() != null && !getActivity().isFinishing())
            ((BaseActivity) Objects.requireNonNull(getActivity())).CustomShippingBottomDialog(shipping, listener);
    }

    public void hideKeyboard() {
        if (getActivity() != null && !getActivity().isFinishing())
            ((BaseActivity) Objects.requireNonNull(getActivity())).hideKeyboard();
    }

    public void showNoNetworkError() {
        if (getActivity() != null && !getActivity().isFinishing())
            ((BaseActivity) Objects.requireNonNull(getActivity())).showNoNetworkError();
    }

    public void popFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStackImmediate();
        }
    }

    public void performShareAction(String url) {
        if (getActivity() != null && !getActivity().isFinishing())
            ((BaseActivity) Objects.requireNonNull(getActivity())).performShareAction(url);
    }
}
