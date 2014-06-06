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

import com.eviware.soapui.ui.desktop.DesktopPanel;
import com.eviware.soapui.SoapUI;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * FileEditor that wraps a SoapUI DesktopPanel
 */

public class SoapUIFileEditor implements FileEditor
{
   private DesktopPanel desktopPanel;

   public SoapUIFileEditor(VirtualFile file)
   {
      SoapUIVirtualFile vf = ( SoapUIVirtualFile ) file;
      desktopPanel = vf.getDesktopPanel();
   }

   public void dispose()
   {
       desktopPanel.onClose( false );
       ((IntellijSoapUIDesktop) SoapUI.getDesktop()).disposeDesktopPanel(desktopPanel);
   }

   @NotNull public JComponent getComponent()
   {
      return desktopPanel.getComponent();
   }

   public JComponent getPreferredFocusedComponent()
   {
      return desktopPanel.getComponent();
   }

   @NonNls
   public String getName()
   {
      return "SoapUI ModelItem Editor";
   }

   public FileEditorState getState( FileEditorStateLevel level )
   {
      return SoapUIEditorProvider.DummyFileEditorState.DUMMY;
   }

   public void setState( FileEditorState state )
   {
   }

   public boolean isModified()
   {
      return false;
   }

   public boolean isValid()
   {
      return true;
   }

   public void selectNotify()
   {
   }

   public void deselectNotify()
   {
   }

   public void addPropertyChangeListener( PropertyChangeListener listener )
   {
   }

   public void removePropertyChangeListener( PropertyChangeListener listener )
   {
   }

   public BackgroundEditorHighlighter getBackgroundHighlighter()
   {
      return null;
   }

   public FileEditorLocation getCurrentLocation()
   {
      return null;
   }

   public StructureViewBuilder getStructureViewBuilder()
   {
      return null;
   }

   public <T> T getUserData( Key<T> key )
   {
      return null;
   }

   public <T> void putUserData( Key<T> key, T value )
   {
   }

   public DesktopPanel getDesktopPanel()
   {
      return desktopPanel;
   }
}
