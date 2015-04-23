package de.cau.cs.kieler.magicdraw.plugin;

/*
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 *
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2013 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */

import java.util.List;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.actions.ActionsCategory;
import com.nomagic.actions.ActionsManager;
import com.nomagic.actions.NMAction;
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager;
import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.actions.ActionsID;
import com.nomagic.magicdraw.actions.MDAction;
import com.nomagic.magicdraw.plugins.Plugin;

/**
 * Starting point for MagicDraw Plugin. Generates the MenuEntry for Automatic Layout with KIELER
 * 
 * @author nbw
 */
public class KIELERLayoutPlugin extends Plugin {

    /**
     * MagicDraw calls this method to identify if this plugin is supported. Plugin is initialized
     * and started only if this method returns true. Override this method to check specific
     * conditions for the plugin supportability.
     * 
     * @return true, if plugin is supported; false, if plugin is not supported.
     */
    @Override
    public boolean isSupported() {
        return true;
    }

    /**
     * Plugin initialization method. Every plugin must override this method and do any action
     * related to plugin initialization. For example registers actions configurators to MagicDraw
     * application. This method is called by MagicDraw application during MagicDraw startup.
     */
    @Override
    public void init() {

        // Generate Menu Item
        final MDAction action =
                new KIELERLayoutDiagramAction("KIELERLAYOUT", "Automatic Layout with KIELER",
                        null, ActionsGroups.DIAGRAM_OPENED_RELATED);

        // Create configurator to insert menu item
        AMConfigurator conf = new AMConfigurator() {

            @Override
            public int getPriority() {
                return AMConfigurator.MEDIUM_PRIORITY;
            }

            @Override
            public void configure(ActionsManager mngr) {

                // Search existing menu item to append new action
                NMAction quickLayout = mngr.getActionFor(ActionsID.QUICK_DIAGRAM_LAYOUT);

                if (quickLayout != null) {
                    // Grab category of layout entries
                    ActionsCategory cat = (ActionsCategory) mngr.getActionParent(quickLayout);
                    // Get list of actions from category
                    // Insert new action after quick layout
                    List<NMAction> actionsInCat = cat.getActions();
                    actionsInCat.add(actionsInCat.indexOf(quickLayout) + 1, action);
                    cat.setActions(actionsInCat);
                } else {
                    System.out.println("[KielerConnector] Menu Hook not found!");
                }
            }
        };

        // Insert configurator into magicdraw
        ActionsConfiguratorsManager.getInstance().addMainMenuConfigurator(conf);
    }

    /**
     * MagicDraw calls this method before exiting the application. If at least one plugin returns
     * 'false', MagicDraw application will not exit. Override this method and do any exit specific
     * action(your plugin state saving and etc).
     * 
     * @return true, if plugin can be closed normally; false, if plugin cannot be closed and
     *         MagicDraw application can not exit.
     */
    @Override
    public boolean close() {
        return true;
    }

}
