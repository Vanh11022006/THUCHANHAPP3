package vn.khohang;

import java.text.NumberFormat;
import java.util.Locale;

public final class Product {
    public static final int LOW_STOCK_LIMIT = 5;

    public final int id;
    public final String name;
    public final int price;
    public final int quantity;
    public final String category;
    public final boolean active;
    public final String description;

    public Product(int id, String name, int price, int quantity, String category,
                   boolean active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    public long stockValue() {
        return (long) price * quantity;
    }

    public boolean isLowStock() {
        return quantity < LOW_STOCK_LIMIT;
    }

    public static String formatMoney(Number value) {
        return NumberFormat.getIntegerInstance(new Locale("vi", "VN")).format(value) + " ₫";
    }
}
