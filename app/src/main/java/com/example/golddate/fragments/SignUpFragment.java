package com.example.golddate.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.golddate.HomeActivity;
import com.example.golddate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {

    private String TAG = "SignUpFragment";

    private EditText signupNameView;
    private EditText signupEmailView;
    private EditText signupPwView;
    private Button dobBtn;
    private TextView selectedAgeTextView;
    private Button signupBtn;

    private String name;
    private String email;
    private String password;
    private String age;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mStore;
    private Map<String, Object> map;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        map = new HashMap<>();

        signupNameView = view.findViewById(R.id.signup_name_editText);
        signupEmailView = view.findViewById(R.id.signup_email_editText);
        signupPwView = view.findViewById(R.id.signup_password_editText);
        dobBtn = view.findViewById(R.id.dob_button);
        selectedAgeTextView = view.findViewById(R.id.selected_age_textView);
        signupBtn = view.findViewById(R.id.signup_button);

        Calendar calendar = Calendar.getInstance();
        final int todayYear = calendar.get(Calendar.YEAR);
        final int todayMonth = calendar.get(Calendar.MONTH);
        final int todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        dobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        age = getAge(year, month, day);
                        selectedAgeTextView.setText(age);
                        selectedAgeTextView.setVisibility(View.VISIBLE);
                        selectedYear = year;
                        selectedMonth = month;
                        selectedDay = day;

                        Log.i(TAG, "year: " + year);
                        Log.i(TAG, "month: " + month);
                        Log.i(TAG, "day: " + day);
                    }
                }, todayYear, todayMonth, todayDay).show();
            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = signupNameView.getText().toString();
                if (signupEmailView.getText().toString().isEmpty()) {
                    signupEmailView.requestFocus();
                    signupEmailView.setError("Must enter a proper email address");
                } else {
                    email = signupEmailView.getText().toString();
                }

                if (signupPwView.getText().toString().isEmpty() || signupPwView.getText().toString().length() < 5) {
                    signupPwView.requestFocus();
                    signupPwView.setError("Enter a password of at least 5 characters");
                } else {
                    password = signupPwView.getText().toString();
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    map.put("name", signupNameView.getText().toString());
                                    map.put("birth_year", selectedYear);
                                    map.put("birth_month", selectedMonth);
                                    map.put("birth_day", selectedDay);
                                    map.put("age", age);
                                    mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Authentication Successful.", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getContext(), HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        }
                                    });
//                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getContext(), "Authentication Failed.",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }
                            }
                        });
            }
        });
        return view;
    }

    private String getAge(int year, int month, int day){
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)){
            age--;
        }

        Integer ageInt = new Integer(age);
        String ageS = ageInt.toString();

        return ageS;
    }

}
