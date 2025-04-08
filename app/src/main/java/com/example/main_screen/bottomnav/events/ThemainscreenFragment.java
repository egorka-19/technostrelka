package com.example.main_screen.bottomnav.events;

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
import com.example.main_screen.databinding.ActivityThemainscreenBinding;

import java.util.ArrayList;
import java.util.List;

import adapter.Course;
import adapter.CourseAdapter;

public class ThemainscreenFragment extends Fragment {
    private ActivityThemainscreenBinding binding;
    private List<CategoryWithCourses> categoriesWithCourses = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ActivityThemainscreenBinding.inflate(inflater, container, false);

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
        if (checkBoxId == R.id.checkBoxMovies) {
            List<Course> ListCinema = new ArrayList<>();
            ListCinema.add(new Course(1, "Мастер и Маргарита", "Фэнтезийная драма",  "master", "Кино", false, 0, 0, "Амбициозная версия романа от создателей «Серебряных коньков». Мастер — Евгений Цыганов, Маргарита — Юлия Снигирь, Воланд — Аугуст Диль"));
            ListCinema.add(new Course(2, "Лёд 3", "Мелодрама",  "ice", "Кино",false, 0, 0, "Финал романтической трилогии с Александром Петровым: дочь хоккеиста Горина становится фигуристкой и влюбляется"));
            ListCinema.add(new Course(3, "Онегин", "Мелодрама, драма",  "onegin", "Кино",false,0, 0, "Сарик Андреасян экранизирует Пушкина. В роли Онегина — Виктор Добронравов"));
            categoriesWithCourses.add(new CategoryWithCourses("Кино", ListCinema));
        } else if (checkBoxId == R.id.checkBoxTheaters) {
            List<Course> ListTheatres = new ArrayList<>();
            ListTheatres.add(new Course(13, "Робин Гуд", "Русский драматический театр Удмуртии",  "robin", "Театры",false,56.844125, 53.199509, "Благородный разбойник Робин Гуд всегда приходит на помощь угнетенным и обиженным. Недаром уже восьмой век про него слагают легенды и баллады, пишут книги, а в последнее время снимают фильмы и ставят спектакли"));
            ListTheatres.add(new Course(14, "Любовь и голуби ", "ДК “Аксион”",  "loveandgolub", "Театры",false,56.850470, 53.199591, "Музыкальная история с актёрами трёх поколений"));
            ListTheatres.add(new Course(15, "Алые паруса", "Национальный театр",  "redparus", "Театры",false,56.845329, 53.198977, "Известная каждому трогательная история о любви и мечте, романтическая повесть-феерия «Алые паруса» Александра Грина в сверхпопулярном жанре мюзикла обретает абсолютно новое, оригинальное, во многом неожиданное звучание"));
            categoriesWithCourses.add(new CategoryWithCourses("Театры", ListTheatres));
        } else if (checkBoxId == R.id.checkBoxRestaurants) {
            List<Course> ListRestaurants = new ArrayList<>();
            ListRestaurants.add(new Course(7, "Panorama", "Открыт до 23:00",  "panorama", "Рестораны",false,56.848942, 53.195590, "Ресторан на набережной Ижевского пруда. Проведите время со вкусом и с удовольствием, наслаждаясь авторской кухней, шикарными видами в одном из самых живописных мест города Ижевска"));
            ListRestaurants.add(new Course(8, "Penthouse", "Открыт до 00:00",  "penthouse", "Рестораны",false,56.866523, 53.207575, "Ресторан авторской кухни, расположенный на высоком этаже с панорамными окнами, откуда открывается захватывающий вид на город с высоты птичьего полета"));
            ListRestaurants.add(new Course(9, "Каре", "Открыт до 00:00",  "kare", "Рестораны",false,56.848160, 53.205816, "Современный городской ресторан, завоевавший доверие и предпочтение своих гостей качеством кухни и обслуживания."));
            categoriesWithCourses.add(new CategoryWithCourses("Рестораны", ListRestaurants));
        } else if (checkBoxId == R.id.checkBoxParks) {
            List<Course> ListParks = new ArrayList<>();
            ListParks.add(new Course(10, "Парк имени С. М. Кирова", "Открыт до 23:00",  "kirova", "Парки",false,56.864117, 53.163655, "Самая большая в Ижевске лесопарковая территория. Это место для прогулок, велоспорта, пробежек. Зимой тут организованы каток и лыжная трасса"));
            ListParks.add(new Course(11, "Летний сад им. М. Горького", "Открыт до 22:00",  "gorkogo", "Парки",false,56.846736, 53.197960, "Одно из самых известных и старейших общественных пространств Ижевска. Это место сосредоточения городских аттракционов, здесь проходят масштабные мероприятия"));
            ListParks.add(new Course(12, "Парк Космонавтов", "Открыт до 21:00",  "cosmos", "Парки",false,56.887326, 53.249373, "Популярное место для семейного отдыха в районе Буммаш в Ижевске. В теплое время года посетители могут насладиться прогулкой по тенистым аллеям и извилистым дорожкам, прокатиться на аттракционах"));
            categoriesWithCourses.add(new CategoryWithCourses("Парки", ListParks));
        } else if (checkBoxId == R.id.checkBoxMuseums) {
            List<Course> ListMuseum = new ArrayList<>();
            ListMuseum.add(new Course(4, "Музей ИЖМАШ", "ул. Свердлова, 32",  "izhmash", "Музеи",false,56.843974, 53.198077, "Музей, где представлена коллекция оружия начиная с 1808 года и до наших дней, а также мотоциклы"));
            ListMuseum.add(new Course(5, "Музей почты Удмуртии", "ул. Кирова, 7",  "pochta", "Музеи",false,56.860644, 53.182360, "Более 800 архивных материалов XIX-XX веков и почтовых предметов хранится в фондах музея. "));
            ListMuseum.add(new Course(6, "Ижевский Мотомузей Кожушковых", "Советская ул., 9",  "Музеи","kozhushkovi",false,56.845400, 53.206505, "Уникальная частная коллекция мототехники, расположенная в лофт-пространстве среди винтажный вещей, окутанных семейной историей."));
            categoriesWithCourses.add(new CategoryWithCourses("Музеи", ListMuseum));
        }
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
