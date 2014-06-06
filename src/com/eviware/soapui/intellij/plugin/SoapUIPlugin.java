/*
 *  SoapUI IntelliJ Plugin, copyright (C) 2006-2011 smartbear.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.eviware.soapui.intellij.plugin;

import com.eviware.soapui.SoapUI;
import com.eviware.soapui.SwingPluginSoapUICore;
import com.eviware.soapui.SwingSoapUICore;
import com.eviware.soapui.actions.SoapUIPreferencesAction;
import com.eviware.soapui.impl.WorkspaceImpl;
import com.eviware.soapui.impl.wsdl.panels.teststeps.support.PropertyHolderTable;
import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.TestPropertyHolder;
import com.eviware.soapui.model.settings.Settings;
import com.eviware.soapui.model.testsuite.LoadTestRunner;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.tree.SoapUITreeNode;
import com.eviware.soapui.model.util.PanelBuilderRegistry;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.model.workspace.WorkspaceFactory;
import com.eviware.soapui.monitor.TestMonitor;
import com.eviware.soapui.monitor.support.TestMonitorListenerAdapter;
import com.eviware.soapui.settings.UISettings;
import com.eviware.soapui.support.SoapUIException;
import com.eviware.soapui.support.UISupport;
import com.eviware.soapui.support.action.swing.SwingActionDelegate;
import com.eviware.soapui.support.components.JPropertiesTable;
import com.eviware.soapui.support.log.TabbedLog4JMonitor;
import com.eviware.soapui.ui.Navigator;
import com.eviware.soapui.ui.NavigatorListener;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.eviware.soapui.ui.support.DesktopListenerAdapter;
import com.eviware.x.impl.swing.SwingDialogs;
import com.eviware.x.impl.swing.SwingFileDialogs;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main Plugin class, sets up SoapUI core and creates toolwindows
 */

public class SoapUIPlugin implements ProjectComponent {
    private Project myProject;

    private static Logger log;
    public static final String WORKSPACE_WINDOW_ID = "soapUI Navigator";
    private Navigator navigator;
    private static Workspace workspace;
    public static final String LOG_WINDOW_ID = "soapUI Log";
    private JPanel overviewPanel;
    private static final String DEFAULT_WORKSPACE_FILE = "intellij-soapui-workspace.xml";
    private static SwingSoapUICore soapUICore;
    private SwingDialogs swingDialogs;
    private SwingFileDialogs swingFileDialogs;
    private Window window;
    private IntellijSoapUIDesktop desktop;
    private JPanel logPanel;
    private InternalDesktopListenerAdapter internalDesktopListenerAdapter;
    public static final String NAME = "SoapUI Plugin";
    private boolean initialized = false;
    private static AutoSaveTimerTask autoSaveTimerTask;
    private static Timer backgroundTimer;


    public SoapUIPlugin(Project project) {
        myProject = project;
    }

    public void projectOpened() {

    }

    public boolean isInitialized() {
        return initialized;
    }

    public void init() {
        initialized = true;

        initToolWindow();
        window = WindowManager.getInstance().suggestParentWindow(myProject);
        UISupport.setMainFrame(window);
        swingDialogs = new SwingDialogs(UISupport.getMainFrame());
        UISupport.setDialogs(swingDialogs);
        swingFileDialogs = new SwingFileDialogs(UISupport.getMainFrame());
        UISupport.setFileDialogs(swingFileDialogs);

        WindowManager.getInstance().getFrame(myProject).addWindowFocusListener(new WindowAdapter() {
            public void windowGainedFocus(WindowEvent windowEvent) {
                SoapUI.setNavigator(navigator);
                SoapUI.setDesktop(desktop);
                UISupport.setMainFrame(window);
                UISupport.setDialogs(swingDialogs);
                UISupport.setFileDialogs(swingFileDialogs);

                if (soapUICore == null) {
                    initSoapUICore();
                }

                //  logPanel.add(SoapUI.getLogMonitor().getComponent(), BorderLayout.CENTER);
            }
        });
    }

