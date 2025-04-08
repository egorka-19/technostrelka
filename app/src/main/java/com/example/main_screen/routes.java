package com.example.main_screen;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import adapter.ItemAdapter;

public class routes extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    ImageButton button, pause_button, back_button;
    boolean flag;

    private void stopMusic() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        mediaPlayer = null;
        button = findViewById(R.id.button);
        pause_button = findViewById(R.id.pause_button);
        back_button = findViewById(R.id.back_button);
        pause_button.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.INVISIBLE);
                pause_button.setVisibility(View.VISIBLE);
                if (v.getId() == R.id.button) {
                    // Check if mediaPlayer is null. If true, we'll instantiate the MediaPlayer object
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(v.getContext(), R.raw.music);
                    }
                    // Then, register OnCompletionListener that calls a user supplied callback method onCompletion() when
                    // looping mode was set to false to indicate playback is completed.
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            // Here, call a method to release the MediaPlayer object and to set it to null.
                            stopMusic();
                        }
                    });
                    // Next, call start() method on mediaPlayer to start playing the music.
                    mediaPlayer.start();
                }
            }
        });
        pause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setVisibility(View.VISIBLE);
                pause_button.setVisibility(View.INVISIBLE);
                if (v.getId() == R.id.pause_button) {
                    // Check if mediaPlayer is null. If true, we'll instantiate the MediaPlayer object
                    if (mediaPlayer != null) {
                        // Here, call pause() method on mediaPlayer to pause the music.
                        mediaPlayer.pause();
                    }
                }
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(routes.this, MainActivity.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<ItemData> itemsData = new ArrayList<>();
        itemsData.add(new ItemData("Начало экскурсии", R.drawable.one, "Дорогие гости, Вы приехали в столицу Удмуртской Республики - город Ижевск. В самом начале это был всего лишь рабочий поселок с железоделательным заводом, постепенно город развивался в промышленном направлении, здесь начали производить оружие, благодаря которому город стал известен на всю Россию. Сейчас же население его составляет около 600 тысяч человек, и Ижевск входит в двадцать крупнейших городов России."));
        itemsData.add(new ItemData("Ж/Д вокзал", R.drawable.two, "Вы приехали на железнодорожный вокзал города Ижевска, первое название вокзала – Казанский и был он построен в годы первой мировой войны. Раньше это было обычное деревянное здание, ничем не напоминающее вокзал, сейчас же это современное здание, которое принимает поезда со всей России. А пока мы подъезжаем к следующей остановке."));
        itemsData.add(new ItemData("Завод мин.вод", R.drawable.three, "В 1949 году возле железнодорожного вокзала бурили скважину, чтобы обеспечивать водой паровозы и сам вокзал. Но из скважины неожиданно для всех пошла минеральная вода. Эта скважина получила в дальнейшем название \"Ново-Ижевский источник минеральных лечебных вод\" и стала отправной точкой для создания Ижевского завода минерально-фруктовых вод."));
        itemsData.add(new ItemData("Ресторан Кинза", R.drawable.five, "Первый раз люди приходят в Кинзу попробовать знаменитые Хинкали и Хачапури по-аджарски, так и происходит наше первое знакомство с гостями. Затем, люди возвращаются не только за вкусными хинкали с бульоном,но и за атмосферой Грузии, за качественными сервисом и уютной остановкой. Они приходят целыми семьями, ведь мы позаботились о комфорте каждого и сделали игровые комнаты и отдельное меню для детей."));
        itemsData.add(new ItemData("ул.Гагарина", R.drawable.four, "Улица Гагарина носит имя первого человека, полетевшего в космос – Юрия Гагарина. Любопытно, что в Ижевске целых 217 улиц названы в честь известных людей. Причем, 210 из них - это мужчины и только 7 – женщины. Например, улица Кузебая Герда, это псевдоним Кузьмы Чайникова, основоположника удмуртской детской литературы. Им были опубликованы более 80 стихов и около 40 рассказов на удмуртском языке."));
        ItemAdapter adapter = new ItemAdapter(itemsData);
        recyclerView.setAdapter(adapter);

    }

}
