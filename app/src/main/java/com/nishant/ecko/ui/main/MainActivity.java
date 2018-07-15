package com.nishant.ecko.ui.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nishant.ecko.R;
import com.nishant.ecko.util.AppConstants;
import com.nishant.ecko.data.network.ApiClient;
import com.nishant.ecko.data.network.ApiInterface;
import com.nishant.ecko.data.network.model.FlickrSearchResponse;
import com.nishant.ecko.ui.ImageItemDecorator;
import com.nishant.ecko.util.DrawableManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nishant.ecko.ui.main.ImageAdapter.VIEW_FOOTER;
import static com.nishant.ecko.ui.main.ImageAdapter.VIEW_IMAGE;

public class MainActivity extends AppCompatActivity {
    private RecyclerView mImageRv;
    private ArrayList<FlickrSearchResponse.Photos.Photo> mPhotoList;
    private ImageAdapter mImageAdapter;

    private Boolean isFabOpen;
    private FloatingActionButton mAddFab;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;

    private GridLayoutManager mGridLayoutManager;
    private ApiInterface apiInterface;

    private long currentPage = 1;
    private long totalPages;
    private String tag;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar toolbar;
    private FrameLayout mFabFrame;

    private LinearLayout mProfileFabContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prepareImageRv();

        prepareDrawer();


        mAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });
        mImageRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    if (currentPage < totalPages) {
                        fetchImages();
                    } else {
                        mImageAdapter.setMorePagesAvailable(false);
                    }
                }
            }
        });
    }

    private void prepareDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void prepareImageRv() {
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mImageAdapter.getItemViewType(position)) {
                    case VIEW_IMAGE:
                        return 1;
                    case VIEW_FOOTER:
                        if (mGridLayoutManager.getSpanCount() == 2) {
                            return 2;
                        } else if (mGridLayoutManager.getSpanCount() == 3) {
                            return 3;
                        } else if (mGridLayoutManager.getSpanCount() == 4) {
                            return 4;
                        }
                    default:
                        return -1;
                }
            }
        });
        mImageRv.setAdapter(mImageAdapter);
        mImageRv.setLayoutManager(mGridLayoutManager);
        mImageRv.addItemDecoration(new ImageItemDecorator(10));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initialize() {
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        mProfileFabContainer = findViewById(R.id.profile_fab_container);
        mFabFrame = findViewById(R.id.fab_frame);
        toolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        apiInterface = ApiClient.getApiInterface();
        mImageRv = findViewById(R.id.main_activity_image_rv);
        mPhotoList = new ArrayList<>();
        mImageAdapter = new ImageAdapter(mPhotoList, this);
        mGridLayoutManager = new GridLayoutManager(this, 3);
        mAddFab = findViewById(R.id.add_fab);
        isFabOpen = false;


    }

    public void animateFAB() {

        if (isFabOpen) {
            mFabFrame.setBackgroundColor(Color.TRANSPARENT);
            mAddFab.startAnimation(rotate_backward);
            mProfileFabContainer.startAnimation(fab_close);
            mProfileFabContainer.setVisibility(View.INVISIBLE);
            isFabOpen = false;

        } else {
            mFabFrame.setBackgroundColor(Color.parseColor("#e6ffffff"));
            mAddFab.startAnimation(rotate_forward);
            mProfileFabContainer.startAnimation(fab_open);
            mProfileFabContainer.setVisibility(View.VISIBLE);
            isFabOpen = true;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);

        final MenuItem item = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().length() == 0) {
                    Toast.makeText(MainActivity.this, R.string.warn_enter_text, Toast.LENGTH_SHORT).show();
                    return false;
                }
                item.collapseActionView();
                mPhotoList.clear();
                currentPage = 1;
                totalPages = 0;
                mImageAdapter.setDrawableManager(new DrawableManager());
                tag = query;
                fetchImages();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void fetchImages() {
        Call<FlickrSearchResponse> getData = apiInterface.searchImages(AppConstants.METHOD
                , getString(R.string.flickr_api_key)
                , tag
                , currentPage + 1
                , AppConstants.FORMAT
                , AppConstants.NO_JSON_CALLBACK
                , AppConstants.PER_PAGE
        );
        getData.enqueue(new Callback<FlickrSearchResponse>() {
            @Override
            public void onResponse(Call<FlickrSearchResponse> call, Response<FlickrSearchResponse> response) {
                if (response.isSuccessful()) {
                    currentPage = response.body().getPhotos().getPage();

                    if (totalPages == 0) {
                        totalPages = response.body().getPhotos().getTotal();
                    }
                    mPhotoList.addAll(response.body().getPhotos().getPhoto());
                    mImageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<FlickrSearchResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.size_two) {
            mGridLayoutManager.setSpanCount(2);
            return true;
        } else if (item.getItemId() == R.id.size_four) {
            mGridLayoutManager.setSpanCount(4);
            return true;
        } else if (item.getItemId() == R.id.size_three) {
            mGridLayoutManager.setSpanCount(3);
            return true;
        }
        return mDrawerToggle.onOptionsItemSelected(item);
    }
}
