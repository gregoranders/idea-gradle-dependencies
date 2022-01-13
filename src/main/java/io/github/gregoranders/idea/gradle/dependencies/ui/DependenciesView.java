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

import com.intellij.ide.actions.RefreshAction;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.treeStructure.Tree;
import io.github.gregoranders.idea.gradle.dependencies.gradle.GradleUtilities;
import io.github.gregoranders.idea.gradle.dependencies.gradle.configuration.Configuration;
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency;
import org.immutables.value.Generated;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

@Generated
public final class DependenciesView extends SimpleToolWindowPanel {

    private static final long serialVersionUID = -1;

    @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    private final transient Project currentProject;

    private final AtomicBoolean busy = new AtomicBoolean(false);

    private final transient DefaultActionGroup actionGroup;

    private final transient RefreshAction refreshAction;

    private final transient ActionToolbar actionToolbar;

    private final DefaultMutableTreeNode rootNode;

    private final Tree tree;

    public DependenciesView(final @NotNull Project project, final ToolWindow toolWindow) {
        super(true, true);
        currentProject = project;
        final ActionManager actionManager = ActionManager.getInstance();
        actionGroup = new DefaultActionGroup("ACTION_GROUP", false);
        refreshAction = new DependenciesRefreshAction(this);
        actionGroup.add(refreshAction);
        actionToolbar = actionManager.createActionToolbar("ACTION_TOOLBAR", actionGroup, true);
        setToolbar(actionToolbar.getComponent());

        Content content = ContentFactory.SERVICE.getInstance().createContent(this, "", false);
        toolWindow.getContentManager().addContent(content);

        rootNode = new DefaultMutableTreeNode();
        tree = new Tree(rootNode);
        tree.setCellRenderer(new ModelCellRenderer());
        setContent(new JBScrollPane(tree));

        StartupManager.getInstance(project).runWhenProjectIsInitialized(this::refresh);
    }

    @SuppressWarnings("PMD.LawOfDemeter")
    public void refresh() {
        final String basePath = currentProject.getBasePath();

        busy.set(true);
        actionToolbar.updateActionsImmediately();
        updateUI();

        ApplicationManager.getApplication().invokeLater(() -> {
            if (basePath != null) {

                try {
                    final GradleUtilities gradleUtilities = new GradleUtilities(new Configuration());
                    final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project project
                        = gradleUtilities.getDependencies(Path.of(basePath));
                    if (project != null) {
                        updateView(project);
                    }
                } catch (Exception e) {
                    // TODO
                } finally {
                    busy.set(false);
                }
            }
        });
    }

    private void updateView(final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project project) {
        rootNode.removeAllChildren();
        addProject(rootNode, project);
    }

    private void addProject(final DefaultMutableTreeNode node,
                            final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project project) {
        node.setUserObject(project);

        if (project.subProjects().size() > 0) {
            project.subProjects().forEach(subProject -> {
                final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
                addProject(childNode, subProject);
                node.add(childNode);
            });
        }
        if (project.configurations().size() > 0) {
            project.configurations().forEach(configuration -> {
                if (configuration.dependencies().size() > 0) {
                    final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
                    addConfiguration(childNode, configuration);
                    node.add(childNode);
                }
            });
        }
    }

    private void addConfiguration(final DefaultMutableTreeNode node,
                                  final io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Configuration configuration) {
        node.setUserObject(configuration);
        configuration.dependencies().forEach(dependency -> {
            final DefaultMutableTreeNode childNode = new DefaultMutableTreeNode();
            addDependency(childNode, dependency);
            node.add(childNode);
        });
    }

    private void addDependency(final DefaultMutableTreeNode node, final Dependency dependency) {
        node.setUserObject(dependency);
    }

    public boolean isBusy() {
        return busy.get();
    }
}
