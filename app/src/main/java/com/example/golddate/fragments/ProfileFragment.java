package com.example.golddate.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.golddate.HomeActivity;
import com.example.golddate.MainActivity;
import com.example.golddate.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {
    CircleImageView mCircleImage;
    Uri url = null;
    String img_url;
    String imageName;
    Button saveBtn;

    EditText mInterest;
    EditText mLang;
    EditText mDesc;

    ChipGroup mInterestChipGroup;
    ChipGroup mLangChipGroup;
    List<String> mInterestChipList;
    List<String> mLangChipList;

    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    FirebaseStorage mStorage;
    StorageReference mStorageRef;

    Button logoutBtn;

    public ProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mCircleImage = view.findViewById(R.id.profile_circleImageView);
        final int PICK_IMAGE = 1111;
        mInterest = view.findViewById(R.id.profile_interest_editText);
        mLang = view.findViewById(R.id.profile_lang_editText);
        mDesc = view.findViewById(R.id.profile_desc_editText);
        mInterestChipGroup = view.findViewById(R.id.profile_chipGroup);
        mLangChipGroup = view.findViewById(R.id.lang_chipGroup);
        mInterestChipList = new ArrayList<>();
        mLangChipList = new ArrayList<>();

        saveBtn = view.findViewById(R.id.save_button);
        logoutBtn = view.findViewById(R.id.logout_button);

        getProfile();

        displayInterestChips(mInterestChipList);

        mCircleImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        mInterest.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_GO) {
                    mInterestChipList.add(mInterest.getText().toString());
                    displayInterestChips(mInterestChipList);
                    mInterest.setText("");
                    return true;
                }
                return false;
            }
        });

        mLang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                if (actionID == EditorInfo.IME_ACTION_GO) {
                    mLangChipList.add(mLang.getText().toString());
                    displayLangChips(mLangChipList);
                    mLang.setText("");
                    return true;
                }
                return false;
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.i("Save Button", "onClick: " + mAuth.getCurrentUser().getUid());
//                Log.i("imageName", "onClick: " + imageName);
//                Log.i("URL", "onClick: " + url.getPath().substring(url.getPath().lastIndexOf("/") + 1));
//                imageName = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);

                if (imageName == null || url == null) {
                    mStorageRef.child(mAuth.getCurrentUser().getUid()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            Map<String, Object> textMap = new HashMap<>();
                            textMap.put("interest", mInterestChipList);
                            textMap.put("lang", mLangChipList);
                            textMap.put("desc", mDesc.getText().toString());
                            mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).update(textMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    mStorageRef.child(mAuth.getCurrentUser().getUid()).child(imageName).putFile(url).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String downloadURL = taskSnapshot.getStorage().getDownloadUrl().toString();
                            Task<Uri> response = taskSnapshot.getStorage().getDownloadUrl();
                            response.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadURL = uri.toString();
                                    Map<String, Object> textMap = new HashMap<>();
                                    textMap.put("interest", mInterestChipList);
                                    textMap.put("lang", mLangChipList);
                                    textMap.put("desc", mDesc.getText().toString());
                                    textMap.put("img_url", downloadURL);
                                    mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).update(textMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

        return view;
    }

    private void getProfile() {
        mStore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String desc = task.getResult().getString("desc");
                    List<String> interest = (List<String>) task.getResult().get("interest");
                    Log.i("ProfileFragment", "onComplete: " + task.getResult().getString("img_url"));
                    try {
                        url = Uri.parse(task.getResult().getString("img_url"));
                    } catch (NullPointerException e) {
                        url = Uri.parse("https://www.dovercourt.org/wp-content/uploads/2019/11/610-6104451_image-placeholder-png-user-profile-placeholder-image-png.jpg");
                    }

//                    if (img_url == null) {
////                        img_url = "https://www.dovercourt.org/wp-content/uploads/2019/11/610-6104451_image-placeholder-png-user-profile-placeholder-image-png.jpg";
//                        url = Uri.parse("https://www.dovercourt.org/wp-content/uploads/2019/11/610-6104451_image-placeholder-png-user-profile-placeholder-image-png.jpg");
//                    }
                    if (img_url != null) {
                        img_url = task.getResult().getString("img_url");
                        url = Uri.parse(img_url);
//                        Log.i("img_url was null", "onComplete: " + img_url);
                    }
                    List<String> lang = (List<String>) task.getResult().get("lang");
//                    Log.i("Profile", "onComplete: " + interest);
                    mDesc.setText(desc);
                    Glide.with(getContext()).load(url).into(mCircleImage);
                    mInterestChipList.clear();
                    if (interest != null) {
                        for (String item : interest) {
                            mInterestChipList.add(item);
                        }
                        displayInterestChips(mInterestChipList);
                    }

                    if (lang != null) {
                        for (String item : lang) {
                            mLangChipList.add(item);
                        }
                        displayLangChips(mLangChipList);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            final Uri imageURI = data.getData();
            url = imageURI;
//            Log.i("Image path", String.valueOf(url.getPath().lastIndexOf("/")));
//            Log.i("Image path", url.getPath().substring(url.getPath().lastIndexOf("/") + 1));
            imageName = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
            Glide.with(this).load(imageURI).into(mCircleImage);
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        } else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayInterestChips(final List<String> mChipList) {
        mInterestChipGroup.removeAllViews();
        for (String c : mChipList) {
            Chip chip = (Chip) getActivity().getLayoutInflater().inflate(R.layout.single_chip_item, null, false);
            chip.setText(c);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mInterestChipGroup.removeView(view);
                    Chip targetChip = (Chip) view;
                    mChipList.remove(targetChip.getText().toString());
                }
            });

            mInterestChipGroup.addView(chip);
        }
    }

    private void displayLangChips(final List<String> mChipList) {
        mLangChipGroup.removeAllViews();
        for (String c : mChipList) {
            Chip chip = (Chip) getActivity().getLayoutInflater().inflate(R.layout.single_chip_item, null, false);
            chip.setText(c);

            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLangChipGroup.removeView(view);
                    Chip targetChip = (Chip) view;
                    mChipList.remove(targetChip.getText().toString());
                }
            });
            mLangChipGroup.addView(chip);
        }
    }

}
