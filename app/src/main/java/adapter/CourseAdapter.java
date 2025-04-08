package adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.main_screen.CoursePage;
import com.example.main_screen.R;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private Context context;
    private List<Course> courses;

    public CourseAdapter(Context context, List<Course> courses) {
        this.context = context;
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        int imageId = context.getResources().getIdentifier("ic_" + course.getImage(), "drawable", context.getPackageName());
        holder.courseImage.setImageResource(imageId);
        holder.courseTitle.setText(course.getTitle1());
        holder.courseDescription.setText(course.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CoursePage.class);
                intent.putExtra("courseImage", imageId);
                intent.putExtra("courseTitle", courses.get(holder.getAdapterPosition()).getTitle1());
                intent.putExtra("courseDescription", courses.get(holder.getAdapterPosition()).getDescription());
                intent.putExtra("categoryTitle", courses.get(holder.getAdapterPosition()).getCategoryTitle());
                intent.putExtra("courseText", courses.get(holder.getAdapterPosition()).getText());
                intent.putExtra("x", courses.get(holder.getAdapterPosition()).getX());
                intent.putExtra("y", courses.get(holder.getAdapterPosition()).getY());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseTitle, courseDescription;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.image_course);
            courseTitle = itemView.findViewById(R.id.text_course_title);
            courseDescription = itemView.findViewById(R.id.text_course_description);
        }
    }
}
