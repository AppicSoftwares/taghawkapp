package com.taghawk.gallery;

import android.view.View;

/**
 * Created by Navjot Singh
 * on 29/8/18.
 */

public interface RecyclerOnItemListener<T> {
    void onClick(View view, int position, T object, int requestCode);

    boolean onLongClick(View view, int position, T object, int requestCode);
}
