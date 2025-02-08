package ru.plumsoftware.costcontrol.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.plumsoftware.costcontrol.R;
import ru.plumsoftware.costcontrol.StatisticActivity;
import ru.plumsoftware.costcontrol.data.Category;
import ru.plumsoftware.costcontrol.data.CategoryWithFinanceOperation;
import ru.plumsoftware.costcontrol.data.DatabaseConstants;
import ru.plumsoftware.costcontrol.data.FinanceOperation;

public class CategoryWithFinanceOperationAdapter extends RecyclerView.Adapter<CategoryWithFinanceOperationViewHolder> {
    private Context context;
    private Activity activity;
    private List<CategoryWithFinanceOperation> categoryWithFinanceOperationList;
    private int mode;
    private BottomSheetDialog bottomSheetDialog;

    public CategoryWithFinanceOperationAdapter(Context context, Activity activity, List<CategoryWithFinanceOperation> categoryWithFinanceOperationList, int mode) {
        this.context = context;
        this.activity = activity;
        this.categoryWithFinanceOperationList = categoryWithFinanceOperationList;
        this.mode = mode;
    }

    public CategoryWithFinanceOperationAdapter(Context context, Activity activity, List<CategoryWithFinanceOperation> categoryWithFinanceOperationList, int mode, BottomSheetDialog bottomSheetDialog) {
        this.context = context;
        this.activity = activity;
        this.categoryWithFinanceOperationList = categoryWithFinanceOperationList;
        this.mode = mode;
        this.bottomSheetDialog = bottomSheetDialog;
    }

    public static int clickedId = 0;


    @NonNull
    @Override
    public CategoryWithFinanceOperationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CategoryWithFinanceOperationViewHolder(LayoutInflater.from(context).inflate(R.layout.finance_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CategoryWithFinanceOperationViewHolder holder, int position) {
        CategoryWithFinanceOperation categoryWithFinanceOperation = categoryWithFinanceOperationList.get(position);

        holder.textViewTitle.setText(categoryWithFinanceOperation.getCategory().getCategoryName());
        if (categoryWithFinanceOperation.getValue() != 0)
            holder.textViewExpense.setText(Integer.toString(categoryWithFinanceOperation.getValue()) + categoryWithFinanceOperation.getCurrency());
        else
            holder.textViewExpense.setVisibility(View.GONE);
        Glide.with(context).load(categoryWithFinanceOperation.getCategory().getCategoryPromoResId()).into(holder.imageButton);
        holder.card.setCardBackgroundColor(categoryWithFinanceOperation.getCategory().getCategoryColor());

        switch (mode) {
            case 0:
                holder.checkBox.setVisibility(View.GONE);
                holder.textViewTime.setVisibility(View.GONE);
                break;
            case 1:
                holder.checkBox.setVisibility(View.VISIBLE);
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        clickedId = categoryWithFinanceOperation.getCategory().getId() - 1;
                        bottomSheetDialog.dismiss();
                    }
                });
                holder.textViewTime.setVisibility(View.GONE);
                break;
            case 2:
                holder.checkBox.setVisibility(View.GONE);
                holder.textViewTime.setVisibility(View.VISIBLE);
                holder.textViewTime.setText(new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(new Date(categoryWithFinanceOperation.getFinanceOperation().getTime())));

                holder.card.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                        showPopupMenu(view, categoryWithFinanceOperation);
                        return false;
                    }
                });
                break;
        }
    }

    private void showPopupMenu(View v, CategoryWithFinanceOperation categoryWithFinanceOperation) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.item_menu);

        popupMenu
                .setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.remove:
                                StatisticActivity.sqLiteDatabase.delete(DatabaseConstants.FINANCE_TABLE, DatabaseConstants._ID + " = ? ", new String[]{Integer.toString(categoryWithFinanceOperation.getFinanceOperation().getId())});
                                activity.startActivity(new Intent(context, StatisticActivity.class));
                                activity.overridePendingTransition(0, 0);
                                return true;
