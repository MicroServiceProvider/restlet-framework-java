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

package org.restlet.example.book.restlet.ch9.resources;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.example.book.restlet.ch9.objects.Feed;
import org.restlet.example.book.restlet.ch9.objects.Mailbox;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

/**
 * Resource for a user's feed.
 * 
 */
public class FeedResource extends BaseResource {

    /** The feed represented by this resource. */
    private Feed feed;

    /** The parent mailbox. */
    private Mailbox mailbox;

    public FeedResource(Context context, Request request, Response response) {
        super(context, request, response);
        String mailboxId = (String) request.getAttributes().get("mailboxId");
        mailbox = getDAOFactory().getMailboxDAO().getMailboxById(mailboxId);

        if (mailbox != null) {
            String feedId = (String) request.getAttributes().get("feedId");
            feed = getDAOFactory().getFeedDAO().getFeedById(feedId);

            if (feed != null) {
                getVariants().add(new Variant(MediaType.TEXT_HTML));
                getVariants().add(new Variant(MediaType.APPLICATION_ATOM_XML));
            }
        }

    }

    @Override
    public boolean allowDelete() {
        return true;
    }

    @Override
    public boolean allowPut() {
        return true;
    }

    @Override
    public void removeRepresentations() throws ResourceException {
        getDAOFactory().getMailboxDAO().deleteFeed(mailbox, feed);
        getResponse().redirectSeeOther(
                getRequest().getResourceRef().getParentRef());
    }

    @Override
    public Representation represent(Variant variant) throws ResourceException {
        Map<String, Object> dataModel = new TreeMap<String, Object>();
        dataModel.put("currentUser", getCurrentUser());
        dataModel.put("mailbox", mailbox);
        dataModel.put("feed", feed);
        dataModel.put("resourceRef", getRequest().getResourceRef());
        dataModel.put("rootRef", getRequest().getRootRef());

        if (feed.getTags() != null) {
            StringBuilder builder = new StringBuilder();
            for (Iterator<String> iterator = feed.getTags().iterator(); iterator
                    .hasNext();) {
                String tag = iterator.next();
                builder.append(tag);
                if (iterator.hasNext()) {
                    builder.append(", ");
                }
            }
            dataModel.put("tags", builder.toString());
        }

        Representation representation = null;
        MediaType mediaType = variant.getMediaType();
        if (MediaType.TEXT_HTML.equals(mediaType)) {
            representation = new TemplateRepresentation("feed.html",
                    getFmcConfiguration(), dataModel, variant.getMediaType());
        } else if (MediaType.APPLICATION_ATOM_XML.equals(mediaType)) {
            // <link rel="alternate" type="application/rss+xml"
            // href="http://blog.noelios.com/feed/?cat=15314" title="Restlet
            // latest news" />
        }

        return representation;
    }

    @Override
    public void storeRepresentation(Representation entity)
            throws ResourceException {
        Form form = new Form(entity);
        feed.setNickname(form.getFirstValue("nickname"));
        feed.setTags(Arrays.asList(form.getFirstValue("tags").split(" ")));

        getDAOFactory().getMailboxDAO().updateFeed(mailbox, feed);
        getResponse().redirectSeeOther(getRequest().getResourceRef());
    }

}
