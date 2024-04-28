package com.example.testapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;

public class LogIn extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPost();
            }
        });
    }

    private void sendPost() {
        // 获取用户输入的用户名和密码
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            // 如果用户名或密码为空，显示错误消息
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }


        // 创建 Executor 以执行任务
        Executor executor = Executors.newSingleThreadExecutor();

        // 使用 CompletableFuture 异步执行后台任务
        CompletableFuture.supplyAsync(() -> {
            try {
                // 服务器 URL
                URL url = new URL("http://120.26.205.81:9000/login");

                // 创建连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);

                // 构建参数字符串
                String params = "user_name=" + username + "&user_password=" + password;

                // 将参数写入请求体
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = params.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // 发送请求并获取响应
                int responseCode = conn.getResponseCode();
                System.out.println("Response Code: " + responseCode);

                // 关闭连接
                conn.disconnect();

                return responseCode == HttpURLConnection.HTTP_OK;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }, executor).thenAccept(success -> {
            // 在 UI 线程上处理结果
            if (success) {
                // POST 请求成功
                runOnUiThread(() -> Toast.makeText(LogIn.this, "POST 请求发送成功", Toast.LENGTH_SHORT).show());
                // 执行任何 UI 更新或导航操作
            } else {
                // POST 请求失败
                runOnUiThread(() -> Toast.makeText(LogIn.this, "发送 POST 请求失败", Toast.LENGTH_SHORT).show());
            }
        });
    }
}