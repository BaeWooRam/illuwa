package com.geek.soft.illuwa.category;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geek.soft.illuwa.R;

import java.util.List;

public class CategoryAdapter  extends BaseAdapter {
    private List<CategoryBin> listData;
    private LayoutInflater layoutInflater;
    private Context context;

    public CategoryAdapter(Context aContext, List<CategoryBin> listData) {
        this.context = aContext;
        this.listData = listData;
        layoutInflater = LayoutInflater.from(aContext);

    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.category_item, null);
            holder = new ViewHolder();
            holder.iconView = (ImageView) convertView.findViewById(R.id.imageicon);
            holder.ListTitle = (TextView) convertView.findViewById(R.id.itemtitle);
            holder.checked= (ImageView) convertView.findViewById(R.id.list_selected_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CategoryBin bin = this.listData.get(position);

        if(bin.isSelected()==true){
            holder.checked.setVisibility(View.VISIBLE);
        }else{
            holder.checked.setVisibility(View.INVISIBLE);
        }

        holder.ListTitle.setText(bin.getTitle());

        int imageId = bin.getDrawableid();

        holder.iconView.setImageResource(imageId);

        return convertView;
    }

    // Find Image ID corresponding to the name of the image (in the directory mipmap).
    public int getMipmapResIdByName(String resName)  {
        String pkgName = context.getPackageName();
        // Return 0 if not found.
        int resID = context.getResources().getIdentifier(resName , "mipmap", pkgName);
        return resID;
    }

    static class ViewHolder {
        ImageView iconView;
        TextView ListTitle;
        ImageView checked;
    }

}