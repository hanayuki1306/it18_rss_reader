package it18.ehou.rssreaderapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.adapters.CategoryPagerAdapter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Cài đặt Top Bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 2. Cài đặt Tabs và ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        CategoryPagerAdapter pagerAdapter = new CategoryPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // 3. Tiêu đề các Tab từ string resources
        int[] categoryTitleRes = {
                R.string.category_education,
                R.string.category_sports,
                R.string.category_entertainment,
                R.string.category_travel,
                R.string.category_health
        };

        // 4. Kết nối Tab và ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position >= 0 && position < categoryTitleRes.length) {
                tab.setText(getString(categoryTitleRes[position]));
            }
        }).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_saved_news) {
            Intent intent = new Intent(MainActivity.this, SavedNewsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}