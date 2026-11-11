package vn.khohang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public final class MainActivity extends Activity {
    private DatabaseHelper database;
    private ListView productList;
    private TextView statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new DatabaseHelper(this);
        productList = findViewById(R.id.product_list);
        productList.setEmptyView(findViewById(R.id.empty_list));
        statistics = findViewById(R.id.statistics);

        findViewById(R.id.add_product).setOnClickListener(view ->
                startActivity(new Intent(this, AddProductActivity.class)));
        productList.setOnItemClickListener((parent, view, position, id) -> {
            Product product = (Product) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, ProductDetailActivity.class);
            intent.putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<Product> products = database.getAllProducts();
        productList.setAdapter(new ArrayAdapter<Product>(this, R.layout.row_product, products) {
            @Override
            public View getView(int position, View row, ViewGroup parent) {
                if (row == null) {
                    row = getLayoutInflater().inflate(R.layout.row_product, parent, false);
                }
                Product product = getItem(position);
                TextView name = row.findViewById(R.id.row_name);
                TextView metadata = row.findViewById(R.id.row_metadata);
                TextView quantity = row.findViewById(R.id.row_quantity);
                TextView warning = row.findViewById(R.id.row_warning);

                name.setText(product.name);
                metadata.setText(product.category + " · " + Product.formatMoney(product.price));
                quantity.setText("Tồn kho: " + product.quantity);
                quantity.setTextColor(getColor(product.isLowStock() ? R.color.warning : R.color.ink));
                warning.setVisibility(product.isLowStock() ? View.VISIBLE : View.GONE);
                return row;
            }
        });
        statistics.setText("Tổng số sản phẩm: " + products.size()
                + "\nTổng giá trị kho: " + Product.formatMoney(database.getTotalValue())
                + "\nSắp hết hàng: " + database.countLowStock());
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
