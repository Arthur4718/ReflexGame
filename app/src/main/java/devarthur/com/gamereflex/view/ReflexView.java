package devarthur.com.gamereflex.view;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

import devarthur.com.gamereflex.R;

/**
 * Created by Arthur on 05/11/2018.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
    private int highScore;

    //Collections types for our cirles/spots (imageviews) and Animators
    private final Queue<ImageView> spots = new ConcurrentLinkedDeque<>();
    private final Queue<Animator> animators = new ConcurrentLinkedDeque<>();

    //Elements in the view
    private TextView highscoreTextView;
    private TextView currentScoreTextView;
    private TextView levelTextView;
    private LinearLayout livesLinearLayout;
    private RelativeLayout relativeLayout;
    private Resources resources;
    private LayoutInflater layoutInflater;

    //Static variables for view behvarior
    public static final int INITIAL_ANIMATION_DURATION = 6000; //6 seconds
    public static final Random random = new Random();
    public static final float SCALE_X = 0.25f;
    public static final float SCALE_Y = 0.25f;
    public static final int SPOT_DIAMETER = 100;
    public static final int INITIAL_SPOT = 5;
    public static final int SPOT_DELAY = 500;
    public static final int LIVES = 3;
    public static final int MAX_LIVES = 7;
    public static final int NEW_LEVEL = 10;
    private Handler spotHandler;
    //Static variables for sounde effects behaviors
    public static final int HIT_SOUND_ID = 1;
    public static final int MISS_SOUND_ID = 2;
    public static final int DISSAPEAR_SOUND_ID = 3;
    public static final int SOUND_PRIORITY = 100;
    public static final int MAX_STREAMS = 4;
    //Stadard tool to handle sounds
    private SoundPool soundPool;
    private int volume;
    private Map<Integer, Integer> soundMap;

    public ReflexView(Context context, SharedPreferences sharedPreferences, RelativeLayout parentLayout) {
        super(context);

        preferences = sharedPreferences;
        highScore = preferences.getInt(HIGH_SCORE, 0 );

        //save resources for loading external values
        resources = context.getResources();

        //save Layout Inflater
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        //Setup UI components
        relativeLayout = parentLayout;
        livesLinearLayout = relativeLayout.findViewById(R.id.livesLinearLayaout);
        highscoreTextView = relativeLayout.findViewById(R.id.highscore_tv);
        currentScoreTextView = relativeLayout.findViewById(R.id.score);
        levelTextView = relativeLayout.findViewById(R.id.level_tv);

        spotHandler = new Handler();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewHeight = h;
        viewWidth = w;

    }

    public void addNewSpot(){
        int x = random.nextInt(viewWidth - SPOT_DIAMETER);
        int y = random.nextInt(viewHeight - SPOT_DIAMETER);
        int x2 = random.nextInt(viewWidth - SPOT_DIAMETER);
        int y2 = random.nextInt(viewHeight - SPOT_DIAMETER);

        //Create the actual spot/cirle
        final ImageView spot = (ImageView) layoutInflater.inflate(R.layout.untouched, null);

        spots.add(spot);
        spot.setLayoutParams(new RelativeLayout.LayoutParams(SPOT_DIAMETER, SPOT_DIAMETER));

        spot.setImageResource(random.nextInt(2) == 0 ? R.drawable.ic_my_location_green_24dp : R.drawable.ic_my_location_red_24dp);

        spot.setX(x);
        spot.setY(y);

        relativeLayout.addView(spot); //Put the view on the screen
    }

}
