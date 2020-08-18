package com.taghawk.model.block_user;

import com.google.gson.annotations.SerializedName;
import com.taghawk.model.commonresponse.CommonResponse;

public class BlockUserModel extends CommonResponse {

    @SerializedName("data")
    private BlockUserData blockUserData;

    public BlockUserData getBlockUserData() {
        return blockUserData;
    }

    public void setBlockUserData(BlockUserData blockUserData) {
        this.blockUserData = blockUserData;
    }
}
