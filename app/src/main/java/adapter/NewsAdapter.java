package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.main_screen.R;

import java.util.List;

import model.Newss;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    Context context;
    List<Newss> news;

    public NewsAdapter(Context context, List<Newss> news) {
        this.context = context;
        this.news = news;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View newsItems = LayoutInflater.from(context).inflate(R.layout.news, parent, false);
        return new NewsAdapter.NewsViewHolder(newsItems);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        String imageUrl = news.get(position).getImage();
        String avatarUrl = news.get(position).getAvatar();
        if (news.get(position).getId() == 1) {
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.image);

            Glide.with(context)
                    .load(avatarUrl)
                    .into(holder.avatar);
        } else {
            int imageId = context.getResources().getIdentifier(imageUrl, "drawable", context.getPackageName());
            int avatarId = context.getResources().getIdentifier(avatarUrl, "drawable", context.getPackageName());
            holder.avatar.setImageResource(avatarId);
            holder.image.setImageResource(imageId);
        }

        holder.Nickname.setText(news.get(position).getNickname());
        holder.name_post.setText(news.get(position).getName_post());
        holder.postText.setText(news.get(position).getPostText());
    }


    @Override
    public int getItemCount() {
        return news.size();
    }

    public static final class NewsViewHolder extends RecyclerView.ViewHolder {

        TextView Nickname, name_post, postText;

        ImageView avatar, image;
        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            Nickname = itemView.findViewById(R.id.username_Tv_fav);
            name_post = itemView.findViewById(R.id.post_Name_Tv);
            postText = itemView.findViewById(R.id.postText_Tv);
            avatar = itemView.findViewById(R.id.profile_ImageView);
            image = itemView.findViewById(R.id.post_ImageView);

        }
    }
}