    public void projectClosed() {
        if (initialized) {
            unregisterToolWindow();

            navigator = null;
            desktop.removeDesktopListener(internalDesktopListenerAdapter);
            desktop.release();
        }
    }

    public void initComponent() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(SoapUI.class.getClassLoader());

        try {
//            BrowserComponent.setDisabled(true);

            if (soapUICore == null) {
                initSoapUICore();
            }

            if (workspace == null) {
                initWorkspace();
            }

            navigator = new Navigator(workspace);
            navigator.addNavigatorListener(new InternalNavigatorListener());

            desktop = new IntellijSoapUIDesktop(workspace, myProject);
            internalDesktopListenerAdapter = new InternalDesktopListenerAdapter();
            desktop.addDesktopListener(internalDesktopListenerAdapter);

            SoapUI.setNavigator(navigator);
            SoapUI.setDesktop(desktop);
        }
        catch (Exception e) {
            SoapUI.logError(e);
        }
        finally {
            Thread.currentThread().setContextClassLoader(loader);
        }
    }

    private void initWorkspace() throws SoapUIException {
        workspace = WorkspaceFactory.getInstance().openWorkspace(
                System.getProperty("user.home") + File.separatorChar + DEFAULT_WORKSPACE_FILE, null);

        soapUICore.afterStartup(workspace);

        TestMonitor testMonitor = SoapUI.getTestMonitor();

        testMonitor.addTestMonitorListener(new InternalTestMonitorListener());
        testMonitor.init(workspace);
        initAutoSaveTimer();
    }

    private void initSoapUICore() {
        soapUICore = new SwingPluginSoapUICore() {
            @Override
            protected String importSettingsOnStartup(String fileName) throws Exception {
// dont show the popup..
                return fileName;
            }
        };

        SoapUI.initLogMonitor(true, "soapui", new TabbedLog4JMonitor());
        SoapUIPreferencesAction soapUIPreferencesAction = new SoapUIPreferencesAction();

        log = Logger.getLogger(SoapUIPlugin.class);
        SwingActionDelegate.switchClassloader = true;
    }
    public static void initAutoSaveTimer()
    {
        Settings settings = SoapUI.getSettings();
        long interval = settings.getLong( UISettings.AUTO_SAVE_INTERVAL, 0 );

        if( autoSaveTimerTask != null )
        {
            if( interval == 0 )
                SoapUI.log( "Idea Cancelling AutoSave Timer" );

            autoSaveTimerTask.cancel();
            autoSaveTimerTask = null;
        }

        if( interval > 0 )
        {
            autoSaveTimerTask = new AutoSaveTimerTask();

            SoapUI.log( "Idea Scheduling autosave every " + interval + " minutes" );

            if( backgroundTimer == null ) {
                backgroundTimer = new Timer("AutoSave Timer");
            }

            backgroundTimer.schedule( autoSaveTimerTask, interval * 1000 * 60, interval * 1000 * 60 );
        }
    }
    private static class AutoSaveTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            SoapUI.log( "Idea Autosaving Workspace" );
            ( (WorkspaceImpl)workspace ).save( false, true );
        }
    }


    public void disposeComponent() {
        if (workspace != null) {
            workspace.save(false);
            try {
                SoapUI.saveSettings();
            }
            catch (Exception e) {
                SoapUI.logError(e);
            }
        }
    }

    public String getComponentName() {
        return NAME;
    }

    public void initToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);

        ToolWindow toolWindow = toolWindowManager.registerToolWindow(WORKSPACE_WINDOW_ID, true, ToolWindowAnchor.LEFT);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(buildMainPanel(), "", false); // first arg is a JPanel
        toolWindow.getContentManager().addContent(content);

        logPanel = new JPanel(new BorderLayout());
        logPanel.add(SoapUI.getLogMonitor().getComponent(), BorderLayout.CENTER);

        toolWindowManager.registerToolWindow(LOG_WINDOW_ID, logPanel, ToolWindowAnchor.BOTTOM);
    }

    public void unregisterToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
        toolWindowManager.unregisterToolWindow(WORKSPACE_WINDOW_ID);
        toolWindowManager.unregisterToolWindow(LOG_WINDOW_ID);
    }

    private JComponent buildMainPanel() {
        JSplitPane splitPane = UISupport.createVerticalSplit();

        splitPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        splitPane.setTopComponent(navigator);
        splitPane.setBottomComponent(buildOverviewPanel());
        splitPane.setDividerLocation(0.7);
        splitPane.setResizeWeight(0.7);
        return splitPane;
    }

    private Component buildOverviewPanel() {
        overviewPanel = new JPanel(new BorderLayout());
        return overviewPanel;
    }

    private void setOverviewPanel(Component panel) {
        if (overviewPanel == null || overviewPanel.getComponentCount() == 0 && panel == null) {
            return;
        }

        overviewPanel.removeAll();
        if (panel != null) {
            overviewPanel.add(panel, BorderLayout.CENTER);
        }

        overviewPanel.revalidate();
        overviewPanel.repaint();
    }

    public class InternalNavigatorListener implements NavigatorListener {
        public void nodeSelected(SoapUITreeNode treeNode) {
            if (treeNode == null) {
                setOverviewPanel(null);
            } else {
                if (treeNode == null) {
                    setOverviewPanel(null);
                } else {
                    ModelItem modelItem = treeNode.getModelItem();
                    PropertyHolderTable propertyHolderTable = null;

                    if (modelItem instanceof TestPropertyHolder) {
                        propertyHolderTable = new PropertyHolderTable((TestPropertyHolder) modelItem);
                    }

                    PanelBuilder<ModelItem> panelBuilder = PanelBuilderRegistry.getPanelBuilder(modelItem);
                    if (panelBuilder != null && panelBuilder.hasOverviewPanel()) {
                        Component overviewPanel = panelBuilder.buildOverviewPanel(modelItem);
                        if (propertyHolderTable != null) {
                            JTabbedPane tabs = new JTabbedPane();
                            if (overviewPanel instanceof JPropertiesTable) {
                                JPropertiesTable<?> t = (JPropertiesTable<?>) overviewPanel;
                                tabs.addTab(t.getTitle(), overviewPanel);
                                t.setTitle(null);
                            } else {
                                tabs.addTab("Overview", overviewPanel);
                            }

                            tabs.addTab(((TestPropertyHolder) modelItem).getPropertiesLabel(), propertyHolderTable);
                            overviewPanel = UISupport.createTabPanel(tabs, false);
                        }

                        setOverviewPanel(overviewPanel);
                    } else {
                        setOverviewPanel(null);
                    }
                }
            }
        }
    }

    private static final class InternalTestMonitorListener extends TestMonitorListenerAdapter {
        private Set<LoadTestRunner> loadTestRunners = new HashSet<LoadTestRunner>();

        public void testCaseStarted( TestCaseRunner runner )
        {}

        public void loadTestStarted(LoadTestRunner runner) {
            if (loadTestRunners.isEmpty()) {
                log.info("Disabling httpclient and groovy logs during loadtests");
                Logger.getLogger("httpclient.wire").setLevel(Level.OFF);
                Logger.getLogger("groovy.log").setLevel(Level.OFF);
            }

            loadTestRunners.add(runner);
        }



        public void loadTestFinished(LoadTestRunner runner) {
            loadTestRunners.remove(runner);

            if (loadTestRunners.isEmpty()) {
                Logger.getLogger("httpclient.wire").setLevel(Level.DEBUG);
                Logger.getLogger("groovy.log").setLevel(Level.DEBUG);
                log.info("Enabled httpclient and groovy logs after loadtests");
            }
        }
    }

    private class InternalDesktopListenerAdapter extends DesktopListenerAdapter {
        public void desktopPanelSelected(DesktopPanel desktopPanel) {
            ModelItem modelItem = desktopPanel.getModelItem();
            if (modelItem != null && navigator != null) {
                navigator.selectModelItem(modelItem);
            }
        }
    }
}
