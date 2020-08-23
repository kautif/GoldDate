package com.example.golddate.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.golddate.HomeActivity;
import com.example.golddate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private EditText loginEmail;
    private EditText loginPw;
    private Button loginBtn;

    private String email;
    private String password;

    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        loginEmail = view.findViewById(R.id.login_email_editText);
        loginPw = view.findViewById(R.id.login_password_editText);
        loginBtn = view.findViewById(R.id.login_button);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Logging in");
        progressDialog.setMessage("Please wait...");

        mAuth = FirebaseAuth.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    email = loginEmail.getText().toString();
                    password = loginPw.getText().toString();


                if (email.isEmpty()) {
                    loginEmail.requestFocus();
                    loginEmail.setError("Enter a valid email address");
                } else {
                    email = loginEmail.getText().toString();
                }

                if (password.isEmpty()) {
                    loginPw.requestFocus();
                    loginPw.setError("Password is not valid");
                } else {
                    password = loginPw.getText().toString();
                }

                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Logging In", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}
