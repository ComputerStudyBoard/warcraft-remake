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
package com.b3dgs.warcraft.skill;

import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.strategy.ControlPanelModel;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.warcraft.entity.BuildingProducer;
import com.b3dgs.warcraft.entity.EntityType;
import com.b3dgs.warcraft.entity.FactoryProduction;
import com.b3dgs.warcraft.entity.ProducibleEntity;

/**
 * Skill build implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class SkillProduceEntity
        extends Skill
{
    /** Production factory. */
    protected final FactoryProduction factoryProduction;
    /** Entity type to produce. */
    private final EntityType entity;
    /** The production cost gold. */
    private final int gold;
    /** The production cost wood. */
    private final int wood;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param entity The entity type to produce.
     */
    protected SkillProduceEntity(SetupSkill setup, EntityType entity)
    {
        super(setup);
        this.entity = entity;
        factoryProduction = setup.factoryProduction;
        final Configurable config = factoryProduction.getSetup(entity).configurable;
        gold = config.getDataInteger("gold", "cost");
        wood = config.getDataInteger("wood", "cost");
        setOrder(false);
    }

    /*
     * Skill
     */

    @Override
    public void action(ControlPanelModel<?> panel, CursorStrategy cursor)
    {
        if (owner instanceof BuildingProducer)
        {
            final ProducibleEntity producible = factoryProduction.create(entity, destX, destY);
            ((BuildingProducer) owner).addToProductionQueue(producible);
        }
    }

    @Override
    public String getDescription()
    {
        final StringBuilder description = new StringBuilder(super.getDescription());
        description.append(":     ").append(gold).append("      ").append(wood);
        return description.toString();
    }
}
