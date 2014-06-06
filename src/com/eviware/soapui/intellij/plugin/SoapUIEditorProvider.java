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

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * FileEditorProvider for providing SoapUI File Editors
 */

public class SoapUIEditorProvider implements ApplicationComponent, FileEditorProvider
{
   public static final FileType SOAPUI_FILE_TYPE = new SoapUIFileType();

   public void initComponent()
   {
   }

   public void disposeComponent()
   {
   }

   @NotNull
   public String getComponentName()
   {
      return "SoapUIEditorProvider";
   }

   public boolean accept( Project project, @NotNull VirtualFile file )
   {
      return file.getFileType() == SOAPUI_FILE_TYPE;
   }

   public FileEditor createEditor( Project project, @NotNull final VirtualFile file )
   {
      return new SoapUIFileEditor(file );
   }

   public void disposeEditor( @NotNull FileEditor editor )
   {
       ((SoapUIFileEditor)editor).dispose();
   }

   public FileEditorState readState( Element sourceElement, Project
           project, VirtualFile file )
   {
      return DummyFileEditorState.DUMMY;
   }

   public void writeState( FileEditorState state, Project project,
                           Element targetElement )
   {
   }

   @NotNull@NonNls
   public String getEditorTypeId()
   {
      return getComponentName();
   }

   @NotNull public FileEditorPolicy getPolicy()
   {
      return FileEditorPolicy.HIDE_DEFAULT_EDITOR;
   }

   public static class DummyFileEditorState implements FileEditorState
   {
      public static final FileEditorState DUMMY = new DummyFileEditorState();

      public boolean canBeMergedWith( FileEditorState otherState,
                                      FileEditorStateLevel level )
      {
         return false;
      }
   }

   public static class SoapUIFileType implements FileType
   {

      @NotNull
      @NonNls
      public String getName()
      {
         return "SoapUI File";
      }

      @NotNull
      public String getDescription()
      {
         return "SoapUI File";
      }

      @NotNull
      @NonNls
      public String getDefaultExtension()
      {
         return "xml";
      }

      @Nullable
      public Icon getIcon()
      {
         return null;
      }

      public boolean isBinary()
      {
         return false;
      }

      public boolean isReadOnly()
      {
         return false;
      }

      @Nullable
      @NonNls
      public String getCharset( @NotNull VirtualFile file )
      {
         return null;
      }

       public String getCharset(@NotNull VirtualFile virtualFile, byte[] bytes) {
           return null;
       }

       @Nullable
      public SyntaxHighlighter getHighlighter( @Nullable Project project, final VirtualFile virtualFile )
      {
         return null;
      }

      @Nullable
      public StructureViewBuilder getStructureViewBuilder( @NotNull VirtualFile file, @NotNull Project project )
      {
         return null; 
      }
   }

}

