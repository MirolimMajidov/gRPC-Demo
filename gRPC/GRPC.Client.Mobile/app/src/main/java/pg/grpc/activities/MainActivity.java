package pg.grpc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import pg.grpc.R;
import pg.grpc.data.User;
import pg.grpc.adapters.UsersAdapter;
import pg.grpc.data.UserController;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ArrayList<User> users = new ArrayList();
    private ProgressBar progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress = findViewById(R.id.mainProgress);

        rv = findViewById(R.id.usersRv);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadUsers() {
        users = UserController.getAll();
        rv.setAdapter(new UsersAdapter(this, users, new UsersAdapter.UserListener() {
            @Override
            public void info(String id) {
                InfoActivity.getInstance(MainActivity.this, id);
            }

            @Override
            public void edit(String id) {
                EditOrCreateActivity.getInstance(MainActivity.this, EditOrCreateActivity.ACTION_EDIT, id);
            }

            @Override
            public void delete(String id) {
                if (UserController.deleteUser(id)) {
                    loadUsers();
                }
            }
        }));
        progress.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, R.string.add).setIcon(R.drawable.ic_baseline_add_24)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            EditOrCreateActivity.getInstance(this, EditOrCreateActivity.ACTION_CREATE, null);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (rv != null) {
            loadUsers();
        }
    }
}