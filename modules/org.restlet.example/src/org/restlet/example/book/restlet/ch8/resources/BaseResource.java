/*
 * Copyright 2005-2008 Noelios Consulting.
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the "License"). You may not use this file except in
 * compliance with the License.
 * 
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.txt See the License for the specific
 * language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at http://www.opensource.org/licenses/cddl1.txt If
 * applicable, add the following below this CDDL HEADER, with the fields
 * enclosed by brackets "[]" replaced with your own identifying information:
 * Portions Copyright [yyyy] [name of copyright owner]
 */

package org.restlet.example.book.restlet.ch8.resources;

import org.restlet.Context;
import org.restlet.data.Reference;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.example.book.restlet.ch8.Application;
import org.restlet.example.book.restlet.ch8.RmepGuard;
import org.restlet.example.book.restlet.ch8.objects.Facade;
import org.restlet.example.book.restlet.ch8.objects.User;
import org.restlet.resource.Resource;

import freemarker.template.Configuration;

/**
 * Base resource class that supports common behaviours or attributes shared by
 * all resources.
 * 
 */
public class BaseResource extends Resource {

    public BaseResource(Context context, Request request, Response response) {
        super(context, request, response);
    }

    /**
     * Gives access to the Objects layer.
     * 
     * @return a facade.
     */
    protected Facade getObjectsFacade() {
        Application application = (Application) getApplication();
        return application.getObjectsFacade();
    }

    /**
     * Returns the Freemarker's configuration object.
     * 
     * @return the Freemarker's configuration object.
     */
    protected Configuration getFmcConfiguration() {
        Application application = (Application) getApplication();
        return application.getFmc();
    }

    /**
     * Returns a User object representing the current user connected.
     * 
     * @return the current user connected.
     */
    protected User getCurrentUser() {
        return (User) getRequest().getAttributes().get(RmepGuard.CURRENT_USER);
    }

    /**
     * Returns the reference of a resource according to its id and the reference
     * of its "parent".
     * 
     * @param parentRef
     *                parent reference.
     * @param childId
     *                id of this resource
     * @return the reference object of the child resource.
     */
    protected Reference getChildReference(Reference parentRef, String childId) {
        if (parentRef.getIdentifier().endsWith("/")) {
            return new Reference(parentRef.getIdentifier() + childId);
        } else {
            return new Reference(parentRef.getIdentifier() + "/" + childId);
        }
    }
}
