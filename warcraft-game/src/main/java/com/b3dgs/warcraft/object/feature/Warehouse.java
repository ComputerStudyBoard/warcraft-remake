/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.warcraft.object.feature;

import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;

/**
 * Represents warehouse storage.
 */
@FeatureInterface
public class Warehouse extends FeatureModel implements Tiled
{
    @FeatureGet private Pathfindable pathfindable;

    /**
     * Create warehouse.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Warehouse(Services services, Setup setup)
    {
        super(services, setup);
    }

    @Override
    public int getInTileX()
    {
        return pathfindable.getInTileX();
    }

    @Override
    public int getInTileY()
    {
        return pathfindable.getInTileY();
    }

    @Override
    public int getInTileWidth()
    {
        return pathfindable.getInTileWidth();
    }

    @Override
    public int getInTileHeight()
    {
        return pathfindable.getInTileHeight();
    }
}
