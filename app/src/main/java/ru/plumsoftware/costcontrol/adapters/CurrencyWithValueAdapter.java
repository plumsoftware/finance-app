package ru.plumsoftware.costcontrol.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.plumsoftware.costcontrol.R;
import ru.plumsoftware.costcontrol.data.CurrencyWithValue;

public class CurrencyWithValueAdapter extends RecyclerView.Adapter<CurrencyWithValueViewHolder>{
    private Context context;
    private Activity activity;
    private List<CurrencyWithValue> currencyWithValueList;

    public CurrencyWithValueAdapter(Context context, Activity activity, List<CurrencyWithValue> currencyWithValueList) {
        this.context = context;
        this.activity = activity;
        this.currencyWithValueList = currencyWithValueList;
    }

    @NonNull
    @Override
    public CurrencyWithValueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CurrencyWithValueViewHolder(LayoutInflater.from(context).inflate(R.layout.currency_with_value_layout, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CurrencyWithValueViewHolder holder, int position) {
        CurrencyWithValue currencyWithValue = currencyWithValueList.get(position);

        holder.textViewSign.setText(currencyWithValue.getCurrencySign());
        holder.textViewValue.setText(Long.toString(currencyWithValue.getSum()));
    }

    @Override
    public int getItemCount() {
        return currencyWithValueList.size();
    }
}

class CurrencyWithValueViewHolder extends RecyclerView.ViewHolder{
    protected TextView textViewSign, textViewValue;

    public CurrencyWithValueViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewSign = (TextView) itemView.findViewById(R.id.textViewSign);
        textViewValue = (TextView) itemView.findViewById(R.id.textViewValue);
    }
}
