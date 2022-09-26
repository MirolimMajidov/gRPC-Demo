package pg.grpc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pg.grpc.R;
import pg.grpc.data.User;
import pg.grpc.data.UserController;

public class EditOrCreateActivity extends AppCompatActivity {

    public static final String ID = "id";
    public static final String ACTION = "action";
    public static final String ACTION_EDIT = "action_edit";
    public static final String ACTION_CREATE = "action_create";

    public static void getInstance(Activity activity, String action, String userId) {
        Intent intent = new Intent(activity, EditOrCreateActivity.class);
        intent.putExtra(ACTION, action);
        intent.putExtra(ID, userId);
        activity.startActivity(intent);
    }

    private String id = null;
    private int gender = User.MALE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        EditText nameEdt = findViewById(R.id.fNameEdt);
        EditText lNameEdt = findViewById(R.id.lNameEdt);
        EditText ageEdt = findViewById(R.id.ageEdt);
        Spinner genderSpin = findViewById(R.id.genderSpin);
        genderSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    gender = User.MALE;
                } else  {
                    gender = User.FEMALE;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        String action = getIntent().getStringExtra(ACTION);
        if (ACTION_EDIT.equals(action)) {
            setTitle(getString(R.string.edit_user));
            id = getIntent().getStringExtra(ID);
            User user = UserController.getById(id);
            if (user == null) {
                Log.e("EditActivity", "onCreate: User not found");
                finish();
                return;
            }

            nameEdt.setText(user.getFirstName());
            lNameEdt.setText(user.getLastName());
            ageEdt.setText(String.valueOf(user.getAge()));
            gender = user.getGender();
            if (user.getGender() == User.FEMALE) {
                genderSpin.setSelection(1);
            }
        } else {
            setTitle(getString(R.string.create_user));
        }

        findViewById(R.id.saveBtn).setOnClickListener(v -> {
            String name = nameEdt.getText().toString().trim();
            String lName = lNameEdt.getText().toString().trim();
            String age = ageEdt.getText().toString().trim();
            if (name.length() > 0 && lName.length() > 0 && age.length() > 0) {
                if (ACTION_EDIT.equals(action)) {
                    UserController.updateUser(id, name, lName, Integer.parseInt(age), gender);
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_LONG).show();
                } else {
                    UserController.createUser(name, lName, Integer.parseInt(age), gender);
                    Toast.makeText(this, getString(R.string.created), Toast.LENGTH_LONG).show();
                }
                finish();
            } else {
                Toast.makeText(this, getString(R.string.fill), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}