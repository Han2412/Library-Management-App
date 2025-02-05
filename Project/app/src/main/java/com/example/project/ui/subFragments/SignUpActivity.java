package com.example.project.ui.subFragments;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Printer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project.DataManager;
import com.example.project.R;
import com.example.project.entities.User;
import com.example.project.network.SocketEventListener;
import com.example.project.network.WebSocketClient;
import com.example.project.network.WebSocketResponseListener;
import com.example.project.ui.LoginActivity;
import com.example.project.utils.Constants;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NUMBERS = "0123456789";

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private static final SecureRandom random = new SecureRandom();

    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextOtp;

    private EditText editTextConfirmPassword;

    private String currentOtp;
    private String currentEmail;

    private Boolean resultCheck = new Boolean(true);

    boolean isPasswordVisible = false;
    boolean isPasswordVisible1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        editTextUsername = findViewById(R.id.editUsername);
        editTextEmail = findViewById(R.id.editMail);
        editTextPassword = findViewById(R.id.editPassword);
        editTextOtp = findViewById(R.id.editOtp);
        editTextConfirmPassword = findViewById((R.id.editConfirmPassword));

        findViewById(R.id.btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = generateRandomString(10);
                String name = ""; // Assuming name is not required during signup
                String username = editTextUsername.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String otp = editTextOtp.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                if (resultCheck) {
                    Toast.makeText(SignUpActivity.this, "Username đã tồn tại!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!confirmPassword.equals((password))){
                    Toast.makeText(SignUpActivity.this, "Mật khẩu xác nhận không trùng khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(otp.length()<6){
                    Toast.makeText(SignUpActivity.this, "Vui lòng nhập mã OTP đủ 6 số!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!otp.equals(currentOtp)||!email.equals(currentEmail)){
                    Toast.makeText(SignUpActivity.this, "Mã OTP không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // Create a new User object with the collected user data
                User user = new User(id, name, username, password, email);

                // Convert the User object to JSON
                Gson gson = new Gson();
                String userJson = gson.toJson(user);

                // Create a JSON object to send to the server
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("event", Constants.EVENT_ADD_USER); // Assuming this is the correct event name for adding a user
                    jsonObject.put("user", userJson); // Include the user data in the request
                    String message = jsonObject.toString();

                    // Send the message via WebSocket to the server
                    WebSocketClient.getInstance().send(message);

                    // Inform the user about successful registration
                    Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                    // Redirect the user to the login activity
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.sendOtp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentOtp = generateRandomOTP(6);
                currentEmail = editTextEmail.getText().toString().trim();
                if(!validateEmail(currentEmail)){
                    Toast.makeText(SignUpActivity.this, "Định dạng email không đúng!", Toast.LENGTH_SHORT).show();
                    return;
                }
                startCountDown();
                EmailSender.sendOTP(currentEmail, currentOtp, SignUpActivity.this);
            }
        });
        editTextUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String username = editTextUsername.getText().toString().trim();
                    checkUserName(username);
                }
            }
        });

        ImageView iconEye = findViewById(R.id.icon_eye);
        ImageView iconEye1 = findViewById(R.id.icon_eye1);
        iconEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đảo ngược trạng thái hiện tại của mật khẩu (ẩn thành hiện và ngược lại)
                isPasswordVisible = !isPasswordVisible;
                EditText editPassword = (EditText)findViewById(R.id.editPassword);

                // Thay đổi loại dữ liệu đầu vào của EditText
                if (isPasswordVisible) {
                    // Nếu mật khẩu đang ẩn, hiển thị văn bản
                    iconEye.setImageResource(R.drawable.icon_eye);
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // Nếu mật khẩu đang hiển thị, ẩn văn bản
                    iconEye.setImageResource(R.drawable.icon_eye1);
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Di chuyển con trỏ văn bản về cuối chuỗi
                editPassword.setSelection(editPassword.getText().length());
            }
        });

        iconEye1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Đảo ngược trạng thái hiện tại của mật khẩu (ẩn thành hiện và ngược lại)
                isPasswordVisible1 = !isPasswordVisible1;
                EditText editPassword = (EditText)findViewById(R.id.editConfirmPassword);

                // Thay đổi loại dữ liệu đầu vào của EditText
                if (isPasswordVisible1) {
                    // Nếu mật khẩu đang ẩn, hiển thị văn bản
                    iconEye1.setImageResource(R.drawable.icon_eye);
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                } else {
                    // Nếu mật khẩu đang hiển thị, ẩn văn bản
                    iconEye1.setImageResource(R.drawable.icon_eye1);
                    editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                // Di chuyển con trỏ văn bản về cuối chuỗi
                editPassword.setSelection(editPassword.getText().length());
            }
        });
    }
    private static String generateRandomString(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }

    private static String generateRandomOTP(int length) {
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(NUMBERS.length());
            char randomChar = NUMBERS.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
    private void startCountDown() {
        TextView sendOtpTextView = findViewById(R.id.sendOtp);
        sendOtpTextView.setEnabled(false);

        new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Cập nhật văn bản của TextView để hiển thị đếm ngược
                sendOtpTextView.setText("Gửi lại sau " + millisUntilFinished / 1000 + " giây");
            }

            @Override
            public void onFinish() {
                // Đếm ngược kết thúc, cập nhật văn bản và kích hoạt lại TextView
                sendOtpTextView.setText("Send");
                sendOtpTextView.setEnabled(true);
            }
        }.start();

        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                currentOtp = generateRandomOTP(6);
            }
        }.start();
    }
    public boolean validateEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void checkUserName(String userName) {
        Gson gson = new Gson();
        String userJson = gson.toJson(userName);
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("event", Constants.EVENT_CHECK_USERNAME);
            jsonObject.put("username", userName);
            String message = jsonObject.toString();

            // Gửi tin nhắn và gắn listener
            WebSocketClient.getInstance().send(message, new WebSocketResponseListener() {
                @Override
                public void checkUserNameResponse(String data) {
                    // Xử lý phản hồi từ máy chủ
                    if(data.equals("null")){
                        resultCheck = false;
                    }
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


}