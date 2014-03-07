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

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.strategy.ability.producer.FactoryProductionStrategy;

/**
 * The production factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryProduction
        extends FactoryProductionStrategy<EntityType, ProductionCost, ProducibleEntity>
{
    /**
     * Constructor.
     */
    public FactoryProduction()
    {
        super(EntityType.class);
        load();
    }

    /*
     * FactoryProductionRts
     */

    @Override
    public ProducibleEntity create(EntityType type)
    {
        final Configurable config = getSetup(type).configurable;
        final int step = config.getDataInteger("steps", "cost");
        final int gold = config.getDataInteger("gold", "cost");
        final int wood = config.getDataInteger("wood", "cost");
        final int width = config.getDataInteger("widthInTile", "size");
        final int height = config.getDataInteger("heightInTile", "size");

        final ProductionCost cost = new ProductionCost(step, gold, wood);
        final ProducibleEntity producible = new ProducibleEntity(type, cost, height, width);

        return producible;
    }

    @Override
    public ProducibleEntity create(EntityType type, int tx, int ty)
    {
        final ProducibleEntity producible = create(type);

        producible.setLocation(tx, ty);

        return producible;
    }

    @Override
    protected SetupGame createSetup(EntityType type)
    {
        final Media config = UtilityMedia.get("entities", type.getPathName() + ".xml");
        return new SetupGame(config);
    }
}
