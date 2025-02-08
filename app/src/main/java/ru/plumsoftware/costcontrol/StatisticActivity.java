package ru.plumsoftware.costcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.yandex.mobile.ads.common.AdRequest;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.InitializationListener;
import com.yandex.mobile.ads.common.MobileAds;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.costcontrol.adapters.CategoryWithFinanceOperationAdapter;
import ru.plumsoftware.costcontrol.data.Category;
import ru.plumsoftware.costcontrol.data.CategoryWithFinanceOperation;
import ru.plumsoftware.costcontrol.data.DatabaseConstants;
import ru.plumsoftware.costcontrol.data.FinanceOperation;
import ru.plumsoftware.costcontrol.data.SQLiteDatabaseManager;

public class StatisticActivity extends AppCompatActivity {
    public static SQLiteDatabase sqLiteDatabase;
    private Calendar pickedDate = Calendar.getInstance();
    //    public static final Calendar dateAndTime = Calendar.getInstance();
    private int dateMode = 7;
    private int financeMode = 0;

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Global
        Context context = StatisticActivity.this;
        Activity activity = StatisticActivity.this;
        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
        sqLiteDatabase = sqLiteDatabaseManager.getWritableDatabase();

//        UI theme
        SharedPreferences sharedPreferences = context.getSharedPreferences("finance_manager_settings", MODE_PRIVATE);
        boolean ui_theme = sharedPreferences.getBoolean("ui_theme", false);

        if (ui_theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_statistic);

        MobileAds.initialize(this, new InitializationListener() {
            @Override
            public void onInitializationCompleted() {

            }
        });

//        mInterstitialAd = new InterstitialAd(StatisticActivity.this);
//        mInterstitialAd.setAdUnitId("R-M-2118026-1");
//        mInterstitialAd.setInterstitialAdEventListener(new InterstitialAdEventListener() {
//            @Override
//            public void onAdLoaded() {
//                mInterstitialAd.show();
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull AdRequestError adRequestError) {
//                //Toast.makeText(MainActivity.this, adRequestError.getDescription().toString(), Toast.LENGTH_LONG).show();
//                startActivity(new Intent(StatisticActivity.this, MainActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//                Log.i("ADS_TAG", adRequestError.getDescription() + "/" + adRequestError.getCode());
//            }
//
//            @Override
//            public void onAdShown() {
//
//            }
//
//            @Override
//            public void onAdDismissed() {
//                startActivity(new Intent(StatisticActivity.this, MainActivity.class));
//                overridePendingTransition(0, 0);
//                finish();
//            }
//
//            @Override
//            public void onAdClicked() {
//
//            }
//
//            @Override
//            public void onLeftApplication() {
//
//            }
//
//            @Override
//            public void onReturnedToApplication() {
//
//            }
//
//            @Override
//            public void onImpression(@Nullable ImpressionData impressionData) {
//
//            }
//        });

        TextView textViewUseYear = (TextView) findViewById(R.id.textViewUseYear);
        TextView textViewUseMonth = (TextView) findViewById(R.id.textViewUseMonth);
        TextView textViewUseWeek = (TextView) findViewById(R.id.textViewUseWeek);
        TextView textViewChangeFinMode = (TextView) findViewById(R.id.textViewChangeFinMode);

        ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);
//        LineChart lineChart = (LineChart) findViewById(R.id.chart1);

//        RecyclerView recyclerViewCurrency = (RecyclerView) findViewById(R.id.recyclerViewCurrency);
        RecyclerView recyclerViewHistory = (RecyclerView) findViewById(R.id.recyclerViewHistory);

//        Setup data
        setupCategoryWithFinanceOperation(recyclerViewHistory, context, activity, mCubicValueLineChart);
        setSpan(textViewUseWeek);

