package com.tecnologiajo.diagnostictestsuniajc;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.tecnologiajo.diagnostictestsuniajc.modelos.RequestResult;

import java.util.List;

/**
 * Created by ADMIN on 13/04/2016.
 */
public class ResultAdapter extends ArrayAdapter<RequestResult> {
    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    private Context contextAdapter;
    private DrawableProvider mProvider;

    public ResultAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        contextAdapter = context;
        mDrawableBuilder = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .round();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            convertView = View.inflate(contextAdapter, R.layout.list_item_layout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Get the data item for this position
        final RequestResult result = getItem(position);

        //final Drawable drawable = result.getDrawable();
       // viewHolder.imageView.setImageDrawable(drawable);
        viewHolder.textView.setText(result.getDescripcion());

        viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(null,
                null,
                contextAdapter.getResources().getDrawable(R.drawable.ic_action_next_item),
                null);

        // Return the completed view to render on screen
        return convertView;
    }

    private static class ViewHolder {

        private ImageView imageView;

        private TextView textView;

        private ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
            textView = (TextView) view.findViewById(R.id.textView);
        }
    }
}
