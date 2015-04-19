package com.example.jackjack.glwithoutndk;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class SampleRenderer implements Renderer {

    private GLSurfaceView view;
    private int program;
    private int aPositionIndex; // シェーダの位置情報のインデックス
    private FloatBuffer penguinPositionBuffer; // ペンギンの場所のバッファ
    private FloatBuffer birdPositionBuffer; // 鳥の場所のバッファ
    private int penguinTexture; // ペンギンのテクスチャ
    private int birdTexture; // 鳥のテクスチャ

    private static final float[] penguinPosition = {0.5f, 0.5f, 0.0f}; // ペンギンの位置(x, y, z)
    private static final float[] birdPosition = {-0.3f, -0.6f, 0.0f}; // 鳥の位置

    // バーテックスシェーダのコード
    private static final String vertexShaderCode =
            "attribute vec4 a_Position;" +
    "void main()" +
            "{" +
            "gl_PointSize = 80.0;" +
            "gl_Position = a_Position;" +
            "}";
    // フラグメントシェーダのコード
    private static final String fragmentShaderCode =
            "precision mediump float;" +
    "uniform sampler2D u_Texture;" +
            "void main()" +
            "{" +
            "vec4 color;" +
            "color = texture2D(u_Texture, gl_PointCoord);" +
            "gl_FragColor = color;" +
            "}";

    public SampleRenderer(GLSurfaceView view) {
        this.view = view;
    }

    @Override
    // ここで描画
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT); // 描画クリア
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 0.5f); // 背景色指(灰色)

        // 位置指定の有効化
        GLES20.glEnableVertexAttribArray(aPositionIndex);
        // 画像の透過の有効化
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);

        // ペンギン画像の指定
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, penguinTexture);
        // 位置指定
        GLES20.glVertexAttribPointer(aPositionIndex, 3, GLES20.GL_FLOAT, false, 0, penguinPositionBuffer);
        // 描画
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        // ペンギン画像の指定
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, birdTexture);
        // 位置指定
        GLES20.glVertexAttribPointer(aPositionIndex, 3, GLES20.GL_FLOAT, false, 0, birdPositionBuffer);
        // 描画
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);

        // 位置指定の無効化
        GLES20.glDisable(aPositionIndex);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    // 初期化
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 位置バッファの登録
        penguinPositionBuffer = makeFloatBuffer(penguinPosition);
        birdPositionBuffer = makeFloatBuffer(birdPosition);

        // テクスチャの登録(GLSurfaceViewのqueueEventのに登録する)
        view.queueEvent(new Runnable() {
            public void run() {
                //penguinTexture = makeTexture(R.drawable.penguin);
                penguinTexture = makeTexture(R.mipmap.ic_launcher);
                //birdTexture = makeTexture(R.drawable.bird);
                birdTexture = makeTexture(R.mipmap.ic_launcher);
            }
        });

        // バーテックスシェーダの生成
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexShaderCode);
        GLES20.glCompileShader(vertexShader);

        // フラグメントシェーダの生成
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);
        GLES20.glCompileShader(fragmentShader);

        // プログラムの生成して上で作ったシェーダをはっつける
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);

        GLES20.glLinkProgram(program); // プログラムをリンクする
        // 位置パラメータインデックスをとっとく
        aPositionIndex = GLES20.glGetAttribLocation(program, "a_Position");

        GLES20.glUseProgram(program); // 作ったプログラムを使う
    }

    // フロートの配列からフロートバッファを返す
    private FloatBuffer makeFloatBuffer(float[] fArray){
        FloatBuffer fb = ByteBuffer.allocateDirect(fArray.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(fArray).position(0);

        return fb;
    }

    // リソースIDから bitmap を GL に登録して テクスチャID を返す
    private int makeTexture(int resId){
        Resources r = view.getResources();
        Bitmap bp;
        int textures[] = new int[1];
        bp = BitmapFactory.decodeResource(r, resId);
        GLES20.glGenTextures(1, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bp, 0);

        bp.recycle();
        return textures[0];
    }
}
