package sjcomputers.com.qualitycooling;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sjcomputers.com.qualitycooling.models.FindItemModel;
import sjcomputers.com.qualitycooling.models.ItemModel;

/**
 * Created by RabbitJang on 8/29/2018.
 */

public class FindItemAdapter extends ArrayAdapter<FindItemModel> {
    private ArrayList<FindItemModel> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName, tvPiece, tvQuantitiy, tvWidth, tvHeight, tvLength, tvDepth, tvLoaded, tvCompleted, tvCompletedBy, tvDelivered, tvLocation;
    }

    public FindItemAdapter(ArrayList<FindItemModel> data, Context context) {
        super(context, R.layout.item_itemlist, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FindItemModel dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_find_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.tvItemName);
            viewHolder.tvPiece = (TextView) convertView.findViewById(R.id.tvPiece);
            viewHolder.tvQuantitiy = (TextView) convertView.findViewById(R.id.tvQuantity);
            viewHolder.tvWidth = (TextView) convertView.findViewById(R.id.tvWidth);
            viewHolder.tvHeight = (TextView) convertView.findViewById(R.id.tvHeight);
            viewHolder.tvLength = (TextView) convertView.findViewById(R.id.tvLength);
            viewHolder.tvDepth = (TextView) convertView.findViewById(R.id.tvDepth);
            viewHolder.tvLoaded = (TextView) convertView.findViewById(R.id.tvLoaded);
            viewHolder.tvCompleted = (TextView) convertView.findViewById(R.id.tvCompleted);
            viewHolder.tvCompletedBy = (TextView) convertView.findViewById(R.id.tvCompletedBy);
            viewHolder.tvDelivered = (TextView) convertView.findViewById(R.id.tvDelivered);
            viewHolder.tvLocation = convertView.findViewById(R.id.tvLocation);

            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if (position % 2 == 1) {
            convertView.setBackgroundColor(Color.LTGRAY);
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        viewHolder.txtName.setText(dataModel.ItemName);
        viewHolder.tvPiece.setText(dataModel.Piece);
        viewHolder.tvQuantitiy.setText(dataModel.Quantity);
        viewHolder.tvWidth.setText(dataModel.Width);
        viewHolder.tvHeight.setText(dataModel.Height);
        viewHolder.tvLength.setText(dataModel.Length);
        viewHolder.tvDepth.setText(dataModel.Depth);
        viewHolder.tvLoaded.setText(dataModel.Loaded);
        viewHolder.tvCompleted.setText(dataModel.Completed);
        viewHolder.tvCompletedBy.setText(dataModel.CompletedBy);
        viewHolder.tvDelivered.setText(dataModel.Delivered);
        viewHolder.tvLocation.setText(dataModel.Location);
        return convertView;
    }
}
