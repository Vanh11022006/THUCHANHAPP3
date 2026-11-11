package vn.khohang;

import android.app.Activity;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public final class AddProductActivity extends Activity {
    private DatabaseHelper database;
    private EditText name;
    private EditText price;
    private EditText quantity;
    private EditText description;
    private Spinner category;
    private Switch active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        database = new DatabaseHelper(this);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        quantity = findViewById(R.id.quantity);
        description = findViewById(R.id.description);
        category = findViewById(R.id.category);
        active = findViewById(R.id.active);
        ArrayAdapter<CharSequence> categories = ArrayAdapter.createFromResource(
                this, R.array.categories, android.R.layout.simple_spinner_item);
        categories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(categories);
        findViewById(R.id.save_product).setOnClickListener(view -> saveProduct());
    }

    private void saveProduct() {
        String productName = name.getText().toString().trim();
        if (productName.isEmpty()) {
            name.setError("Vui lòng nhập tên sản phẩm");
            return;
        }
        Integer productPrice = readNumber(price, 1, "Giá bán phải là số nguyên lớn hơn 0");
        if (productPrice == null) {
            return;
        }
        Integer productQuantity = readNumber(quantity, 0, "Số lượng phải là số nguyên không âm");
        if (productQuantity == null) {
            return;
        }
        Product product = new Product(0, productName, productPrice, productQuantity,
                category.getSelectedItem().toString(), active.isChecked(),
                description.getText().toString().trim());
        try {
            database.insertProduct(product);
            Toast.makeText(this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        } catch (SQLiteException error) {
            Toast.makeText(this, "Không lưu được sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private Integer readNumber(EditText input, int minimum, String errorMessage) {
        try {
            int value = Integer.parseInt(input.getText().toString().trim());
            if (value >= minimum) {
                return value;
            }
        } catch (NumberFormatException ignored) {
        }
        input.setError(errorMessage);
        return null;
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
