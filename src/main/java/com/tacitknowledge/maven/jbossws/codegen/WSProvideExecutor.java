package com.tacitknowledge.maven.jbossws.codegen;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import org.jboss.ws.tools.jaxws.impl.JBossWSProviderFactoryImpl;
import org.jboss.wsf.spi.tools.WSContractProvider;

/**
 * Goal which generates WSDL resources for given endpoints list.
 * 
 * @goal wsprovide
 * 
 * @phase process-sources
 */
public class WSProvideExecutor extends AbstractMojo {
	/**
	 * Directory to store output files
	 * 
	 * @parameter expression="${project.build.directory}/generated-wsdl"
	 * @required
	 */
	private String outputDirectory;

	/**
	 * Default endpoint value
	 * 
	 * @parameter expression="http://127.0.0.1:8080"
	 * @required
	 */
	private String defaultEndpoint;
	
	/**
	 * Service endpoints class names.
	 * 
	 * @parameter
	 * @required
	 */
	@SuppressWarnings("unchecked")
	private List endpoints;

	/**
	 * Executes the goal.
	 */
	public void execute() throws MojoExecutionException {
		for (Object endpoint : endpoints) {
			getLog().info("Endpoint processing: " + endpoint);
			generate(endpoint.toString());
		}
		// TODO stupid hack here
		// if endpoint address not specified, JBoss code generator puts "REPLACE_WITH_ACTUAL_URL" instead. 
		// axistools-maven-plugin cannot process it. We need to replace it with some valid. 
		try {
			TextUtils.replaceString(new File(outputDirectory), ".WSDL", "REPLACE_WITH_ACTUAL_URL", defaultEndpoint);
		} catch (IOException e) {
			getLog().info("-------------------------------------------------------------");
			getLog().error(" CODE POSTPROCESSING ERROR ");
			getLog().info("-------------------------------------------------------------");
			getLog().error("Error: filesystem exception " + e.getMessage());
			throw new MojoExecutionException("Error: filesystem exception " + e.getMessage(), e);
		}
		
	}

	private ClassLoader loader = Thread.currentThread().getContextClassLoader();

	/**
	 * Generates the WSDL for specified endpoint.
	 * 
	 * @param endpoint fully-qualified class name of the webservice endpoint.
	 * @throws MojoExecutionException if error occurred
	 */
	private void generate(String endpoint) throws MojoExecutionException {
		try {
			Class.forName(endpoint);
		} catch (ClassNotFoundException e) {
			getLog().info("-------------------------------------------------------------");
			getLog().error(" CODE GENERATION ERROR ");
			getLog().info("-------------------------------------------------------------");
			getLog().error("Error: Could not load class " + e.getMessage());
			throw new MojoExecutionException("Error: Could not load class ["
					+ endpoint + "]. Did you specify a valid dependency?", e);
		}

		// preparing provider factory and contract provider to generate WSDL
		JBossWSProviderFactoryImpl factory = new JBossWSProviderFactoryImpl();
		WSContractProvider gen = factory.createProvider(loader);

		// preparing print stream to collect contract provider messages
		OutputStream providerOutput = new OutputStream() {
			private StringBuilder string = new StringBuilder();
			@Override
			public void write(int b) throws IOException {
				this.string.append((char) b);
			}
			public String toString() {
				return this.string.toString();
			}
		};
		
		PrintStream ps = new PrintStream(providerOutput);

		// setting properties for code generator
		gen.setGenerateWsdl(true);
		gen.setOutputDirectory(new File(outputDirectory));
		gen.setMessageStream(ps);
		gen.setClassLoader(Thread.currentThread().getContextClassLoader());

		// call code generation facility and display the results.
		try {
			gen.provide(endpoint);
			getLog().info(providerOutput.toString());
		} catch (Throwable t) {
			getLog().info("-------------------------------------------------------------");
			getLog().error(" CODE GENERATION ERROR ");
			getLog().info("-------------------------------------------------------------");
			getLog().error("Error: " + t.getMessage());
			throw new MojoExecutionException("Error: Could not generate WSDL.",	t);
		}
	}

}
