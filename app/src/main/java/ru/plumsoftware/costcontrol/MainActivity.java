package ru.plumsoftware.costcontrol;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yandex.mobile.ads.appopenad.AppOpenAd;
import com.yandex.mobile.ads.appopenad.AppOpenAdEventListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoadListener;
import com.yandex.mobile.ads.appopenad.AppOpenAdLoader;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.common.MobileAds;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.costcontrol.adapters.CategoryWithFinanceOperationAdapter;
import ru.plumsoftware.costcontrol.adapters.CurrencyWithValueAdapter;
import ru.plumsoftware.costcontrol.data.Category;
import ru.plumsoftware.costcontrol.data.CategoryWithFinanceOperation;
import ru.plumsoftware.costcontrol.data.CurrencyWithValue;
import ru.plumsoftware.costcontrol.data.DatabaseConstants;
import ru.plumsoftware.costcontrol.data.FinanceOperation;
import ru.plumsoftware.costcontrol.data.SQLiteDatabaseManager;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase sqLiteDatabase;
    private final Calendar dateAndTime = Calendar.getInstance();
    private final Calendar pickedDate = Calendar.getInstance();
    private PieChart pieChart;
    private BarChart barchart;
    private int financeMode = 0, chartMode = 0;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final ProgressDialog dialogAd = new ProgressDialog();
    private AppOpenAd mAppOpenAd = null;

    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Global
        Context context = MainActivity.this;
        Activity activity = MainActivity.this;
        SQLiteDatabaseManager sqLiteDatabaseManager = new SQLiteDatabaseManager(context);
        ProgressDialog progressDialog = new ProgressDialog();
        sqLiteDatabase = sqLiteDatabaseManager.getWritableDatabase();

        //UI theme
        SharedPreferences sharedPreferences = context.getSharedPreferences("finance_manager_settings", MODE_PRIVATE);
        boolean ui_theme = sharedPreferences.getBoolean("ui_theme", false);

        if (ui_theme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

//        Get instance
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        MobileAds.initialize(this, () -> {
        });

//        mRewardedAd = new RewardedAd(context);
//        mRewardedAd.setAdUnitId("R-M-2118026-2");

        final AppOpenAdLoader appOpenAdLoader = new AppOpenAdLoader(context);
        final String AD_UNIT_ID = BuildConfig.openAdsId;
        final AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(AD_UNIT_ID).build();

        AppOpenAdLoadListener appOpenAdLoadListener = getAppOpenAdLoadListener(progressDialog);

        appOpenAdLoader.setAdLoadListener(appOpenAdLoadListener);
        appOpenAdLoader.loadAd(adRequestConfiguration);

        RecyclerView recyclerViewCurrencyWithValue = (RecyclerView) findViewById(R.id.recyclerViewCurrency);
        RecyclerView recyclerViewCategoriesWithFinanceOperations = (RecyclerView) findViewById(R.id.recyclerViewValueWithCategories);
        TextView textViewMonth = (TextView) findViewById(R.id.textViewMonth);
        TextView textViewYear = (TextView) findViewById(R.id.textViewYear);
        TextView textViewChangeMode = (TextView) findViewById(R.id.textViewChangeMode);
        ImageView imageViewAdd = (ImageView) findViewById(R.id.imageViewAdd);
        ImageView imageViewChangeChart = (ImageView) findViewById(R.id.imageViewChangeChart);
        ImageView imageViewSettings = (ImageView) findViewById(R.id.imageViewSettings);
        ImageView imageViewStatistic = (ImageView) findViewById(R.id.imageViewStatistic);
        CardView cardDate = (CardView) findViewById(R.id.cardDate);
        pieChart = (PieChart) findViewById(R.id.pieChart);
        barchart = (BarChart) findViewById(R.id.barchart);

        ProgressDialog dialog = new ProgressDialog();
        dialog.showDialog(context);
        setupCurrency(recyclerViewCurrencyWithValue, context, activity, financeMode, pickedDate);
        setupDate(textViewMonth, textViewYear, pickedDate);
        setupCategoryWithFinanceOperation(recyclerViewCategoriesWithFinanceOperations, context, activity, financeMode, DatabaseConstants.getBaseExpenseCategories(), chartMode);
        dialog.dismissDialog();

        //Clickers
        imageViewAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);//, R.style.BottomSheetTheme);
                bottomSheetDialog.setContentView(R.layout.add_finance_layout);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setDismissWithAnimation(true);

                TextView textView = (TextView) bottomSheetDialog.findViewById(R.id.textView);
                TextView textView2 = (TextView) bottomSheetDialog.findViewById(R.id.textView2);
                TextView textViewCurrencySign = (TextView) bottomSheetDialog.findViewById(R.id.textViewCurrencySign);
                TextView textViewAddCategory = (TextView) bottomSheetDialog.findViewById(R.id.textViewAddCategory);
                TextView textViewSelectedDate = (TextView) bottomSheetDialog.findViewById(R.id.textViewSelectedDate);
                LinearLayout pickCurrency = (LinearLayout) bottomSheetDialog.findViewById(R.id.pickCurrency);
                LinearLayout pickDate = (LinearLayout) bottomSheetDialog.findViewById(R.id.pickDate);
                RecyclerView recyclerViewCategories = (RecyclerView) bottomSheetDialog.findViewById(R.id.recyclerViewCategories);
                EditText editTextNumber = (EditText) bottomSheetDialog.findViewById(R.id.editTextNumber);
                ImageView imageViewDone = (ImageView) bottomSheetDialog.findViewById(R.id.imageViewDone);

                TextView textViewTitle = (TextView) bottomSheetDialog.findViewById(R.id.textViewTitle);
                TextView textViewValue = (TextView) bottomSheetDialog.findViewById(R.id.textViewExpense);
                CheckBox checkBox = (CheckBox) bottomSheetDialog.findViewById(R.id.checkBox);
                ImageButton imageButton = (ImageButton) bottomSheetDialog.findViewById(R.id.imageButton);
                CardView card = (CardView) bottomSheetDialog.findViewById(R.id.card);

                switch (financeMode) {
                    case 0:
                        assert textView != null;
                        textView.setText("Расходы");
                        break;
                    case 1:
                        assert textView != null;
                        textView.setText("Доходы");
                        break;
                }

                assert card != null;
                card.setVisibility(View.GONE);

                switch (financeMode) {
                    case 0:
                        assert textView2 != null;
                        textView2.setText("Сколько вы потратили");
                        break;
                    case 1:
                        assert textView2 != null;
                        textView2.setText("Сколько вы заработали");
                        break;
                }

                bottomSheetDialog.show();

                final int[] categoryId = {11};

                assert textViewAddCategory != null;
                textViewAddCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);//, R.style.BottomSheetTheme);
                        bottomSheetDialog.setContentView(R.layout.select_category);
                        bottomSheetDialog.setCancelable(true);
                        bottomSheetDialog.setDismissWithAnimation(true);


                        RecyclerView recyclerViewCategories = (RecyclerView) bottomSheetDialog.findViewById(R.id.recyclerViewCategories);

                        bottomSheetDialog.show();

                        switch (financeMode) {
                            case 0:
                                setupCategories(recyclerViewCategories, context, activity, 1, bottomSheetDialog, DatabaseConstants.getBaseExpenseCategories());

                                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        if (financeMode == 0) {
                                            List<Category> baseExpenseCategories = DatabaseConstants.getBaseExpenseCategories();
                                            Category category = baseExpenseCategories.get(CategoryWithFinanceOperationAdapter.clickedId);

                                            categoryId[0] = category.getId();

                                            assert textViewTitle != null;
                                            textViewTitle.setText(category.getCategoryName());
                                            assert imageButton != null;
                                            Glide.with(context).load(category.getCategoryPromoResId()).into(imageButton);
                                            card.setVisibility(View.VISIBLE);
                                            card.setCardBackgroundColor(category.getCategoryColor());
                                        }
                                    }
                                });
                                break;
                            case 1:
                                setupCategories(recyclerViewCategories, context, activity, 1, bottomSheetDialog, DatabaseConstants.getBaseEarningCategories());

                                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialogInterface) {
                                        if (financeMode == 1) {
                                            List<Category> baseExpenseCategories = DatabaseConstants.getBaseEarningCategories();
                                            Category category = baseExpenseCategories.get(CategoryWithFinanceOperationAdapter.clickedId);

                                            categoryId[0] = category.getId();

                                            assert textViewTitle != null;
                                            textViewTitle.setText(category.getCategoryName());
                                            assert imageButton != null;
                                            Glide.with(context).load(category.getCategoryPromoResId()).into(imageButton);
                                            card.setVisibility(View.VISIBLE);
                                            card.setCardBackgroundColor(category.getCategoryColor());
                                        }
                                    }
                                });
                                break;
                        }
                    }
                });

                assert textViewSelectedDate != null;
                textViewSelectedDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(pickedDate.getTimeInMillis())));

                assert pickDate != null;
                pickDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        setDate(textViewSelectedDate);
                    }
                });

                assert imageViewDone != null;
                imageViewDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        assert editTextNumber != null;
                        if (!editTextNumber.getText().toString().isEmpty()) {
                            FinanceOperation financeOperation = new FinanceOperation(
                                    -1,
                                    Integer.parseInt(editTextNumber.getText().toString()),
                                    dateAndTime.getTimeInMillis(),
                                    categoryId[0],
                                    "₽",
                                    financeMode,
                                    System.currentTimeMillis()
                            );

                            dialogAd.showDialog(context);
                            saveOperation(financeOperation);
                            bottomSheetDialog.dismiss();
                        } else {
                            editTextNumber.setError("Введите значение");
                        }
                    }
                });

                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        switch (financeMode) {
                            case 0:
                                setupFinance(
                                        financeMode,
                                        recyclerViewCurrencyWithValue,
                                        recyclerViewCategoriesWithFinanceOperations,
                                        context,
                                        activity,
                                        textViewMonth,
                                        textViewYear,
                                        pickedDate,
                                        DatabaseConstants.getBaseExpenseCategories(),
                                        0,
                                        chartMode);
                                break;
                            case 1:
                                setupFinance(
                                        financeMode,
                                        recyclerViewCurrencyWithValue,
                                        recyclerViewCategoriesWithFinanceOperations,
                                        context,
                                        activity,
                                        textViewMonth,
                                        textViewYear,
                                        pickedDate,
                                        DatabaseConstants.getBaseEarningCategories(),
                                        0,
                                        chartMode);
                                break;
                        }
                    }
                });
            }
        });

        textViewChangeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (financeMode) {
                    case 0:
                        financeMode = 1;
                        setupFinance(
                                financeMode,
                                recyclerViewCurrencyWithValue,
                                recyclerViewCategoriesWithFinanceOperations,
                                context,
                                activity,
                                textViewMonth,
                                textViewYear,
                                pickedDate,
                                DatabaseConstants.getBaseEarningCategories(),
                                0,
                                chartMode);
                        textViewChangeMode.setText("Расходы");
                        break;
                    case 1:
                        financeMode = 0;
                        setupFinance(
                                financeMode,
                                recyclerViewCurrencyWithValue,
                                recyclerViewCategoriesWithFinanceOperations,
                                context,
                                activity,
                                textViewMonth,
                                textViewYear,
                                pickedDate,
                                DatabaseConstants.getBaseExpenseCategories(),
                                0,
                                chartMode);
                        textViewChangeMode.setText("Доходы");
                        break;
                }
            }
        });

        cardDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (financeMode) {
                    case 0:
                        setDate(financeMode,
                                recyclerViewCurrencyWithValue,
                                recyclerViewCategoriesWithFinanceOperations,
                                context,
                                activity,
                                textViewMonth,
                                textViewYear,
                                pickedDate,
                                DatabaseConstants.getBaseExpenseCategories(),
                                pieChart,
                                1);
                        break;
                    case 1:
                        setDate(
                                financeMode,
                                recyclerViewCurrencyWithValue,
                                recyclerViewCategoriesWithFinanceOperations,
                                context,
                                activity,
                                textViewMonth,
                                textViewYear,
                                pickedDate,
                                DatabaseConstants.getBaseEarningCategories(),
                                pieChart,
                                1);
                        break;
                }
            }
        });

        imageViewChangeChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (chartMode) {
                    case 0:
                        Glide.with(context).load(R.drawable.ic_baseline_pie_chart_outline_24).into(imageViewChangeChart);
                        chartMode = 1;
                        break;
                    case 1:
                        Glide.with(context).load(R.drawable.ic_baseline_insert_chart_outlined_24).into(imageViewChangeChart);
                        chartMode = 0;
                        break;
                }

                switch (financeMode) {
                    case 0:
                        setupFinance(
                                financeMode,
                                recyclerViewCurrencyWithValue,
                                recyclerViewCategoriesWithFinanceOperations,
                                context,
                                activity,
                                textViewMonth,
                                textViewYear,
                                pickedDate,
                                DatabaseConstants.getBaseExpenseCategories(),
                                0,
                                chartMode);
                        break;
                    case 1:
                        setupFinance(
                                financeMode,
                                recyclerViewCurrencyWithValue,
                                recyclerViewCategoriesWithFinanceOperations,
                                context,
                                activity,
                                textViewMonth,
                                textViewYear,
                                pickedDate,
                                DatabaseConstants.getBaseEarningCategories(),
                                0,
                                chartMode);
                        break;
                }
            }
        });

        imageViewSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
            }
        });

        imageViewStatistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, StatisticActivity.class));
                overridePendingTransition(0, 0);
                //finish();
            }
        });
    }

    @NonNull
    private AppOpenAdLoadListener getAppOpenAdLoadListener(ProgressDialog progressDialog) {
        progressDialog.showDialog(this);
        AppOpenAdEventListener appOpenAdEventListener = new AppOpenAdEventListener() {
            @Override
            public void onAdShown() {
                // Called when ad is shown.
            }

            @Override
            public void onAdFailedToShow(@NonNull final AdError adError) {
                // Called when ad failed to show.
            }

            @Override
            public void onAdDismissed() {
                // Called when ad is dismissed.
                // Clean resources after dismiss and preload new ad.
            }

            @Override
            public void onAdClicked() {
                // Called when a click is recorded for an ad.
            }

            @Override
            public void onAdImpression(@Nullable final ImpressionData impressionData) {
                // Called when an impression is recorded for an ad.
            }
        };

        return new AppOpenAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final AppOpenAd appOpenAd) {
                mAppOpenAd = appOpenAd;
                mAppOpenAd.setAdEventListener(appOpenAdEventListener);
                progressDialog.dismissDialog();
                showAppOpenAd();
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                progressDialog.dismissDialog();
            }
        };
    }

    private void setupFinance(
            int financeMode,
            RecyclerView recyclerViewCurrencyWithValue,
            RecyclerView recyclerViewCategoriesWithFinanceOperations,
            Context context,
            Activity activity,
            TextView textViewMonth,
            TextView textViewYear,
            Calendar calendar,
            List<Category> categoryList,
            int dateMode,
            int chartMode) {
        ProgressDialog dialog = new ProgressDialog();
        dialog.showDialog(context);
        setupCurrency(recyclerViewCurrencyWithValue, context, activity, financeMode, calendar);
        switch (dateMode) {
            case 0:
                setupDate(textViewMonth, textViewYear, calendar);
                break;
            case 1:

                break;
        }
        setupCategoryWithFinanceOperation(recyclerViewCategoriesWithFinanceOperations, context, activity, financeMode, categoryList, chartMode);
        dialog.dismissDialog();
    }


    private void setupPieChart(PieChart pieChart, List<CategoryWithFinanceOperation> categoryWithFinanceOperations) {
        pieChart.setVisibility(View.VISIBLE);
        barchart.setVisibility(View.GONE);

        pieChart.clearChart();

        for (int i = 0; i < categoryWithFinanceOperations.size(); i++) {
            CategoryWithFinanceOperation categoryWithFinanceOperation = categoryWithFinanceOperations.get(i);
            pieChart.addPieSlice(new PieModel(
                    categoryWithFinanceOperation.getCategory().getCategoryName(),
                    categoryWithFinanceOperation.getValue(),
                    categoryWithFinanceOperation.getCategory().getCategoryColor())
            );
        }

        pieChart.startAnimation();
    }

    private void setupBarChart(BarChart barChart, List<CategoryWithFinanceOperation> categoryWithFinanceOperations) {
        barchart.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);

        barChart.clearChart();

        for (int i = 0; i < categoryWithFinanceOperations.size(); i++) {
            CategoryWithFinanceOperation categoryWithFinanceOperation = categoryWithFinanceOperations.get(i);
            barChart.addBar(new BarModel(
                    categoryWithFinanceOperation.getCategory().getCategoryName(),
                    categoryWithFinanceOperation.getValue(),
                    categoryWithFinanceOperation.getCategory().getCategoryColor())
            );
        }

        barChart.startAnimation();
    }

    @SuppressLint("Recycle")
    void setupCurrency(RecyclerView recyclerViewCurrencyWithValue, Context context, Activity activity, int mode, Calendar calendar) {
        List<CurrencyWithValue> currencyWithValueList = new ArrayList<>();
        Cursor cursor = null;

        int sum = 0;
        String currencySign = "₽";

        cursor = sqLiteDatabase.query(
                DatabaseConstants.FINANCE_TABLE,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                DatabaseConstants.FINANCE_MODE + " = ?",              // The columns for the WHERE clause
                new String[]{Integer.toString(mode)},          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                //"DATE_FORMAT("+new SimpleDateFormat("")+", '%m%d')"               // The sort order
                DatabaseConstants._ID + " DESC"
        );

        while (cursor.moveToNext()) {
            int value = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_VALUE));
            long financeTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_TIME));

            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(financeTime);

            if (calendar1.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && calendar1.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
                sum = sum + value;
        }

        if (sum != 0) {
            CurrencyWithValue currencyWithValue = new CurrencyWithValue(currencySign, sum);
            currencyWithValueList.add(currencyWithValue);
        }

        if (currencyWithValueList.size() == 0)
            currencyWithValueList.add(new CurrencyWithValue("₽", 0));

        CurrencyWithValueAdapter currencyWithValueAdapter = new CurrencyWithValueAdapter(context, activity, currencyWithValueList);

        recyclerViewCurrencyWithValue.setHasFixedSize(true);
        recyclerViewCurrencyWithValue.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCurrencyWithValue.setAdapter(currencyWithValueAdapter);
    }

    void setupCategories(
            RecyclerView recyclerViewCategories,
            Context context,
            Activity activity,
            int mode,
            BottomSheetDialog bottomSheetDialog,
            List<Category> categoryList) {
        List<CategoryWithFinanceOperation> categoryWithFinanceOperationList = new ArrayList<>();

        for (int i = 0; i < categoryList.size(); i++) {
            categoryWithFinanceOperationList.add(new CategoryWithFinanceOperation(categoryList.get(i), 0, ""));
        }

        CategoryWithFinanceOperationAdapter categoryWithFinanceOperationAdapter = new CategoryWithFinanceOperationAdapter(context, activity, categoryWithFinanceOperationList, mode, bottomSheetDialog);
        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewCategories.setAdapter(categoryWithFinanceOperationAdapter);
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
                DatabaseConstants._ID + " DESC"
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants._ID));
            int financeValue = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_VALUE));
            long financeTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_TIME));
            int financeCategoryId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_CATEGORY_ID));
            String financeCurrency = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_CURRENCY));
            int financeMode = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_MODE));
            long financeAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseConstants.FINANCE_ADD_TIME));

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(financeTime);

            if (pickedDate.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && pickedDate.get(Calendar.YEAR) == calendar.get(Calendar.YEAR))
                financeOperationList.add(new FinanceOperation(id, financeValue, financeTime, financeCategoryId, financeCurrency, financeMode, financeAddTime));
        }

        return financeOperationList;
    }

    List<CategoryWithFinanceOperation> loadCategoryWithFinanceOperation(int financeMode, List<Category> categoryList) {
        List<FinanceOperation> financeOperationList = loadAllOperations();
        List<CategoryWithFinanceOperation> categoryWithFinanceOperationList = new ArrayList<>();

        int sum = 0;


        for (int i = 0; i < categoryList.size(); i++) {
            Category category = categoryList.get(i);
            sum = 0;

            for (int j = 0; j < financeOperationList.size(); j++) {
                FinanceOperation financeOperation = financeOperationList.get(j);

                if (financeMode == financeOperation.getFinanceMode() && financeOperation.getCategoryId() == category.getId()) {
                    sum = sum + financeOperation.getValue();
                }
            }

            if (sum != 0)
                categoryWithFinanceOperationList.add(new CategoryWithFinanceOperation(category, sum, DatabaseConstants.CURRENCIES[0]));
        }

        return categoryWithFinanceOperationList;
    }

    void setupCategoryWithFinanceOperation(
            RecyclerView recyclerViewCategories,
            Context context,
            Activity activity,
            int mode,
            List<Category> categoryList,
            int chartMode) {
        List<CategoryWithFinanceOperation> categoryWithFinanceOperations = loadCategoryWithFinanceOperation(mode, categoryList);

        switch (chartMode) {
            case 0:
                setupPieChart(pieChart, categoryWithFinanceOperations);
                break;
            case 1:
                setupBarChart(barchart, categoryWithFinanceOperations);
                break;
        }

        CategoryWithFinanceOperationAdapter categoryWithFinanceOperationAdapter = new CategoryWithFinanceOperationAdapter(context, activity, categoryWithFinanceOperations, 0);
        recyclerViewCategories.setHasFixedSize(true);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewCategories.setAdapter(categoryWithFinanceOperationAdapter);
    }

    @SuppressLint("SetTextI18n")
    void setupDate(TextView textViewMonth, TextView textViewYear, Calendar calendar) {
        String s = new SimpleDateFormat("LLLL", Locale.getDefault()).format(new Date(calendar.getTimeInMillis()));
        textViewMonth.setText(s.substring(0, 1).toUpperCase(Locale.ROOT) + s.substring(1));
        textViewYear.setText(Integer.toString(calendar.get(Calendar.YEAR)));
    }

    @SuppressLint("SetTextI18n")
    void setupDate_(TextView textViewSelectedDate, Calendar calendar) {
        String s = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(calendar.getTimeInMillis()));
        textViewSelectedDate.setText(s);
    }

