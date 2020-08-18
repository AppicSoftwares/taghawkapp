/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.taghawk.camera2basic;

import android.os.Bundle;

import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.taghawk.R;
import com.taghawk.constants.AppConstants;

import java.util.ArrayList;

/**
 * Created by Rishabh Saxena
 * rishabh.saxena@appinventiv.com
 * Appinventiv Technologies Pvt. Ltd.
 * on 25/06/17.
 */
public class CameraTwoActivity extends AppCompatActivity {

    private String photoTitleString;
    private boolean isFirst;
    private int maxImages;
    private ArrayList<String> mFileString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_two_basic);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mFileString = new ArrayList<>();
        getIntentData();

        Camera2BasicFragment camera2BasicFragment = new Camera2BasicFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("ISFIRST", isFirst);
        bundle.putInt("MAX_IMAGES", maxImages);
        if (mFileString != null && mFileString.size() > 0)
            bundle.putStringArrayList("PREVIOUS_LIST", mFileString);
        if (null == savedInstanceState) {
            camera2BasicFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2BasicFragment)
                    .commit();
        }


        /*if (getIntent().getExtras() != null && getIntent().hasExtra(Constants.FLAG_TYPE_CAMERA_ACTION)) {

            if (getIntent().getStringExtra(Constants.FLAG_TYPE_CAMERA_ACTION).equalsIgnoreCase(Constants.BEFORE))
                photoTitleString = getString(R.string.before_wash);
            else if (getIntent().getStringExtra(Constants.FLAG_TYPE_CAMERA_ACTION).equalsIgnoreCase(Constants.AFTER))
                photoTitleString = getString(R.string.after_wash);


        }
*/

    }

    private void getIntentData() {
        isFirst = getIntent().getBooleanExtra("ISFIRST", false);

        if (getIntent().hasExtra(AppConstants.CAMERA_CONSTANTS.IMAGE_LIMIT_ONESHOT)) {
            maxImages = getIntent().getIntExtra("MAX_IMAGES", 1);
        } else {
            maxImages = getIntent().getIntExtra("MAX_IMAGES", 10);
        }
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("PREVIOUS_LIST")) {
            if (getIntent().getExtras().getStringArrayList("PREVIOUS_LIST").size() > 0)
                mFileString.addAll(getIntent().getExtras().getStringArrayList("PREVIOUS_LIST"));
        }
    }

}
