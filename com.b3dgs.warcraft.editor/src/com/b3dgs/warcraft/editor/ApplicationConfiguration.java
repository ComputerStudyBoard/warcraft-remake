/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.warcraft.editor;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.editor.project.Project;
import com.b3dgs.lionengine.editor.project.handler.ProjectImportHandler;
import com.b3dgs.lionengine.editor.utility.UtilPart;
import com.b3dgs.lionengine.editor.world.WorldModel;
import com.b3dgs.lionengine.editor.world.WorldPart;
import com.b3dgs.lionengine.editor.world.handler.SetPointerCollisionHandler;
import com.b3dgs.lionengine.editor.world.handler.SetShowCollisionsHandler;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileCollision;
import com.b3dgs.lionengine.game.map.MapTileCollisionModel;

/**
 * Configure the editor with the right name.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ApplicationConfiguration
{
    /** Import project argument. */
    private static final String ARG_IMPORT = "-import";

    /** Application reference. */
    @Inject
    private MApplication application;

    /**
     * Execute the injection.
     * 
     * @param eventBroker The event broker service.
     */
    @PostConstruct
    public void execute(IEventBroker eventBroker)
    {
        final MWindow existingWindow = application.getChildren().get(0);
        existingWindow.setLabel(Activator.PLUGIN_NAME);
        eventBroker.subscribe(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE, new AppStartupCompleteEventHandler());
    }

    /**
     * Handler called on startup complete.
     */
    private class AppStartupCompleteEventHandler implements EventHandler
    {
        /**
         * Constructor.
         */
        public AppStartupCompleteEventHandler()
        {
            // Nothing to do
        }

        /**
         * Check if there is a project to import.
         */
        private void checkProjectImport()
        {
            final String[] args = Platform.getApplicationArgs();
            for (int i = 0; i < args.length; i++)
            {
                if (ApplicationConfiguration.ARG_IMPORT.equals(args[i]))
                {
                    i++;
                    if (i < args.length)
                    {
                        importProject(args[i]);

                        final MapTile map = WorldModel.INSTANCE.getMap();
                        map.create(Medias.create("map", "swamp", "swamp.png"));
                        map.createFeature(MapTileCollisionModel.class)
                           .loadCollisions(Medias.create("map", "swamp", MapTileCollision.DEFAULT_FORMULAS_FILE),
                                           Medias.create("map", "swamp", MapTileCollision.DEFAULT_COLLISIONS_FILE));

                        final WorldPart part = UtilPart.getPart(WorldPart.ID, WorldPart.class);
                        part.setToolItemEnabled(SetShowCollisionsHandler.ID, true);
                        part.setToolItemEnabled(SetPointerCollisionHandler.ID, true);
                        part.update();
                    }
                }
            }
        }

        /**
         * Import a project from a path.
         * 
         * @param projectPath The project path.
         */
        private void importProject(String projectPath)
        {
            final File path = new File(projectPath);
            try
            {
                final Project project = Project.create(path.getCanonicalFile());
                ProjectImportHandler.importProject(project);
            }
            catch (final IOException exception)
            {
                Verbose.exception(getClass(), "importProject", exception);
            }
        }

        @Override
        public void handleEvent(Event event)
        {
            checkProjectImport();
        }
    }
}
