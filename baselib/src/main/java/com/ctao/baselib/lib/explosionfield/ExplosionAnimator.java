package com.ctao.baselib.lib.explosionfield;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.ctao.baselib.utils.DisplayUtils;

import java.util.Random;

public class ExplosionAnimator extends ValueAnimator {

    static long DEFAULT_DURATION = 0x400; //默认动画时间
    private static final Interpolator DEFAULT_INTERPOLATOR = new AccelerateInterpolator(0.6f); //加速插入器
    private static final float END_VALUE = 1.4f; //浮动值
    private static final float R_MIN = DisplayUtils.converDip2px(5);
    private static final float R_MAX = DisplayUtils.converDip2px(20);
    private static final float R_2 = DisplayUtils.converDip2px(2);
    private static final float R_1 = DisplayUtils.converDip2px(1);
    private Paint mPaint;
    private Rect mBound;
    private Particle[] mParticles;
    private View mContainer;

    /**
     * 爆炸动画
     * @param container 动画容器, 动画在哪上面执行
     * @param bitmap 被分割的图片
     * @param bound 爆炸区域大小
     */
    public ExplosionAnimator(View container, Bitmap bitmap, Rect bound){
        mPaint = new Paint();
        mBound = new Rect(bound);
        int partLen = 15;
        mParticles = new Particle[partLen * partLen]; // 粒子碎片数量
        Random random = new Random(System.currentTimeMillis());
        int w = bitmap.getWidth() / (partLen + 2); //横排粒子间隔
        int h = bitmap.getHeight() / (partLen + 2); //纵排粒子间隔
        for (int i = 0; i < partLen; i++) { //行数
            for (int j = 0; j < partLen; j++) { //列数
                mParticles[(i * partLen) + j] = generateParticle(bitmap.getPixel((j + 1) * w, (i + 1) * h), random);
            }
        }
        mContainer = container;
        setFloatValues(0f, END_VALUE);
        setInterpolator(DEFAULT_INTERPOLATOR);
        setDuration(DEFAULT_DURATION);
    }

    /**
     * 生成粒子碎片
     * @param color 颜色
     * @param random 随机数对象
     * @return
     */
    private Particle generateParticle(int color, Random random) {
        Particle particle = new Particle();
        particle.color = color;
        particle.radius = R_2; //2
        if (random.nextFloat() < 0.2f) {
            particle.baseRadius = R_2 + ((R_MIN - R_2) * random.nextFloat()); //2+3*random.nextFloat()
        } else {
            particle.baseRadius = R_1 + ((R_2 - R_1) * random.nextFloat()); //1+random.nextFloat()
        }
        float nextFloat = random.nextFloat();

        particle.top = mBound.height() * ((0.18f * random.nextFloat()) + 0.2f);//mBound.height()*(0.2~0.38)
        particle.top = nextFloat < 0.2f ? particle.top : particle.top + ((particle.top * 0.2f) * random.nextFloat());

        particle.bottom = (mBound.height() * (random.nextFloat() - 0.5f)) * 1.8f; // + -
        float f = nextFloat < 0.2f ? particle.bottom : nextFloat < 0.8f ? particle.bottom * 0.6f : particle.bottom * 0.3f;
        particle.bottom = f;
        particle.mag = 4.0f * particle.top / particle.bottom;
        particle.neg = (-particle.mag) / particle.bottom;
        f = mBound.centerX() + (R_MAX * (random.nextFloat() - 0.5f));
        particle.baseCx = f;
        particle.cx = f;
        f = mBound.centerY() + (R_MAX * (random.nextFloat() - 0.5f));
        particle.baseCy = f;
        particle.cy = f;
        particle.life = END_VALUE / 10 * random.nextFloat();
        particle.overflow = 0.4f * random.nextFloat();
        particle.alpha = 1f;
        return particle;
    }

    @Override
    public void start() {
        super.start();
        mContainer.invalidate(mBound);
    }

    public boolean draw(Canvas canvas) {
        if (!isStarted()) {
            return false;
        }
        for (Particle particle : mParticles) {
            particle.advance((float) getAnimatedValue());
            if (particle.alpha > 0f) {
                mPaint.setColor(particle.color);
                mPaint.setAlpha((int) (Color.alpha(particle.color) * particle.alpha));
                canvas.drawCircle(particle.cx, particle.cy, particle.radius, mPaint);
            }
        }
        mContainer.invalidate();
        return true;
    }

    private class Particle {
        float alpha; //透明度
        int color; //颜色
        float cx; //圆心x
        float cy; //圆心y
        float radius; //半径
        float baseCx; //原始圆心x
        float baseCy; //原始圆心y
        float baseRadius; //原始半径
        float top;
        float bottom;
        float mag; //正的
        float neg; //负的
        float life; //生活
        float overflow; //溢出

        public void advance(float factor) {
            float f = 0f;
            float normalization = factor / END_VALUE;
            if (normalization < life || normalization > 1f - overflow) {
                alpha = 0f;
                return;
            }
            normalization = (normalization - life) / (1f - life - overflow);
            float f2 = normalization * END_VALUE;
            if (normalization >= 0.7f) {
                f = (normalization - 0.7f) / 0.3f;
            }
            alpha = 1f - f;
            f = bottom * f2;
            cx = baseCx + f;
            cy = (float) (baseCy - this.neg * Math.pow(f, 2.0)) - f * mag;
            radius = R_2 + (baseRadius - R_2) * f2;
        }
    }
}
