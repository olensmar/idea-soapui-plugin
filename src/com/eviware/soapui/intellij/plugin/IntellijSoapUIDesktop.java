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

import com.eviware.soapui.model.ModelItem;
import com.eviware.soapui.model.PanelBuilder;
import com.eviware.soapui.model.util.PanelBuilderRegistry;
import com.eviware.soapui.model.workspace.Workspace;
import com.eviware.soapui.ui.desktop.AbstractSoapUIDesktop;
import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.eviware.soapui.ui.desktop.SoapUIDesktop;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * SoapUIDesktop implementation for Intellij
 */

public class IntellijSoapUIDesktop extends AbstractSoapUIDesktop
{
   private FileEditorManager editorManager;
   private Map<DesktopPanel, SoapUIVirtualFile> desktopPanelToFileMap = new HashMap<DesktopPanel, SoapUIVirtualFile>();
   private Map<ModelItem, DesktopPanel> modelItemToDesktopPanelMap = new HashMap<ModelItem, DesktopPanel>();

   public IntellijSoapUIDesktop( Workspace workspace, Project project )
   {
      super( workspace );

      editorManager = FileEditorManager.getInstance( project );

      editorManager.addFileEditorManagerListener( new InternalFileEditorManagerListener() );
   }

   public boolean closeDesktopPanel( DesktopPanel desktopPanel )
   {
      SoapUIVirtualFile vf = desktopPanelToFileMap.get( desktopPanel );
      if( vf != null )
      {
         editorManager.closeFile( vf );
         desktopPanelToFileMap.remove( desktopPanel );
      }

       return true;
   }

   public boolean hasDesktopPanel( ModelItem modelItem )
   {
      return modelItemToDesktopPanelMap.containsKey( modelItem );
   }

   public DesktopPanel showDesktopPanel( ModelItem modelItem )
   {
      PanelBuilder panelBuilder = PanelBuilderRegistry.getPanelBuilder( modelItem ); 

      if( modelItemToDesktopPanelMap.containsKey( modelItem ) )
      {
         DesktopPanel desktopPanel = modelItemToDesktopPanelMap.get( modelItem );
         SoapUIVirtualFile soapUIVirtualFile = desktopPanelToFileMap.get( desktopPanel );
         editorManager.openFile( soapUIVirtualFile, true );
         return desktopPanel;
      }
      else if (panelBuilder != null && panelBuilder.hasDesktopPanel())
      {
         DesktopPanel desktopPanel = showDesktopPanel( panelBuilder.buildDesktopPanel( modelItem ) );
         modelItemToDesktopPanelMap.put( modelItem, desktopPanel );
         return desktopPanel;
      }
      else
			Toolkit.getDefaultToolkit().beep();

      return null;
   }

   public boolean closeDesktopPanel( ModelItem modelItem )
   {
      DesktopPanel desktopPanel = modelItemToDesktopPanelMap.get( modelItem );
      if( desktopPanel != null )
      {
         closeDesktopPanel( desktopPanel );
         modelItemToDesktopPanelMap.remove( modelItem );
      }

       return desktopPanel != null;
   }

   public DesktopPanel[] getDesktopPanels()
   {
      return desktopPanelToFileMap.keySet().toArray( new DesktopPanel[desktopPanelToFileMap.size()] );
   }

   public DesktopPanel getDesktopPanel( ModelItem modelItem )
   {
      return modelItemToDesktopPanelMap.get( modelItem );
   }

   public DesktopPanel showDesktopPanel( DesktopPanel desktopPanel )
   {
      SoapUIVirtualFile vf = new SoapUIVirtualFile( desktopPanel );

      FileEditor[] fileEditors = editorManager.openFile( vf, true );
      desktopPanelToFileMap.put( desktopPanel, vf );

      return ( (SoapUIFileEditor) fileEditors[0] ).getDesktopPanel();
   }

    public JComponent getDesktopComponent() {
        return null;
    }

    public void transferTo(SoapUIDesktop newDesktop) {
    }

    public boolean closeAll() {
        while( !desktopPanelToFileMap.isEmpty() )
        {
            closeDesktopPanel( desktopPanelToFileMap.keySet().iterator().next() );
        }

        modelItemToDesktopPanelMap.clear();
        return true;
    }

    public void disposeDesktopPanel( DesktopPanel desktopPanel )
    {
        desktopPanelToFileMap.remove( desktopPanel );
        if( desktopPanel.getModelItem() != null )
        {
            modelItemToDesktopPanelMap.remove( desktopPanel.getModelItem() );
        }
    }

    private class InternalFileEditorManagerListener extends FileEditorManagerAdapter
   {
      public void selectionChanged( FileEditorManagerEvent event )
      {
         if( event.getNewFile() instanceof SoapUIVirtualFile )
         {
             DesktopPanel desktopPanel = ((SoapUIVirtualFile) event.getNewFile()).getDesktopPanel();
           //  System.out.println("Selected " + desktopPanel.getTitle() );
             fireDesktopPanelSelected(desktopPanel);
         }
      }
   }

	public void minimize( DesktopPanel arg0 )
	{
	}

	public void maximize(DesktopPanel arg0)
	{
	}
}
