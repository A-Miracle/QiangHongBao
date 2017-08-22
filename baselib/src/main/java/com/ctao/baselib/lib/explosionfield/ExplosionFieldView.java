package com.ctao.baselib.lib.explosionfield;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ctao.baselib.utils.BitmapUtils;
import com.ctao.baselib.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 感谢 https://github.com/tyrantgit/ExplosionField
 */
public class ExplosionFieldView extends View {

    private List<ExplosionAnimator> mExplosions = new ArrayList<>();
    private int[] mExpandInset = new int[2];

    public ExplosionFieldView(Context context) {
        this(context, null);
    }

    public ExplosionFieldView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExplosionFieldView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 将指定的int值赋给指定数组的每个元素的整数。
        Arrays.fill(mExpandInset, DisplayUtils.converDip2px(32));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (ExplosionAnimator explosion : mExplosions) {
            explosion.draw(canvas);
        }
    }

    /**
     * 扩大爆炸范围
     * @param dx x方向向两边
     * @param dy y方向向上下
     */
    public void expandExplosionBound(int dx, int dy) {
        mExpandInset[0] = dx;
        mExpandInset[1] = dy;
    }

    /**
     * 爆炸
     * @param bitmap 爆炸图片
     * @param bound 爆炸区域
     * @param startDelay 启动爆炸延迟
     * @param duration 爆炸持续时间
     * @param listener 动画结束回调
     */
    public void explode(Bitmap bitmap, Rect bound, long startDelay, long duration, final OnAnimationEndListener listener) {
        final ExplosionAnimator explosion = new ExplosionAnimator(this, bitmap, bound);
        explosion.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mExplosions.remove(animation);
                if(listener != null){
                    listener.onAnimationEnd();
                }
            }
        });
        explosion.setStartDelay(startDelay); //启动延迟
        explosion.setDuration(duration); //动画持续时间
        mExplosions.add(explosion); //添加到动画集合
        explosion.start(); //启动动画
    }

    /**
     * 爆炸
     * @param view 爆炸view
     * @param isJitterAnima 是否执行抖动动画
     * @param listener 动画结束回调
     */
    public void explode(final View view, boolean isJitterAnima, OnAnimationEndListener listener) {
        Rect r = new Rect();
        view.getGlobalVisibleRect(r); //view
        int[] location = new int[2];
        getLocationOnScreen(location);
        r.offset(-location[0], -location[1]);
        r.inset(-mExpandInset[0], -mExpandInset[1]);
        int startDelay = 100;

        if(isJitterAnima){
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(150);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                Random random = new Random();

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    view.setTranslationX((random.nextFloat() - 0.5f) * view.getWidth() * 0.05f);
                    view.setTranslationY((random.nextFloat() - 0.5f) * view.getHeight() * 0.05f);

                }
            });
            animator.start();
            view.animate().setDuration(150).setStartDelay(startDelay).scaleX(0f).scaleY(0f).alpha(0f).start();
        }else{
            view.setScaleX(0);
            view.setScaleY(0);
            view.setAlpha(0);
        }

        explode(BitmapUtils.createBitmapFromView(view), r, startDelay, ExplosionAnimator.DEFAULT_DURATION, listener);
    }

    public void clear() {
        mExplosions.clear();
        invalidate();
    }

    public static ExplosionFieldView attach2Window(Activity activity) {
        ViewGroup rootView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
        ExplosionFieldView explosionField = new ExplosionFieldView(activity.getApplication());
        rootView.addView(explosionField, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return explosionField;
    }

    public interface OnAnimationEndListener{
        void onAnimationEnd();
    }
}