//        Clickers
        textViewUseWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateMode = 7;
                setupCategoryWithFinanceOperation(recyclerViewHistory, context, activity, mCubicValueLineChart);
                setSpan(textViewUseWeek);
                textViewUseMonth.setText("3 Месяца");
                textViewUseYear.setText("Год");
            }
        });

        textViewUseMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateMode = 63;
                setupCategoryWithFinanceOperation(recyclerViewHistory, context, activity, mCubicValueLineChart);
                setSpan(textViewUseMonth);
                textViewUseWeek.setText("Неделя");
                textViewUseYear.setText("Год");
            }
        });

        textViewUseYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateMode = 365;
                setupCategoryWithFinanceOperation(recyclerViewHistory, context, activity, mCubicValueLineChart);
                setSpan(textViewUseYear);

                textViewUseWeek.setText("Неделя");
                textViewUseMonth.setText("3 Месяца");
            }
        });

        textViewChangeFinMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (financeMode) {
                    case 0:
                        textViewChangeFinMode.setText("Расходы");
                        financeMode = 1;
                        break;
                    case 1:
                        textViewChangeFinMode.setText("Доходы");
                        financeMode = 0;
                        break;
                }

                setupCategoryWithFinanceOperation(recyclerViewHistory, context, activity, mCubicValueLineChart);
            }
        });
    }

    void setupLineChart(ValueLineChart mCubicValueLineChart, List<CategoryWithFinanceOperation> categoryWithFinanceOperationList) {
        mCubicValueLineChart.clearChart();

        ValueLineSeries series = new ValueLineSeries();

        switch (financeMode) {
            case 0:
                series.setColor(Color.parseColor("#80DEEA"));
                break;
            case 1:
                series.setColor(Color.parseColor("#C5E1A5"));
                break;
        }

        for (int j = 0; j < categoryWithFinanceOperationList.size(); j++) {
            CategoryWithFinanceOperation categoryWithFinanceOperation = categoryWithFinanceOperationList.get(j);

            Category category = categoryWithFinanceOperation.getCategory();
            FinanceOperation financeOperation = categoryWithFinanceOperation.getFinanceOperation();

            if (financeOperation.getFinanceMode() == financeMode) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(financeOperation.getTime());
                series.addPoint(new ValueLinePoint(Integer.toString(calendar.get(Calendar.DAY_OF_MONTH)), (float) financeOperation.getValue()));
            }
        }

        mCubicValueLineChart.addSeries(series);
        mCubicValueLineChart.startAnimation();

    }

    @SuppressLint("Recycle")
    List<FinanceOperation> loadAllOperations() {
        List<FinanceOperation> financeOperationList = new ArrayList<>();

        Cursor cursor = null;

        cursor = sqLiteDatabase.query(
                DatabaseConstants.FINANCE_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                //"DATE_FORMAT("+new SimpleDateFormat("")+", '%m%d')"               // The sort order
                DatabaseConstants.FINANCE_TIME + " ASC"
        );

        //pickedDate.set(Calendar.YEAR, pickedDate.get(Calendar.YEAR));
        //pickedDate.set(Calendar.MONTH, Calendar.JANUARY);

        pickedDate = Calendar.getInstance();

//        Date startDate = new Date(pickedDate.getTimeInMillis());
//        Date endDate   =
//
//        long duration  = endDate.getTime() - startDate.getTime();
//
//        long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
//        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
//        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

        switch (dateMode) {
            case 7:
                pickedDate.add(Calendar.DAY_OF_WEEK, -7);
                break;
            case 63:
                pickedDate.add(Calendar.MONTH,  -3);
                break;
            case 365:
                pickedDate.add(Calendar.YEAR, -1);
                break;
        }

        //Toast.makeText(this, new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(pickedDate.getTimeInMillis())), Toast.LENGTH_SHORT).show();

        Calendar now = Calendar.getInstance();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
            int financeValue = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_VALUE));
            long financeTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_TIME));
            int financeCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_CATEGORY_ID));
            String financeCurrency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_CURRENCY));
            int financeMode1 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_MODE));
            long financeAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_ADD_TIME));

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(financeTime);

            if (financeMode1 == financeMode) {
                if (calendar.after(pickedDate) && calendar.before(now))
                    financeOperationList.add(new FinanceOperation(id, financeValue, financeTime, financeCategoryId, financeCurrency, financeMode, financeAddTime));
//                if (dateMode == 7) {
//
//                    if (calendar.after(pickedDate) && calendar.before(now))
//                        financeOperationList.add(new FinanceOperation(id, financeValue, financeTime, financeCategoryId, financeCurrency, financeMode, financeAddTime));
//
//                    if (calendar.get(Calendar.MONTH) == pickedDate.get(Calendar.MONTH)) {
//                        if (calendar.get(Calendar.DAY_OF_MONTH) >= pickedDate.get(Calendar.DAY_OF_MONTH) && calendar.get(Calendar.DAY_OF_MONTH) <= now.get(Calendar.DAY_OF_MONTH)) {
//
//                        }
//                    }
//                }
//                if (dateMode == 63) {
//                    if (calendar.get(Calendar.MONTH) >= pickedDate.get(Calendar.MONTH) && calendar.get(Calendar.MONTH) <= now.get(Calendar.MONTH)) {
//                        financeOperationList.add(new FinanceOperation(id, financeValue, financeTime, financeCategoryId, financeCurrency, financeMode, financeAddTime));
//                    }
//                }
//                if (dateMode == 365) {
//                    if (calendar.get(Calendar.YEAR) >= pickedDate.get(Calendar.YEAR) && calendar.get(Calendar.YEAR) <= now.get(Calendar.YEAR)) {
//                        financeOperationList.add(new FinanceOperation(id, financeValue, financeTime, financeCategoryId, financeCurrency, financeMode, financeAddTime));
//                    }
//                }
            }
        }

        return financeOperationList;
    }

    List<CategoryWithFinanceOperation> loadCategoryWithFinanceOperation() {
        List<FinanceOperation> financeOperationList = loadAllOperations();
        List<CategoryWithFinanceOperation> categoryWithFinanceOperationList = new ArrayList<>();

        for (int i = 0; i < financeOperationList.size(); i++) {
            FinanceOperation financeOperation = financeOperationList.get(i);
            int categoryId = financeOperation.getCategoryId();
            int financeMode = financeOperation.getFinanceMode();
            int value = financeOperation.getValue();

            Category category = null;
            List<Category> categories;

            switch (financeMode) {
                case 0:
                    categories = DatabaseConstants.getBaseExpenseCategories();

                    for (int j = 0; j < categories.size(); j++) {
                        if (categoryId == categories.get(j).getId()) {
                            category = categories.get(j);
                            category.setCategoryColor(Color.parseColor("#80DEEA"));
                            categories.set(j, category);
                        }
                    }

                    break;
                case 1:
                    categories = DatabaseConstants.getBaseEarningCategories();

                    for (int j = 0; j < categories.size(); j++) {
                        if (categoryId == categories.get(j).getId()) {
                            category = categories.get(j);
                            category.setCategoryColor(Color.parseColor("#C5E1A5"));
                            categories.set(j, category);
                        }
                    }
                    break;
            }

            categoryWithFinanceOperationList.add(new CategoryWithFinanceOperation(category, value, DatabaseConstants.CURRENCIES[0], financeOperation));
        }

        return categoryWithFinanceOperationList;
    }

    void setupCategoryWithFinanceOperation(
            RecyclerView recyclerViewHistory,
            Context context,
            Activity activity,
            ValueLineChart mCubicValueLineChart) {
        ProgressDialog dialog = new ProgressDialog();
        dialog.showDialog(context);
        List<CategoryWithFinanceOperation> categoryWithFinanceOperationList = loadCategoryWithFinanceOperation();
        CategoryWithFinanceOperationAdapter categoryWithFinanceOperationAdapter = new CategoryWithFinanceOperationAdapter(context, activity, categoryWithFinanceOperationList, 2);

        recyclerViewHistory.setHasFixedSize(true);
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewHistory.setAdapter(categoryWithFinanceOperationAdapter);

        setupLineChart(mCubicValueLineChart, categoryWithFinanceOperationList);
        dialog.dismissDialog();
    }

