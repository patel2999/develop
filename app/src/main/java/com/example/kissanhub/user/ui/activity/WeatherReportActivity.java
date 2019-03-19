package com.example.kissanhub.user.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.kissanhub.MyApplication;
import com.example.kissanhub.R;
import com.example.kissanhub.base.view.BaseActivity;
import com.example.kissanhub.user.di.component.ApplicationComponent;
import com.example.kissanhub.user.di.component.DaggerMainActivityComponent;
import com.example.kissanhub.user.di.component.MainActivityComponent;
import com.example.kissanhub.user.di.module.MainActivityContextModule;
import com.example.kissanhub.user.di.qualifier.ActivityContext;
import com.example.kissanhub.user.di.qualifier.ApplicationContext;
import com.example.kissanhub.user.presenter.WeatherPresenter;
import com.example.kissanhub.user.storage.local.WeatherDatabase;
import com.example.kissanhub.user.storage.local.dao.WeatherDao;
import com.example.kissanhub.user.storage.models.WeatherDataEntity;
import com.example.kissanhub.user.storage.enums.LocationType;
import com.example.kissanhub.user.storage.enums.MetricsType;
import com.example.kissanhub.user.storage.remote.WeatherApiInterface;
import com.example.kissanhub.user.ui.view.WeatherView;
import com.example.kissanhub.user.utils.barchartutils.DayAxisValueFormatter;
import com.example.kissanhub.user.utils.barchartutils.MyValueFormatter;
import com.example.kissanhub.user.utils.barchartutils.ValueFormatter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.model.GradientColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class WeatherReportActivity extends BaseActivity<WeatherPresenter> implements WeatherView,
        SwipeRefreshLayout.OnRefreshListener {

    /**
     * Menu and usb menu to selection metrics and location to filter the weather data
     */
    public static final int GROUP_METRICS_ID = 201;
    public static final int ITEM_MAX_TEMP = 101;
    public static final int ITEM_MIN_TEMP = 102;
    public static final int ITEM_RAINFALL = 103;
    public static final int GROUP_LOCATION_ID = 202;
    public static final int ITEM_UK = 104;
    public static final int ITEM_ENGLAND = 105;
    public static final int ITEM_SCOTLAND = 106;
    public static final int ITEM_WALES = 107;
    /**
     * Initialize default metrics and location
     */
    private MetricsType mMetricsType = MetricsType.MAX_TEMPERATURE;
    private LocationType mLocationType = LocationType.UK;
    /**
     * Holds Class name to show logger.
     */
    public static final String TAG = WeatherReportActivity.class.getSimpleName();
    /**
     * Holds the Activity component instance
     */
    public MainActivityComponent mMainActivityComponent;

    /**
     * Holds weather data service interface instance.
     */
    @Inject
    public WeatherApiInterface mWeatherApiInterface;

    /**
     * Holds application context instance.
     */
    @Inject
    @ApplicationContext
    public Context mApplicationContext;

    /**
     * Holds activity context instance.
     */
    @Inject
    @ActivityContext
    public Context mActivityContext;

    /**
     * Holds Swipe refresh layout instance.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Hold weather database access object instance
     */
    public WeatherDao mWeatherDao;

    /**
     * Holds Bar chart instance
     */
    private BarChart mBarChart;

    /**
     * Holds spinner view instance
     */
    private Spinner mSpinnerYear;

    /**
     * Holds layout instance to show snack bar
     */
    private CoordinatorLayout mCoordinatorLayout;

    /**
     * Holds text view instance to show selected metrics value.
     */
    private TextView mMetricsTextView;

    /**
     * Holds text view instance to show selected location value.
     */
    private TextView mLocationTextView;

    /**
     * Holds text view instance to show year value.
     */
    private TextView mYearTextView;

    @Override
    protected WeatherPresenter createPresenter() {
        registerComponent();
        return new WeatherPresenter(mActivityContext, mWeatherDao, mWeatherApiInterface);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeToolbar();
        initializeResources();
        getPresenter().setMetricsAndLocation(mMetricsType, mLocationType);
        getPresenter().notifyUiReady();
    }

    /**
     * Registering Application component
     */
    private void registerComponent() {
        ApplicationComponent applicationComponent =
                MyApplication.get(this).getApplicationComponent();
        mMainActivityComponent = DaggerMainActivityComponent.builder()
                .mainActivityContextModule(new MainActivityContextModule(this))
                .applicationComponent(applicationComponent)
                .build();
        mMainActivityComponent.injectMainActivity(this);
        WeatherDatabase weatherDatabase = WeatherDatabase.getAppDatabase(mActivityContext);
        mWeatherDao = weatherDatabase.weatherDao();
    }

    /**
     * Initialize toolbar
     */
    private void initializeToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(false);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(R.string.app_name);
    }

    /**
     * Initialize resources.
     */
    private void initializeResources() {
        mCoordinatorLayout = findViewById(R.id.coordinateLayout);
        mSpinnerYear = findViewById(R.id.toolbar_spinner);
        mMetricsTextView = findViewById(R.id.txt_metrics);
        mLocationTextView = findViewById(R.id.txt_location);
        mYearTextView = findViewById(R.id.txt_year);
        mMetricsTextView.setText(getString(R.string.metrics_value, mMetricsType.getMetricsType()));
        mLocationTextView.setText(getString(R.string.location_value,
                mLocationType.getLocationName()));
        mYearTextView.setText(getString(R.string.year_value, getString(R.string.empty_value)));
        // SwipeRefreshLayout
        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        mBarChart = findViewById(R.id.bar_chart);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setDrawValueAboveBar(false);
        mBarChart.getDescription().setEnabled(true);
        mBarChart.getDescription().setText("Weather Report");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mBarChart.setMaxVisibleValueCount(60);
        // scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);
        mBarChart.setDrawGridBackground(false);
        drawGraphAttributes();
        // add a nice and smooth animation
        mBarChart.animateY(1500);
        mBarChart.getLegend().setEnabled(false);
    }

    @Override
    public void onRefresh() {
        getPresenter().setMetricsAndLocation(mMetricsType, mLocationType);
        Log.d(TAG, "Selected Metrics: " + mMetricsType.getMetricsType());
        Log.d(TAG, "Location: " + mLocationType.getLocationName());
        getPresenter().notifyUiReady();
        Log.d(TAG, "notifying to refresh UI");
    }

    @Override
    public void showLoadingView() {
        if (!mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    @Override
    public void hideLoadingView() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void setYearDataOnView(List<WeatherDataEntity> weatherDataResponse) {
        // create an empty set
        Set<String> yearList = new HashSet<>();
        for (WeatherDataEntity dataEntity : weatherDataResponse) {
            yearList.add(dataEntity.getYear());
        }
        List<String> mainList = new ArrayList<>(yearList);
        Collections.sort(mainList);
        mainList.add(0, getString(R.string.SelectYear));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item_row, mainList);
        adapter.setDropDownViewResource(R.layout.drop_down_view);
        // Apply the adapter to the spinner
        if (mSpinnerYear != null) {
            mSpinnerYear.setAdapter(adapter);
            mSpinnerYear.setOnItemSelectedListener(mOnItemSelectedListener);
            mSpinnerYear.setSelection(0);
        }
        mMetricsTextView.setText(getString(R.string.metrics_value, mMetricsType.getMetricsType()));
        mLocationTextView.setText(getString(R.string.location_value,
                mLocationType.getLocationName()));
    }

    /**
     * Creating listener to select year from spinner view
     */
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position,
                                           long l) {
                    String selectedYear = adapterView.getItemAtPosition(position).toString();
                    if (position == 0) {
                        selectedYear = getString(R.string.empty_value);
                        mYearTextView.setText(getString(R.string.year_value, selectedYear));
                        mBarChart.clear();
                        mBarChart.invalidate();
                        return;
                    }
                    getPresenter().getYearlyWeatherReport(selectedYear);
                    mYearTextView.setText(getString(R.string.year_value, selectedYear));

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            };

    @Override
    public void showSnackBarWhenNoInternet() {
        Snackbar snackbar = Snackbar
                .make(mCoordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRefresh();
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);

        // Changing snack bar background color
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(mActivityContext, android.R.color.black));
        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    public void updateWeatherReportOnGraph(List<WeatherDataEntity> weatherDataResponse) {
        setDataBarGraph(weatherDataResponse);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        addMenuAndSubMenu(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        item.setChecked(!item.isChecked());
        switch (item.getItemId()) {
            case ITEM_MAX_TEMP:
                mMetricsType = MetricsType.MAX_TEMPERATURE;
                break;
            case ITEM_MIN_TEMP:
                mMetricsType = MetricsType.MIN_TEMPERATURE;
                break;
            case ITEM_RAINFALL:
                mMetricsType = MetricsType.RAINFALL;
                break;
            case ITEM_UK:
                mLocationType = LocationType.UK;
                break;
            case ITEM_ENGLAND:
                mLocationType = LocationType.ENGLAND;
                break;
            case ITEM_SCOTLAND:
                mLocationType = LocationType.SCOTLAND;
                break;
            case ITEM_WALES:
                mLocationType = LocationType.WALES;
                break;
            default:
                return super.onContextItemSelected(item);
        }
        if (item.isChecked()) {
            onRefresh();
        }
        return true;
    }

    /**
     * Added menu and sub menu on action bar.
     *
     * @param menu menu item
     */
    private void addMenuAndSubMenu(Menu menu) {
        SubMenu subMenu1 = menu.addSubMenu(getString(R.string.menu_metrics_filter));
        subMenu1.add(GROUP_METRICS_ID, ITEM_MAX_TEMP, Menu.NONE,
                getString(R.string.MaxTemperature)).setCheckable(true);
        subMenu1.add(GROUP_METRICS_ID, ITEM_MIN_TEMP, Menu.NONE,
                getString(R.string.MinTemperature)).setCheckable(true);
        subMenu1.add(GROUP_METRICS_ID, ITEM_RAINFALL, Menu.NONE,
                getString(R.string.Rainfall)).setCheckable(true);
        subMenu1.setGroupCheckable(GROUP_METRICS_ID, true, true);
        subMenu1.getItem(0).setChecked(true);

        SubMenu subMenu2 = menu.addSubMenu(getString(R.string.menu_location_filter));
        subMenu2.add(GROUP_LOCATION_ID, ITEM_UK, Menu.NONE, getString(R.string.UK))
                .setCheckable(true);
        subMenu2.add(GROUP_LOCATION_ID, ITEM_ENGLAND, Menu.NONE, getString(R.string.England))
                .setCheckable(true);
        subMenu2.add(GROUP_LOCATION_ID, ITEM_SCOTLAND, Menu.NONE, getString(R.string.Scotland))
                .setCheckable(true);
        subMenu2.add(GROUP_LOCATION_ID, ITEM_WALES, Menu.NONE, getString(R.string.Wales))
                .setCheckable(true);
        subMenu2.setGroupCheckable(GROUP_LOCATION_ID, true, true);
        subMenu2.getItem(0).setChecked(true);
    }

    /**
     * Apply formatter on Bar Graph to draw graph attributes.
     */
    private void drawGraphAttributes() {
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(mBarChart);
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new MyValueFormatter("");
        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
    }

    /**
     * Set Weather report data to see in bar chart.
     *
     * @param weatherDataResponse weatherDataResponse
     */
    private void setDataBarGraph(List<WeatherDataEntity> weatherDataResponse) {
        int start = 0;
        int dataSize = weatherDataResponse.size();
        ArrayList<BarEntry> weatherRangeList = new ArrayList<>();
        String year = weatherDataResponse.get(0).getYear();

        for (int i = start; i < start + dataSize; i++) {
            float tempValue = Float.valueOf(weatherDataResponse.get(i).getTemperature());
            weatherRangeList.add(new BarEntry(i, tempValue));
        }

        BarDataSet weatherBarGraphDataSet;
        if (mBarChart.getData() != null &&
                mBarChart.getData().getDataSetCount() > 0) {
            weatherBarGraphDataSet = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            weatherBarGraphDataSet.setValues(weatherRangeList);
            mBarChart.invalidate();
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        } else {
            weatherBarGraphDataSet = new BarDataSet(weatherRangeList, "The year " + year);
            weatherBarGraphDataSet.setDrawIcons(false);

            int startColor1 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            int startColor2 = ContextCompat.getColor(this, android.R.color.holo_blue_light);
            int startColor3 = ContextCompat.getColor(this, android.R.color.holo_orange_light);
            int startColor4 = ContextCompat.getColor(this, android.R.color.holo_green_light);
            int startColor5 = ContextCompat.getColor(this, android.R.color.holo_red_light);
            int endColor1 = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor2 = ContextCompat.getColor(this, android.R.color.holo_purple);
            int endColor3 = ContextCompat.getColor(this, android.R.color.holo_green_dark);
            int endColor4 = ContextCompat.getColor(this, android.R.color.holo_red_dark);
            int endColor5 = ContextCompat.getColor(this, android.R.color.holo_orange_dark);

            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, endColor1));
            gradientColors.add(new GradientColor(startColor2, endColor2));
            gradientColors.add(new GradientColor(startColor3, endColor3));
            gradientColors.add(new GradientColor(startColor4, endColor4));
            gradientColors.add(new GradientColor(startColor5, endColor5));
            weatherBarGraphDataSet.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(weatherBarGraphDataSet);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            mBarChart.setData(data);

            for (IDataSet set : mBarChart.getData().getDataSets()) {
                set.setDrawValues(true);
            }
            mBarChart.invalidate();
            mBarChart.getData().notifyDataChanged();
            mBarChart.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        WeatherDatabase.destroyInstance();
    }
}


