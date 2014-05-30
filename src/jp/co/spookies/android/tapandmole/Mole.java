package jp.co.spookies.android.tapandmole;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * モグラクラス
 */
public class Mole {

    // 状態毎のイメージ[穴、半分穴、全部　]
    public static final Bitmap[] gangImages = new Bitmap[3];
    public static final Bitmap[] hitGangImages = new Bitmap[3];
    public static final Bitmap[] friendImages = new Bitmap[3];
    public static final Bitmap[] hitFriendImages = new Bitmap[3];

    public static final int STATUS_HIDDEN = 0; // 穴
    public static final int STATUS_HARF = 1; // 半分出現
    public static final int STATUS_FULL = 2; // 全部出現

    public static final int DIRECTION_UP = 0; // 上向きに移動
    public static final int DIRECTION_DOWN = 1; // 下向きに移動

    public static final int MOGURA_TOUCH_WIDTH = 60; // 各モグラタッチ可能幅
    public static final int MOGURA_TOUCH_HEIGHT = 71; // 各モグラタッチ可能高さ

    public static final double MOGURA_DIRECTION_UP_RATE = 0.01; // モグラ出現割合
    public static final double MOGURA_DIRECTION_DOWN_RATE = 0.05; // モグラ隠れる割合

    protected float density; // density
    protected boolean isGang; // ギャングモグラの真偽
    protected int status; // 表示ステータス
    protected boolean isHit; // ヒットされた状態か
    protected int direction; // 上下移動向きの方向
    protected float posX; // 設置位置のX座標
    protected float posY; // 設置位置のY座標

    /**
     * コンストラクタ
     * 
     * @param posX
     * @param posY
     * @param density
     */
    public Mole(float posX, float posY, float density) {
        this.posX = posX;
        this.posY = posY;
        this.density = density;
        this.status = STATUS_HIDDEN;
    }

    /**
     * タップした位置で、モグラがヒットしたかチェックする
     * 
     * @param hitX
     * @param hitY
     * @return TRUE: ヒットした　FALSE: ヒットしていない(既にヒット中を含む)
     */
    public boolean checkHit(float hitX, float hitY) {
        if (this.posX > hitX || hitX > this.posX + MOGURA_TOUCH_WIDTH * density
                || this.posY > hitY
                || hitY > this.posY + MOGURA_TOUCH_HEIGHT * density) {
            // 領域外をタッチされた
            return false;
        }
        if (this.status == STATUS_HIDDEN) {
            // 穴の中に隠れている
            return false;
        }
        if (this.isHit) {
            // 既にヒットされている
            return false;
        }
        return true;
    }

    /**
     * ヒットされた状態にセット
     */
    public void setHit() {
        this.isHit = true;
    }

    /**
     * ギャングタイプかチェック
     * 
     * @return TRUE: ギャング　FALSE: 味方
     */
    public boolean checkGang() {
        if (this.isGang) {
            return true;
        }
        return false;
    }

    /**
     * 状態を計算する
     */
    public void nextFrame() {

        if (this.status == STATUS_HIDDEN) {
            // モグラが隠れた状態

            if (Math.random() <= MOGURA_DIRECTION_UP_RATE) {
                this.direction = DIRECTION_UP;
                this.isHit = false; // 初期化
                this.status = STATUS_HARF;

                // モグラタイプを設定
                if (Math.random() * 2 < 1) {
                    this.isGang = true;
                } else {
                    this.isGang = false;
                }
            }
        } else if (this.status == STATUS_HARF) {
            // モグラが半分の状態
            if (this.isHit || this.direction == DIRECTION_DOWN) {
                this.status = STATUS_HIDDEN;
            } else {
                this.status = STATUS_FULL;
            }
        } else if (this.status == STATUS_FULL) {
            // モグラが全部表示されている
            if (this.isHit || Math.random() <= MOGURA_DIRECTION_DOWN_RATE) {
                this.direction = DIRECTION_DOWN;
                this.status = STATUS_HARF;
            }
        }
    }

    /**
     * モグラを描画
     * 
     * @param Canvas
     */
    public void draw(Canvas c) {
        if (this.isHit) {
            if (this.isGang) {
                c.drawBitmap(hitGangImages[this.status], this.posX, this.posY,
                        null);
            } else {
                c.drawBitmap(hitFriendImages[this.status], this.posX,
                        this.posY, null);
            }
        } else {
            if (this.isGang) {
                c.drawBitmap(gangImages[this.status], this.posX, this.posY,
                        null);
            } else {
                c.drawBitmap(friendImages[this.status], this.posX, this.posY,
                        null);
            }
        }
    }

}
