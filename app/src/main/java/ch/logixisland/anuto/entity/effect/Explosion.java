package ch.logixisland.anuto.entity.effect;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import ch.logixisland.anuto.engine.logic.GameEngine;
import ch.logixisland.anuto.engine.render.DrawCommand;
import ch.logixisland.anuto.engine.render.DrawCommandBuffer;
import ch.logixisland.anuto.engine.render.Drawable;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.entity.Entity;
import ch.logixisland.anuto.entity.Types;
import ch.logixisland.anuto.entity.enemy.Enemy;
import ch.logixisland.anuto.util.iterator.StreamIterator;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class Explosion extends Effect {

    private final static float EFFECT_DURATION = 0.2f;
    private final static int ALPHA_START = 180;
    private final static int ALPHA_STEP = (int) (ALPHA_START / (GameEngine.TARGET_FRAME_RATE * EFFECT_DURATION));

    private class ExplosionDrawable implements Drawable {
        private Paint mPaint;
        private int mAlpha = ALPHA_START;

        public ExplosionDrawable() {
            mPaint = new Paint();
            mPaint.setColor(Color.YELLOW);
            mPaint.setAlpha(mAlpha);
        }

        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public int getLayer() {
            return Layers.SHOT;
        }

        @Override
        public void draw(DrawCommandBuffer buffer) {
            buffer.drawCircle(getPosition().x, getPosition().y, mRadius, mPaint);
        }
    }

    private float mDamage;
    private float mRadius;
    private int mDrawAlpha;

    private ExplosionDrawable mDrawObject;

    public Explosion(Entity origin, Vector2 position, float damage, float radius) {
        super(origin, EFFECT_DURATION);
        setPosition(position);

        mDamage = damage;
        mRadius = radius;

        mDrawObject = new ExplosionDrawable();
    }

    @Override
    public void init() {
        super.init();

        mDrawAlpha = ALPHA_START;
        getGameEngine().add(mDrawObject);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mDrawObject);
    }

    @Override
    public void tick() {
        super.tick();

        mDrawAlpha -= ALPHA_START;
        mDrawObject.setAlpha(mDrawAlpha);
    }

    @Override
    protected void effectBegin() {
        StreamIterator<Enemy> enemies = getGameEngine().get(Types.ENEMY)
                .filter(inRange(getPosition(), mRadius))
                .cast(Enemy.class);

        while (enemies.hasNext()) {
            Enemy enemy = enemies.next();
            enemy.damage(mDamage, getOrigin());
        }
    }

    @Override
    protected void effectEnd() {

    }
}
