package org.techtown.reviewapp.Login_SignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.techtown.reviewapp.R;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    EditText userId, userPw, userPw2, userNickname, userEmail;
    Button idCheckBtn, emailCheckBtn, submitBtn, nicknameCheckBtn;
    String inputId, inputEmail,inputNickname;
    boolean isIdChecked = false;
    boolean isNicknameChecked = false;
    boolean isEmailChecked = false;
    int userNum;
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("SKKU");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userId = (EditText) findViewById(R.id.userId);
        userPw = (EditText) findViewById(R.id.userPw);
        userPw2 = (EditText) findViewById(R.id.userPw2);
        userNickname = (EditText) findViewById(R.id.nickname);
        userEmail = (EditText) findViewById(R.id.userEmail);
        idCheckBtn = (Button) findViewById(R.id.idCheckBtn);
        emailCheckBtn = (Button) findViewById(R.id.emailCheckBtn);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        nicknameCheckBtn = findViewById(R.id.nicknameCheckBtn);

        idCheckBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputId = userId.getText().toString();
                //Toast.makeText(getApplicationContext(), "입력한 아이디는 " + inputId + "입니다.", Toast.LENGTH_LONG).show();
                reference.addListenerForSingleValueEvent(dataListener);
            }
        });

        nicknameCheckBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputNickname = userNickname.getText().toString();
                //Toast.makeText(getApplicationContext(), "입력한 아이디는 " + inputId + "입니다.", Toast.LENGTH_LONG).show();
                reference.addListenerForSingleValueEvent(dataListener3);
            }
        });

        emailCheckBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputEmail = userEmail.getText().toString();
                reference.addListenerForSingleValueEvent(dataListener2);
            }
        });

        submitBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isIdChecked) {
                    Toast.makeText(getApplicationContext(), "아이디 중복 확인을 해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isNicknameChecked) {
                    Toast.makeText(getApplicationContext(), "닉네임 중복 확인을 해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                if(userPw.getText().toString().length() < 8) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 8자 이상으로 해주세요", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!userPw.getText().toString().equals(userPw2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "비밀번호 확인이 일치하지 않습니다", Toast.LENGTH_LONG).show();
                    return;
                }
                if(userNickname.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "닉네임을 입력 해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isEmailChecked) {
                    Toast.makeText(getApplicationContext(), "이메일 중복 확인을 해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                Toast.makeText(getApplicationContext(), "가입 완료", Toast.LENGTH_LONG).show();

                Map<String, Object> childUpdates = new HashMap<>();
                Map<String, Object> postValues = new HashMap<>();

                postValues.put("nickname", inputNickname);
                postValues.put("id", inputId);
                postValues.put("password", userPw.getText().toString());
                postValues.put("email", inputEmail);
                postValues.put("exp",0);
                postValues.put("num_of_reviews",0);
                postValues.put("level",1);
                userNum++;
                childUpdates.put("user/" + userNum, postValues);
                childUpdates.put("user/num",userNum);


                reference.updateChildren(childUpdates);

                finish();
            }
        });

        reference.addListenerForSingleValueEvent(dataListener4);


        //일단 idCheckBtn 리스너를 만들어 준다
        //아이디 값을 받아서 DB에 그 있나 없나 리턴함

        //emailCheckBtn 리스너를 만들어준다
        //이메일을 받아서 DB에 그 이메일이 있나 없나 리턴함

        //submitBtn 리스너를 만들어준다
        //뷰에서 값을 받아옴
        //빈 게 있으면 토스트 메세지 띠우고 포커스를 넣어줌
        //빈 게 없으면
        //DB에 정보 올리고 토스트메시지로 성공 표시하고 액티비티 닫음
    }

    ValueEventListener dataListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message_SKKU = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_user = (Map<String, Object>) message_SKKU.get("user");
            int user_num = Integer.parseInt(message_user.get("num").toString());

            for(int i=1;i<=user_num;i++){
                Map<String, Object> message_i = (Map<String, Object>) message_user.get(Integer.toString(i));
                String id = (String) message_i.get("id");

                if (inputId.equals(id)) {
                    //아이디가 중복됨
                    Toast.makeText(getApplicationContext(), "아이디가 중복됩니다. ", Toast.LENGTH_LONG).show();
                    isIdChecked = false;
                    return;
                }
            }


            if(inputId.equals("")) {
                Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_LONG).show();
                return;
            }
            if(inputId.equals("deleted")||inputId.equals("accepted")) {
                Toast.makeText(getApplicationContext(), "사용할 수 없는 아이디입니다.", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getApplicationContext(), "아이디 중복 확인 완료. ", Toast.LENGTH_LONG).show();
            isIdChecked = true;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message_SKKU = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_user = (Map<String, Object>) message_SKKU.get("user");
            int user_num = Integer.parseInt(message_user.get("num").toString());

            for(int i=1;i<=user_num;i++){
                Map<String, Object> message_i = (Map<String, Object>) message_user.get(Integer.toString(i));
                String email = (String) message_i.get("email");

                if (inputEmail.equals(email)) {
                    //아이디가 중복됨
                    Toast.makeText(getApplicationContext(), "아이디가 중복됩니다. ", Toast.LENGTH_LONG).show();
                    isEmailChecked = false;
                    return;
                }
            }

            Toast.makeText(getApplicationContext(), "이메일 중복 확인 완료. ", Toast.LENGTH_LONG).show();
            isEmailChecked = true;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener3 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message_SKKU = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_user = (Map<String, Object>) message_SKKU.get("user");
            int user_num = Integer.parseInt(message_user.get("num").toString());

            for(int i=1;i<=user_num;i++){
                Map<String, Object> message_i = (Map<String, Object>) message_user.get(Integer.toString(i));
                String nickname = (String) message_i.get("nickname");

                if (inputNickname.equals(nickname)) {
                    //닉네임이 중복됨
                    Toast.makeText(getApplicationContext(), "닉니엠이 중복됩니다. ", Toast.LENGTH_LONG).show();
                    isNicknameChecked = false;
                    return;
                }
            }

            if(inputNickname.equals("")) {
                Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요", Toast.LENGTH_LONG).show();
                return;
            }

            Toast.makeText(getApplicationContext(), "닉네임 중복 확인 완료. ", Toast.LENGTH_LONG).show();
            isNicknameChecked = true;
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    ValueEventListener dataListener4 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Map<String, Object> message_SKKU = (Map<String, Object>) dataSnapshot.getValue();
            Map<String, Object> message_user = (Map<String, Object>) message_SKKU.get("user");
            userNum = Integer.parseInt(message_user.get("num").toString());


        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
}