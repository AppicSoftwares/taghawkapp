package com.taghawk.ui.chat;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;

public class SearchChatActivity extends BaseActivity {


    @Override
    protected int getResourceId() {
        return R.layout.activity_messages_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadSearchChatFragment();
    }

    /**
     * load the required fragment to the container
     */
    private void loadSearchChatFragment() {
        addFragment(R.id.rl_base_container, new SearchChatFragment(), SearchChatFragment.class.getSimpleName());
    }
}
