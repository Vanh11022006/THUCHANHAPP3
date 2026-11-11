package vn.khohang;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public final class ProductDetailActivity extends Activity {
    public static final String EXTRA_PRODUCT_ID = "vn.khohang.PRODUCT_ID";

    private DatabaseHelper database;
    private int productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        database = new DatabaseHelper(this);
        productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        findViewById(R.id.receive).setOnClickListener(view -> changeQuantity(1));
        findViewById(R.id.sell).setOnClickListener(view -> changeQuantity(-1));
        findViewById(R.id.go_back).setOnClickListener(view -> finish());
        findViewById(R.id.delete).setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn muốn xóa “" + product.name + "” khỏi kho?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> deleteProduct())
                .show());
        showProduct();
    }

    private void showProduct() {
        product = database.getProductById(productId);
        if (product == null) {
            toast("Sản phẩm không còn trong kho");
            finish();
            return;
        }
        setText(R.id.detail_name, product.name);
        setText(R.id.detail_category, "Loại: " + product.category);
        setText(R.id.detail_price, "Giá bán: " + Product.formatMoney(product.price));
        setText(R.id.detail_value, "Giá trị tồn kho: " + Product.formatMoney(product.stockValue()));
        setText(R.id.detail_active, product.active ? "Đang kinh doanh" : "Ngừng kinh doanh");
        setText(R.id.detail_description, product.description == null || product.description.isEmpty()
                ? "Chưa có mô tả" : product.description);

        TextView quantity = findViewById(R.id.detail_quantity);
        quantity.setText("Tồn kho: " + product.quantity);
        quantity.setTextColor(getColor(product.isLowStock() ? R.color.warning : R.color.ink));
        findViewById(R.id.detail_warning).setVisibility(product.isLowStock() ? View.VISIBLE : View.GONE);
    }

    private void changeQuantity(int change) {
        if (change < 0 && product.quantity == 0) {
            toast("Hết hàng");
            return;
        }
        if (change > 0 && product.quantity == Integer.MAX_VALUE) {
            toast("Số lượng đã đạt giới hạn");
            return;
        }
        try {
            database.updateQuantity(productId, product.quantity + change);
            showProduct();
        } catch (SQLiteException error) {
            toast("Không cập nhật được số lượng");
        }
    }

    private void deleteProduct() {
        try {
            if (database.deleteProduct(productId)) {
                toast("Đã xóa sản phẩm");
            } else {
                toast("Sản phẩm không còn trong kho");
            }
            finish();
        } catch (SQLiteException error) {
            toast("Không xóa được sản phẩm");
        }
    }

    private void setText(int id, String value) {
        TextView text = findViewById(id);
        text.setText(value);
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
