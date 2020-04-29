package com.example.airdeposit;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {


    ImageView boxes;
    TextView welcomeText;
    Button btnEnter;
    EditText empoloyeeIDText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

         boxes = findViewById(R.id.imgVBoxes);
         welcomeText = findViewById(R.id.tv_Welcome);
         btnEnter = findViewById(R.id.btnEnter);
        empoloyeeIDText = findViewById(R.id.inputEmployeeId);

         playAnimation();


    }


    public void playAnimation(){
        boxes.setY(-1000);
        ObjectAnimator boxesAnimator = ObjectAnimator.ofFloat(boxes,"translationY", 0);
        boxesAnimator.setDuration(2000);
        boxesAnimator.setInterpolator(new BounceInterpolator());

        welcomeText.setAlpha(0.0f);
        ObjectAnimator textAnimator = ObjectAnimator.ofFloat(welcomeText,"alpha", 1f);
        textAnimator.setInterpolator(new AccelerateInterpolator());
        textAnimator.setDuration(1000);


        AnimatorSet mAnimationSet = new AnimatorSet();
        mAnimationSet.play(textAnimator).after(boxesAnimator);
        mAnimationSet.start();

    }


    public void logIn(View view) {
        int employeeID =Integer.parseInt(empoloyeeIDText.getText().toString());

        final ObjectAnimator boxesAnimator = ObjectAnimator.ofFloat(boxes,"rotation", 360);
        boxesAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        boxesAnimator.setRepeatCount(ValueAnimator.INFINITE);
        boxesAnimator.setDuration(1000);
        boxesAnimator.start();

        Firebase.getEmployee(employeeID, new CallbackEmployee() {


            @Override
            public void onCallback(Employee employee) {

                boxesAnimator.cancel();
                ObjectAnimator.ofFloat (boxes, "rotation", 0) .start ();

                if (employee.getEmployeeID() == 0){
                    Toast.makeText(getApplicationContext(), "Invalid id", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(getApplicationContext(), employee.toString(), Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(getApplicationContext(),MainActivity.class);
                    it.putExtra("employee",  employee);
                    startActivity(it);
                }
            }
        });



    }

}
