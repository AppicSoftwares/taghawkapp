package com.taghawk.ui.chat;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.taghawk.R;
import com.taghawk.base.BaseActivity;
import com.taghawk.base.BaseFragment;
import com.taghawk.constants.AppConstants;
import com.taghawk.model.chat.ChatModel;

import java.util.Calendar;
import java.util.TimeZone;

public class MessagesDetailActivity extends BaseActivity {

    @Override
    protected int getResourceId() {
        return R.layout.activity_messages_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadMessageDetailFragment();
    }

    /**
     * load the required fragment in the container
     */
    private void loadMessageDetailFragment() {
        BaseFragment fragment = null;
        ChatModel chatModel = getIntent().getParcelableExtra(AppConstants.FIREBASE.FIREBASE_CHAT_DATA);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConstants.FIREBASE.FIREBASE_CHAT_DATA, chatModel);
        bundle.putString("tag",getIntent().getStringExtra("tag"));
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(AppConstants.FIREBASE.FIREBASE_OTHER_USER_ID))
            bundle.putString(AppConstants.FIREBASE.FIREBASE_OTHER_USER_ID, getIntent().getExtras().getString(AppConstants.FIREBASE.FIREBASE_OTHER_USER_ID));
        bundle.putLong(AppConstants.FIREBASE.TIMESTAMP, getIntent().getLongExtra(AppConstants.FIREBASE.TIMESTAMP, Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()));
        if (chatModel.getChatType().equalsIgnoreCase(AppConstants.FIREBASE.FIREBASE_SINGLE_CHAT))
            fragment = new MessagesDetailSingleChatFragment();
        else
            fragment = new MessagesDetailGroupChatFragment();
        fragment.setArguments(bundle);
        addFragment(R.id.rl_base_container, fragment, MessagesDetailSingleChatFragment.class.getSimpleName());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case AppConstants.ACTIVITY_RESULT.CHAT_SHELF_DETAILS:
//                break;
//        }
//    }
}
