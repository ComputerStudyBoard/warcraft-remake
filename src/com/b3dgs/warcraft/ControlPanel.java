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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Rectangle;
import com.b3dgs.lionengine.Text;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;
import com.b3dgs.lionengine.game.Bar;
import com.b3dgs.lionengine.game.strategy.CameraStrategy;
import com.b3dgs.lionengine.game.strategy.ControlPanelModel;
import com.b3dgs.lionengine.game.strategy.CursorStrategy;
import com.b3dgs.lionengine.game.strategy.ability.extractor.Extractible;
import com.b3dgs.warcraft.entity.BuildingProducer;
import com.b3dgs.warcraft.entity.Entity;
import com.b3dgs.warcraft.entity.EntityType;
import com.b3dgs.warcraft.skill.Skill;
import com.b3dgs.warcraft.skill.SkillType;

/**
 * Control panel implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class ControlPanel
        extends ControlPanelModel<Entity>
{
    /** Text. */
    private final Text text;
    /** Surface. */
    private final Sprite sprite;
    /** Entity stats. */
    private final Sprite entityStats;
    /** Progress bar. */
    private final Sprite progress;
    /** Cursor reference. */
    private final Cursor cursor;
    /** Last selection. */
    private Set<Entity> lastSelection;
    /** Player reference for this panel. */
    private Player player;
    /** Entity life bar. */
    private final Bar barLife;
    /** Building progress bar. */
    private final Bar barProgress;

    /**
     * Constructor.
     * 
     * @param cursor The cursor reference.
     */
    ControlPanel(Cursor cursor)
    {
        super();
        this.cursor = cursor;
        barLife = new Bar(27, 3);
        barProgress = new Bar(62, 6);
        barProgress.setColorForeground(ColorRgba.GREEN);
        text = UtilityImage.createText(Text.DIALOG, 9, TextStyle.NORMAL);
        sprite = Drawable.loadSprite(UtilityMedia.get("hud.png"));
        entityStats = Drawable.loadSprite(UtilityMedia.get("entity_stats.png"));
        progress = Drawable.loadSprite(UtilityMedia.get("progress.png"));
        lastSelection = null;
        setClickSelection(Click.LEFT);

        sprite.load(false);
        entityStats.load(false);
        progress.load(false);
    }

    /**
     * Render panel.
     * 
     * @param g The graphics output.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    public void render(Graphic g, CursorStrategy cursor, CameraStrategy camera)
    {
        sprite.render(g, 0, 0);

        // Render the selection if has
        if (lastSelection != null)
        {
            if (lastSelection.size() == 1)
            {
                renderSingleEntity(g, 2, 76, lastSelection.iterator().next(), cursor, camera);
            }
            else if (lastSelection.size() > 1)
            {
                ControlPanel.renderMultipleEntity(g, lastSelection, cursor, camera);
            }
        }

        // Render player resources
        text.draw(g, 136, 1, String.valueOf(player.getWood()));
        text.draw(g, 235, 1, String.valueOf(player.getGold()));
    }

    /**
     * Set the player reference for the panel information.
     * 
     * @param player The player reference.
     */
    public void setPlayer(Player player)
    {
        this.player = player;
    }

    /**
     * Get the player in control.
     * 
     * @return The player in control.
     */
    public Player getPlayer()
    {
        return player;
    }

    /**
     * Update a single entity and its skills.
     * 
     * @param entity The entity to update.
     * @param cursor The cursor reference.
     * @param extrp The extrapolation value.
     */
    private void updateSingleEntity(Entity entity, CursorStrategy cursor, double extrp)
    {
        if (entity.getPlayer() == player)
        {
            for (final Skill skill : entity.getSkills(entity.getSkillPanel()))
            {
                if (skill.isIgnored())
                {
                    continue;
                }
                skill.updateOnPanel(cursor, this);
            }
        }
    }

    /**
     * Update entities and their skills.
     * 
     * @param entities The entities to update.
     * @param cursor The cursor reference.
     * @param extrp The extrapolation value.
     */
    private void updateMultipleEntity(Set<Entity> entities, CursorStrategy cursor, double extrp)
    {
        final Collection<SkillType> skills = ControlPanel.getSkillsInCommon(entities);
        for (final Entity entity : entities)
        {
            for (final Skill skill : entity.getSkills(entity.getSkillPanel()))
            {
                if (skills.contains(skill.getType()))
                {
                    skill.updateOnPanel(cursor, this);
                }
            }
        }
    }

    /**
     * Render a single entity with its details.
     * 
     * @param g The graphics output.
     * @param x The rendering location x.
     * @param y The rendering location y.
     * @param entity The entity to render.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    private void renderSingleEntity(Graphic g, int x, int y, Entity entity, CursorStrategy cursor, CameraStrategy camera)
    {
        // Entity stats
        entityStats.render(g, x, y);
        entity.getIcon().render(g, x + 4, y + 4);
        text.draw(g, x + 4, y + 25, entity.getName());
        final int life = entity.getLifePercent();
        ColorRgba color = ColorRgba.GREEN;
        if (life <= 50)
        {
            color = ColorRgba.YELLOW;
        }
        if (life < 25)
        {
            color = ColorRgba.RED;
        }
        barLife.setLocation(x + 35, y + 20);
        barLife.setWidthPercent(entity.getLifePercent());
        barLife.setColorForeground(color);
        barLife.render(g);

        // Gold amount
        if (entity instanceof Extractible)
        {
            text.draw(g, x + 4, y + 50, String.valueOf(((Extractible<?>) entity).getResourceQuantity()));
        }

        if (entity.getPlayer() == player)
        {
            // Under production
            if (entity.getProgressPercent() < 100)
            {
                renderProductionProgress(g, x, y, entity.getProgressPercent());
            }
            else
            {
                if (entity instanceof BuildingProducer && ((BuildingProducer) entity).isProducing())
                {
                    renderProductionProgress(g, x, y, ((BuildingProducer) entity).getProductionProgressPercent());
                }
                else
                {
                    // Population capacity
                    if (EntityType.FARM_ORC == entity.type || EntityType.FARM_HUMAN == entity.type)
                    {
                        final String population = "Pop: " + String.valueOf(player.getPopulation()) + " of "
                                + String.valueOf(player.getPopulationCapacity());
                        text.draw(g, x + 1, y + 50, population);
                    }

                    // Entity skills
                    for (final Skill skill : entity.getSkills(entity.getSkillPanel()))
                    {
                        if (skill.isIgnored())
                        {
                            continue;
                        }
                        skill.renderOnPanel(g);
                    }
                }
            }
        }
    }

    /**
     * Render a production progress.
     * 
     * @param g The graphics output.
     * @param x The horizontal progress location.
     * @param y The vertical progress location.
     * @param percent The percent.
     */
    private void renderProductionProgress(Graphic g, int x, int y, int percent)
    {
        progress.render(g, x, y + 35);
        barProgress.setLocation(x + 2, y + 37);
        barProgress.setWidthPercent(percent);
        barProgress.render(g);
        text.draw(g, x + 4, y + 36, "% complete");
    }

    /**
     * Render entities with their details.
     * 
     * @param g The graphics output.
     * @param entities The entities to render.
     * @param cursor The cursor reference.
     * @param camera The camera reference.
     */
    private static void renderMultipleEntity(Graphic g, Set<Entity> entities, CursorStrategy cursor,
            CameraStrategy camera)
    {
        final Collection<SkillType> skills = ControlPanel.getSkillsInCommon(entities);
        final Entity entity = entities.iterator().next();
        for (final Skill skill : entity.getSkills(entity.getSkillPanel()))
        {
            if (skills.contains(skill.getType()))
            {
                skill.renderOnPanel(g);
            }
        }
        skills.clear();
    }

    /**
     * Get the list of skill in common.
     * 
     * @param entities Entities list.
     * @return Skill list shared by all entities.
     */
    private static Collection<SkillType> getSkillsInCommon(Set<Entity> entities)
    {
        final Set<SkillType> skillsInCommon = new HashSet<>(4);
        final Entity entity = entities.iterator().next();
        final Collection<Skill> skills = entity.getSkills(entity.getSkillPanel());
        for (final Skill skill : skills)
        {
            if (!skill.isIgnored() && ControlPanel.hasSkillInCommon(entities, skill))
            {
                skillsInCommon.add(skill.getType());
            }
        }
        return skillsInCommon;
    }

    /**
     * Check if current skills is in common with other entities.
     * 
     * @param entities Entities list.
     * @param skill Skill to check.
     * @return <code>true</code> if in common, <code>false</code> else.
     */
    private static boolean hasSkillInCommon(Set<Entity> entities, Skill skill)
    {
        final int size = entities.size();
        int count = 0;
        for (final Entity entityB : entities)
        {
            final Collection<Skill> skillsB = entityB.getSkills(entityB.getSkillPanel());
            for (final Skill skillB : skillsB)
            {
                if (skillB.getType() == skill.getType() && !skillB.isIgnored())
                {
                    count += 1;
                    break;
                }
            }
        }
        return size == count;
    }

    /*
     * ControlPanelModel
     */

    @Override
    public void update(double extrp, CameraStrategy camera, CursorStrategy cursor)
    {
        super.update(extrp, camera, cursor);

        // Update the selection if has
        if (lastSelection != null)
        {
            if (lastSelection.size() == 1)
            {
                final Entity single = lastSelection.iterator().next();
                if (!single.isSelected())
                {
                    lastSelection = null;
                }
                else
                {
                    updateSingleEntity(single, cursor, extrp);
                }
            }
            else if (lastSelection.size() > 1)
            {
                updateMultipleEntity(lastSelection, cursor, extrp);
            }
        }
    }

    @Override
    public void notifyUpdatedSelection(Set<Entity> selection)
    {
        lastSelection = selection;
    }

    @Override
    protected int computeSelectionWidth(CursorStrategy cursor, CameraStrategy camera, int sx, int sy)
    {
        final Rectangle area = getArea();
        final int widthMin = camera.getLocationIntX() - sx + (int) area.getX();
        final int widthMax = camera.getLocationIntX() - sx + (int) (area.getX() + area.getWidth()) - 1;
        return UtilityMath.fixBetween(super.computeSelectionWidth(cursor, camera, sx, sy), widthMin, widthMax);
    }

    @Override
    protected int computeSelectionHeight(CursorStrategy cursor, CameraStrategy camera, int sx, int sy)
    {
        final Rectangle area = getArea();
        final int heightMin = camera.getLocationIntY() - sy + (int) -area.getY() + 1;
        final int heightMax = camera.getLocationIntY() - sy + (int) (-area.getY() + area.getHeight());
        return UtilityMath.fixBetween(super.computeSelectionHeight(cursor, camera, sx, sy), heightMin, heightMax);
    }

    @Override
    protected void onStartOrder()
    {
        if (cursor.getType() != CursorType.BOX)
        {
            cursor.setType(CursorType.CROSS);
        }
    }

    @Override
    protected void onTerminateOrder()
    {
        cursor.setType(CursorType.POINTER);
    }
}
