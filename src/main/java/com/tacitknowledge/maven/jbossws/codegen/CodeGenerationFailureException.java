package com.tacitknowledge.maven.jbossws.codegen;

import org.apache.maven.plugin.MojoFailureException;

/**
 * @author vkozlov@tacitknowledge.com
 * @version $Id: $
 */
public class CodeGenerationFailureException extends MojoFailureException
{

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param message the error message
     */
    public CodeGenerationFailureException(String message)
    {
        super(message);
        // TODO Auto-generated constructor stub
    }
}