package com.nowfloats.NavigationDrawer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nowfloats.NavigationDrawer.API.WildFireApis;
import com.nowfloats.NavigationDrawer.Adapter.GoogleWildFireAdapter;
import com.nowfloats.NavigationDrawer.model.WildFireKeyStatsModel;
import com.nowfloats.Store.NewPricingPlansActivity;
import com.nowfloats.util.Constants;
import com.nowfloats.util.Methods;
import com.thinksity.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Admin on 29-11-2017.
 */

public class GoogleWildFireActivity extends AppCompatActivity implements WildFireDialogFragment.OnMenuDialogOptionSelection, View.OnClickListener {

    private final int PROCESS_ITEMS = 10;
    private ProgressDialog progressDialog;
    private ArrayList<WildFireKeyStatsModel> wildFireList = new ArrayList<>();
    private GoogleWildFireAdapter adapter;
    private Menu menu;
    private boolean stop;
    private FrameLayout fragLayout;
    private String pattern = "yyyy-MM-dd", calendarPattern = "d MMMM yyyy";
    private SimpleDateFormat formatter = new SimpleDateFormat(pattern, Locale.ENGLISH);
    private SimpleDateFormat calendarFormatter = new SimpleDateFormat(calendarPattern, Locale.ENGLISH);
    private HashMap<String, Object> map = new HashMap<>();
    private WildFireDialogFragment.SortType currentSortType = WildFireDialogFragment.SortType.ALPHABETIC;
    private boolean isMenuVisible;
    private String dateSelectedPeriod;
    private int filterByMonth = WildFireFilterFragment.ALL_SELECTED;
    private TextView title;

    @Override
    public void onSortOptionSelect(WildFireDialogFragment.SortType type) {
        currentSortType = type;
        sort(wildFireList, type);
        onBackPressed();
    }

