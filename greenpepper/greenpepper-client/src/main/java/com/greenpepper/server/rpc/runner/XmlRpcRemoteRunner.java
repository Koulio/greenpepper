/**
 * Copyright (c) 2008 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */
package com.greenpepper.server.rpc.runner;

import com.greenpepper.server.GreenPepperServerException;
import com.greenpepper.server.ServerPropertiesManager;
import com.greenpepper.server.domain.DocumentNode;
import com.greenpepper.server.domain.Execution;
import com.greenpepper.server.domain.Project;
import com.greenpepper.server.domain.Reference;
import com.greenpepper.server.domain.Repository;
import com.greenpepper.server.domain.Requirement;
import com.greenpepper.server.domain.Specification;
import com.greenpepper.server.domain.SystemUnderTest;
import com.greenpepper.server.rpc.xmlrpc.GreenPepperXmlRpcClient;

public class XmlRpcRemoteRunner
{
	private final GreenPepperXmlRpcClient xmlRpc;

	public XmlRpcRemoteRunner(final String url, final String handler)
	{
		xmlRpc = new GreenPepperXmlRpcClient(new ServerPropertiesManager()
		{
			public String getProperty(String key, String identifier)
			{
				if (ServerPropertiesManager.URL.equals(key))
				{
					return url;
				}
				else if (ServerPropertiesManager.HANDLER.equals(key))
				{
					return handler;
				}
				else
				{
					return null;
				}
			}

			public void setProperty(String key, String value, String identifier)
			{

			}
		});
	}

	public DocumentNode getSpecificationHierarchy(Repository repository, SystemUnderTest systemUnderTest)
			throws GreenPepperServerException
	{
		return xmlRpc.getSpecificationHierarchy(repository, systemUnderTest, getIdentifier());
	}

	public Execution runSpecification(String projectName, String sutName, String repositoryId, String specificationName,
									  boolean implementedVersion, String locale)
			throws GreenPepperServerException
	{
		SystemUnderTest sut = SystemUnderTest.newInstance(sutName);
		sut.setProject(Project.newInstance(projectName));

		Specification specification = Specification.newInstance(specificationName);
		specification.setRepository(Repository.newInstance(repositoryId));

		return runSpecification(sut, specification, implementedVersion, locale);
	}

	public Execution runSpecification(SystemUnderTest sut, Specification specification,
									  boolean implementedVersion, String locale)
			throws GreenPepperServerException
	{
		if (sut.getProject() == null)
		{
			throw new IllegalArgumentException("Missing Project in SystemUnderTest");
		}

		if (specification.getRepository() == null)
		{
			throw new IllegalArgumentException("Missing Repository in Specification");
		}

		return xmlRpc.runSpecification(sut, specification, implementedVersion, locale, getIdentifier());
	}

	public Reference runReference(String projectName, String sutName, String requirementRepositoryId,
								  String requirementName, String locale)
			throws GreenPepperServerException
	{
		return runReference(projectName,  sutName, requirementRepositoryId, requirementName, null, null, locale);
	}
		
	public Reference runReference(String projectName, String sutName, String requirementRepositoryId,
								  String requirementName, String repositoryId, String specificationName, String locale)
			throws GreenPepperServerException
	{
		SystemUnderTest sut = SystemUnderTest.newInstance(sutName);
		sut.setProject(Project.newInstance(projectName));

		Specification specification =Specification.newInstance(specificationName == null ? requirementName : specificationName);
		specification.setRepository(Repository.newInstance(repositoryId == null ? requirementRepositoryId : repositoryId));

		Requirement requirement = Requirement.newInstance(requirementName);
		requirement.setRepository(Repository.newInstance(requirementRepositoryId));

		Reference reference = Reference.newInstance(requirement, specification, sut);

		return runReference(reference, locale);
	}

	public Reference runReference(SystemUnderTest sut, Specification specification, Requirement requirement, String locale)
			throws GreenPepperServerException
	{
		if (sut.getProject() == null)
		{
			throw new IllegalArgumentException("Missing Project in SystemUnderTest");
		}

		if (specification.getRepository() == null)
		{
			throw new IllegalArgumentException("Missing Repository in Specification");
		}

		if (requirement.getRepository() == null)
		{
			throw new IllegalArgumentException("Missing Repository in Requirement");
		}

		Reference reference = Reference.newInstance(requirement, specification, sut);

		return runReference(reference, locale);
	}

	@SuppressWarnings("unchecked")
	public Reference runReference(Reference reference, String locale)
			throws GreenPepperServerException
	{
		return xmlRpc.runReference(reference, locale, getIdentifier());
	}

	private String getIdentifier()
	{
		return null;
	}
}