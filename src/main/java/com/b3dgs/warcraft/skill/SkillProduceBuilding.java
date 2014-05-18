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

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.ControlPanelModel;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.warcraft.Cursor;
import com.b3dgs.warcraft.CursorType;
import com.b3dgs.warcraft.entity.Entity;
import com.b3dgs.warcraft.entity.FactoryProduction;
import com.b3dgs.warcraft.entity.ProducibleEntity;
import com.b3dgs.warcraft.entity.UnitWorker;
import com.b3dgs.warcraft.map.Map;

/**
 * Skill build implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class SkillProduceBuilding
        extends Skill
{
    /** Production factory. */
    protected final FactoryProduction factoryProduction;
    /** Entity type to produce. */
    private final Class<? extends Entity> entity;
    /** Production width in tile. */
    private final int width;
    /** Production height in tile. */
    private final int height;
    /** The production cost gold. */
    private final int gold;
    /** The production cost wood. */
    private final int wood;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Map reference. */
    private final Map map;
    /** To produce. */
    private ProducibleEntity toProduce;

    /**
     * Constructor.
     * 
     * @param setup The setup skill reference.
     * @param entity The entity type to produce.
     */
    protected SkillProduceBuilding(SetupSkill setup, Class<? extends Entity> entity)
    {
        super(setup);
        cursor = setup.cursor;
        map = setup.map;
        this.entity = entity;
        factoryProduction = setup.factoryProduction;
        final Configurable config = factoryProduction.getSetup(entity).configurable;
        width = config.getDataInteger("widthInTile", "lionengine:tileSize");
        height = config.getDataInteger("heightInTile", "lionengine:tileSize");
        gold = config.getDataInteger("gold", "cost");
        wood = config.getDataInteger("wood", "cost");
        setOrder(true);
    }

    /*
     * Skill
     */

    @Override
    public void updateOnMap(double extrp, CameraStrategy camera, CursorStrategy cursor)
    {
        super.updateOnMap(extrp, camera, cursor);
        if (isActive())
        {
            if (map.isAreaAvailable(cursor.getLocationInTileX(), cursor.getLocationInTileY(),
                    toProduce.getWidthInTile(), toProduce.getHeightInTile(), owner.getId().intValue()))
            {
                this.cursor.setBoxColor(ColorRgba.GREEN);
            }
            else
            {
                this.cursor.setBoxColor(ColorRgba.RED);
            }
        }
    }

    @Override
    public void action(ControlPanelModel<?> panel, CursorStrategy cursor)
    {
        if (!map.isAreaAvailable(cursor.getLocationInTileX(), cursor.getLocationInTileY(), toProduce.getWidthInTile(),
                toProduce.getHeightInTile(), owner.getId().intValue()))
        {
            setActive(true);
            panel.ordered();
            this.cursor.setType(CursorType.BOX);
        }
        else if (owner instanceof UnitWorker && toProduce != null)
        {
            toProduce.setLocation(destX, destY);
            ((UnitWorker) owner).addToProductionQueue(toProduce);
        }
    }

    @Override
    public void onClicked(ControlPanelModel<?> panel)
    {
        if (owner instanceof UnitWorker)
        {
            final UnitWorker worker = (UnitWorker) owner;
            final ProducibleEntity producible = factoryProduction.create(entity, 0, 0);
            if (worker.canProduce(producible))
            {
                cursor.setType(CursorType.BOX);
                cursor.setBoxColor(ColorRgba.GREEN);
                cursor.setBoxSize(width, height);
                toProduce = producible;
            }
            else
            {
                worker.notifyCanNotProduce(producible);
                toProduce = null;
                cursor.setType(CursorType.POINTER);
                setActive(false);
                panel.resetOrder();
            }
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
