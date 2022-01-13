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
import com.intellij.ui.ColoredTreeCellRenderer;
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Configuration;
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Dependency;
import io.github.gregoranders.idea.gradle.dependencies.gradle.tooling.model.api.Project;
import org.immutables.value.Generated;
import org.jetbrains.annotations.NotNull;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

@Generated
public final class ModelCellRenderer extends ColoredTreeCellRenderer {

    @Override
    public void customizeCellRenderer(@NotNull final JTree tree,
                                      final Object value,
                                      final boolean selected,
                                      final boolean expanded,
                                      final boolean leaf,
                                      final int row, final
                                      boolean hasFocus) {

        final DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        final Object object = node.getUserObject();

        if (object instanceof Project) {
            final Project project = (Project) object;
            setIcon(AllIcons.Nodes.Project);
            append(project.group() + "." + project.name());
        }

        if (object instanceof Configuration) {
            final Configuration configuration = (Configuration) object;
            setIcon(AllIcons.Nodes.ConfigFolder);
            append(configuration.name());
        }

        if (object instanceof Dependency) {
            final Dependency dependency = (Dependency) object;
            setIcon(AllIcons.Nodes.Artifact);
            append(dependency.group() + ":" + dependency.name() + ":" + dependency.version());
        }
    }
}