    @Override
    public void onFilterOptionSelect() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
        }
        Fragment frag = manager.findFragmentByTag("calendarFrag");
        if (frag == null) {
            frag = new WildFireCalenderFragment();
        }
        manager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .replace(R.id.layout_fragment, frag, "calendarFrag")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDateSelected(String start_end) {
        String[] dates = start_end.split("_");
        String newDatePeriod = String.format(Locale.ENGLISH, "%s_%s",
                calendarFormatter.format(Long.valueOf(dates[0])), calendarFormatter.format(Long.valueOf(dates[1])));
        if (TextUtils.isEmpty(dateSelectedPeriod) || !dateSelectedPeriod.equals(newDatePeriod)) {
            filterByMonth = WildFireFilterFragment.DATE_SELECT;
            map.put("endDate", formatter.format(Long.valueOf(dates[1])));
            map.put("startDate", formatter.format(Long.valueOf(dates[0])));
            dateSelectedPeriod = newDatePeriod;
            wildFireList.clear();
            adapter.notifyDataSetChanged();
            getKeyWordStats(map);
        }
        onBackPressed();
    }

    @Override
    public void onMonthOptionSelect(int days) {
        if (filterByMonth != days) {
            filterByMonth = days;
            dateSelectedPeriod = null;
            // month selected
            Calendar calendar = formatter.getCalendar();
            map.put("endDate", formatter.format(new Date()));
            calendar.add(Calendar.DATE, -days);
            map.put("startDate", formatter.format(calendar.getTime()));
            wildFireList.clear();
            adapter.notifyDataSetChanged();
            getKeyWordStats(map);
        }
        onBackPressed();
    }

    @Override
    public void onAllSelected() {
        if (filterByMonth != WildFireFilterFragment.ALL_SELECTED) {
            filterByMonth = WildFireFilterFragment.ALL_SELECTED;
            map.remove("startDate");
            dateSelectedPeriod = null;
            map.remove("endDate");
            wildFireList.clear();
            adapter.notifyDataSetChanged();
            getKeyWordStats(map);
        }
        onBackPressed();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wildfire);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        title = toolbar.findViewById(R.id.text1);
        Typeface tf = Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf");
        title.setPaintFlags(title.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        title.setTypeface(tf);
        ((ImageView) toolbar.findViewById(R.id.image1)).setImageResource(R.drawable.ic_google_glass_logo);
        title.setText("Keywords");
        String wildFireId = getIntent().getStringExtra("WILDFIRE_ID");
        map.put("count", PROCESS_ITEMS);
        map.put("clientId", Constants.clientId);
        map.put("accountId", wildFireId);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.please_wait));
        fragLayout = findViewById(R.id.layout_fragment);
        fragLayout.setOnClickListener(this);
        adapter = new GoogleWildFireAdapter(this);
        RecyclerView keywordRv = findViewById(R.id.keyword_rv);
        keywordRv.setHasFixedSize(true);
        keywordRv.setVisibility(View.VISIBLE);
        keywordRv.setLayoutManager(new LinearLayoutManager(this));
        adapter.setGoogleModelList(wildFireList);
        keywordRv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        keywordRv.setAdapter(adapter);
        keywordRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int count = manager.getItemCount();
                if (manager.findLastVisibleItemPosition() >= count - 2 && !stop) {
                    getKeyWordStats(map);
                }
            }
        });

        getKeyWordStats(map);
    }

    private void getKeyWordStats(HashMap map) {
        map.put("start", wildFireList.size());
        stop = true;
        showProgress();
        //int start = wildFireList.size() == 0 ? 0 : wildFireList.size()-1;
        WildFireApis apis = WildFireApis.adapter.create(WildFireApis.class);
        apis.getGoogleStats(map, new Callback<ArrayList<WildFireKeyStatsModel>>() {
            @Override
            public void success(ArrayList<WildFireKeyStatsModel> wildFireKeyStatsModels, Response response) {
                hideProgress();
                if (wildFireKeyStatsModels == null) {
                    Methods.showSnackBarNegative(GoogleWildFireActivity.this, getString(R.string.something_went_wrong));
                    title.setText(wildFireList.size() == 0 ? "Keywords" : "Keywords (" + wildFireList.size() + ")");
                    return;
                }
                if (!isMenuVisible) {
                    showMenu();
                }
                wildFireList.addAll(wildFireKeyStatsModels);
                sort(wildFireList, currentSortType);
                if (wildFireKeyStatsModels.size() == PROCESS_ITEMS) {
                    stop = false;
                }
                title.setText(wildFireList.size() == 0 ? "Keywords" : "Keywords (" + wildFireList.size() + ")");
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                title.setText(wildFireList.size() == 0 ? "Keywords" : "Keywords (" + wildFireList.size() + ")");
            }
        });
    }

    private void showMenu() {
        isMenuVisible = true;
        getMenuInflater().inflate(R.menu.wildfire_menu, menu);
        MenuItem myActionMenuItem = menu.findItem(R.id.item_search);
        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void showProgress() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgress() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llayout_wildfire:
                startActivity(new Intent(GoogleWildFireActivity.this, NewPricingPlansActivity.class));
                break;
            case R.id.layout_fragment:
                onBackPressed();
                break;
            case R.id.llayout_know_more:
                // email
                break;
        }
    }

    private void sort(ArrayList<WildFireKeyStatsModel> keywordModelList, final WildFireDialogFragment.SortType sortType) {
        Collections.sort(keywordModelList, new Comparator<WildFireKeyStatsModel>() {
            @Override
            public int compare(WildFireKeyStatsModel o1, WildFireKeyStatsModel o2) {
                switch (sortType) {
                    case ALPHABETIC:
                        return o1.getKeyword().replace("+", "").compareToIgnoreCase(o2.getKeyword().replace("+", ""));
                    case CLICKS:
                    default:
                        int o1_click = Integer.valueOf(o1.getClicks());
                        int o2_click = Integer.valueOf(o2.getClicks());
                        if (o1_click < o2_click) {
                            return 1;
                        } else if (o1_click == o2_click) {
                            return 0;
                        } else return -1;

                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentManager manager = getSupportFragmentManager();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_sort:

                if (manager.getBackStackEntryCount() > 0) {
                    manager.popBackStack();
                    fragLayout.setVisibility(View.GONE);
                } else {
                    fragLayout.setVisibility(View.VISIBLE);
                    Bundle b = new Bundle();
                    b.putString("sortType", currentSortType.toString());
                    manager.beginTransaction()
                            .replace(R.id.layout_fragment, WildFireDialogFragment.getInstance(b))
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.item_filter:
                if (manager.getBackStackEntryCount() > 0) {
                    manager.popBackStack();
                    fragLayout.setVisibility(View.GONE);
                } else {
                    fragLayout.setVisibility(View.VISIBLE);
                    Bundle b = new Bundle();
                    b.putInt("monthSelected", filterByMonth);
                    b.putString("datePeriod", dateSelectedPeriod);
                    manager.beginTransaction()
                            .replace(R.id.layout_fragment, WildFireFilterFragment.getInstance(b))
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.item_search:
                if (manager.getBackStackEntryCount() > 0) {
                    manager.popBackStack();
                    fragLayout.setVisibility(View.GONE);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            manager.popBackStack();
            fragLayout.setVisibility(View.GONE);

        } else {
            super.onBackPressed();
        }
    }

}
