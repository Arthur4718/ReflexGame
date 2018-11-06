package devarthur.com.gamereflex;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import devarthur.com.gamereflex.view.ReflexView;

public class MainActivity extends AppCompatActivity {
    private ReflexView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout layout = findViewById(R.id.relativeLayout);

        gameView = new ReflexView(this, getPreferences(Context.MODE_PRIVATE), layout);
        layout.addView(gameView, 0);


    }
}
