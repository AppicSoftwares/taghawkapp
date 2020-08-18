package com.taghawk.ui.chat;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.chat.ChatModel;

import java.util.Calendar;
import java.util.TimeZone;

public class GroupDetailActivity extends BaseActivity {
    private GroupDetailFragment groupDetailFragment;

    @Override
    protected int getResourceId() {
        return R.layout.activity_messages_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadGroupDetailFragment();
    }

    /**
     * load the required fragment in the container
     */
    private void loadGroupDetailFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, getIntent().getParcelableExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA));
        groupDetailFragment = new GroupDetailFragment();
        groupDetailFragment.setArguments(bundle);
        addFragment(R.id.rl_base_container, groupDetailFragment, MessagesDetailSingleChatFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent();
        intent.putExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA,groupDetailFragment.chatModel);
        setResult(RESULT_OK,intent);
        super.onBackPressed();
    }
}
