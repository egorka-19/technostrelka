package adapter;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.ItemData;
import com.example.main_screen.R;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<ItemData> itemsData;
    private int selectedPosition = -1;

    public ItemAdapter(List<ItemData> itemsData) {
        this.itemsData = itemsData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();


        // Получаем данные для текущего элемента
        ItemData currentItem = itemsData.get(adapterPosition);

        // Устанавливаем заголовок, картинку, текст и номер
        holder.titleText.setText(currentItem.getTitle());
        holder.imageView.setImageResource(currentItem.getImageResId());
        holder.extraText.setText(currentItem.getText());
        holder.numberText.setText(String.valueOf(adapterPosition + 1));  // Устанавливаем номер пункта

        // Проверяем, выбран ли пункт
        if (adapterPosition == selectedPosition) {
            holder.extraContent.setVisibility(View.VISIBLE);
            holder.arrowImage.setImageResource(R.drawable.up_button);  // Стрелка вверх
        } else {
            holder.extraContent.setVisibility(View.GONE);
            holder.arrowImage.setImageResource(R.drawable.down_button);  // Стрелка вниз
        }

        // Обработка клика на элемент
        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition == adapterPosition) {
                selectedPosition = -1;  // Скрыть выбранный пункт
            } else {
                selectedPosition = adapterPosition;  // Показать выбранный пункт
            }
            notifyDataSetChanged();  // Обновить список
        });
    }

    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        LinearLayout extraContent;
        ImageView imageView;
        ImageButton promo_btn;
        ImageButton button;
        TextView extraText;
        MediaPlayer mediaPlayer;
        TextView numberText;  // Номер пункта
        View verticalLine;  // Вертикальная линия
        ImageView arrowImage;  // Стрелка
        boolean flag = true;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            extraContent = itemView.findViewById(R.id.extraContent);
            promo_btn = itemView.findViewById(R.id.promo_btn);
            imageView = itemView.findViewById(R.id.imageView);
            button = itemView.findViewById(R.id.button);
            extraText = itemView.findViewById(R.id.extraText);
            numberText = itemView.findViewById(R.id.numberText);
            arrowImage = itemView.findViewById(R.id.arrowImage);
            mediaPlayer = null;

            promo_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (flag){
                        promo_btn.setImageResource(R.drawable.promo_btn);
                        flag = false;
                    }else{
                        // возвращаем первую картинку
                        promo_btn.setImageResource(R.drawable.podarok_btn);
                        flag=true;
                    }
                }
            });// Инициализация стрелки
        }

    }
}
