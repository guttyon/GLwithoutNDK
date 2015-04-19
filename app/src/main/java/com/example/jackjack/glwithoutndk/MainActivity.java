package com.example.jackjack.glwithoutndk;


import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/*
public class MainActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全画面表示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        GLSurfaceView view = new GLSurfaceView(this);
        view.setEGLContextClientVersion(2);  // GLESのバージョンは 2 を指定

super.setEGLConfigChooser(8 , 8, 8, 8, 16, 0);

        SampleRenderer renderer = new SampleRenderer(view); // renderer に GLSurfaceView を渡しとく
        view.setRenderer(renderer);

        setContentView(view);
    }
}
*/
public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 全画面表示
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);


        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }

    private GLSurfaceView mGLView;
}
class MyGLSurfaceView extends GLSurfaceView {
    public MyGLSurfaceView(Context context) {
        super(context);

        // Turn on error-checking and logging
        //setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);


        /*
        * 絶え間ないレンダリング  vs その都度のレンダリング

ゲームやシミュレーションなどほとんどの 3D アプリケーションは絶え間なく動いています。
しかし中には反応型の 3D アプリケーションもあります。
ユーザによるなんらかの動作を受け入れるまで待ち、それに応答するのです。
そのようなタイプのアプリケーションでは、画面を絶えず再描画する GLSurfaceView のデフォルトの振る舞いだと時間を浪費することになってしまいます。
反応型のアプリケーションを開発している場合は、
GLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY) を呼び出すことにより、
絶え間なくアニメーションを動かすことを無効にすることができます。
その後再度レンダリングしたい都度 GLSurfaceView.requestRender() を呼び出します。
        *
        * */

        /*

setEGLConfigChooser(boolean needDepth)
    16 ビットのフレームバッファを使用または使用しない R5G6B5 に最も近い設定を選択します。
setEGLConfigChooser(int redSize, int greenSize,int blueSize, int alphaSize,int depthSize, int stencilSize)
    最低値がコンストラクタで指定したビット／チャネルと同じビット／ピクセルの値が最も少ない設定を選択します。
setEGLConfigChooser(EGLConfigChooser configChooser)
    選択のすべての管理を指定の設定に任せます。 デバイスの機能を検出して選択するような独自の EGLConfigChooser を実装し、それを渡します。

         */
        setEGLContextClientVersion(2);  // GLESのバージョンは 2 を指定
        setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        //mRenderer = new SampleRenderer(mGLView); // renderer に GLSurfaceView を渡しとく
        mRenderer = new ClearRenderer();

        setRenderer(mRenderer);
    }

    public boolean onTouchEvent(final MotionEvent event) {
        queueEvent(new Runnable(){
            public void run() {
                mRenderer.setColor(event.getX() / getWidth(),
                        event.getY() / getHeight(), 1.0f);
            }});
        return true;
    }

    ClearRenderer mRenderer;
}
class ClearRenderer implements GLSurfaceView.Renderer {
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Do nothing special.
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
    }

    public void onDrawFrame(GL10 gl) {
        gl.glClearColor(mRed, mGreen, mBlue, 1.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    public void setColor(float r, float g, float b) {
        mRed = r;
        mGreen = g;
        mBlue = b;
    }

    private float mRed;
    private float mGreen;
    private float mBlue;
}
