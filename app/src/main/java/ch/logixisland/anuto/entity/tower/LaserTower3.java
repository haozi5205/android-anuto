package ch.logixisland.anuto.entity.tower;

import java.util.ArrayList;
import java.util.List;

import ch.logixisland.anuto.R;
import ch.logixisland.anuto.engine.render.DrawCommandBuffer;
import ch.logixisland.anuto.engine.render.Layers;
import ch.logixisland.anuto.engine.render.sprite.SpriteInstance;
import ch.logixisland.anuto.engine.render.sprite.SpriteTemplate;
import ch.logixisland.anuto.engine.render.sprite.StaticSprite;
import ch.logixisland.anuto.entity.effect.LaserStraight;
import ch.logixisland.anuto.util.RandomUtils;
import ch.logixisland.anuto.util.data.TowerConfig;
import ch.logixisland.anuto.util.math.vector.Vector2;

public class LaserTower3 extends AimingTower {

    private final static float LASER_SPAWN_OFFSET = 0.8f;

    private class StaticData {
        SpriteTemplate mSpriteTemplateBase;
        SpriteTemplate mSpriteTemplateCanon;
    }

    private float mAngle = 90f;
    private float mLaserLength;

    private StaticSprite mSpriteBase;
    private StaticSprite mSpriteCanon;

    public LaserTower3(TowerConfig config) {
        super(config);
        StaticData s = (StaticData) getStaticData();

        mSpriteBase = getSpriteFactory().createStatic(Layers.TOWER_BASE, s.mSpriteTemplateBase);
        mSpriteBase.setIndex(RandomUtils.next(4));
        mSpriteBase.setListener(this);

        mSpriteCanon = getSpriteFactory().createStatic(Layers.TOWER, s.mSpriteTemplateCanon);
        mSpriteCanon.setIndex(RandomUtils.next(4));
        mSpriteCanon.setListener(this);

        mLaserLength = 100f;
    }

    @Override
    public Object initStatic() {
        StaticData s = new StaticData();

        s.mSpriteTemplateBase = getSpriteFactory().createTemplate(R.drawable.base5, 4);
        s.mSpriteTemplateBase.setMatrix(1f, 1f, null, -90f);

        s.mSpriteTemplateCanon = getSpriteFactory().createTemplate(R.drawable.laser_tower3, 4);
        s.mSpriteTemplateCanon.setMatrix(0.4f, 1.2f, new Vector2(0.2f, 0.2f), -90f);

        return s;
    }

    @Override
    public void init() {
        super.init();

        getGameEngine().add(mSpriteBase);
        getGameEngine().add(mSpriteCanon);
    }

    @Override
    public void clean() {
        super.clean();

        getGameEngine().remove(mSpriteBase);
        getGameEngine().remove(mSpriteCanon);
    }

    @Override
    public void draw(SpriteInstance sprite, DrawCommandBuffer buffer) {
        super.draw(sprite, buffer);

        buffer.rotate(mAngle);
    }

    @Override
    public void tick() {
        super.tick();

        if (getTarget() != null) {
            mAngle = getAngleTo(getTarget());

            if (isReloaded()) {
                Vector2 laserFrom = Vector2.polar(LASER_SPAWN_OFFSET, mAngle).add(getPosition());
                Vector2 laserTo = Vector2.polar(mLaserLength, mAngle).add(getPosition());
                getGameEngine().add(new LaserStraight(this, laserFrom, laserTo, getDamage()));
                setReloaded(false);
            }
        }
    }

    @Override
    public void preview(DrawCommandBuffer buffer) {
        mSpriteBase.draw(buffer);
        mSpriteCanon.draw(buffer);
    }

    @Override
    public List<TowerProperty> getProperties() {
        List<TowerProperty> properties = new ArrayList<>();
        properties.add(new TowerProperty(R.string.damage, getDamage()));
        properties.add(new TowerProperty(R.string.reload, getReloadTime()));
        properties.add(new TowerProperty(R.string.range, getRange()));
        properties.add(new TowerProperty(R.string.inflicted, getDamageInflicted()));
        return properties;
    }
}
