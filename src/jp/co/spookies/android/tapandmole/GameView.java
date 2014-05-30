package jp.co.spookies.android.tapandmole;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * ゲームView
 */
class GameView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final long GAME_TIME_SECOND = 30;// 制限時間（秒）
    private static final int MODE_SETTING = 0; // 開始前
    private static final int MODE_GAME = 1; // ゲーム中
    private static final int MODE_GAME_END = 2; // タイムアップ

    private static final int END_TIME_POS_X = 20; // 残り時間表示位置X
    private static final int END_TIME_POS_Y = 45; // 残り時間表示位置Y
    private static final int POINT_POS_X = 145; // ポイント表示位置X
    private static final int POINT_POS_Y = 45; // ポイント表示位置Y

    private static final int FIRST_MOLE_Y = 170; // 左上のモグラの位置Y
    private static final int RETRY_BUTTON_Y = 110; // もう一度ボタンの表示位置Y

    private Thread thread = null;
    private Canvas canvas;
    private Paint paint;

    private Bitmap imageBack = BitmapFactory.decodeResource(getResources(),
            R.drawable.back); // 背景画像
    private Rect backSrcRect; // 背景画像のRect
    private Rect backRect; // 背景画像の表示Rect
    private Bitmap imageRetry = BitmapFactory.decodeResource(getResources(),
            R.drawable.retry); // リトライ画像
    private Rect retrySrcRect; // リトライ画像のRect
    private RectF retryRect; // リトライ画像の表示Rect
    private Bitmap[] imageNumbers = { // 数値画像
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_0),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_1),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_2),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_3),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_4),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_5),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_6),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_7),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_8),
            BitmapFactory.decodeResource(getResources(),
                    R.drawable.number_white_9) };
    private int mode; // 表示モード
    private int point; // 得点
    private long restTime; // 残り時間
    private long endTimeMillis; // 終わり時間
    private float density;
    private ArrayList<Mole> moguras = new ArrayList<Mole>(); // モグラインスタンス

    /**
     * コンストラクタ
     * 
     * @param Context
     */
    public GameView(Context context) {
        super(context);
        density = getContext().getResources().getDisplayMetrics().density;
        getHolder().addCallback(this);
        setResources();
        paint = new Paint();
    }

    /**
     * 初期化処理
     */
    private void init() {
        point = 0;
        restTime = GAME_TIME_SECOND;
        mode = MODE_SETTING;
        moguras.clear();

        float spaceX = getWidth() / 3;
        float marginX = (spaceX / 2 - Mole.gangImages[0].getWidth() / 2);
        float firstMoleY = FIRST_MOLE_Y * density;
        float spaceY = Mole.gangImages[0].getHeight();

        // モグラ
        for (int col = 0; col < 3; col++) {
            for (int row = 0; row < 4; row++) {
                this.moguras.add(new Mole(col * spaceX + marginX, firstMoleY
                        + row * spaceY, density));
            }
        }
    }

    /**
     * surfaceViewが変化したときに呼ばれるコールバック関数
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
            int height) {
    }

    /**
     * surfaceViewが生成される時に呼ばれるコールバック関数
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        init();
        backSrcRect = new Rect(0, 0, imageBack.getWidth(),
                imageBack.getHeight());
        backRect = new Rect(0, 0, getWidth(), getHeight());
        retrySrcRect = new Rect(0, 0, imageRetry.getWidth(),
                imageRetry.getHeight());
        float destLeft = getWidth() / 2 - imageRetry.getWidth() / 2;
        float destTop = RETRY_BUTTON_Y * density;
        retryRect = new RectF(destLeft, destTop, destLeft
                + imageRetry.getWidth(), destTop + imageRetry.getHeight());

        thread = new Thread(this);
        thread.start();
    }

    /**
     * surfaceViewが削除されるときに呼ばれるコールバック関数
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

    /**
     * runメソッド
     */
    @Override
    public void run() {
        while (thread != null) {
            update(); // 計算処理
            doDraw(); // 描画処理

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * タッチイベント処理
     * 
     * @param MotionEvent
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        if (mode == MODE_SETTING) {

        } else if (mode == MODE_GAME) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // モグラのタッチ判定
                for (int i = 0; i < this.moguras.size(); i++) {
                    if (!this.moguras.get(i).checkHit(x, y)) {
                        continue;
                    }
                    this.moguras.get(i).setHit();
                    if (this.moguras.get(i).checkGang()) {
                        point += 1;
                    } else if (point > 0) {
                        point -= 1;
                    }
                    break;
                }
            }
        } else if (this.mode == MODE_GAME_END) {
            if (retryRect.contains(x, y)) {
                init();
            }
        }
        return true;
    }

    /**
     * 更新処理
     */
    private void update() {

        if (mode == MODE_SETTING) {
            mode = MODE_GAME;
            endTimeMillis = System.currentTimeMillis() + 1000
                    * GAME_TIME_SECOND; // 現在の時間 +制限時間
        } else if (mode == MODE_GAME) {
            restTime = (int) Math.ceil((endTimeMillis - System
                    .currentTimeMillis()) / 1000); // 切り上げ

            // モグラをピョコピョコさせる
            for (int i = 0; i < this.moguras.size(); i++) {
                this.moguras.get(i).nextFrame();
            }
            if (restTime <= 0) {
                this.mode = MODE_GAME_END;
            }
        } else if (this.mode == MODE_GAME_END) {
        }
    }

    /**
     * 描画処理
     */
    private void doDraw() {
        canvas = getHolder().lockCanvas();
        // 背景
        canvas.drawBitmap(imageBack, backSrcRect, backRect, paint);

        drawPoint();
        drawEndTime();

        // モグラ描画
        for (int i = 0; i < moguras.size(); i++) {
            moguras.get(i).draw(canvas);
        }

        if (mode == MODE_GAME_END) {
            canvas.drawBitmap(imageRetry, retrySrcRect, retryRect, null);
        }

        getHolder().unlockCanvasAndPost(canvas);
    }

    /**
     * ポイントを描画する
     */
    private void drawPoint() {
        String str_point = String.valueOf(point);
        float pos = POINT_POS_X * density;
        for (int i = 0; i < str_point.length(); i++) {
            int num = Integer.parseInt(str_point.substring(i, i + 1));
            canvas.drawBitmap(imageNumbers[num], pos, POINT_POS_Y * density,
                    null);
            pos += imageNumbers[num].getWidth();
        }
    }

    /**
     * 残り時間を描画する
     */
    private void drawEndTime() {

        if (0 <= restTime && restTime <= GAME_TIME_SECOND) {
            String str_time = String.valueOf(restTime);
            float pos = END_TIME_POS_X * density;
            for (int i = 0; i < str_time.length(); i++) {
                int num = Integer.parseInt(str_time.substring(i, i + 1));
                canvas.drawBitmap(imageNumbers[num], pos, END_TIME_POS_Y
                        * density, null);
                pos += imageNumbers[num].getWidth();
            }
        } else {
            canvas.drawBitmap(imageNumbers[0], END_TIME_POS_X * density,
                    END_TIME_POS_Y * density, null);
        }
    }

    /**
     * 画像データの読み込み
     */
    private void setResources() {
        Mole.gangImages[0] = BitmapFactory.decodeResource(getResources(),
                R.drawable.hole);
        Mole.gangImages[1] = BitmapFactory.decodeResource(getResources(),
                R.drawable.gang_mogura1);
        Mole.gangImages[2] = BitmapFactory.decodeResource(getResources(),
                R.drawable.gang_mogura2);
        Mole.hitGangImages[0] = BitmapFactory.decodeResource(getResources(),
                R.drawable.hole);
        Mole.hitGangImages[1] = BitmapFactory.decodeResource(getResources(),
                R.drawable.hit_gang_mogura1);
        Mole.hitGangImages[2] = BitmapFactory.decodeResource(getResources(),
                R.drawable.hit_gang_mogura2);

        Mole.friendImages[0] = BitmapFactory.decodeResource(getResources(),
                R.drawable.hole);
        Mole.friendImages[1] = BitmapFactory.decodeResource(getResources(),
                R.drawable.friend_mogura1);
        Mole.friendImages[2] = BitmapFactory.decodeResource(getResources(),
                R.drawable.friend_mogura2);
        Mole.hitFriendImages[0] = BitmapFactory.decodeResource(getResources(),
                R.drawable.hole);
        Mole.hitFriendImages[1] = BitmapFactory.decodeResource(getResources(),
                R.drawable.hit_friend_mogura1);
        Mole.hitFriendImages[2] = BitmapFactory.decodeResource(getResources(),
                R.drawable.hit_friend_mogura2);
    }
}
