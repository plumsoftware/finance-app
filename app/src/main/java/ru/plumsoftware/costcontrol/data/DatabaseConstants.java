package ru.plumsoftware.costcontrol.data;

import android.app.Activity;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import ru.plumsoftware.costcontrol.R;

public class DatabaseConstants {
    public static final String DATABASE_NAME = "expenses.db";

    //    GENERAL
    public static final String _ID = "_id";
    public static final String _COLOR_INT = "_color_int";
    public static final String[] CURRENCIES = new String[]{"₽", "Br₽", "¥", "HK$", "₹", "$", "€"};
    public static final String[] CURRENCIES_ = new String[]{"Рубль (₽)", "Белорусский рубль (Br₽)", "Юань (¥)", "Гонконский доллар (HK$)", "Индийская рупия (₹)", "Доллар США ($)", "Евро (€)"};

    public static final String FINANCE_TABLE = "_finance_table";
    public static final String FINANCE_VALUE = "_finance_value";
    public static final String FINANCE_TIME = "_finance_time";
    public static final String FINANCE_CATEGORY_ID = "_finance_category_id";
    public static final String FINANCE_CURRENCY = "_finance_currency";
    public static final String FINANCE_MODE = "_finance_mode";
    public static final String FINANCE_ADD_TIME = "_finance_add_time";

    public static final String CREATE_FINANCE_TABLE = "CREATE TABLE IF NOT EXISTS " +
            FINANCE_TABLE + " (" + _ID + " INTEGER PRIMARY KEY," +
            FINANCE_VALUE + " INTEGER," +
            FINANCE_TIME + " LONG," +
            FINANCE_CATEGORY_ID + " INTEGER," +
            FINANCE_CURRENCY + " TEXT," +
            FINANCE_MODE + " INTEGER," +
            FINANCE_ADD_TIME + " LONG)";

    public static final String DROP_FINANCE_TABLE = "DROP TABLE IF EXISTS " + FINANCE_TABLE;

    public static final String CATEGORY_TABLE = "_categories_table";
    public static final String CATEGORY_NAME = "_category_name";
    public static final String CATEGORY_COLOR = "_category_color";
    public static final String CATEGORY_PROMO = "_category_promo";
    public static final String CATEGORY_ADD_TIME = "_category_add_time";

    public static final String CREATE_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS " +
            CATEGORY_TABLE + " (" + _ID + " INTEGER PRIMARY KEY," +
            CATEGORY_NAME + " TEXT," +
            CATEGORY_COLOR + " INTEGER," +
            CATEGORY_PROMO + " INTEGER," +
            CATEGORY_ADD_TIME + " LONG)";

    public static final String DROP_CATEGORY_TABLE = "DROP TABLE IF EXISTS " + CATEGORY_TABLE;

    public static List<ColorItem> getBaseColors(Activity activity) {
        List<ColorItem> colors = new ArrayList<>();

        colors.add(new ColorItem(activity.getResources().getColor(R.color.red_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.green_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.orange_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.pink_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.purple_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.blue_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.yellow_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.brown_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.dark_green_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.dark_brown_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.sky_blue_200)));
        colors.add(new ColorItem(activity.getResources().getColor(R.color.light_green_200)));

        return colors;
    }

    public static List<Category> getBaseExpenseCategories() {
        final List<Category> expenseCategories = new ArrayList<>();
        expenseCategories.add(new Category(
                1,
                "Транспорт",
                R.drawable.directions_car_black_24dp,
                Color.parseColor("#dce1ff"),
                0));

        expenseCategories.add(new Category(
                2,
                "Бары и рестораны",
                R.drawable.local_bar_black_24dp,
                Color.parseColor("#FFDAD5"),
                0));

        expenseCategories.add(new Category(
                3,
                "Алкоголь",
                R.drawable.liquor_black_24dp,
                Color.parseColor("#FFDEA0"),
                0));

        expenseCategories.add(new Category(
                4,
                "Еда вне дома",
                R.drawable.ic_baseline_fastfood_24,
                Color.parseColor("#89FA9B"),
                0));

        expenseCategories.add(new Category(
                5,
                "Продукты",
                R.drawable.cake_black_24dp,
                Color.parseColor("#FFF59D"),
                0));

        expenseCategories.add(new Category(
                6,
                "Здоровье",
                R.drawable.ic_baseline_health_and_safety_24,
                Color.parseColor("#80DEEA"),
                0));

        expenseCategories.add(new Category(
                7,
                "Развлечения",
                R.drawable.sports_esports_fill0_wght400_grad0_opsz48,
                Color.parseColor("#C5E1A5"),
                0));

        expenseCategories.add(new Category(
                8,
                "Подарки",
                R.drawable.card_giftcard_black_24dp,
                Color.parseColor("#ffdbcc"),
                0));

        expenseCategories.add(new Category(
                9,
                "Одежда",
                R.drawable.checkroom_black_24dp,
                Color.parseColor("#e9ddff"),
                0));

        expenseCategories.add(new Category(
                10,
                "Домашние животные",
                R.drawable.pets_black_24dp,
                Color.parseColor("#80CBC4"),
                0));

        expenseCategories.add(new Category(
                11,
                "Прочее",
                R.drawable.shopping_basket_black_24dp,
                Color.parseColor("#FFAB91"),
                0));

        expenseCategories.add(new Category(
                12,
                "Регулярное",
                R.drawable.shopping_basket_black_24dp,
                Color.parseColor("#ffd7f1"),
                0));

        return expenseCategories;
    }

    public static List<Category> getBaseEarningCategories() {
        final List<Category> expenseCategories = new ArrayList<>();

        expenseCategories.add(new Category(
                1,
                "Заработная плата",
                R.drawable.currency_ruble_black_24dp,
                Color.parseColor("#FFAB91"),
                0));

        expenseCategories.add(new Category(
                2,
                "Пенсия или стипендия",
                R.drawable.currency_ruble_black_24dp,
                Color.parseColor("#80DEEA"),
                0));

        expenseCategories.add(new Category(
                3,
                "Выплаты или льготы из общественных организаций",
                R.drawable.payments_black_24dp,
                Color.parseColor("#e9ddff"),
                0));

        expenseCategories.add(new Category(
                4,
                "Приусадебное хозяйство",
                R.drawable.yard_black_24dp,
                Color.parseColor("#89FA9B"),
                0));

        expenseCategories.add(new Category(
                5,
                "Другие источники",
                R.drawable.currency_ruble_black_24dp,
                Color.parseColor("#FFDEA0"),
                0));

        expenseCategories.add(new Category(
                6,
                "Ценные бумаг",
                R.drawable.receipt_long_black_24dp,
                Color.parseColor("#FFDAD5"),
                0));

        expenseCategories.add(new Category(
                7,
                "Сдача недвижимости и других средств в аренду",
                R.drawable.real_estate_agent_black_24dp,
                Color.parseColor("#ffdbcc"),
                0));

        expenseCategories.add(new Category(
                8,
                "Предпринимательская деятельность",
                R.drawable.business_center_black_24dp,
                Color.parseColor("#80CBC4"),
                0));

        return expenseCategories;
    }
}
