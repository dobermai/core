/*
 * JBoss, by Red Hat.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.maven.locators;

import java.io.File;

import javax.inject.Inject;

import org.jboss.forge.maven.ProjectImpl;
import org.jboss.forge.maven.facets.MavenContainer;
import org.jboss.forge.maven.facets.MavenCoreFacetImpl;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.locator.ProjectLocator;
import org.jboss.forge.project.services.ProjectFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;

/**
 * Locate a Maven project starting in the current directory, and progressing up the chain of parent directories until a
 * project is found, or the root directory is found. If a project is found, return the {@link File} referring to the
 * directory containing that project, or return null if no projects were found.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("maven-project-locator")
public class MavenProjectLocator implements ProjectLocator
{
   private final MavenContainer container;
   private final ProjectFactory factory;
   private final ShellPrintWriter writer;

   @Inject
   public MavenProjectLocator(final MavenContainer container, final ProjectFactory factory,
            final ShellPrintWriter writer)
   {
      this.factory = factory;
      this.container = container;
      this.writer = writer;
   }

   @Override
   public Project createProject(final DirectoryResource dir)
   {
      Resource<?> pom = dir.getChild("pom.xml");

      Project result = null;
      if (pom.exists())
      {
         result = new ProjectImpl(factory, dir);
         Facet facet = new MavenCoreFacetImpl(container, writer);
         facet.setProject(result);
         result.registerFacet(facet);
      }
      return result;
   }

   @Override
   public boolean containsProject(final DirectoryResource dir)
   {
      Resource<?> pom = dir.getChild("pom.xml");
      return pom.exists();
   }
}
