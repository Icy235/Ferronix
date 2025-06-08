package Control;

import android.content.Context;
import android.graphics.Color;
// import android.graphics.fonts.Font; // Это, вероятно, не нужно и может быть удалено, если не используется
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.calculate.ferronix.R;

import java.util.List;

class LanguageAdapter extends ArrayAdapter<LanguageAdapter.LanguageItem> {
    private final String currentLang;
    private final Context context;

    static class LanguageItem {
        final String name;
        final String code;
        final int flagResId;

        LanguageItem(String name, String code, int flagResId) {
            this.name = name;
            this.code = code;
            this.flagResId = flagResId;
        }
    }

    LanguageAdapter(Context context, List<LanguageItem> languages, String currentLang) {
        super(context, R.layout.item_language, languages);
        this.context = context;
        this.currentLang = currentLang;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_language, parent, false);
            holder = new ViewHolder();
            holder.flagIcon = convertView.findViewById(R.id.flag_icon);
            holder.languageName = convertView.findViewById(R.id.language_name);
            // Инициализация selectedIcon здесь!
            holder.selectedIcon = convertView.findViewById(R.id.selected_icon); // <-- ЭТО ВАЖНОЕ ИЗМЕНЕНИЕ

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LanguageItem item = getItem(position);
        if (item != null) {
            holder.flagIcon.setImageResource(item.flagResId);
            holder.languageName.setText(item.name);

            // Проверяем, выбран ли текущий язык
            boolean isSelected = currentLang != null && currentLang.equals(item.code);
            // Теперь holder.selectedIcon не будет null, если item_language.xml содержит R.id.selected_icon
            if (holder.selectedIcon != null) { // Дополнительная проверка на всякий случай
                holder.selectedIcon.setVisibility(isSelected ? View.VISIBLE : View.INVISIBLE);
            }


            // Подсветка выбранного элемента
            int bgColor = isSelected ?
                    ContextCompat.getColor(context, R.color.selected_language_bg) :
                    Color.TRANSPARENT;

            convertView.setBackgroundColor(bgColor);
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView flagIcon;
        TextView languageName;
        ImageView selectedIcon; // Убедитесь, что это поле существует
    }
}