package gcit.edu.jobhut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class  MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView login, forgotPassword;
    private ProgressBar progress;
    private EditText email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.editTextTextEmailAddress);
        password = (EditText) findViewById(R.id.editTextTextPassword);
        progress = (ProgressBar) findViewById(R.id.progress);


        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        forgotPassword = (TextView) findViewById(R.id.forgetPassword);
        forgotPassword.setOnClickListener(this);
    }

    public void jobseeker(View view) {
        Intent job= new Intent(this, jobseekerActivity.class);
        startActivity(job);
    }

    public void employer(View view) {
        Intent employe= new Intent(this,gcit.edu.jobhut.EmployerActivity.class);
        Log.d("Error","this is login");
        startActivity(employe);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                loginUser();
                break;
            case R.id.forgetPassword:
                startActivity(new Intent(this, forgetPassword.class));
                break;


        }
    }

   public void loginUser() {
        String emailL = email.getText().toString().trim();
        String passwordL = password.getText().toString().trim();
        if (emailL.isEmpty()) {
            email.setError("Email Required!");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(emailL).matches()) {
            email.setError("Please Enter Valid Email");
            return;

        }
        if (passwordL.isEmpty()) {
            password.setError("Password is required!");
            return;
        }
        if (passwordL.length() < 6) {
            email.setError("Atleast should have 6 characters");
            return;

        }

        progress.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(emailL, passwordL).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Employer");
                        Query check = databaseReference.orderByChild("email").equalTo(emailL);
                        check.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                   startActivity(new Intent(getApplicationContext(),gcit.edu.jobhut.HomeActivity.class));
                                    progress.setVisibility(View.GONE);
                                }
                                else {
                                    Intent ab=new Intent(gcit.edu.jobhut.MainActivity.this, gcit.edu.jobhut.MainActivityJobseeker.class);
                                    startActivity(ab);
                                    progress.setVisibility(View.GONE);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(gcit.edu.jobhut.MainActivity.this, "Check your email verify your account!", Toast.LENGTH_LONG).show();
                        progress.setVisibility(View.GONE);
                    }

                } else {
                    Toast.makeText(gcit.edu.jobhut.MainActivity.this, "Fail to login! Check your Credentials", Toast.LENGTH_LONG).show();
                    progress.setVisibility(View.GONE);
                }
            }
        });

    }

    public void Admin(View view) {
        startActivity(new Intent(getApplicationContext(),SplashActivity.class));

    }
}




