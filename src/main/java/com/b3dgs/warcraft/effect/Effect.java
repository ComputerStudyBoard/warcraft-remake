/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.warcraft.effect;

import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.effect.EffectGame;

/**
 * Effect implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Effect
        extends EffectGame
{
    /** Sprite. */
    private final SpriteAnimated sprite;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public Effect(SetupSurfaceGame setup)
    {
        super(setup);
        final int horizontalFrames = getDataInteger("horizontal", "frames");
        final int verticalFrames = getDataInteger("vertical", "frames");
        sprite = Drawable.loadSpriteAnimated(setup.surface, horizontalFrames, verticalFrames);
        setSize(sprite.getFrameWidth(), sprite.getFrameHeight());
    }

    /**
     * Start the effect.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void start(int x, int y)
    {
        teleport(x, y);
    }

    /**
     * Play the animation.
     * 
     * @param animation The animation to play.
     */
    public void play(Animation animation)
    {
        sprite.play(animation);
    }

    /**
     * Set the effect frame.
     * 
     * @param frame The frame.
     */
    public void setFrame(int frame)
    {
        sprite.setFrame(frame);
    }

    /**
     * Get the current frame.
     * 
     * @return The current frame.
     */
    public int getFrame()
    {
        return sprite.getFrame();
    }

    /*
     * EffectGame
     */

    @Override
    public void update(double extrp)
    {
        sprite.updateAnimation(extrp);
        if (sprite.getAnimState() == AnimState.FINISHED)
        {
            destroy();
        }
    }

    @Override
    public void render(Graphic g, CameraGame camera)
    {
        sprite.render(g, camera.getViewpointX(getLocationIntX()),
                camera.getViewpointY(getLocationIntY() + sprite.getFrameHeight()));
    }
}
