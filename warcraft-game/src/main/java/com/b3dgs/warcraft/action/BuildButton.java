/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.warcraft.action;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.selector.Hud;
import com.b3dgs.lionengine.game.feature.collidable.selector.Selectable;
import com.b3dgs.lionengine.game.feature.producible.Producer;
import com.b3dgs.lionengine.game.feature.producible.ProducerListenerVoid;
import com.b3dgs.lionengine.game.feature.producible.Producible;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.CoordTile;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.MapTilePath;
import com.b3dgs.lionengine.game.feature.tile.map.pathfinding.Pathfindable;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.warcraft.Resources;
import com.b3dgs.warcraft.Sfx;
import com.b3dgs.warcraft.object.CostConfig;
import com.b3dgs.warcraft.object.EntityModel;
import com.b3dgs.warcraft.object.feature.EntitySfx;

/**
 * Build button action.
 */
public class BuildButton extends ActionModel
{
    private final Media target;
    private Rectangle area;

    private final Factory factory = services.get(Factory.class);
    private final Viewer viewer = services.get(Viewer.class);
    private final InputDevicePointer pointer = services.get(InputDevicePointer.class);
    private final Hud hud = services.get(Hud.class);
    private final Resources resources = services.get(Resources.class);

    /**
     * Create build button action.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public BuildButton(Services services, Setup setup)
    {
        super(services, setup);

        target = Medias.create(setup.getText("media").split("/"));
    }

    @Override
    protected void action()
    {
        final SizeConfig size = SizeConfig.imports(new Xml(target));
        area = new Rectangle(0, 0, size.getWidth(), size.getHeight());
        cursor.setVisible(false);
        hud.setCancelShortcut(() -> pointer.hasClickedOnce(3));
    }

    @Override
    protected void assign()
    {
        for (final Selectable selectable : selector.getSelection())
        {
            final CostConfig config = CostConfig.imports(new Configurer(target));
            if (resources.isAvailableWood(config.getWood()) && resources.isAvailableGold(config.getGold()))
            {
                resources.decreaseWood(config.getWood());
                resources.decreaseGold(config.getGold());

                final Featurable building = factory.create(target);
                final Producible producible = building.getFeature(Producible.class);
                producible.setLocation(area.getX(), area.getY());

                final Producer producer = selectable.getFeature(Producer.class);
                final Transformable transformable = producer.getFeature(Transformable.class);
                producer.setChecker(featurable -> UtilMath.getDistance(featurable.getFeature(Producible.class),
                                                                       transformable) < map.getTileWidth());

                producer.addToProductionQueue(building);

                final Pathfindable pathfindable = producer.getFeature(Pathfindable.class);
                pathfindable.setDestination(area);

                final EntityModel model = producer.getFeature(EntityModel.class);
                producer.addListener(new ProducerListenerVoid()
                {
                    @Override
                    public void notifyStartProduction(Featurable featurable)
                    {
                        pathfindable.stopMoves();
                        pathfindable.clearPath();
                        featurable.getFeature(Pathfindable.class)
                                  .setLocation(map.getInTileX(producible), map.getInTileY(producible));
                        featurable.getFeature(EntitySfx.class).onStarted();
                        model.setVisible(false);
                    }

                    @Override
                    public void notifyProduced(Featurable featurable)
                    {
                        model.setVisible(true);
                        final CoordTile coord = map.getFeature(MapTilePath.class)
                                                   .getFreeTileAround(pathfindable,
                                                                      featurable.getFeature(Pathfindable.class));
                        pathfindable.setLocation(coord);
                        featurable.getFeature(EntitySfx.class).onProduced();
                    }
                });
            }
        }
        area = null;
        cursor.setVisible(true);
        hud.clearMenus();
        hud.setCancelShortcut(() -> false);
        Sfx.NEUTRAL_BUILD.play();
    }

    @Override
    public void update(double extrp)
    {
        if (area != null)
        {
            area.set(UtilMath.getRounded(cursor.getX(), cursor.getWidth()),
                     UtilMath.getRounded(cursor.getY(), cursor.getHeight()),
                     area.getWidthReal(),
                     area.getHeightReal());
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (area != null && viewer.isViewable((Localizable) cursor, 0, 0))
        {
            g.setColor(ColorRgba.GREEN);
            g.drawRect(viewer, Origin.BOTTOM_LEFT, area, false);
        }
    }
}
