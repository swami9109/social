package com.example.samscials.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.samscials.R;
import com.example.samscials.model.HomeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    private List<HomeModel> list;
    Context context;
    OnPressed onPressed;
    public HomeAdapter(Context context, List<HomeModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext())).inflate(R.layout.home_items, parent, false);
        return new HomeHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHolder holder, int position) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        holder.userNameTv.setText(list.get(position).getName());
        holder.timeTv.setText(""+list.get(position).getTimeStamp());

        List<String> likeList = list.get(position).getLikes();
        int count = likeList.size();

        if (count == 0){
            holder.likeCountTv.setText("0 like");
        } else if (count == 1) {
            holder.likeCountTv.setText(count + " like");
        }else {
            holder.likeCountTv.setText(count + " likes");
        }
        if (likeList.contains(user.getUid())){
            holder.likeCheckBox.setChecked(true);
        }else {
            holder.likeCheckBox.setChecked(false);
        }

        holder.descriptionTv.setText(list.get(position).getDescription());

        Random random = new Random();

        int color = Color.argb(255, random.nextInt(256),random.nextInt(256),random.nextInt(256));

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getProfileImage())
                .placeholder(R.drawable.profile)
                .timeout(6500)
                .into(holder.profileImage);

        Glide.with(context.getApplicationContext())
                .load(list.get(position).getImageUrl())
                .placeholder(new ColorDrawable(color))
                .timeout(7000)
                .into(holder.imageView);

        holder.clickListener(position,
                list.get(position).getId(),
                list.get(position).getName(),
                list.get(position).getUid(),
                list.get(position).getLikes());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class HomeHolder extends RecyclerView.ViewHolder{
        private CircleImageView profileImage;
        private TextView userNameTv, timeTv, likeCountTv, descriptionTv;
        private ImageView imageView;
        private CheckBox likeCheckBox;
        private ImageButton  commentBtn, shareBtn, commentSendBtn;
        private EditText commentEt;
        LinearLayout commentLayout;
        public HomeHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profileImage);
            userNameTv = itemView.findViewById(R.id.userNameTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            likeCountTv = itemView.findViewById(R.id.likes);
            imageView = itemView.findViewById(R.id.imageView);
            likeCheckBox = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            descriptionTv = itemView.findViewById(R.id.descTv);
            commentEt = itemView.findViewById(R.id.commentEt);
            commentSendBtn = itemView.findViewById(R.id.commentSendBtn);
            commentLayout = itemView.findViewById(R.id.commentLayout);

        }
        public void clickListener(final int position, String id, String name, String uid, List<String> likes) {
            likeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    onPressed.onLiked(position, id, uid, likes, isChecked);
                }
            });

            commentEt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commentLayout.getVisibility() == View.GONE){
                        commentLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

            commentSendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comment = commentEt.getText().toString();
                    onPressed.onComment(position, id, uid, comment, commentLayout, commentEt);
                }
            });
        }
    }
    public interface OnPressed{
        void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked);
        void onComment(int position, String id, String uid, String comment, LinearLayout commentLayout, EditText commentEt);
    }
    public void OnPressed(OnPressed onPressed){
        this.onPressed = onPressed;
    }

}
