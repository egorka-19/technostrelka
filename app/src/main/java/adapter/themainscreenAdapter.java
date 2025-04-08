package adapter;// themainscreen.java
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.main_screen.R;
import com.example.main_screen.bottomnav.events.ThemainscreenFragment;


import java.util.List;

public class themainscreenAdapter extends RecyclerView.Adapter<themainscreenAdapter.CategoryViewHolder> {

    private Context context;
    private List<ThemainscreenFragment.CategoryWithCourses> categoriesWithCourses;

    public themainscreenAdapter(Context context, List<ThemainscreenFragment.CategoryWithCourses> categoriesWithCourses) {
        this.context = context;
        this.categoriesWithCourses = categoriesWithCourses;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_with_cources, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ThemainscreenFragment.CategoryWithCourses categoryWithCourses = categoriesWithCourses.get(position);
        holder.categoryTitle.setText(categoryWithCourses.getCategoryTitle());

        CourseAdapter courseAdapter = new CourseAdapter(context, categoryWithCourses.getCourses());
        holder.courseRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.courseRecyclerView.setAdapter(courseAdapter);

    }

    @Override
    public int getItemCount() {
        return categoriesWithCourses.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView courseRecyclerView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.category_title);
            courseRecyclerView = itemView.findViewById(R.id.course_recycler_view);
        }
    }
}
