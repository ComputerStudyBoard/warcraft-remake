/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.warcraft.entity;

import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.warcraft.effect.Effect;
import com.b3dgs.warcraft.effect.EffectType;
import com.b3dgs.warcraft.effect.HandlerEffect;

/**
 * Abstract building entity implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Building
        extends Entity
{
    /**
     * Die states.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    private static enum Destroy
    {
        /** No death. */
        NONE,
        /** Burning low. */
        BURNING_LOW,
        /** Burning high. */
        BURNING_HIGH,
        /** Exploding. */
        EXPLODING;
    }

    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Burning animation. */
    private final Effect burning;
    /** Explode animation surface. */
    private final Effect explode;
    /** Burning low animation. */
    private final Animation animBurningLow;
    /** Burning high animation. */
    private final Animation animBurningHigh;
    /** Explode animation. */
    private final Animation animExplode;
    /** Destroy flag. */
    private Destroy destroy;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Building(SetupEntity setup)
    {
        super(setup);
        setLayer(0);
        setFrame(2);
        handlerEffect = setup.handlerEffect;
        burning = setup.factoryEffect.create(EffectType.BURNING);
        explode = setup.factoryEffect.create(EffectType.EXPLODE);
        animBurningLow = Anim.createAnimation(1, 4, 0.125, false, true);
        animBurningHigh = Anim.createAnimation(5, 8, 0.125, false, true);
        animExplode = Anim.createAnimation(1, 18, 0.125, false, false);
        destroy = Destroy.NONE;
    }

    /**
     * Start burning low effect.
     * 
     * @param current The current destroy status.
     * @param anim The animation to play.
     */
    private void burning(Destroy current, Animation anim)
    {
        if (Destroy.NONE == destroy)
        {
            final int x = getLocationIntX() + getWidth() / 2 - 6;
            final int y = getLocationIntY() + getHeight() / 2 - 4;
            burning.start(x, y);
            handlerEffect.add(burning);
        }
        burning.play(anim);
        destroy = current;
    }

    /**
     * Start explode effect.
     */
    private void explode()
    {
        final int x = getLocationIntX() + getWidth() / 2 - explode.getWidth() / 2;
        final int y = getLocationIntY();

        handlerEffect.remove(burning);
        explode.start(x, y);
        explode.play(animExplode);
        handlerEffect.add(explode);
        destroy = Destroy.EXPLODING;
    }

    /*
     * Entity
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        if (isDead())
        {
            if (explode.getFrame() > 7)
            {
                setVisible(false);
            }
        }
    }

    @Override
    public void stop()
    {
        // Nothing to do
    }

    @Override
    public void decreaseLife(int damages)
    {
        super.decreaseLife(damages);

        if (isDead())
        {
            explode();
        }
        else if (getLifePercent() > 20 && getLifePercent() < 50 && Destroy.NONE == destroy)
        {
            burning(Destroy.BURNING_LOW, animBurningLow);
        }
        else if (getLifePercent() <= 20 && (Destroy.NONE == destroy || Destroy.BURNING_LOW == destroy))
        {
            burning(Destroy.BURNING_HIGH, animBurningHigh);
        }
    }
}
