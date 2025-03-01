package com.sagiziv.connectx.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sagiziv.connectx.Grid;
import com.sagiziv.connectx.R;
import com.sagiziv.connectx.dto.Position;
import com.sagiziv.connectx.dto.PositionedComponent;
import com.sagiziv.connectx.dto.Size;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private View view;
    private Grid grid;
    private PositionedComponent[] components;
    int index = 0;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            (ActivityResult result) -> {
                startActivity(new Intent(this, HomeScreenActivity.class));
                finish();
            });

    private void startLoginMethod() {
        Log.d("pttt", "startLoginMethod");
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build(),
                        new AuthUI.IdpConfig.EmailBuilder().build()
                ))
                .setLogo(R.drawable.ic_app_icon)
                .setTheme(R.style.Theme_ConnectX)
                .setIsSmartLockEnabled(false)
                .build();

        signInLauncher.launch(signInIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(this, HomeScreenActivity.class));
            finish();
        } else {
            startLoginMethod();
        }
//        Button button = findViewById(R.id.main_BTN);
//
////        LinearLayoutCompat layout = findViewById(R.id.main_LAY_layout);
////        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
////        layout.getLayoutTransition().setDuration(LayoutTransition.CHANGING, 1500);
//        LinearLayoutCompat layout1 = findViewById(R.id.main);
//        layout1.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
////        layout1.getLayoutTransition().setDuration(LayoutTransition.CHANGING, 1000);
//        MaterialCardView layout2 = findViewById(R.id.main_TEMP);
//        layout2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
////        layout2.getLayoutTransition().setDuration(LayoutTransition.CHANGING, 1000);
//        View collapsable = findViewById(R.id.main_details);
//
//
//        button.setOnClickListener(v1 -> {
//            int v = (collapsable.getVisibility() == View.GONE) ? View.VISIBLE : View.GONE;
////            TransitionManager.beginDelayedTransition(layout, new AutoTransition());
//            collapsable.setVisibility(v);
//        });

//        final Display display = getWindowManager().getDefaultDisplay();
//        Point outPoint = new Point();
//        if (Build.VERSION.SDK_INT >= 19) {
//            // include navigation bar
//            display.getRealSize(outPoint);
//        } else {
//            // exclude navigation bar
//            display.getSize(outPoint);
//        }
//        Log.d("pttt", "(" + outPoint.x + ", " + outPoint.y + ")");
//        Log.d("pttt", "(" + getScreenWidth() + ", " + getScreenHeight() + ")");
////        ViewGroup.LayoutParams params = layout.getLayoutParams();
//        Log.d("pttt", "(" + layout.getWidth() + ", " + layout.getHeight() + ")");

//        View layout = findViewById(R.id.main_layout);
//        view = findViewById(R.id.main_VIEW_back);
//
//        components = new PositionedComponent[]{
//                new PositionedComponent(new Size(2, 2), new Position(0, 0), "123"),
//                new PositionedComponent(new Size(1, 1), new Position(3, 0), "124"),
//                new PositionedComponent(new Size(1, 2), new Position(0, 4), "125"),
//                new PositionedComponent(new Size(4, 4), new Position(1, 1), "126"),
//        };
//
//        ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
//        if (viewTreeObserver.isAlive()) {
//            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    Rect r = new Rect();
//                    layout.getHitRect(r);
//                    grid = new Grid(10, 5, r);
//                }
//            });
//        }
//        findViewById(R.id.position_BTN).setOnClickListener(v -> PlaceComponent());
    }

    private void PlaceComponent() {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        PositionedComponent component = components[index];
        Size size = grid.calculateViewSize(component.getSize());
        Position position = grid.calculateViewPosition(component.getPosition());
        layoutParams.width = size.getWidth();
        layoutParams.height = size.getHeight();
        view.setLayoutParams(layoutParams);
        view.setX(position.getX());
        view.setY(position.getY());
        Log.d("pfff", "Size: " + size + ", Position: " + position);
        index = (index + 1) % components.length;
    }

    public float pixelsToDp(float px) {
        // Source: https://stackoverflow.com/a/9563438
        return px / ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
}