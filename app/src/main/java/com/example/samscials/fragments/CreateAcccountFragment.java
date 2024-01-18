package com.example.samscials.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samscials.FragmentReplacerActivity;
import com.example.samscials.MainActivity;
import com.example.samscials.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreateAcccountFragment extends Fragment {

    private EditText nameEt, passwordEt, emailEt, confirmPasswordEt;
    private Button signUpBtn;
    private TextView logInTv;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    public static final String EMAIL_REGEX = "^(.+)@(.+)$";

    public CreateAcccountFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_acccount, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        innit(view);

        clickListener();

    }

    private void clickListener() {

        logInTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentReplacerActivity)getActivity()).setFragment(new LogInFragment());
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEt.getText().toString();
                String email = emailEt.getText().toString();
                String password = passwordEt.getText().toString();
                String confirmPassword = confirmPasswordEt.getText().toString();

                if (name.isEmpty() || name.equals(" ")){
                    nameEt.setError("Please input valid name");
                    return;
                }
                if (email.isEmpty() || !email.matches(EMAIL_REGEX)){
                    emailEt.setError("Please input valid email");
                    return;
                }
                if (password.isEmpty() || password.length() < 6){
                    passwordEt.setError("Please input valid password");
                    return;
                }
                if (!password.equals(confirmPassword)){
                    passwordEt.setError("Password did not match");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                createAccount(name, email, password);
            }
        });
    }

    private void createAccount(String name, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            FirebaseUser user = auth.getCurrentUser();

                            UserProfileChangeRequest.Builder request = new UserProfileChangeRequest.Builder();
                            request.setDisplayName(name);
                            user.updateProfile(request.build());
                            user.sendEmailVerification()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(getContext(), "Email verification link sent", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                            uploadUser(user, name, email);

                        }else {
                            progressBar.setVisibility(View.GONE);
                            String exception = task.getException().getMessage();
                            Toast.makeText(getContext(), "Error:" +
                                    " "+exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadUser(FirebaseUser user, String name, String email) {

        List<String> list = new ArrayList<>();
        List<String> list1 = new ArrayList<>();

        Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("email", email);
        map.put("profileImage", " ");
        map.put("uid", user.getUid());
        map.put("status", " ");
        map.put("search", name.toLowerCase());

        map.put("following", list);
        map.put("followers", list1);

        FirebaseFirestore.getInstance().collection("Users").document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            progressBar.setVisibility(View.GONE);
                            assert getActivity() != null;
                            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
                            getActivity().finish();
                        }else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error: " +
                                    ""+ task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void innit(View view) {
        nameEt = view.findViewById(R.id.nameEt);
        emailEt = view.findViewById(R.id.emailEt);
        passwordEt = view.findViewById(R.id.passwordEt);
        confirmPasswordEt = view.findViewById(R.id.confirmPasswordEt);
        progressBar = view.findViewById(R.id.progressBar);
        logInTv = view.findViewById(R.id.logInTv);
        signUpBtn = view.findViewById(R.id.SignUpBtn);

        auth = FirebaseAuth.getInstance();

    }
}