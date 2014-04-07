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
package com.b3dgs.warcraft;

/**
 * Game configuration.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class GameConfig
{
    /** Player race. */
    public final RaceType player;
    /** Opponent race. */
    public final RaceType opponent;
    /** Game map. */
    public final String map;
    /** Fog of war state. */
    public final boolean hide;
    /** Fog state. */
    public final boolean fog;

    /**
     * Constructor.
     * 
     * @param player The player race.
     * @param opponent The opponent race.
     * @param map The game map.
     * @param hide <code>true</code> to enable fog of war map hiding, <code>false</code> else.
     * @param fog <code>true</code> to enable fog map, <code>false</code> else.
     */
    public GameConfig(RaceType player, RaceType opponent, String map, boolean hide, boolean fog)
    {
        this.player = player;
        this.opponent = opponent;
        this.map = map;
        this.hide = hide;
        this.fog = fog;
    }
}
