package vn.khohang;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigInteger;
import java.util.ArrayList;

public final class DatabaseHelper extends SQLiteOpenHelper {
    private static final String USERS = "users";
    private static final String PRODUCTS = "products";
    private static final String[] PRODUCT_COLUMNS = {
            "id", "name", "price", "quantity", "category", "active", "description"
    };

    public DatabaseHelper(Context context) {
        super(context, "warehouse.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, password TEXT NOT NULL)");
        db.execSQL("CREATE TABLE products (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, price INTEGER NOT NULL CHECK(price > 0), "
                + "quantity INTEGER NOT NULL CHECK(quantity >= 0), "
                + "category TEXT NOT NULL, active INTEGER NOT NULL CHECK(active IN (0, 1)), "
                + "description TEXT)");
        insertUser(db, "admin", "123456");
        insertProduct(db, new Product(0, "Chuột không dây", 150000, 20, "Điện tử", true, ""));
        insertProduct(db, new Product(0, "Bàn phím cơ", 450000, 3, "Điện tử", true, ""));
        insertProduct(db, new Product(0, "Sổ tay A5", 25000, 40, "Văn phòng phẩm", true, ""));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new IllegalStateException("Chưa có cấu hình nâng cấp cơ sở dữ liệu");
    }

    public boolean checkLogin(String username, String password) {
        try (Cursor cursor = getReadableDatabase().query(USERS, new String[]{"id"},
                "username = ? AND password = ?", new String[]{username, password},
                null, null, null, "1")) {
            return cursor.moveToFirst();
        }
    }

    public long insertUser(String username, String password) {
        return insertUser(getWritableDatabase(), username, password);
    }

    private long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        return db.insertOrThrow(USERS, null, values);
    }

    public long insertProduct(Product product) {
        return insertProduct(getWritableDatabase(), product);
    }

    private long insertProduct(SQLiteDatabase db, Product product) {
        ContentValues values = new ContentValues();
        values.put("name", product.name);
        values.put("price", product.price);
        values.put("quantity", product.quantity);
        values.put("category", product.category);
        values.put("active", product.active ? 1 : 0);
        values.put("description", product.description);
        return db.insertOrThrow(PRODUCTS, null, values);
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(PRODUCTS, PRODUCT_COLUMNS,
                null, null, null, null, "id DESC")) {
            while (cursor.moveToNext()) {
                products.add(readProduct(cursor));
            }
        }
        return products;
    }

    public Product getProductById(int id) {
        try (Cursor cursor = getReadableDatabase().query(PRODUCTS, PRODUCT_COLUMNS,
                "id = ?", new String[]{String.valueOf(id)}, null, null, null)) {
            return cursor.moveToFirst() ? readProduct(cursor) : null;
        }
    }

    public boolean updateQuantity(int id, int quantity) {
        ContentValues values = new ContentValues();
        values.put("quantity", quantity);
        return getWritableDatabase().update(PRODUCTS, values,
                "id = ?", new String[]{String.valueOf(id)}) == 1;
    }

    public boolean deleteProduct(int id) {
        return getWritableDatabase().delete(PRODUCTS,
                "id = ?", new String[]{String.valueOf(id)}) == 1;
    }

    public BigInteger getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT COALESCE(SUM(price * quantity), 0) FROM products",
                null)) {
            cursor.moveToFirst();
            return BigInteger.valueOf(cursor.getLong(0));
        } catch (SQLiteException error) {
            if (error.getMessage() == null || !error.getMessage().contains("integer overflow")) {
                throw error;
            }
            BigInteger total = BigInteger.ZERO;
            try (Cursor cursor = db.rawQuery("SELECT price * quantity FROM products", null)) {
                while (cursor.moveToNext()) {
                    total = total.add(BigInteger.valueOf(cursor.getLong(0)));
                }
            }
            return total;
        }
    }

    public int countLowStock() {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM products WHERE quantity < ?",
                new String[]{String.valueOf(Product.LOW_STOCK_LIMIT)})) {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
    }

    private Product readProduct(Cursor cursor) {
        return new Product(cursor.getInt(0), cursor.getString(1), cursor.getInt(2),
                cursor.getInt(3), cursor.getString(4), cursor.getInt(5) == 1, cursor.getString(6));
    }
}
