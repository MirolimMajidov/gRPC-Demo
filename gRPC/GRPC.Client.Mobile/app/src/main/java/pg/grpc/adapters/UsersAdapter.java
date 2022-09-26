package pg.grpc.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pg.grpc.R;
import pg.grpc.activities.EditOrCreateActivity;
import pg.grpc.activities.InfoActivity;
import pg.grpc.data.User;
import pg.grpc.data.UserController;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserHolder> {

    private final Activity activity;
    private final UserListener listener;
    private final ArrayList<User> users;

    public interface UserListener {
        void info(String id);
        void edit(String id);
        void delete(String id);
    }

    public UsersAdapter(Activity activity, ArrayList<User> users, @NonNull UserListener listener) {
        this.activity = activity;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserHolder(activity.getLayoutInflater().inflate(R.layout.user_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder holder, int position) {
        User user = users.get(position);
        holder.tvNum.setText((position + 1) + ".");
        holder.tvFullName.setText(user.getFullName());
        holder.tvAge.setText(String.valueOf(user.getAge()));

        holder.btnInfo.setOnClickListener(v -> listener.info(user.getId()));
        holder.btnEdit.setOnClickListener(v -> listener.edit(user.getId()));
        holder.btnDelete.setOnClickListener(v -> listener.delete(user.getId()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserHolder extends RecyclerView.ViewHolder {
        TextView tvNum;
        TextView tvFullName;
        TextView tvAge;
        Button btnEdit;
        Button btnDelete;
        ImageView btnInfo;
        public UserHolder(@NonNull View itemView) {
            super(itemView);
            tvNum = itemView.findViewById(R.id.numTv);
            tvFullName = itemView.findViewById(R.id.fullNameTv);
            tvAge = itemView.findViewById(R.id.ageTv);
            btnEdit = itemView.findViewById(R.id.editBtn);
            btnDelete = itemView.findViewById(R.id.deleteBtn);
            btnInfo = itemView.findViewById(R.id.infoBtn);
        }
    }
}
