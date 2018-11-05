package devarthur.com.gamereflex.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Arthur on 05/11/2018.
 */

public class ReflexView extends View {
    //Contants;
    private static final String HIGH_SCORE = "HIGH_SCORE";
    private SharedPreferences preferences;

    //Variables that manage the game
    private int spotsTouched;
    private int score;
    private int level;
    private int viewWidth;
    private int viewHeight;
    private int animationTime;
    private boolean gameOver;
    private boolean gamePaused;
    private boolean dialogDisplay;
    private int highScor; 


    public ReflexView(Context context, SharedPreferences sharedPreferences, RelativeLayout parentLayout) {
        super(context);
    }


}
