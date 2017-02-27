package ru.cardiacare.cardiacare.MainFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.cardiacare.cardiacare.AboutActivity;
import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.user.CreateAccountActivity;
import ru.cardiacare.cardiacare.user.ForgotPasswordActivity;

// Интерфейс для авторизации пользователя
// Один фрагмент

public class FragmentAuthorizationScreen extends Fragment {

    public static final String TAG = "FragmentAuthorizationScreen";

    Button nextButton;
    TextView forgotPassword;
    TextView createAccount;
    EditText etEmail;
    EditText etPassword;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorization_screen, null);
        final Context context = getActivity();

        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etPassword = (EditText) view.findViewById(R.id.etPassword);

        nextButton = (Button) view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                MainActivity.authorization(context, etEmail.getText().toString(), etPassword.getText().toString());
                ((MainActivity) getActivity()).authorization(etEmail.getText().toString(), etPassword.getText().toString());
            }
        });

        forgotPassword = (TextView) view.findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                authorization(etEmail.getText().toString(), etPassword.getText().toString());
                startActivity(new Intent(MainActivity.mContext, ForgotPasswordActivity.class));
            }
        });

        createAccount = (TextView) view.findViewById(R.id.createAccount);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                authorization(etEmail.getText().toString(), etPassword.getText().toString());
                startActivity(new Intent(MainActivity.mContext, CreateAccountActivity.class));
            }
        });

        return view;
    }

    public FragmentAuthorizationScreen() {
    }

}