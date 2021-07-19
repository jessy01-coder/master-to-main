package com.sanj.nyaladairy.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.sanj.nyaladairy.R;
import com.sanj.nyaladairy.data.URLs;
import com.sanj.nyaladairy.wrapper.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUp extends AppCompatActivity {
    private TextInputEditText edFirstName, edSecondName, edIdNo, edPhone, edPassword, edConfirmPassword;
    private Spinner routeSpinner;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        mContext = this;
        edFirstName = findViewById(R.id.edFirstName);
        edSecondName = findViewById(R.id.edSecondName);
        edIdNo = findViewById(R.id.edIdNo);
        edPhone = findViewById(R.id.edPhone);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        routeSpinner = findViewById(R.id.routeSpinner);
        Button btnSignUp = findViewById(R.id.btnSignUp);
        TextView txtToSignIn = findViewById(R.id.txtToSignIn);

        txtToSignIn.setOnClickListener(v -> finish());
        btnSignUp.setOnClickListener(v -> register());
    }

    private void register() {
        AlertDialog waitingDialog = new Helper().waitingDialog("Creating Agent Account", mContext);
        Runnable registrationThread = () -> {
            if (!(TextUtils.isEmpty(Objects.requireNonNull(edFirstName.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edSecondName.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edIdNo.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edPhone.getText()).toString().trim())
                    || TextUtils.isEmpty(Objects.requireNonNull(edPassword.getText()).toString().trim()) || TextUtils.isEmpty(Objects.requireNonNull(edConfirmPassword.getText()).toString().trim()))) {
                if (edPassword.getText().toString().trim().equals(edConfirmPassword.getText().toString().trim())) {
                    runOnUiThread(waitingDialog::show);
                    String[] routes = getResources().getStringArray(R.array.routes);
                    String selectedRoute = routes[(int) routeSpinner.getSelectedItemId()];
                    String name = edFirstName.getText().toString().trim() + " " + edSecondName.getText().toString().trim();
                    String idNo = edIdNo.getText().toString().trim();
                    String phone = edPhone.getText().toString().trim();
                    String password = edPassword.getText().toString().trim();

                    HashMap<String, String> params = new HashMap<>();
                    params.put("nid", idNo);
                    params.put("password", password);
                    params.put("name", name);
                    params.put("phone", phone);
                    params.put("route", selectedRoute);

                    StringRequest request = new StringRequest(Request.Method.POST, URLs.createAccountUrl, response -> {
                        try {
                            JSONObject responseObject = new JSONObject(response);
                            String responseCode = responseObject.getString("responseCode");
                            String responseMessage = responseObject.getString("responseMessage");

                            if (responseCode.equals("1")) {
                                runOnUiThread(() -> new Helper().successToast(responseMessage, mContext));
                                finish();
                            } else {
                                runOnUiThread(() -> new Helper().messageDialog(responseMessage, mContext));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            runOnUiThread(() -> {
                                waitingDialog.dismiss();
                                new Helper().messageDialog("Sorry an internal error occurred please try again later\n" + e.getMessage() + response, mContext);
                            });
                        }
                        runOnUiThread(waitingDialog::dismiss);
                    }, error -> runOnUiThread(() -> {
                        waitingDialog.dismiss();
                        new Helper().messageDialog("Sorry failed to connect to server please check your internet connection and try again later\n" + error.getMessage(), mContext);
                    })) {
                        @Override
                        protected Map<String, String> getParams() {
                            return params;
                        }
                    };
                    Volley.newRequestQueue(mContext).add(request);

                } else {
                    runOnUiThread(() -> new Helper().errorToast("Passwords provided do not match!", mContext));
                }
            } else {
                runOnUiThread(() -> new Helper().errorToast("Incomplete registration form!", mContext));
            }
        };
        new Thread(registrationThread).start();
    }
}