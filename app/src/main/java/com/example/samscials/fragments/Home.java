package com.example.samscials.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.samscials.R;
import com.example.samscials.adapter.HomeAdapter;
import com.example.samscials.model.HomeModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {
    private RecyclerView recyclerView;
    HomeAdapter adapter;
    private List<HomeModel> list;
    private FirebaseUser user;
    public Home() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        innit(view);

        list = new ArrayList<>();
        adapter = new HomeAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        
        loadDataFromFireStore();

        adapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {

                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);

                if (likeList.contains(user.getUid())){
                    likeList.remove(user.getUid());
                }else {
                    likeList.add(user.getUid());
                }

                Map<String, Object> map = new HashMap<>();
                map.put("likes", likeList);

                reference.update(map);
            }
            @Override
            public void onComment(int position, String id, String uid, String comment, LinearLayout commentLayout, EditText commentEt) {

                if (comment.isEmpty() || comment.equals(" ")){
                    Toast.makeText(getContext(), "You cannot send an " +
                            "empty comment", Toast.LENGTH_SHORT).show();
                    return;
                }

                CollectionReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id)
                        .collection("Comments");

                String commentID = reference.document().getId();

                Map<String, Object> map = new HashMap<>();
                map.put("uid", user.getUid());
                map.put("comment", comment);
                map.put("commentID", commentID);
                map.put("postID", id);

                reference.document(commentID)
                        .set(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    commentEt.setText("");
                                    commentLayout.setVisibility(View.GONE);
                                }else {
                                    Toast.makeText(getContext(),
                                            "Failed to connect: "
                                            +task.getException().getMessage()
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void loadDataFromFireStore() {

        final DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(user.getUid());

        final CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users");

        reference.addSnapshotListener((value, error) -> {
            if (error != null){
                Log.d("Error: " , error.getMessage());
                return;
            }
            if (value == null)return;

            List<String> uidList = (List<String>) value.get("following");

            if (uidList == null || uidList.isEmpty())return;

            collectionReference.whereIn("uid", uidList)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null){
                                Log.d("Error: ", error.getMessage());
                            }

                            if (value == null)return;

                            for (QueryDocumentSnapshot snapshot: value){

                                snapshot.getReference().collection("Post Images")
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                                if (error != null){
                                                    Log.d("Error: ", error.getMessage());
                                                }
                                                list.clear();
                                                for (QueryDocumentSnapshot snapshot: value){

                                                    if (!snapshot.exists())return;

                                                    HomeModel model = snapshot.toObject(HomeModel.class);

                                                    System.out.println(model.getName());
                                                    list.add(new HomeModel(
                                                            model.getName(),
                                                            model.getProfileImage(),
                                                            model.getImageUrl(),
                                                            model.getUid(),
                                                            model.getComments(),
                                                            model.getDescription(),
                                                            model.getId(),
                                                            model.getTimeStamp(),
                                                            model.getLikes()
                                                    ));
                                                }
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                            }
                        }
                    });
        });
    }

    private void innit(View view) {

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null)
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }
}