package pg.grpc.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import pg.grpc.R;
import pg.grpc.data.User;
import pg.grpc.data.UserController;

public class InfoActivity extends AppCompatActivity {

    public static final String ID = "id";

    public static void getInstance(Activity activity, String userId) {
        Intent intent = new Intent(activity, InfoActivity.class);
        intent.putExtra(ID, userId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String id = getIntent().getStringExtra(ID);
        User user = UserController.getById(id);
        if (user == null) {
            Log.e("InfoActivity", "onCreate: User not found" );
            finish();
            return;
        }
        setTitle(user.getFullName());

        ((TextView) findViewById(R.id.idTv)).setText(getString(R.string.id) + ": " + user.getId());
        ((TextView) findViewById(R.id.fNameTv)).setText(getString(R.string.firstname) + ": " + user.getFirstName());
        ((TextView) findViewById(R.id.lNameTv)).setText(getString(R.string.lastname) + ": " + user.getLastName());
        ((TextView) findViewById(R.id.ageTv)).setText(getString(R.string.age) + ": " + user.getAge());
        String gender = getString(R.string.male);
        if (user.getGender() == User.FEMALE) {
            gender = getString(R.string.female);
        }
        ((TextView) findViewById(R.id.genderTv)).setText(getString(R.string.gender) + ": " + gender);
        findViewById(R.id.editBtn).setOnClickListener(v -> EditOrCreateActivity.getInstance(this, EditOrCreateActivity.ACTION_EDIT, user.getId()));
        findViewById(R.id.deleteBtn).setOnClickListener(v -> {
            UserController.deleteUser(user.getId());
            finish();
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