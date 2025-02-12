package org.asciidoc.intellij.editor.notification;

import com.intellij.ide.BrowserUtil;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EditorNotificationPanel;
import com.intellij.ui.EditorNotifications;
import org.asciidoc.intellij.file.AsciiDocFileType;
import org.asciidoc.intellij.psi.AsciiDocUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Notify the user that Hugo support is available.
 * Triggers notification if the AsciiDoc document is part of a Hugo site.
 * The condition needs to be fulfilled when opening the editor.
 * It will not be re-checked during typing or generating files.
 */
public class HugoNotificationProvider extends EditorNotifications.Provider<EditorNotificationPanel> implements DumbAware {
  private static final Key<EditorNotificationPanel> KEY = Key.create("Hugo Support available");

  private static final String HUGO_AVAILABLE = "asciidoc.hugo.available";

  @NotNull
  @Override
  public Key<EditorNotificationPanel> getKey() {
    return KEY;
  }

  @Nullable
  @Override
  public EditorNotificationPanel createNotificationPanel(@NotNull VirtualFile file, @NotNull final FileEditor fileEditor, @NotNull Project project) {
    // only in AsciiDoc files
    if (file.getFileType() != AsciiDocFileType.INSTANCE) {
      return null;
    }

    // only if not previously turned off
    if (PropertiesComponent.getInstance().getBoolean(HUGO_AVAILABLE)) {
      return null;
    }

    // find out if we're in an Antora module
    VirtualFile hugoStaticFolder = AsciiDocUtil.findHugoStaticFolder(project, file.getParent());
    if (hugoStaticFolder == null) {
      return null;
    }

    final EditorNotificationPanel panel = new EditorNotificationPanel();
    panel.setText("It seems you are editing a document that is part of an Hugo site. Do you want to learn more how this plugin can support you?");
    panel.createActionLabel("Yes, tell me more!", ()
      -> BrowserUtil.browse("https://intellij-asciidoc-plugin.ahus1.de/docs/users-guide/features/advanced/hugo.html"));
    panel.createActionLabel("Do not show again", () -> {
      PropertiesComponent.getInstance().setValue(HUGO_AVAILABLE, true);
      EditorNotifications.updateAll();
    });
    return panel;
  }
}
