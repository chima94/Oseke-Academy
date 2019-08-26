package com.example.osekeacademy;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseHelperClass {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private List<UserClass> users = new ArrayList<>();

    public FirebaseHelperClass(){
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("users");
    }

    public interface DataStatus{
        void dataIsLoaded(List<UserClass>user, List<String>key);
        void dataIsInserted();
        void dataIsUpdated();
        void dataIsDeleted();
    }

    public void readUser(final DataStatus dataStatus){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                List<String>keys = new ArrayList<>();
                for(DataSnapshot keyNode : dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    UserClass user = keyNode.getValue(UserClass.class);
                    users.add(user);
                }
                dataStatus.dataIsLoaded(users, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void AddUser(UserClass users, String key, final DataStatus dataStatus){
        databaseReference.child(key).setValue(users).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dataStatus.dataIsInserted();
            }
        });
    }
}