//                            case R.id.edit:
//                                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);//, R.style.BottomSheetTheme);
//                                bottomSheetDialog.setContentView(R.layout.add_finance_layout);
//                                bottomSheetDialog.setCancelable(true);
//                                bottomSheetDialog.setDismissWithAnimation(true);
//
//                                TextView textView = (TextView) bottomSheetDialog.findViewById(R.id.textView);
//                                TextView textViewCurrencySign = (TextView) bottomSheetDialog.findViewById(R.id.textViewCurrencySign);
//                                TextView textViewAddCategory = (TextView) bottomSheetDialog.findViewById(R.id.textViewAddCategory);
//                                TextView textViewSelectedDate = (TextView) bottomSheetDialog.findViewById(R.id.textViewSelectedDate);
//                                LinearLayout pickCurrency = (LinearLayout) bottomSheetDialog.findViewById(R.id.pickCurrency);
//                                LinearLayout pickDate = (LinearLayout) bottomSheetDialog.findViewById(R.id.pickDate);
//                                RecyclerView recyclerViewCategories = (RecyclerView) bottomSheetDialog.findViewById(R.id.recyclerViewCategories);
//                                EditText editTextNumber = (EditText) bottomSheetDialog.findViewById(R.id.editTextNumber);
//                                ImageView imageViewDone = (ImageView) bottomSheetDialog.findViewById(R.id.imageViewDone);
//
//                                TextView textViewTitle = (TextView) bottomSheetDialog.findViewById(R.id.textViewTitle);
//                                TextView textViewValue = (TextView) bottomSheetDialog.findViewById(R.id.textViewExpense);
//                                CheckBox checkBox = (CheckBox) bottomSheetDialog.findViewById(R.id.checkBox);
//                                ImageButton imageButton = (ImageButton) bottomSheetDialog.findViewById(R.id.imageButton);
//                                CardView card = (CardView) bottomSheetDialog.findViewById(R.id.card);
//
//                                switch (StatisticActivity.financeMode) {
//                                    case 0:
//                                        assert textView != null;
//                                        textView.setText("Расходы");
//                                        break;
//                                    case 1:
//                                        assert textView != null;
//                                        textView.setText("Доходы");
//                                        break;
//                                }
//
//                                assert card != null;
//                                card.setVisibility(View.GONE);
//
//                                bottomSheetDialog.show();
//
//                                final int[] categoryId = {11};
//
//                                assert textViewAddCategory != null;
//                                textViewAddCategory.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);//, R.style.BottomSheetTheme);
//                                        bottomSheetDialog.setContentView(R.layout.select_category);
//                                        bottomSheetDialog.setCancelable(true);
//                                        bottomSheetDialog.setDismissWithAnimation(true);
//
//
//                                        RecyclerView recyclerViewCategories = (RecyclerView) bottomSheetDialog.findViewById(R.id.recyclerViewCategories);
//
//                                        bottomSheetDialog.show();
//
//                                        switch (StatisticActivity.financeMode) {
//                                            case 0:
//                                                StatisticActivity.setupCategories(recyclerViewCategories, context, activity, 1, bottomSheetDialog, DatabaseConstants.getBaseExpenseCategories());
//
//                                                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                                    @Override
//                                                    public void onDismiss(DialogInterface dialogInterface) {
//                                                        if (StatisticActivity.financeMode == 0) {
//                                                            List<Category> baseExpenseCategories = DatabaseConstants.getBaseExpenseCategories();
//                                                            Category category = baseExpenseCategories.get(CategoryWithFinanceOperationAdapter.clickedId);
//
//                                                            categoryId[0] = category.getId();
//
//                                                            assert textViewTitle != null;
//                                                            textViewTitle.setText(category.getCategoryName());
//                                                            assert imageButton != null;
//                                                            Glide.with(context).load(category.getCategoryPromoResId()).into(imageButton);
//                                                            card.setVisibility(View.VISIBLE);
//                                                            card.setCardBackgroundColor(category.getCategoryColor());
//                                                        }
//                                                    }
//                                                });
//                                                break;
//                                            case 1:
//                                                StatisticActivity.setupCategories(recyclerViewCategories, context, activity, 1, bottomSheetDialog, DatabaseConstants.getBaseEarningCategories());
//
//                                                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                                    @Override
//                                                    public void onDismiss(DialogInterface dialogInterface) {
//                                                        if (StatisticActivity.financeMode == 1) {
//                                                            List<Category> baseExpenseCategories = DatabaseConstants.getBaseEarningCategories();
//                                                            Category category = baseExpenseCategories.get(CategoryWithFinanceOperationAdapter.clickedId);
//
//                                                            categoryId[0] = category.getId();
//
//                                                            assert textViewTitle != null;
//                                                            textViewTitle.setText(category.getCategoryName());
//                                                            assert imageButton != null;
//                                                            Glide.with(context).load(category.getCategoryPromoResId()).into(imageButton);
//                                                            card.setVisibility(View.VISIBLE);
//                                                            card.setCardBackgroundColor(category.getCategoryColor());
//                                                        }
//                                                    }
//                                                });
//                                                break;
//                                        }
//                                    }
//                                });
//
//                                assert textViewSelectedDate != null;
//                                textViewSelectedDate.setText(new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(StatisticActivity.pickedDate.getTimeInMillis())));
//
//                                assert pickDate != null;
//                                pickDate.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        StatisticActivity.setDate(textViewSelectedDate, context);
//                                    }
//                                });
//
//                                assert imageViewDone != null;
//                                imageViewDone.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        assert editTextNumber != null;
//                                        if (!editTextNumber.getText().toString().isEmpty()) {
//                                            FinanceOperation financeOperation = new FinanceOperation(
//                                                    -1,
//                                                    Integer.parseInt(editTextNumber.getText().toString()),
//                                                    StatisticActivity.dateAndTime.getTimeInMillis(),
//                                                    categoryId[0],
//                                                    "₽",
//                                                    StatisticActivity.financeMode,
//                                                    System.currentTimeMillis()
//                                            );
//
//                                            saveOperation(financeOperation);
//                                            bottomSheetDialog.dismiss();
//                                        } else {
//                                            editTextNumber.setError("Введите значение");
//                                        }
//                                    }
//                                });
//
//                                bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                    @Override
//                                    public void onDismiss(DialogInterface dialogInterface) {
//
//                                    }
//                                });
//
//                                ContentValues contentValues = new ContentValues();
//                                contentValues.put(DatabaseConstants._NOTE_NAME, note.getNoteName());
//                                contentValues.put(DatabaseConstants._NOTE_TEXT, note.getNoteText());
//                                contentValues.put(DatabaseConstants._NOTE_PROMO, note.getNotePromoResId());
//                                contentValues.put(DatabaseConstants._NOTE_COLOR, note.getColor());
//                                contentValues.put(DatabaseConstants._IS_LIKED, 0);
//                                contentValues.put(DatabaseConstants._IS_PINNED, 1);
//                                contentValues.put(DatabaseConstants._ADD_NOTE_TIME, note.getAddNoteTime());
//                                NotepadActivity.sqLiteDatabaseNotes.update(DatabaseConstants._NOTES_TABLE_NAME, contentValues, DatabaseConstants._ADD_NOTE_TIME + " = ?", new String[]{Long.toString(addTime)});
//                                NotepadActivity.reloadRecyclerView(context, activity);
//                                return false;
                            default:
                                return false;
                        }
                    }
                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {

            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return categoryWithFinanceOperationList.size();
    }
}

class CategoryWithFinanceOperationViewHolder extends RecyclerView.ViewHolder {
    protected TextView textViewTitle, textViewExpense, textViewTime;
    protected CheckBox checkBox;
    protected ImageButton imageButton;
    protected CardView card;

    public CategoryWithFinanceOperationViewHolder(@NonNull View itemView) {
        super(itemView);

        setIsRecyclable(false);

        textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
        textViewExpense = (TextView) itemView.findViewById(R.id.textViewExpense);
        textViewTime = (TextView) itemView.findViewById(R.id.textViewTime);

        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);

        imageButton = (ImageButton) itemView.findViewById(R.id.imageButton);

        card = (CardView) itemView.findViewById(R.id.card);
    }
}