//    void showCurrenciesPopupMenu(Context context, View view, TextView textViewCurrencySign) {
//        PopupMenu popupMenu = new PopupMenu(context, view);
//        popupMenu.inflate(R.menu.currencies_menu);
//
//        popupMenu
//                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @SuppressLint({"NonConstantResourceId", "SetTextI18n"})
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.rus:
//                                textViewCurrencySign.setText("₽");
//                                return true;
//                            case R.id.br:
//                                textViewCurrencySign.setText("BR₽");
//                                return true;
//                            case R.id.yua:
//                                textViewCurrencySign.setText("¥");
//                                return true;
//                            case R.id.hk:
//                                textViewCurrencySign.setText("HK$");
//                                return true;
//                            case R.id.rup:
//                                textViewCurrencySign.setText("₹");
//                                return true;
//                            case R.id.dol:
//                                textViewCurrencySign.setText("$");
//                                return true;
//                            case R.id.eur:
//                                textViewCurrencySign.setText("€");
//                                return true;
//
////                            case R.id.delete:
////                                NotepadActivity.sqLiteDatabaseNotes.delete(DatabaseConstants._NOTES_TABLE_NAME, DatabaseConstants._ADD_NOTE_TIME + " = ? ", new String[]{Long.toString(addTime)});
////                                NotepadActivity.reloadRecyclerView(context, activity);
////                                return true;
////                            case R.id.pin:
////                                ContentValues contentValues = new ContentValues();
////                                contentValues.put(DatabaseConstants._NOTE_NAME, note.getNoteName());
////                                contentValues.put(DatabaseConstants._NOTE_TEXT, note.getNoteText());
////                                contentValues.put(DatabaseConstants._NOTE_PROMO, note.getNotePromoResId());
////                                contentValues.put(DatabaseConstants._NOTE_COLOR, note.getColor());
////                                contentValues.put(DatabaseConstants._IS_LIKED, 0);
////                                contentValues.put(DatabaseConstants._IS_PINNED, 1);
////                                contentValues.put(DatabaseConstants._ADD_NOTE_TIME, note.getAddNoteTime());
////                                NotepadActivity.sqLiteDatabaseNotes.update(DatabaseConstants._NOTES_TABLE_NAME, contentValues, DatabaseConstants._ADD_NOTE_TIME + " = ?", new String[]{Long.toString(addTime)});
////                                NotepadActivity.reloadRecyclerView(context, activity);
////                                return false;
//                            default:
//                                return false;
//                        }
//                    }
//                });
//
//        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
//            @Override
//            public void onDismiss(PopupMenu menu) {
//
//            }
//        });
//        popupMenu.show();
//    }

    void setDate(TextView textViewSelectedDate) {
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateAndTime.set(Calendar.YEAR, year);
                dateAndTime.set(Calendar.MONTH, monthOfYear);
                dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setTime(textViewSelectedDate);
            }
        },
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    void setDate(int financeMode,
                 RecyclerView recyclerViewCurrencyWithValue,
                 RecyclerView recyclerViewCategoriesWithFinanceOperations,
                 Context context,
                 Activity activity,
                 TextView textViewMonth,
                 TextView textViewYear,
                 Calendar calendar,
                 List<Category> categoryList,
                 PieChart pieChart,
                 int dateMode) {
        new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                switch (financeMode) {
                    case 0:
                        setupFinance(
                                financeMode,
                                recyclerViewCurrencyWithValue,
                                recyclerViewCategoriesWithFinanceOperations,
                                context,
                                activity,
                                textViewMonth,
                                textViewYear,
                                calendar,
                                DatabaseConstants.getBaseExpenseCategories(),
                                1,
                                chartMode);
                        break;
                    case 1:
                        setupFinance(
                                financeMode,
                                recyclerViewCurrencyWithValue,
                                recyclerViewCategoriesWithFinanceOperations,
                                context,
                                activity,
                                textViewMonth,
                                textViewYear,
                                calendar,
                                DatabaseConstants.getBaseEarningCategories(),
                                1,
                                chartMode);
                        break;
                }
            }
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    void setTime(TextView textViewSelectedDate) {
        new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                dateAndTime.set(Calendar.MINUTE, minute);
                setupDate_(textViewSelectedDate, dateAndTime);
            }
        },
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    void saveOperation(FinanceOperation financeOperation) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.FINANCE_VALUE, financeOperation.getValue());
        contentValues.put(DatabaseConstants.FINANCE_TIME, financeOperation.getTime());
        contentValues.put(DatabaseConstants.FINANCE_CATEGORY_ID, financeOperation.getCategoryId());
        contentValues.put(DatabaseConstants.FINANCE_CURRENCY, financeOperation.getCurrency());
        contentValues.put(DatabaseConstants.FINANCE_MODE, financeOperation.getFinanceMode());
        contentValues.put(DatabaseConstants.FINANCE_ADD_TIME, financeOperation.getAddTime());
        sqLiteDatabase.insert(DatabaseConstants.FINANCE_TABLE, null, contentValues);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sqLiteDatabase.close();
        clearAppOpenAd();
    }

    private void showAppOpenAd() {
        if (mAppOpenAd != null) {
            mAppOpenAd.show(MainActivity.this);
        }
    }

    private void clearAppOpenAd() {
        if (mAppOpenAd != null) {
            mAppOpenAd.setAdEventListener(null);
            mAppOpenAd = null;
        }
    }
}