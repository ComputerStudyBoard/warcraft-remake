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
package com.b3dgs.warcraft.map;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.warcraft.ControlPanel;
import com.b3dgs.warcraft.entity.Entity;
import com.b3dgs.warcraft.entity.HandlerEntity;
import com.b3dgs.warcraft.entity.Unit;

/**
 * Minimap handler.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Minimap
{
    /** Map reference. */
    private final Map map;
    /** Fog of war reference. */
    private final FogOfWar fogOfWar;
    /** Control Panel reference. */
    private final ControlPanel controlPanel;
    /** Fog of war reference. */
    private final HandlerEntity handlerEntity;
    /** Minimap interact flag. */
    private boolean clicked;
    /** Minimap move flag. */
    private boolean move;
    /** Horizontal minimap location on screen. */
    private final int x;
    /** Vertical minimap location on screen. */
    private final int y;

    /**
     * Constructor.
     * 
     * @param map The map reference.
     * @param fogOfWar The fog of war reference.
     * @param handlerEntity The handler entity reference.
     * @param controlPanel The control panel reference.
     * @param x The minimap horizontal location on screen.
     * @param y The minimap vertical location on screen.
     */
    public Minimap(Map map, FogOfWar fogOfWar, ControlPanel controlPanel, HandlerEntity handlerEntity, int x, int y)
    {
        this.map = map;
        this.fogOfWar = fogOfWar;
        this.handlerEntity = handlerEntity;
        this.controlPanel = controlPanel;
        this.x = x;
        this.y = y;
    }

    /**
     * Update the minimap.
     * 
     * @param cursor The cursor reference.
     * @param handler The handler reference.
     * @param camera The camera reference.
     * @param ox The cursor offset x.
     * @param oy The cursor offset y.
     */
    public void update(CursorStrategy cursor, CameraStrategy camera, HandlerEntity handler, int ox, int oy)
    {
        final int mw = map.getWidthInTile();
        final int mh = map.getHeightInTile();
        // Assign unit destination from minimap
        if (cursor.getClick() == Mouse.RIGHT)
        {
            if (!clicked && isOver(cursor))
            {
                clicked = true;
                final int dx = cursor.getScreenX() - x;
                final int dy = map.getHeightInTile() - (cursor.getScreenY() - y);
                for (final Entity entity : handler.getSelection())
                {
                    if (entity.getPlayer() == controlPanel.getPlayer() && entity instanceof Unit)
                    {
                        ((Unit) entity).setDestination(dx, dy);
                    }
                }
            }
        }
        // Move camera from minimap
        else if (cursor.getClick() == Mouse.LEFT)
        {
            final int cx = cursor.getScreenX() - ox;
            final int cy = mh - cursor.getScreenY();
            if (!clicked)
            {
                clicked = true;
                move = cx > x - ox && cx < mw + x - ox && cy > y - oy && cy < mh + y - oy;
            }
            if (move)
            {
                camera.setLocation(UtilityMath.fixBetween(cx, x - ox, mw + x) * map.getTileWidth(),
                        UtilityMath.fixBetween(cy - oy + oy, y - oy, mh + y) * map.getTileHeight());
            }
        }
        else
        {
            clicked = false;
            move = false;
        }
    }

    /**
     * Render the minimap.
     * 
     * @param g The graphics output.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CameraStrategy camera)
    {
        map.renderMiniMap(g, x, y);
        renderEntity(g);
        renderFog(g);
        renderCamera(g, camera, x, y);
    }

    /**
     * Check if cursor is over minimap.
     * 
     * @param cursor The cursor reference.
     * @return <code>true</code> if cursor is over minimap, <code>false</code> else.
     */
    private boolean isOver(CursorStrategy cursor)
    {
        final int cx = cursor.getScreenX();
        final int cy = cursor.getScreenY();
        return cx >= x && cx <= x + map.getWidthInTile() && cy >= y && cy <= y + map.getHeightInTile();
    }

    /**
     * Render the camera.
     * 
     * @param g The graphics output.
     * @param camera The camera reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    private void renderCamera(Graphic g, CameraStrategy camera, int x, int y)
    {
        g.setColor(ColorRgba.GREEN);
        final int cx = (int) camera.getLocationRealX() / map.getTileWidth() - 1 + x;
        final int cy = (int) camera.getLocationRealY() / map.getTileHeight() - 1 + y;
        final int cw = camera.getViewWidth() / map.getTileWidth() + 1;
        final int ch = camera.getViewHeight() / map.getTileHeight() + 1;
        g.drawRect(cx, map.getHeightInTile() - cy - 1, cw, ch, false);
    }

    /**
     * Render the entity minimap representation.
     * 
     * @param g The graphic output.
     */
    private void renderEntity(Graphic g)
    {
        for (final Entity entity : handlerEntity.list())
        {
            if (entity.isAlive() && entity.isActive())
            {
                final int tx = entity.getLocationInTileX();
                final int ty = entity.getLocationInTileY();
                final int tw = entity.getWidthInTile();
                final int th = entity.getHeightInTile();

                if (fogOfWar.isVisited(tx, ty) && fogOfWar.isFogged(tx, ty))
                {
                    g.setColor(handlerEntity.getEntityColorSelection(entity));
                    g.drawRect(tx + x, map.getHeightInTile() - 1 - th - ty + y + 1, tw, th, true);
                }
            }
        }
    }

    /**
     * Render the fog of war.
     * 
     * @param g The graphics output.
     */
    private void renderFog(Graphic g)
    {
        if (fogOfWar.hasFogOfWar())
        {
            for (int ty = 0; ty < map.getHeightInTile(); ty++)
            {
                for (int tx = 0; tx < map.getWidthInTile(); tx++)
                {
                    if (!fogOfWar.isFogged(tx, ty))
                    {
                        g.setColor(ColorRgba.GRAY_DARK);
                        g.drawRect(tx + x, map.getHeightInTile() - 2 - ty + y, 1, 1, true);
                    }
                    if (!fogOfWar.isVisited(tx, ty))
                    {
                        g.setColor(ColorRgba.BLACK);
                        g.drawRect(tx + x, map.getHeightInTile() - 2 - ty + y, 1, 1, true);
                    }
                }
            }
        }
    }
}
