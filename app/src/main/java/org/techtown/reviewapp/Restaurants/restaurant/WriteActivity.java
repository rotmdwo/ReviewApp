package org.techtown.reviewapp.Restaurants.restaurant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;
import org.techtown.reviewapp.Restaurants.Restaurant;
import org.techtown.reviewapp.Restaurants.RestaurantActivity;
import org.techtown.reviewapp.home.HomeActivity;
import org.techtown.reviewapp.post.AddPostListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity {
    //XML 파일 관련 변수
    TextView exit;
    EditText review_text;
    Button submit;
    ImageView picture_add;

    //이미지 업로드 관련 변수
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReference();
    Uri file;
    Bitmap image;
    Boolean pictureSelected = false;

    //DB관련 변수
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");

    //식당
    String restaurantName = ((RestaurantActivity) RestaurantActivity.mContext).name;

    //평점
    ImageView star1, star2, star3, star4, star5;
    Boolean isRated = false;
    int rating = 0;
    int reviewNum;
    float previousRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        // 뒷배경 흐리게 하기
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams();
        layoutParams.flags= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount= 0.3F;
        layoutParams.gravity= Gravity.BOTTOM;
        getWindow().setAttributes(layoutParams);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        //addReviewListener = (AddReviewListener) this;

        // 사이즈 조절
        DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
        int width = (int) (dm.widthPixels * 0.9); // Display 사이즈의 90%
        int height = (int) (dm.heightPixels * 0.8); // Display 사이즈의 90%
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        //xml 연동
        review_text = findViewById(R.id.review_text);
        submit = findViewById(R.id.submit);
        picture_add = findViewById(R.id.picture_add);
        exit = findViewById(R.id.exit);
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);

        //기존 리뷰 수, 평점 가져옴
        Intent intent = getIntent();
        reviewNum = intent.getIntExtra("reviewNum",0);
        previousRating = intent.getFloatExtra("rating",0);

        picture_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,101);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //평점주기
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRated = true;
                rating=1;
                star1.setImageResource(R.drawable.yellowstar);
                star2.setImageResource(R.drawable.emptystar);
                star3.setImageResource(R.drawable.emptystar);
                star4.setImageResource(R.drawable.emptystar);
                star5.setImageResource(R.drawable.emptystar);
            }
        });
        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRated = true;
                rating=2;
                star1.setImageResource(R.drawable.yellowstar);
                star2.setImageResource(R.drawable.yellowstar);
                star3.setImageResource(R.drawable.emptystar);
                star4.setImageResource(R.drawable.emptystar);
                star5.setImageResource(R.drawable.emptystar);
            }
        });
        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRated = true;
                rating=3;
                star1.setImageResource(R.drawable.yellowstar);
                star2.setImageResource(R.drawable.yellowstar);
                star3.setImageResource(R.drawable.yellowstar);
                star4.setImageResource(R.drawable.emptystar);
                star5.setImageResource(R.drawable.emptystar);
            }
        });
        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRated = true;
                rating=4;
                star1.setImageResource(R.drawable.yellowstar);
                star2.setImageResource(R.drawable.yellowstar);
                star3.setImageResource(R.drawable.yellowstar);
                star4.setImageResource(R.drawable.yellowstar);
                star5.setImageResource(R.drawable.emptystar);
            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRated = true;
                rating=5;
                star1.setImageResource(R.drawable.yellowstar);
                star2.setImageResource(R.drawable.yellowstar);
                star3.setImageResource(R.drawable.yellowstar);
                star4.setImageResource(R.drawable.yellowstar);
                star5.setImageResource(R.drawable.yellowstar);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = review_text.getText().toString();
                Date date = new Date();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                SimpleDateFormat format2 = new SimpleDateFormat("yyyy"+"MM"+"dd"+"HH"+"mm"+"ss"); //status에 제목으로 들어감
                String image_path = "SKKU/review_picture/"+format.format(date) +"_"+ restoreState();
                if(isRated == false){
                    Toast.makeText(getApplicationContext(),"평점을 선택해주세요.",Toast.LENGTH_LONG).show();
                }
                else if(pictureSelected == true){  // 사진 넣었을 때
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 15, bytes);

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
                    postValues.put("rating",rating);

                    childUpdates1.put("Review/"+ restaurantName + "/" + format2.format(date), postValues);
                    Map<String, Object> postValues2 = new HashMap<>();
                    postValues2.put("temp","temp");
                    postValues.put("who_liked", postValues2);

                    Map<String, Object> childUpdates2 = new HashMap<>();
                    Map<String, Object> childUpdates3 = new HashMap<>();
                    childUpdates2.put("Restaurants/"+ restaurantName + "/num_of_reviews",reviewNum+1 );
                    childUpdates3.put("Restaurants/"+ restaurantName + "/rating", (reviewNum*previousRating+rating)/(reviewNum+1));

                    reference.updateChildren(childUpdates1);
                    reference.updateChildren(childUpdates2);
                    reference.updateChildren(childUpdates3);
                    setResult(1);
                    finish();

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
                    postValues.put("rating",rating);

                    //numUpdates.put("num",status_num+1);
                    childUpdates1.put("Review/"+ restaurantName + "/" + format2.format(date), postValues);
                    Map<String, Object> postValues2 = new HashMap<>();
                    postValues2.put("temp","temp");
                    postValues.put("who_liked",postValues2);

                    Map<String, Object> childUpdates2 = new HashMap<>();
                    Map<String, Object> childUpdates3 = new HashMap<>();
                    childUpdates2.put("Restaurants/"+ restaurantName + "/num_of_reviews",reviewNum+1 );
                    childUpdates3.put("Restaurants/"+ restaurantName + "/rating", (reviewNum*previousRating+rating)/(reviewNum+1));

                    reference.updateChildren(childUpdates1);
                    reference.updateChildren(childUpdates2);
                    reference.updateChildren(childUpdates3);
                    //reference2.updateChildren(numUpdates);
                    setResult(1);
                    finish();

                }
                else{  // 글조차 안 썼을 때
                    Toast.makeText(getApplicationContext(),"내용을 입력해주세요.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            file = data.getData();
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getApplicationContext().getContentResolver().query(file, filePath, null, null, null);
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
            pictureSelected = true;

            picture_add.setImageBitmap(image);
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
        SharedPreferences pref = this.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getString("id","");
    }

    protected int restoreState2(){
        SharedPreferences pref = this.getSharedPreferences("pref", Activity.MODE_PRIVATE);
        return pref.getInt("user_num",0);
    }
}
