package org.techtown.reviewapp.home;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.techtown.reviewapp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class StatusFragment extends Fragment {
    StatusFragment statusFragment;
    EditText editText;
    TextView textView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReference().child("status_picture");
    Uri file;
    Bitmap image;
    Boolean pictureSelected = false;
    String file_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_status, container, false);
        statusFragment = this;

        editText = rootView.findViewById(R.id.editText);

        Button button = rootView.findViewById(R.id.button); // 취소버튼
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) HomeActivity.mContext).manager.beginTransaction().remove(statusFragment).commit();  // 프래그먼트 자기자신 보이지 않는 법
            }
        });

        Button button2 = rootView.findViewById(R.id.button); // 등록버튼
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView = rootView.findViewById(R.id.textView);
                String text = textView.getText().toString();

                if(pictureSelected == true){
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 1, bytes);
                    String path = MediaStore.Images.Media.insertImage(((HomeActivity)HomeActivity.mContext).getContentResolver(), image, "temp", null);
                    StorageReference ref2 = ref.child(file_name);
                    ref2.putFile(Uri.parse(path));
                } else if(!text.equals("")){

                }
                else{
                    Toast.makeText(((HomeActivity)HomeActivity.mContext),"내용을 입력해주세요.",Toast.LENGTH_LONG).show();
                }
            }
        });

        Button button3 = rootView.findViewById(R.id.button); // 사진첨부 버튼
        button3.setOnClickListener(new View.OnClickListener() {
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
            alreadyUploaded = false;
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
}
