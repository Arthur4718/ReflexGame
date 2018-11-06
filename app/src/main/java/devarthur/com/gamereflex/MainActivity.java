package devarthur.com.gamereflex;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;

import devarthur.com.gamereflex.view.ReflexView;

public class MainActivity extends AppCompatActivity {
    private ReflexView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout layout = findViewById(R.id.relativeLayout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gameView = new ReflexView(this, getPreferences(Context.MODE_PRIVATE), layout);
        }
        layout.addView(gameView, 0);


    }


    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gameView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gameView.resume(getApplicationContext());
        }
    }
}
