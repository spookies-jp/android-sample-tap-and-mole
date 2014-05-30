package jp.co.spookies.android.tapandmole;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class TapAndMoleActivity extends Activity {

    private GameView view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // タイトル画面
        view = new GameView(this);
    }

    /**
     * はじめるボタン押下処理
     * 
     * @param v
     */
    public void onClickStart(View v) {
        setContentView(view); // ゲーム画面呼び出し
    }
}
