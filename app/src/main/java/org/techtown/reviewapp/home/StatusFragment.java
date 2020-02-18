package org.techtown.reviewapp.home;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.post.AddPostListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class StatusFragment extends Fragment {
    private AddPostListener addPostListener;
    StatusFragment statusFragment;
    EditText editText;
    TextView textView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReference();
    Uri file;
    Bitmap image;
    Boolean pictureSelected = false;
    String file_name;
    Date date;
    //int status_num; //status 개수가 무의미함
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");
    //private DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference().child("SKKU").child("Status");

    ImageView picture_add;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_status, container, false);
        statusFragment = this;

        editText = rootView.findViewById(R.id.editText);

        //reference.addListenerForSingleValueEvent(dataListener);
        /*
        reference2.addChildEventListener(new ChildEventListener(){
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s){
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot,String s){
                if(dataSnapshot.getKey().equals("num")){
                    status_num = Integer.parseInt(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot){

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot,String s){

            }

            @Override
            public void onCancelled(DatabaseError databaseError){

            }
        });
         */
        TextView exit = rootView.findViewById(R.id.exit); // 취소버튼
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().remove(statusFragment).commit();  // 프래그먼트 자기자신 보이지 않는 법
                /*
                HomeFragment.mContext.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){  // 스크롤 재허용
                    @Override
                    public boolean canScrollVertically(){
                        return true;
                    }
                });

                 */
                // 비활성화 되었던 네비게이션 버튼 재활성화
                ((HomeActivity) HomeActivity.mContext).home.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).settings.setEnabled(true);
                ((HomeActivity) HomeActivity.mContext).rank.setEnabled(true);
            }
        });

        TextView submit = rootView.findViewById(R.id.submit); // 등록버튼
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView = rootView.findViewById(R.id.textView);
                String text = editText.getText().toString();
                date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy"+"MM"+"dd"+"HH"+"mm"+"ss"); //status에 제목으로 들어감
                String image_path = "SKKU/status_picture/"+format.format(date) +"_"+ restoreState();
                if(pictureSelected == true){  // 사진 넣었을 때
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 15, bytes);
                    //String path = MediaStore.Images.Media.insertImage(((HomeActivity)HomeActivity.mContext).getContentResolver(), image, "temp", null);
                    //StorageReference ref2 = ref.child(image_path);
                    //ref2.putFile(Uri.parse(path));

                    // 사진 압축률, 앨범에 압축사진 저장 되던 문제 해결
                    byte[] byteArray = bytes.toByteArray();
                    StorageReference ref2 = ref.child(image_path);
                    ref2.putBytes(byteArray);


                    Map<String, Object> childUpdates1 = new HashMap<>();
                    Map<String, Object> postValues = new HashMap<>();
                    Map<String, Object> commentValues = new HashMap<>();
                    commentValues.put("num",0);
                    postValues.put("comments",commentValues);
                    postValues.put("date",format.format(date));
                    postValues.put("id",restoreState());
                    postValues.put("like",0);
                    postValues.put("picture",image_path);
                    postValues.put("restaurant","NO");
                    postValues.put("text",text);
                    postValues.put("user_num",restoreState2());

                    childUpdates1.put("Status/"+format2.format(date) ,postValues);
                    Map<String, Object> postValues2 = new HashMap<>();
                    postValues2.put("temp","temp");
                    postValues.put("who_liked",postValues2);

                    reference.updateChildren(childUpdates1);
                    //reference2.updateChildren(numUpdates);
                    ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().remove(statusFragment).commit();  // 프래그먼트 자기자신 보이지 않는 법
                    if(addPostListener != null) {
                        addPostListener.postAdded();
                    }
                    /*
                    HomeFragment.mContext.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){  // 스크롤 재허용
                        @Override
                        public boolean canScrollVertically(){
                            return true;
                        }
                    });

                     */
                    // 비활성화 되었던 네비게이션 버튼 재활성화
                    ((HomeActivity) HomeActivity.mContext).home.setEnabled(true);
                    ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(true);
                    ((HomeActivity) HomeActivity.mContext).settings.setEnabled(true);
                    ((HomeActivity) HomeActivity.mContext).rank.setEnabled(true);
                } else if(!text.equals("")){  // 글만 썼을 때
                    Map<String, Object> childUpdates1 = new HashMap<>();
                    //Map<String, Object> numUpdates = new HashMap<>();
                    Map<String, Object> postValues = new HashMap<>();
                    Map<String, Object> commentValues = new HashMap<>();
                    commentValues.put("num",0);
                    postValues.put("comments",commentValues);
                    postValues.put("date",format.format(date));
                    postValues.put("id",restoreState());
                    postValues.put("like",0);
                    postValues.put("picture","NO");
                    postValues.put("restaurant","NO");
                    postValues.put("text",text);
                    postValues.put("user_num",restoreState2());

                    //numUpdates.put("num",status_num+1);
                    childUpdates1.put("Status/"+format2.format(date),postValues);
                    Map<String, Object> postValues2 = new HashMap<>();
                    postValues2.put("temp","temp");
                    postValues.put("who_liked",postValues2);

                    reference.updateChildren(childUpdates1);
                    //reference2.updateChildren(numUpdates);
                    ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().remove(statusFragment).commit();  // 프래그먼트 자기자신 보이지 않는 법
                    if(addPostListener != null) {
                        addPostListener.postAdded();
                    }
                    /*
                    HomeFragment.mContext.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()){  // 스크롤 재허용
                        @Override
                        public boolean canScrollVertically(){
                            return true;
                        }
                    });

                     */
                    // 비활성화 되었던 네비게이션 버튼 재활성화
                    ((HomeActivity) HomeActivity.mContext).home.setEnabled(true);
                    ((HomeActivity) HomeActivity.mContext).restaurant.setEnabled(true);
                    ((HomeActivity) HomeActivity.mContext).settings.setEnabled(true);
                    ((HomeActivity) HomeActivity.mContext).rank.setEnabled(true);
                }
                else{  // 글조차 안 썼을 때
                    Toast.makeText(((HomeActivity)HomeActivity.mContext),"내용을 입력해주세요.",Toast.LENGTH_LONG).show();
                }
            }
        });

        picture_add= rootView.findViewById(R.id.picture_add); // 사진첨부 버튼
        picture_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,101);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            file = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = ((HomeActivity)HomeActivity.mContext).getContentResolver().query(file, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
            cursor.close();

            ExifInterface exif = null;
            try{

                exif = new ExifInterface(imagePath);
            } catch(IOException e){

                e.printStackTrace();
            }

            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);
            image = BitmapFactory.decodeFile(imagePath);
            image = rotate(image, exifOrientationToDegrees(orientation));
            picture_add.setImageBitmap(image);
            pictureSelected = true;

        }
    }

    public int exifOrientationToDegrees(int exifOrientation)
    {
        if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_90)
        {
            return 90;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_180)
        {
            return 180;
        }
        else if(exifOrientation == ExifInterface.ORIENTATION_ROTATE_270)
        {
            return 270;
        }
        return 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees)
    {
        if(degrees != 0 && bitmap != null)
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);

            try
            {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if(bitmap != converted)
                {
                    bitmap.recycle();
                    bitmap = converted;
                }
            }
            catch(OutOfMemoryError ex)
            {
                // 메모리가 부족하여 회전을 시키지 못할 경우 그냥 원본을 반환합니다.
            }
        }
        return bitmap;
    }

    protected String restoreState(){
        SharedPreferences pref = ((HomeActivity)HomeActivity.mContext).getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getString("id","");
    }

    protected int restoreState2(){
        SharedPreferences pref = ((HomeActivity)HomeActivity.mContext).getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getInt("user_num",0);
    }

    //status 개수가 무의미해서 주석처리함
    /*
    ValueEventListener dataListener = new ValueEventListener() {  // status 개수 가져오기
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message0 = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_status = (Map<String, Object>) message0.get("Status");
            status_num = Integer.parseInt(message_status.get("num").toString());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof AddPostListener){
            addPostListener= (AddPostListener) context;
        }
    }
}
