package pg.grpc.data;

import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import Grpc.Client.Mobile.Empty;
import Grpc.Client.Mobile.UserDTO;
import Grpc.Client.Mobile.UserServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pg.grpc.App;

public class UserController {

    private static ArrayList<User> users = new ArrayList<>();

    public static void init() {
        String saved = App.getInstance().getPrefString(App.USERS);
        if (saved != null) {
            User[] arr = new Gson().fromJson(saved, User[].class);
            users.addAll(Arrays.asList(arr));
        }
    }

    public static ArrayList<User> getTestUsers() {
        ArrayList<User> users = new ArrayList<>();
       if (users.size() == 0) {
            Random rnd = new Random(1);
            for (int i = 1; i <= 10; i++) {
                users.add(new User(UUID.randomUUID().toString(), "User", "Userov " + i, rnd.nextInt(100), User.MALE));
            }
            save(users);
        }
        return users;
    }

    public static User getById(String userId) {
        for (User u : users) {
            if (u.getId().equals(userId)) {
                return u;
            }
        }
        return null;
    }


    public static String createUser(String firstName, String lastName, int age, int gender) {
        User user = new User(UUID.randomUUID().toString(), firstName, lastName, age, gender);
        users.add(user);
        save(users);
        return user.getId();
    }

    private static void save(ArrayList<User> users) {
        String json = new Gson().toJson(users);
        App.getInstance().putPrefString(App.USERS, json);
    }

    public static boolean updateUser(String userId, String firstName, String lastName, int age, int gender) {
        User user = getById(userId);
        if (user == null) {
            return false;
        } else {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setAge(age);
            user.setGender(gender);
            save(users);
            return true;
        }
    }

    public static boolean deleteUser(String userId) {
        User user = getById(userId);
        if (user == null) {
            return false;
        } else {
            users.remove(user);
            save(users);
            return true;
        }
    }
}