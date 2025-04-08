package com.example.main_screen.bottomnav.fav;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.Category;
import com.example.main_screen.MainActivity;
import com.example.main_screen.R;
import com.example.main_screen.databinding.FragmentFavBinding;

import java.util.ArrayList;
import java.util.List;

import adapter.Course;
import adapter.CourseAdapter;

public class fav_fragment extends Fragment {
    private FragmentFavBinding binding;
    private List<CategoryWithCourses> categoriesWithCourses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavBinding.inflate(inflater, container, false);

        // Получение ссылки на кнопку из разметки фрагмента
        ImageButton button = binding.getRoot().findViewById(R.id.category_back);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Создание объекта Intent для перехода на активити
                Intent intent = new Intent(getActivity(), Category.class);

                // Добавление дополнительной информации, если необходимо
                intent.putExtra("key", "value");

                // Запуск активити
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Integer> selectedCheckBoxIds = requireActivity().getIntent().getIntegerArrayListExtra("selectedCheckBoxIds");

        for (Integer checkBoxId : selectedCheckBoxIds) {
            getCheckBoxNameById(checkBoxId);
        }

        RecyclerView recyclerView = binding.recycle;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        MainscreenAdapter adapter = new MainscreenAdapter(requireContext(), categoriesWithCourses);
        recyclerView.setAdapter(adapter);

    }

    private void getCheckBoxNameById(int checkBoxId) {
            List<Course> ListTramvai = new ArrayList<>();
            ListTramvai.add(new Course(1, "Ижевская Однёрка", "Для всей семьи",  "odnerka", "Трамвай", false, 0, 0, "Уважаемые слушатели, приветствую Вас на маршруте 1 трамвая города Ижевска. Сейчас мы начнем наше знакомство со столицей Удмуртской Республики -  с её историей и культурой. За время следования трамвая Вы узнаете информацию об окружающем районе, зданиях и памятных местах города Ижевска. Устраивайтесь поудобнее, и мы начинаем!"));
            ListTramvai.add(new Course(2, "Ижевская Десятка", "Для всей семьи",  "desyatka", "Трамвай",false, 0, 0, "Уважаемые слушатели, приветствую Вас на маршруте 10 трамвая города Ижевска. Сейчас мы начнем наше знакомство со столицей Удмуртской Республики -  с её историей и культурой. За время следования трамвая Вы узнаете информацию об окружающем районе, зданиях и памятных местах города Ижевска. Устраивайтесь поудобнее, и мы начинаем!"));
            ListTramvai.add(new Course(3, "Ижевская Двойка", "Для всей семьи",  "dvoika", "Трамвай",false,0, 0, "Уважаемые слушатели, приветствую Вас на маршруте 2 трамвая города Ижевска. Сейчас мы начнем наше знакомство со столицей Удмуртской Республики -  с её историей и культурой. За время следования трамвая Вы узнаете информацию об окружающем районе, зданиях и памятных местах города Ижевска. Устраивайтесь поудобнее, и мы начинаем!"));
            categoriesWithCourses.add(new CategoryWithCourses("Трамвай", ListTramvai));
            List<Course> List18plus = new ArrayList<>();
            List18plus.add(new Course(13, "Ижевск - крепкий", "18+",  "alko", "18+",false,56.844125, 53.199509, "Маршрут 'Ижевск - крепкий' предлагает увлекательное путешествие по Удмуртской Республике, начиная с Ижевска, где можно попробовать местное пиво и водку"));
            List18plus.add(new Course(14, "Ижевск - гастрономический", "18+",  "gastro", "18+",false,56.850470, 53.199591, "Гастрономический маршрут по Ижевску знакомит с местными кулинарными традициями. Вы попробуете вкуснейшие блюда от традиционных до современных, включая уникальные рецепты Удмуртской кухни и гастрономические находки местных ресторанов и кафе."));
            List18plus.add(new Course(15, "Удмуртский алкоголик", "18+",  "udmalko", "18+",false,56.845329, 53.198977, "Это маршрут, посвященный Удмуртскому спиртному. Вы сможете узнать о культуре употребления алкоголя в регионе, посетить известные пивоварни и винодельни, а также попробовать местные напитки, такие как удмуртская водка и квас.\n"));
            categoriesWithCourses.add(new CategoryWithCourses("18+", List18plus));
            List<Course> ListRestaurants = new ArrayList<>();
            ListRestaurants.add(new Course(7, "Пельмени", "ГастроТУР",  "pelmeni", "ГастроТУР",false,56.848942, 53.195590, "Погрузитесь в мир пельменей на этом маршруте, где главный акцент сделан на самом популярном блюде русской кухни. Вы посетите мастер-классы по приготовлению пельменей и познакомитесь с их различными вариациями и традициями употребления.\n"));
            ListRestaurants.add(new Course(8, "Перепечи", "ГастроТУР",  "perepechi", "ГастроТУР",false,56.866523, 53.207575, "Этот маршрут посвящен таким традиционным изделиям, как перепечи. Вы узнаете об истории этого блюда и сможете попробовать разнообразные начинки в лучших заведениях города, где делают перепечи по старинным рецептам.\n"));
            ListRestaurants.add(new Course(9, "Табани", "ГастроТУР",  "tabani", "ГастроТУР",false,56.848160, 53.205816, "Этот маршрут посвящен таким традиционным изделиям, как табани. Вы узнаете об истории этого блюда и сможете попробовать разнообразные начинки в лучших заведениях города, где делают табани по старинным рецептам.\n"));
            categoriesWithCourses.add(new CategoryWithCourses("ГастроТУР", ListRestaurants));
            List<Course> ListJob = new ArrayList<>();
            ListJob.add(new Course(10, "Ижевск - литературный", "Искусство и наука",  "litra", "Работа",false,56.864117, 53.163655, "Литературный маршрут по Ижевску познакомит вас с выдающимися писателями и поэтами, родившимися и жившими в этом городе. Вы посетите памятники, литературные кафе и места, упоминаемые в произведениях местных авторов.\n"));
            ListJob.add(new Course(11, "Ижевск - научный", "Искусство и наука",  "nauka", "Работа",false,56.846736, 53.197960, "Научный маршрут приглашает вас в мир науки и исследований. Вы посетите научные центры, университеты и инновационные лаборатории, познакомитесь с достижениями ижевских ученых и участниками актуальных проектов.\n"));
            ListJob.add(new Course(12, "Ижевск - оружейный", "Искусство и наука",  "oruzhie", "Работа",false,56.887326, 53.249373, "Маршрут «оружейный» погружает в историю и традиции оружейного производства в Ижевске. Вы посетите заводы, музеи и выставки, где узнаете о производстве оружия и его роли в истории России.\n"));
            categoriesWithCourses.add(new CategoryWithCourses("Работа", ListJob));
            List<Course> ListAuto = new ArrayList<>();
            ListAuto.add(new Course(4, "Ижевск - соврменный", "АвтоМото",  "sovremen", "АвтоМото",false,56.843974, 53.198077, "Этот маршрут предлагает ознакомиться с современным Ижевском — его архитектурой, искусством и культурным наследием. Вы посетите актуальные выставки, арт-объекты и современные культурные центры.\n"));
            ListAuto.add(new Course(5, "Ижевск - автомобильный", "ул. Кирова, 7",  "avto", "АвтоМото",false,56.860644, 53.182360, "Автомобильный маршрут по Ижевску — это возможность насладиться живописными пейзажами и удобно перемещаться по городу. Вы откроете для себя скрытые уголки, уютные места и интересные достопримечательности в удобном формате."));
            ListAuto.add(new Course(6, "МОТО - Ижевск", "Советская ул., 9",  "moto","АвтоМото",false,56.845400, 53.206505, "Маршрут «МОТО - Ижевск» создан для любителей скорости и мототехники. Вы посетите мотоциклетные выставки, исследуете местные клубы и сможете принять участие в совместных заездах по живописным маршрутам вокруг города."));
            categoriesWithCourses.add(new CategoryWithCourses("АвтоМото", ListAuto));

    }

    public static class CategoryWithCourses {
        private String categoryTitle;
        private List<Course> courses;

        public CategoryWithCourses(String categoryTitle, List<Course> courses) {
            this.categoryTitle = categoryTitle;
            this.courses = courses;
        }

        public String getCategoryTitle() {
            return categoryTitle;
        }

        public List<Course> getCourses() {
            return courses;
        }
    }

    public static class MainscreenAdapter extends RecyclerView.Adapter<MainscreenAdapter.CategoryViewHolder> {
        private Context context;
        private List<CategoryWithCourses> categoriesWithCourses;
        public MainscreenAdapter(Context context, List<CategoryWithCourses> categoriesWithCourses) {
            this.context = context;
            this.categoriesWithCourses = categoriesWithCourses;
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_with_cources, parent, false);
            return new CategoryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            CategoryWithCourses categoryWithCourses = categoriesWithCourses.get(position);
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
}
