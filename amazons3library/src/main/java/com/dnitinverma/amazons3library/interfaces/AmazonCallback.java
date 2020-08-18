package com.dnitinverma.amazons3library.interfaces;



import com.dnitinverma.amazons3library.model.ImageBean;

import org.json.JSONObject;

/**
 * Created by Rajat on 21-02-2017.
 */

public interface AmazonCallback {

    void uploadSuccess( ImageBean bean);
    void uploadFailed(ImageBean bean);
    void uploadProgress( ImageBean bean);
    void uploadError(Exception e, ImageBean imageBean);
}
