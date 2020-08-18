package com.taghawk.ui.setting.change_password;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.model.request.ChangePassword;

public class ChangePasswordActivity extends BaseActivity {

    @Override
    protected int getResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addFragment(R.id.home_container, new ChangePasswordFragment(), ChangePassword.class.getSimpleName());
    }
}
