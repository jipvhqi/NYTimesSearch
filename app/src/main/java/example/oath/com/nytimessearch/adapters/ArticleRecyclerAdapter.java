package example.oath.com.nytimessearch.adapters;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.Target;

import java.util.List;

import example.oath.com.nytimessearch.R;
import example.oath.com.nytimessearch.models.Article;
import example.oath.com.nytimessearch.modules.GlideApp;

public class ArticleRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // member variables
    private Context mContext;
    private List<Article> mArticles;
    private final int NOTHUMBNAIL = 0, THUMBNAIL = 1;

    public class ViewHolderWithThumbnail extends RecyclerView.ViewHolder {
        // the holder should contain a member variable
        // for any view that will be set as you render a row
        public ImageView thumbnailImageView;
        public TextView titleTextView;
        public TextView newsDeskTextView;
        public TextView snippetTextView;

        // create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolderWithThumbnail(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            thumbnailImageView = itemView.findViewById(R.id.ivImage);
            titleTextView = itemView.findViewById(R.id.tvTitle);
            newsDeskTextView = itemView.findViewById(R.id.tvNewsDesk);
            snippetTextView = itemView.findViewById(R.id.tvSnippet);
        }
    }

    public class ViewHolderNoThumbnail extends RecyclerView.ViewHolder {
        // the holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView titleTextView;
        public TextView newsDeskTextView;
        public TextView snippetTextView;

        // create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolderNoThumbnail(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            titleTextView = itemView.findViewById(R.id.tvTitle);
            newsDeskTextView = itemView.findViewById(R.id.tvNewsDesk);
            snippetTextView = itemView.findViewById(R.id.tvSnippet);
        }
    }

    public ArticleRecyclerAdapter(Context context, List<Article> articles) {
        mContext = context;
        mArticles = articles;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case NOTHUMBNAIL:
                View v1 = inflater.inflate(R.layout.item_article_result_no_thumbnail, parent, false);
                viewHolder = new ViewHolderNoThumbnail(v1);
                break;
            case THUMBNAIL:
            default:
                View v2 = inflater.inflate(R.layout.item_article_result_w_thumbnail, parent, false);
                viewHolder = new ViewHolderWithThumbnail(v2);
                break;
        }

        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Article article = mArticles.get(position);

        switch (viewHolder.getItemViewType()) {
            case NOTHUMBNAIL:
                ViewHolderNoThumbnail vh1 = (ViewHolderNoThumbnail) viewHolder;
                configureViewHolder(vh1, position);
                break;
            case THUMBNAIL:
            default:
                ViewHolderWithThumbnail vh2 = (ViewHolderWithThumbnail) viewHolder;
                configureViewHolder(vh2, position);
                break;
        }

        viewHolder.itemView.setOnClickListener( v -> {
                    Intent intentShare = new Intent(Intent.ACTION_SEND);
                    intentShare.setType("text/plain");
                    intentShare.putExtra(Intent.EXTRA_TEXT, article.getWebURL());

                    Intent intentEmail = new Intent(Intent.ACTION_SENDTO);
                    intentEmail.setType("text/plain");
                    intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Share a New York Times Article");
                    intentEmail.putExtra(Intent.EXTRA_TEXT, article.getWebURL());

                    Bitmap icon = BitmapFactory.decodeResource(v.getResources(), R.drawable.ic_action_share);
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    // set toolbar color and/or setting custom actions before invoking build()
                    PendingIntent pendingIntentShare = PendingIntent.getActivity(v.getContext(), 0, intentShare, PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent pendingIntentEmail = PendingIntent.getActivity(v.getContext(), 0, intentEmail, PendingIntent.FLAG_ONE_SHOT);
                    builder.setActionButton(icon, "Share Link", pendingIntentShare, true);
                    builder.addMenuItem(v.getResources().getString(R.string.email), pendingIntentEmail);
                    // Once ready, call CustomTabsIntent.Builder.build() to create a CustomTabsIntent
                    CustomTabsIntent customTabsIntent = builder.build();
                    // and launch the desired Url with CustomTabsIntent.launchUrl()
                    customTabsIntent.launchUrl(v.getContext(), Uri.parse(article.getWebURL()));
                }
        );
    }

    private void configureViewHolder(ViewHolderNoThumbnail vh, int position) {
        // Set item views based on your views and data model
        TextView tvTitle = vh.titleTextView;
        TextView tvNewsDesk = vh.newsDeskTextView;
        TextView tvSnippet = vh.snippetTextView;
        tvTitle.setText(mArticles.get(position).getHeadline());
        if (mArticles.get(position).getNewsDesk() != null) {
            String newsDesk = mArticles.get(position).getNewsDesk().toUpperCase();
            if (!newsDesk.equals("NONE") && !newsDesk.isEmpty()) {
                tvNewsDesk.setText(newsDesk);
            }
            else {
                tvNewsDesk.setVisibility(View.GONE);
            }
        }
        tvSnippet.setText(mArticles.get(position).getSnippet());
    }

    private void configureViewHolder(ViewHolderWithThumbnail vh, int position) {
        // Set item views based on your views and data model
        TextView tvTitle = vh.titleTextView;
        TextView tvNewsDesk = vh.newsDeskTextView;
        TextView tvSnippet = vh.snippetTextView;
        tvTitle.setText(mArticles.get(position).getHeadline());
        if (mArticles.get(position).getNewsDesk() != null) {
            String newsDesk = mArticles.get(position).getNewsDesk().toUpperCase();
            if (!newsDesk.equals("NONE") && !newsDesk.isEmpty()) {
                tvNewsDesk.setText(newsDesk);
            }
            else {
                tvNewsDesk.setVisibility(View.GONE);
            }
        }
        tvSnippet.setText(mArticles.get(position).getSnippet());

        ImageView imageView = vh.thumbnailImageView;
        String thumbnail = mArticles.get(position).getThumbNail();

        GlideApp.with(this.mContext)
                .load(thumbnail)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .fitCenter()
                .centerCrop()
                .into(imageView);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    //Returns the view type of the item at position for the purposes of view recycling.
    @Override
    public int getItemViewType(int position) {
        if (mArticles.get(position).getThumbNail() == null || TextUtils.isEmpty(mArticles.get(position).getThumbNail())) {
            return NOTHUMBNAIL;
        } else {
            return THUMBNAIL;
        }
    }
}
