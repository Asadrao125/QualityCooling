package sjcomputers.com.qualitycooling;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sjcomputers.com.qualitycooling.models.ItemModel;

/**
 * Created by RabbitJang on 8/29/2018.
 */

public class ItemListAdapter extends ArrayAdapter<ItemModel> {
    private ArrayList<ItemModel> dataSet;
    Context mContext;

    private static class ViewHolder {
        TextView txtName, tvPiece, tvQuantitiy, tvWidth, tvHeight, tvLength, tvDepth, tvLoaded, tvCompleted, tvCompletedBy, tvDelivered;
    }

    public ItemListAdapter(ArrayList<ItemModel> data, Context context) {
        super(context, R.layout.item_itemlist, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemModel dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_itemlist, parent, false);
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
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }
        viewHolder.txtName.setText("Item Name: " + dataModel.ItemName);
        viewHolder.tvPiece.setText("Price: " + dataModel.Piece);
        viewHolder.tvQuantitiy.setText("Quantity: " + dataModel.Quantity);
        viewHolder.tvWidth.setText("Width: " + dataModel.Width);
        viewHolder.tvHeight.setText("Height: " + dataModel.Height);
        viewHolder.tvLength.setText("Length: " + dataModel.Length);
        viewHolder.tvDepth.setText("Depth: " + dataModel.Depth);
        viewHolder.tvLoaded.setText("Loaded: " + dataModel.Loaded);
        viewHolder.tvCompleted.setText("Completed: " + dataModel.Completed);
        viewHolder.tvCompletedBy.setText("Completed by: " + dataModel.CompletedBy);
        viewHolder.tvDelivered.setText("Delivered: " + dataModel.Delivered);
        return convertView;
    }
}
