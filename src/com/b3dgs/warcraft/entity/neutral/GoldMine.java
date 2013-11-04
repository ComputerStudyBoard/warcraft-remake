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
package com.b3dgs.warcraft.entity.neutral;

import com.b3dgs.lionengine.game.rts.ability.extractor.Extractible;
import com.b3dgs.warcraft.ResourceProgressive;
import com.b3dgs.warcraft.ResourceType;
import com.b3dgs.warcraft.entity.Building;
import com.b3dgs.warcraft.entity.SetupEntity;

/**
 * Gold mine building implementation. This building allows to extract gold with a worker.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class GoldMine
        extends Building
        implements Extractible<ResourceType>
{
    /** Gold amount. */
    private final ResourceProgressive gold;
    /** Resource type. */
    private final ResourceType typeResource;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public GoldMine(SetupEntity setup)
    {
        super(setup);
        typeResource = ResourceType.GOLD;
        gold = new ResourceProgressive(100);
        setFrame(1);
    }

    /*
     * Building
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        gold.update(extrp, 5.0);
    }

    /*
     * Extractible
     */

    @Override
    public int extractResource(int amount)
    {
        final int decreased = gold.decrease(amount);
        if (gold.isEmpty())
        {
            kill();
        }
        return decreased;
    }

    @Override
    public int getResourceQuantity()
    {
        return gold.getCurrent();
    }

    @Override
    public ResourceType getResourceType()
    {
        return typeResource;
    }
}
