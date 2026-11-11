package vn.khohang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

public final class LoginActivity extends Activity {
    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = new DatabaseHelper(this);
        EditText username = findViewById(R.id.username);
        EditText password = findViewById(R.id.password);

        findViewById(R.id.login).setOnClickListener(view -> {
            String user = username.getText().toString().trim();
            String secret = password.getText().toString();
            if (user.isEmpty()) {
                username.setError("Vui lòng nhập tên đăng nhập");
                return;
            }
            if (secret.trim().isEmpty()) {
                password.setError("Vui lòng nhập mật khẩu");
                return;
            }
            if (database.checkLogin(user, secret)) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
