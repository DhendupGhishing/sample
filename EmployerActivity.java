package gcit.edu.jobhut;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class EmployerActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText cid, name, license, email, password, confirm_pass;
    private TextView register;
    FirebaseAuth firebaseAuth;
    private Spinner location;


    private ProgressBar progres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer);
        mAuth = FirebaseAuth.getInstance();
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        cid = (EditText) findViewById(R.id.cid);
        name = (EditText) findViewById(R.id.name);
        license = (EditText) findViewById(R.id.lc);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirm_pass = (EditText) findViewById(R.id.confirm_passE);
        progres = (ProgressBar) findViewById(R.id.progress);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                registerEmp();
                break;
        }

    }

    private void registerEmp() {
        String cid_no = cid.getText().toString().trim();
        String nameE = name.getText().toString().trim();
        String lcE = license.getText().toString().trim();
        String emailE = email.getText().toString().trim();
        String passwordE = password.getText().toString().trim();
        String confirm_passE = confirm_pass.getText().toString().trim();


        if (cid_no.isEmpty()) {
            cid.setError("CID required!");
            cid.requestFocus();
            return;
        } else {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Bhutan_cid");
            Query check = databaseReference.orderByChild("cid").equalTo(cid_no);
            check.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        cid.setError(null);
                        cid.setEnabled(false);
                        String lin = snapshot.child(cid_no).child("license").getValue(String.class);
                        if (nameE.isEmpty()) {
                            name.setError("Name required!");
                            name.requestFocus();
                            return;
                        }
                        if (emailE.isEmpty()) {
                            email.setError("Email required!");
                            email.requestFocus();
                            return;
                        }
                        if (!Patterns.EMAIL_ADDRESS.matcher(emailE).matches()) {
                            email.setError("Please provide valid email");
                            email.requestFocus();
                            return;

                        }

                        if (lcE.isEmpty()) {
                            license.setError("Licence Number required!");
                            license.requestFocus();
                            return;
                        } else if (lin.equals(lcE)) {
                            license.setError(null);
                            license.setEnabled(false);
                            if (passwordE.isEmpty()) {
                                password.setError("Password Required!");
                                password.requestFocus();
                                return;
                            }

                            if (passwordE.length() < 6) {
                                password.setError("Atleast provide Six characters!");
                                password.requestFocus();
                                return;
                            }

                            if (!confirm_passE.equals(passwordE)) {
                                confirm_pass.setError("Password didn't matched!");
                                confirm_pass.requestFocus();
                                return;
                            }
                            progres.setVisibility(View.VISIBLE);
                            mAuth.createUserWithEmailAndPassword(emailE, passwordE).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        gcit.edu.jobhut.User user = new gcit.edu.jobhut.User(cid_no, nameE, lcE, emailE);
                                        FirebaseDatabase.getInstance().getReference("Employer").child(FirebaseAuth.getInstance()
                                                .getCurrentUser().getUid())
                                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(gcit.edu.jobhut.EmployerActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                                    Intent obj = new Intent(getApplicationContext(), gcit.edu.jobhut.MainActivity.class);
                                                    startActivity(obj);
                                                } else {
                                                    Toast.makeText(gcit.edu.jobhut.EmployerActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                                }
                                                progres.setVisibility(View.GONE);
                                            }
                                        });
                                    } else {
                                        Toast.makeText(gcit.edu.jobhut.EmployerActivity.this, "Failed to register! Try again!", Toast.LENGTH_LONG).show();
                                        progres.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            license.setError("Your License Didn't matched");
                            license.requestFocus();
                            return;
                        }

                    } else {
                        cid.setError("CID didn't matched OurDatabase");
                        cid.requestFocus();
                        return;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}