//    public static void setupCategories(
//            RecyclerView recyclerViewCategories,
//            Context context,
//            Activity activity,
//            int mode,
//            BottomSheetDialog bottomSheetDialog,
//            List<Category> categoryList) {
//        List<CategoryWithFinanceOperation> categoryWithFinanceOperationList = new ArrayList<>();
//
//        for (int i = 0; i < categoryList.size(); i++) {
//            categoryWithFinanceOperationList.add(new CategoryWithFinanceOperation(categoryList.get(i), 0, ""));
//        }
//
//        CategoryWithFinanceOperationAdapter categoryWithFinanceOperationAdapter = new CategoryWithFinanceOperationAdapter(context, activity, categoryWithFinanceOperationList, mode, bottomSheetDialog);
//        recyclerViewCategories.setHasFixedSize(true);
//        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(context));
//        recyclerViewCategories.setAdapter(categoryWithFinanceOperationAdapter);
//    }
//
//    public static void setDate(TextView textViewSelectedDate, Context context) {
//        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
//            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                dateAndTime.set(Calendar.YEAR, year);
//                dateAndTime.set(Calendar.MONTH, monthOfYear);
//                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                setTime(textViewSelectedDate, context);
//            }
//        },
//                dateAndTime.get(Calendar.YEAR),
//                dateAndTime.get(Calendar.MONTH),
//                dateAndTime.get(Calendar.DAY_OF_MONTH))
//                .show();
//    }
//
//    static void setTime(TextView textViewSelectedDate, Context context) {
//        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
//                dateAndTime.set(Calendar.MINUTE, minute);
//                setupDate_(textViewSelectedDate, dateAndTime);
//            }
//        },
//                dateAndTime.get(Calendar.HOUR_OF_DAY),
//                dateAndTime.get(Calendar.MINUTE), true)
//                .show();
//    }

    void setSpan(TextView textView) {
        String htmlTaggedString = "<u>" + textView.getText().toString() + "</u>";
        Spanned textSpan = android.text.Html.fromHtml(htmlTaggedString);
        textView.setText(textSpan);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//
//
//        final AdRequest adRequest = new AdRequest.Builder().build();
//
//        mInterstitialAd.loadAd(adRequest);
    }
}