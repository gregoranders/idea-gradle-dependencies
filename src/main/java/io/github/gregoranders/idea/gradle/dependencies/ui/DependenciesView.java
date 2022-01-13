/*
 * MIT License
 *
 * Copyright (c) 2022 Gregor Anders
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.gregoranders.idea.gradle.dependencies.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ide.actions.RefreshAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import io.github.gregoranders.idea.gradle.dependencies.gradle.GradleUtilities;
import io.github.gregoranders.idea.gradle.dependencies.gradle.configuration.Configuration;
import org.immutables.value.Generated;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

@Generated
public final class DependenciesView extends SimpleToolWindowPanel {

    private static final long serialVersionUID = -1;

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    private final transient Project currentProject;

    private final DefaultActionGroup actionGroup;

    private final RefreshAction refreshAction;

    private final ActionToolbar actionToolbar;

    public DependenciesView(final @NotNull Project project, final ToolWindow toolWindow) {
        super(true, true);
        currentProject = project;
        final ActionManager actionManager = ActionManager.getInstance();
        actionGroup = new DefaultActionGroup("ACTION_GROUP", false);
        refreshAction = new RefreshAction("Refresh", "Refresh all dependencies", AllIcons.Actions.Refresh);
        actionGroup.add(refreshAction);
        actionToolbar = actionManager.createActionToolbar("ACTION_TOOLBAR", actionGroup, true);
        setToolbar(actionToolbar.getComponent());
    }

    public void refresh() {
        final String basePath = currentProject.getBasePath();

        ApplicationManager.getApplication().invokeLater(() -> {
            final GradleUtilities gradleUtilities = new GradleUtilities(new Configuration());
            final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project dependencies
                = gradleUtilities.getDependencies(Path.of(basePath));

            assert dependencies != null;
        });
    }
}