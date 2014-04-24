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
package com.b3dgs.warcraft.launcher;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.warcraft.AppWarcraft;
import com.b3dgs.warcraft.projectile.FactoryProjectile;
import com.b3dgs.warcraft.projectile.HandlerProjectile;

/**
 * Factory launcher projectile.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryLauncher
        extends FactoryObjectGame<SetupLauncher, LauncherProjectile>
{
    /** Factory projectile. */
    private final FactoryProjectile factoryProjectile;
    /** Handler projectile. */
    private final HandlerProjectile handlerProjectile;

    /**
     * Constructor.
     * 
     * @param factoryProjectile The factory projectile.
     * @param handlerProjectile The handler projectile.
     */
    public FactoryLauncher(FactoryProjectile factoryProjectile, HandlerProjectile handlerProjectile)
    {
        super(AppWarcraft.LAUNCHERS_DIR);
        this.factoryProjectile = factoryProjectile;
        this.handlerProjectile = handlerProjectile;
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupLauncher createSetup(Class<? extends LauncherProjectile> type, Media config)
    {
        return new SetupLauncher(config, factoryProjectile, handlerProjectile);
    }
}
