package pg.grpc.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import Grpc.Client.Mobile.Empty;
import Grpc.Client.Mobile.UserDTO;
import Grpc.Client.Mobile.UserServiceGrpc;
import Grpc.Client.Mobile.Users;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pg.grpc.R;
import pg.grpc.data.User;
import pg.grpc.adapters.UsersAdapter;
import pg.grpc.data.UserController;

public class MainActivity extends AppCompatActivity {

    public RecyclerView rv;
    public ArrayList<User> users = new ArrayList();
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
        Thread thread = new Thread(() -> {
            String host = "https://192.168.100.3";
            int port = 9595;
            ArrayList<User> users = new ArrayList<>();
            try  {
                ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
                UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
                Empty request = Empty.newBuilder().build();
                List<UserDTO> serverUsers = stub.getAll(request).getItemsList();
                if (serverUsers.size() > 0) {
                    for (int i = 0; i < serverUsers.size(); i++) {
                        UserDTO user = serverUsers.get(i);
                        users.add(new User(user.getId(), user.getFirstName(), user.getLastName(), user.getAge(), user.getGender()));
                    }
                }
            } catch(Exception e)
            {
                users.add(new User("TestId", "", e.getMessage(), 0, 1));
            }
            //TODO: users = UserController.getAll();

            runOnUiThread(()->{
                rv.setAdapter(new UsersAdapter(MainActivity.this, users, new UsersAdapter.UserListener() {
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
            });
        });
        thread.start();
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


    private static class GrpcTask extends AsyncTask<Object, Void, ArrayList<User>> {
        private final WeakReference<MainActivity> activityReference;
        private ManagedChannel channel;

        private GrpcTask(MainActivity activity) {
            this.activityReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        protected ArrayList<User> doInBackground(Object[] objects) {
            String host = "https://109.75.61.39/";
            int port = 9595;
            try {
                ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
                UserServiceGrpc.UserServiceBlockingStub stub = UserServiceGrpc.newBlockingStub(channel);
                Empty request = Empty.newBuilder().build();
                List<UserDTO> serverUsers = stub.getAll(request).getItemsList();
                ArrayList<User> users = new ArrayList<>();
                if (serverUsers.size() >0) {
                    for (int i = 0; i < serverUsers.size(); i++) {
                        UserDTO user = serverUsers.get(i);
                        users.add(new User(user.getId(), user.getFirstName(), user.getLastName(), user.getAge(), user.getGender()));
                    }
                }

                return users;
            } catch (Exception e) {
                return  new ArrayList<User>();
            }
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            MainActivity activity = activityReference.get();
            if (activity == null) {
                return;
            }
            //TODO
            activity.rv.setAdapter(new UsersAdapter(activity, users, new UsersAdapter.UserListener() {
                @Override
                public void info(String id) {
                    InfoActivity.getInstance(activity, id);
                }

                @Override
                public void edit(String id) {
                    EditOrCreateActivity.getInstance(activity, EditOrCreateActivity.ACTION_EDIT, id);
                }

                @Override
                public void delete(String id) {
                    if (UserController.deleteUser(id)) {
                        activity.loadUsers();
                    }
                }
            }));
        }

    }
}