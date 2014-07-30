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
package com.b3dgs.warcraft.entity;

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.configurable.Configurable;
import com.b3dgs.lionengine.game.configurable.TileSizeData;
import com.b3dgs.lionengine.game.purview.Fabricable;
import com.b3dgs.lionengine.game.strategy.ability.producer.FactoryProductionStrategy;
import com.b3dgs.warcraft.AppWarcraft;
import com.b3dgs.warcraft.RaceType;

/**
 * The production factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryProduction
        extends FactoryProductionStrategy<Entity, ProductionCost, ProducibleEntity>
{
    /**
     * Constructor.
     */
    public FactoryProduction()
    {
        super();
    }

    /*
     * FactoryProductionStrategy
     */

    @Override
    public ProducibleEntity create(Class<? extends Entity> type)
    {
        final Configurable configurable = getSetup(type).getConfigurable();
        final int step = configurable.getInteger("steps", "cost");
        final int gold = configurable.getInteger("gold", "cost");
        final int wood = configurable.getInteger("wood", "cost");
        final TileSizeData tileSizeData = configurable.getTileSize();

        final ProductionCost cost = new ProductionCost(step, gold, wood);
        final ProducibleEntity producible = new ProducibleEntity(type, cost, tileSizeData.getWidthInTile(),
                tileSizeData.getHeightInTile());

        return producible;
    }

    @Override
    public ProducibleEntity create(Class<? extends Entity> type, int tx, int ty)
    {
        final ProducibleEntity producible = create(type);

        producible.setLocation(tx, ty);

        return producible;
    }

    @Override
    protected SetupGame createSetup(Class<? extends Fabricable> type)
    {
        final RaceType race = RaceType.getRace(type);
        final Media config = Core.MEDIA.create(AppWarcraft.ENTITIES_DIR, race.getPath(), type.getSimpleName() + ".xml");
        return new SetupGame(config);
    }
}
