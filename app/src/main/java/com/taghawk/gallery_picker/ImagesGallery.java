package com.taghawk.gallery_picker;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.taghawk.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ImagesGallery extends AppCompatActivity {
    public String title;
    public int maxSelection;
    private RecyclerView recyclerView;
    private final String[] projection = new String[]{ MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA };
    private final String[] projection2 = new String[]{MediaStore.Images.Media.DISPLAY_NAME,MediaStore.Images.Media.DATA };
    public ArrayList<String> imagesSelected= new ArrayList<>();
    public List<Boolean> selected=new ArrayList<>();
    private List<String> mediaList = new ArrayList<>();
    private MediaAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_images_gallery);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_black);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnResult();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnResult();
            }
        });
        title=getIntent().getExtras().getString("title");
        maxSelection=getIntent().getExtras().getInt("maxSelection");
        List<String> selectedList = (List<String>) getIntent().getSerializableExtra("selectedList");
        imagesSelected.addAll(selectedList);
        if (maxSelection==0) maxSelection = Integer.MAX_VALUE;
        if (imagesSelected.size() != 0) {
            setTitle(String.valueOf(imagesSelected.size()));
        } else {
            setTitle(title);
        }
        populateRecyclerView();
        getPicBuckets();
    }

    @Override
    public void onBackPressed() {
        returnResult();
    }

    /*
     * Get all folders of images
     * */
    public void getPicBuckets(){
        Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                        null, null, MediaStore.Images.Media.DATE_ADDED);
        ArrayList<String> bucketNamesTEMP = new ArrayList<>(cursor.getCount());
        HashSet<String> albumSet = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String album = cursor.getString(cursor.getColumnIndex(projection[0]));
                if (!albumSet.contains(album)) {
                    bucketNamesTEMP.add(album);
                    albumSet.add(album);
                    getPictures(album);
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
    }

    /*
     * Get all images of particular folder
     * */
    public void getPictures(String bucket){
        Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection2,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME+" =?",new String[]{bucket},MediaStore.Images.Media.DATE_ADDED);
        HashSet<String> albumSet = new HashSet<>();
        File file;
        if (cursor.moveToLast()) {
            do {
                if (Thread.interrupted()) {
                    return;
                }
                String path = cursor.getString(cursor.getColumnIndex(projection2[1]));
                file = new File(path);
                if (file.exists() && !albumSet.contains(path)) {
                    albumSet.add(path);
                    if(imagesSelected.size()>0) {
                        if(imagesSelected.contains(path))
                            selected.add(true);
                        else
                            selected.add(false);
                    }
                    else
                        selected.add(false);
                    mediaList.add(path);
                    mAdapter.notifyDataSetChanged();
                }
            } while (cursor.moveToPrevious());
        }
        cursor.close();
    }


    /*
     * Show all images on recyclerview
     * */
    private void populateRecyclerView() {
        mAdapter = new MediaAdapter(mediaList, selected, getApplicationContext());
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.getItemAnimator().setChangeDuration(0);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(selected.get(position).equals(false) && imagesSelected.size()<maxSelection) {
                    imagesSelected.add(mediaList.get(position));
                    selected.set(position, !selected.get(position));
                    mAdapter.notifyItemChanged(position);
                } else if (selected.get(position).equals(true)) {
                    if (imagesSelected.indexOf(mediaList.get(position)) != -1) {
                        imagesSelected.remove(imagesSelected.indexOf(mediaList.get(position)));
                        selected.set(position, !selected.get(position));
                        mAdapter.notifyItemChanged(position);
                    }
                }
                else
                {
                    Toast.makeText(ImagesGallery.this,"you can select only " +maxSelection,Toast.LENGTH_SHORT).show();
                }
                if (imagesSelected.size() != 0) {
                    setTitle(String.valueOf(imagesSelected.size()));
                } else {
                    setTitle(title);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }

        }));
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    /*
     * Return selected images
     * */
    private void returnResult() {
        Intent returnIntent = new Intent();
        returnIntent.putStringArrayListExtra("result",imagesSelected);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
