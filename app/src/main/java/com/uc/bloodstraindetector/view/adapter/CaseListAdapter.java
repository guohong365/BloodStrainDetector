package com.uc.bloodstraindetector.view.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.uc.bloodstraindetector.R;
import com.uc.bloodstraindetector.model.CaseItem;
import com.uc.bloodstraindetector.model.DataManager;
import com.uc.bloodstraindetector.model.ImageItem;
import com.uc.bloodstraindetector.view.adapter.holder.CaseItemHolder;
import com.uc.bloodstraindetector.view.adapter.holder.CaseItemHolderFactory;
import com.uc.model.Selectable;
import com.uc.utils.FileHelper;
import com.uc.widget.adapter.RecyclerViewAdapterBase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CaseListAdapter extends RecyclerViewAdapterBase<CaseItemHolder>{
    private final TypedArray levels;
    private int current;
    public CaseListAdapter(Context context, List<CaseItem> items) {
        super(context, new ArrayList<>(items), new CaseItemHolderFactory(context));
        levels=context.getResources().obtainTypedArray(R.array.case_level_hint_drawable);
        current=-1;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        levels.recycle();
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public CaseItem getCurrentItem(){
        if(current>=0 && current <getItemCount())
            return getItem(current);
        return null;
    }

    @Override
    public CaseItem getItem(int position) {
        return (CaseItem) super.getItem(position);
    }

    @Override
    protected void onAssigned(CaseItemHolder holder, Selectable item) {
        CaseItem caseItem=(CaseItem)item;
        Log.d(TAG, "onAssigned: case=" + caseItem.toString());
        holder.name.setText(String.format(context.getResources().getString(R.string.fmt_case_name), caseItem.getTitle(), caseItem.getImageCount()));
        holder.description.setText(caseItem.getDescription()==null ? "" : caseItem.getDescription());
        holder.level.setImageDrawable(levels.getDrawable(caseItem.getLevel()));
        Glide.with(context)
                .load(caseItem.getPreviewUri())
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .into(holder.preview);
        caseItem.setTag(holder);
    }

    @Override
    protected void onInsertItem(Selectable item) {
        CaseItem caseItem=(CaseItem)item;
        try{
            DataManager.getInstance().insertCase(caseItem);
        } catch (Exception e){
            Toast.makeText(context, R.string.err_insert_case, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDeleteItem(Selectable item) {
        try{
            CaseItem caseItem=DataManager.getInstance().findCaseById(item.getId());
            for(ImageItem imageItem : caseItem.getImages()){
                File file= FileHelper.getUriFile(context, imageItem.getUri());
                file.delete();
            }
            DataManager.getInstance().deleteCase((CaseItem) item);
        } catch (Exception e){
            Toast.makeText(context, R.string.err_delete_case, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void replaceItem(int position, Selectable item) {
        CaseItem caseItem= (CaseItem) item;
        CaseItem exists=getItem(position);
        exists.setTitle(caseItem.getTitle());
        exists.setOperator(caseItem.getOperator());
        exists.setDescription(caseItem.getDescription());
        exists.setLevel(caseItem.getLevel());
        DataManager.getInstance().updateCase(exists);
        notifyItemChanged(position);
    }
}
