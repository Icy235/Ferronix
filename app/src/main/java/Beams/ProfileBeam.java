package Beams;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.calculate.ferronix.R;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class ProfileBeam extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView textViewSchemeDescription;
    private ToggleButton toggleLoadType;
    private int[][] images = {
            {R.drawable.zadelka_sharnir_sosred, R.drawable.zadelka_sharnir_raspred},
            {R.drawable.free_konets_sosred, R.drawable.free_konets_raspred},
            {R.drawable.shranir_sharnir_sosred, R.drawable.shranir_sharnir_raspred},
            {R.drawable.zadelka_zadelka_sosred, R.drawable.zadelka_zadelka_raspred}
    };

    private String[] descriptions = {
            "Заделка-шарнир",
            "Свободный конец",
            "Шарнир-шарнир",
            "Заделка-заделка"
    };

    private int currentLoadType = 1; // 0 - Сосредоточенная, 1 - Распределенная

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beam_square_calculate);

        viewPager = findViewById(R.id.viewPagerSchemes);
        textViewSchemeDescription = findViewById(R.id.textViewSchemeDescription);
        toggleLoadType = findViewById(R.id.toggleLoadType);
        ImageButton btnBack = findViewById(R.id.btnBack);
        DotsIndicator dotsIndicator = findViewById(R.id.dotsIndicator);

        btnBack.setOnClickListener(v -> finish());

        ProfileBeamAdapter adapter = new ProfileBeamAdapter(this, getCurrentImages());
        viewPager.setAdapter(adapter);
        dotsIndicator.setViewPager2(viewPager);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                textViewSchemeDescription.setText(descriptions[position]);
            }
        });

        toggleLoadType.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentLoadType = isChecked ? 1 : 0;
            updateImages();
        });
    }

    private int[] getCurrentImages() {
        int[] currentImages = new int[images.length];
        for (int i = 0; i < images.length; i++) {
            currentImages[i] = images[i][currentLoadType];
        }
        return currentImages;
    }

    private void updateImages() {
        ProfileBeamAdapter adapter = new ProfileBeamAdapter(this, getCurrentImages());
        viewPager.setAdapter(adapter);
    }
}
