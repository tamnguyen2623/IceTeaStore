package com.example.iceteastore.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.iceteastore.R;
import com.example.iceteastore.adapters.CarouselAdapter;
import com.example.iceteastore.adapters.ProductAdapter;
import com.example.iceteastore.daos.ProductDAO;
import com.example.iceteastore.models.CarouselItem;
import com.example.iceteastore.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import me.relex.circleindicator.CircleIndicator3;

public class HomeActivity extends AppCompatActivity {
    private SearchView searchView;
    private ViewPager2 viewPager;
    private CircleIndicator3 indicator;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private CarouselAdapter carouselAdapter;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private ProductDAO productDAO;
    private List<CarouselItem> carouselItems;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        // Đánh dấu Home là item được chọn
        bottomNavigationView.setSelectedItemId(R.id.home);
        // Xử lý chuyển trang khi bấm vào item navbar
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.shopping_cart) {
//                    startActivity(new Intent(HomeActivity.this, ProductManagementListActivity.class));
//                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.bill) {
//                    startActivity(new Intent(HomeActivity.this, ProductManagementActivity.class));
//                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.profile) {
//                    startActivity(new Intent(HomeActivity.this, ProductManagementActivity.class));
//                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            }
        });

        // Ánh xạ UI
        searchView = findViewById(R.id.searchView);
        viewPager = findViewById(R.id.viewPager);
        indicator = findViewById(R.id.circleIndicator);
        recyclerView = findViewById(R.id.recyclerView);
        tabLayout = findViewById(R.id.tabLayout);
        ImageView ivFavoriteList = findViewById(R.id.ivFavoriteList);
        //  Cấu hình Carousel (Slider sản phẩm nổi bật)
        setupCarousel();

        //  Lấy danh sách sản phẩm từ SQLite
        productDAO = new ProductDAO(this);
        productList = productDAO.getAllProducts();

        // Kiểm tra nếu database rỗng thì thêm dữ liệu mẫu
        if (productList.isEmpty()) {
            insertSampleData();
            productList = productDAO.getAllProducts();
        }

        // Cấu hình RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, productList);
        recyclerView.setAdapter(productAdapter);

        // Thêm danh mục vào TabLayout
        setupTabs();

        //  Xử lý tìm kiếm sản phẩm
        setupSearchView();

        //  Xử lý khi nhấn vào icon danh sách yêu thích
        ivFavoriteList.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FavoriteActivity.class);
            startActivity(intent);
        });
    }

    /** Cấu hình Carousel */
    private void setupCarousel() {
        carouselItems = new ArrayList<>();
        carouselItems.add(new CarouselItem("Lemon Tea", "$10.40", R.drawable.food1));
        carouselItems.add(new CarouselItem("Oolong Tea", "$12.50", R.drawable.food2));
        carouselItems.add(new CarouselItem("Peach Tea", "$15.30", R.drawable.food3));
        carouselAdapter = new CarouselAdapter(carouselItems);
        viewPager.setAdapter(carouselAdapter);
        indicator.setViewPager(viewPager); // Gắn indicator với ViewPager2
    }

    /** Cấu hình TabLayout */
    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All Tea"));
        tabLayout.addTab(tabLayout.newTab().setText("Most Popular"));
        tabLayout.addTab(tabLayout.newTab().setText("Product New"));

        // Xử lý khi chọn tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                filterByCategory(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /** Cấu hình tìm kiếm */
    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
    }

    /** Bộ lọc tìm kiếm sản phẩm */
    private void filter(String text) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter.filterList(filteredList);
    }

    /** Bộ lọc sản phẩm theo danh mục */
    private void filterByCategory(int position) {
        List<Product> filteredList = new ArrayList<>();
        switch (position) {
            case 0: // "All tea"
                filteredList.addAll(productList);
                break;
            case 1: // "Most Popular"
                for (Product product : productList) {
                    if (product.getRating() >= 4.8) {
                        filteredList.add(product);
                    }
                }
                break;
            case 2: // "New"
                for (Product product : productList) {
                    if (product.getName().toLowerCase().contains("new")) {
                        filteredList.add(product);
                    }
                }
                break;
        }
        productAdapter.filterList(filteredList);
    }

    /** Thêm dữ liệu mẫu vào SQLite (Chỉ chạy 1 lần) */
    private void insertSampleData() {
    }
    @Override
    protected void onResume() {
        super.onResume();
        productList = productDAO.getAllProducts(); // Load lại danh sách sản phẩm
        productAdapter.filterList(productList);   // Cập nhật RecyclerView
    }

}
