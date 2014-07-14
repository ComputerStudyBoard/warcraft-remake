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

import com.b3dgs.lionengine.audio.AudioWav;
import com.b3dgs.lionengine.audio.Wav;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;

/**
 * Handle the SFX.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum Sfx
{
    /** Blank. */
    BLANK("blank"),
    /** Blizzard. */
    BLIZZARD("blizzard"),
    /** Click. */
    CLICK("click");

    /** Audio file extension. */
    private static final String AUDIO_FILE_EXTENSION = ".wav";

    /** Sound enabled. */
    private static boolean enabled;

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public static void setEnabled(boolean enabled)
    {
        Sfx.enabled = enabled;
        if (enabled)
        {
            Sfx.BLANK.play();
        }
    }

    /**
     * Stop all sounds.
     */
    public static void stopAll()
    {
        if (Sfx.enabled)
        {
            for (final Sfx sfx : Sfx.values())
            {
                sfx.stop();
            }
        }
    }

    /**
     * Terminate all sounds.
     */
    public static void terminateAll()
    {
        if (Sfx.enabled)
        {
            for (final Sfx sfx : Sfx.values())
            {
                sfx.terminate();
            }
        }
    }

    /** Sounds list composing the effect. */
    private final Wav sound;

    /**
     * Constructor.
     * 
     * @param sound The sound.
     */
    private Sfx(String sound)
    {
        this(sound, 1);
    }

    /**
     * Constructor.
     * 
     * @param sound The sound.
     * @param count The total number of sound.
     */
    private Sfx(String sound, int count)
    {
        final Media media = Core.MEDIA.create(AppWarcraft.SFX_DIR, sound + Sfx.AUDIO_FILE_EXTENSION);
        this.sound = AudioWav.loadWav(media, count);
    }

    /**
     * Play the sound effect.
     * 
     * @param delay The sound delay.
     */
    public void play(int delay)
    {
        if (Sfx.enabled)
        {
            sound.play(delay);
        }
    }

    /**
     * Play the sound effect.
     */
    public void play()
    {
        if (Sfx.enabled)
        {
            sound.play();
        }
    }

    /**
     * Stop all channels.
     */
    private void stop()
    {
        sound.stop();
    }

    /**
     * Terminate all channels.
     */
    private void terminate()
    {
        sound.terminate();
    }
}
