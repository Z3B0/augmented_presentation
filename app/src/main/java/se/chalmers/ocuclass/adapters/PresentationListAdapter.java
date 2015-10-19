package se.chalmers.ocuclass.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import se.chalmers.ocuclass.R;
import se.chalmers.ocuclass.model.Presentation;

/**
 * Created by richard on 01/10/15.
 */
public class PresentationListAdapter extends RecyclerView.Adapter<PresentationListAdapter.ViewHolder> {


    private List<Presentation> items;

    private LayoutInflater inflater;
    private String strSlides;

    public PresentationListAdapter(AdapterView.OnItemClickListener onItemClickListener, String strSlides) {
        this.onItemClickListener = onItemClickListener;
        this.strSlides = strSlides;
    }

    public void setItems(List<Presentation> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_presentation,parent,false),onItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Presentation presentation = items.get(position);

        holder.txtDescription.setText(presentation.getDescription());
        holder.txtTitle.setText(presentation.getName());
        holder.txtInfoDate.setText(presentation.getDate());
        holder.txtInfoSlideCount.setText(presentation.getSlides().size() +" " + strSlides);

        //holder.imgThumbLarge.setText(presentation.getSlides().);
        //holder.txtDescription.setText(presentation.getDescription());
        //holder.txtDescription.setText(presentation.getDescription());


    }

    @Override
    public int getItemCount() {
        return items==null?0:items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txtDescription;
        TextView txtTitle;
        TextView txtInfoSlideCount;
        TextView txtInfoDate;

        ImageView imgThumbLarge;
        ImageView imgThumbOne;
        ImageView imgThumbTwo;



        public ViewHolder(View itemView, final AdapterView.OnItemClickListener onItemClickListener) {
            super(itemView);
            txtDescription = (TextView) itemView.findViewById(R.id.txt_description);
            txtTitle = (TextView) itemView.findViewById(R.id.txt_title);
            txtInfoSlideCount = (TextView) itemView.findViewById(R.id.txt_info_slide_count);
            txtInfoDate = (TextView) itemView.findViewById(R.id.txt_info_date);

            imgThumbLarge = (ImageView) itemView.findViewById(R.id.img_thumb_large);
            imgThumbOne = (ImageView) itemView.findViewById(R.id.img_thumb_one);
            imgThumbTwo = (ImageView) itemView.findViewById(R.id.img_thumb_two);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener!=null){
                        onItemClickListener.onItemClick(null,v,getAdapterPosition(),getItemId());
                    }
                }
            });
        }
    }
}
