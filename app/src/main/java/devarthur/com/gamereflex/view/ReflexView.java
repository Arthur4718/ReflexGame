package devarthur.com.gamereflex.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

import devarthur.com.gamereflex.R;

/**
 * Created by Arthur on 05/11/2018.
 */

//TODO  https://www.udemy.com/android-developers-portfolio-masterclass-build-7-apps/learn/v4/t/lecture/8679406?start=0
    //time 8h30
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
    private boolean dialogDisplayed;
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
    private SoundPool.Builder soundPool;
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

        //spotHandler = new Handler();

        addNewSpot();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        viewHeight = h;
        viewWidth = w;
    }

    public void pause(){
        gamePaused = true;
        soundPool.build().release();
        soundPool = null;
        cancelAnimations();
    }
    public void resume(Context context){
        gamePaused = false;
        initializeSoundEffects(context);
    }
    public void resetGame(){
        spots.clear();
        animators.clear();
        livesLinearLayout.removeAllViews();

        animationTime = INITIAL_ANIMATION_DURATION;

        spotsTouched = 0;
        score = 0;
        level = 1;
        gameOver = false;

        for(int i = 0; i < LIVES; i++){
            livesLinearLayout.addView(
                    (ImageView) layoutInflater.inflate(R.layout.life, null)
            );

        }
        for(int i = 1; i <= INITIAL_SPOT; i++){
            spotHandler.postDelayed(addSpotRunnable, i + SPOT_DELAY);

        }
    }
    private void initializeSoundEffects(Context context){
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();
        soundPool = new SoundPool.Builder();
        soundPool.setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build();
        soundPool.build();

        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        volume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //Sound Map.
        soundMap = new HashMap<Integer, Integer>();
        soundMap.put(HIT_SOUND_ID, soundPool.build().load(context, R.raw.hit, SOUND_PRIORITY));
        soundMap.put(MISS_SOUND_ID, soundPool.build().load(context, R.raw.miss, SOUND_PRIORITY));
        soundMap.put(DISSAPEAR_SOUND_ID, soundPool.build().load(context, R.raw.dissapear, SOUND_PRIORITY));

    }

    private void displayScores(){
        highscoreTextView.setText(resources.getString(R.string.highscore) + " " + highScore);
        currentScoreTextView.setText(resources.getString(R.string.score) + " " + score);
        levelTextView.setText(resources.getString(R.string.level) + " " + level);
    }

    private Runnable addSpotRunnable = new Runnable() {
        @Override
        public void run() {
            addNewSpot();
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(soundPool != null){
            soundPool.build().play(MISS_SOUND_ID, volume, volume, SOUND_PRIORITY, 0 ,1f );
        }
        score -= 15 * level;
        score = Math.max(score, 0); //This prevents score from going below zero
        displayScores();
        return true;
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
/*

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) spot.getContext().getSystemService(Context.WINDOW_SERVICE);

        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
*/

        spot.setImageResource(random.nextInt(2) == 0 ? R.drawable.ic_my_location_green_24dp : R.drawable.ic_my_location_red_24dp);

        spot.setX(x);
        spot.setY(y);

        //Check for click event
        spot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                touchedSpot(spot);
            }
        });

        relativeLayout.addView(spot); //Put the view on the screen

        //Add spot animations
        spot.animate().x(x2).y(y2).scaleX(SCALE_X).scaleY(SCALE_Y)
                .setDuration(animationTime).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animators.add(animation); //Save for later
            }
            @Override
            public void onAnimationCancel(Animator animation) {
                animators.remove(animation);

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if(!gamePaused && spots.contains(spot)){
                    missedSpot(spot);

                }

            }


        });


    }

    private void missedSpot(ImageView spot) {
        spots.remove(spot);
        relativeLayout.removeView(spot);

        if(gameOver){
            return;
        }
        if(soundPool != null){
            soundPool.build().play(DISSAPEAR_SOUND_ID, volume, volume,
                    SOUND_PRIORITY, 0, 1f);
        }
        if(livesLinearLayout.getChildCount() == 0){
            gameOver = true;
            //Check if there as need to update the highscore.
            if(score > highScore){
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(HIGH_SCORE, score);
                editor.apply(); //You can use commit, but apply is the better way.

                highScore = score;
            }
            cancelAnimations();

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Game Over");
            builder.setMessage("Score: " + score);
            builder.setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    displayScores();
                    dialogDisplayed = false;
                    resetGame();
                }
            });
            dialogDisplayed = true;
            builder.show();
        }else{
            livesLinearLayout.removeViewAt(livesLinearLayout.getChildCount() -1);
            addNewSpot();
        }

    }

    private void cancelAnimations() {
        for(Animator animator : animators){
            animator.cancel();
        }

        //remove remaining spots from the screen

        for(ImageView view : spots){
            relativeLayout.removeView(view);

        }
        spotHandler.removeCallbacks(addSpotRunnable);
        animators.clear();
        spots.clear();
    }

    private void touchedSpot(ImageView spot) {

        relativeLayout.removeView(spot);
        //Call the qeue and remove the spot from there
        spots.remove(spot);
        level = 1;
        ++spotsTouched;
        score += 10 * level;
        if(spotsTouched % NEW_LEVEL == 0){
            ++level;
            animationTime *= 0.95; //make the animation go faster.
            if(livesLinearLayout.getChildCount() < MAX_LIVES){
                ImageView life = (ImageView) layoutInflater.inflate(R.layout.life, null);
                livesLinearLayout.addView(life);
            }
        }
        displayScores();

        if(!gameOver){
            addNewSpot();
        }

    }

}
