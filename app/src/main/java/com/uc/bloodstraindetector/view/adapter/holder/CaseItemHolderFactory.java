package com.uc.bloodstraindetector.view.adapter.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.uc.bloodstraindetector.R;
import com.uc.bloodstraindetector.view.adapter.holder.CaseItemHolder;
import com.uc.widget.adapter.ViewHolderFactoryBase;

public class CaseItemHolderFactory extends ViewHolderFactoryBase<CaseItemHolder>{
    public CaseItemHolderFactory(Context context) {
        super(context);
    }

    @Override
    public CaseItemHolder create(ViewGroup parent, int viewType) {
        return new CaseItemHolder(LayoutInflater.from(context).inflate(R.layout.recycler_veiw_item_case, parent, false));
    }
}
