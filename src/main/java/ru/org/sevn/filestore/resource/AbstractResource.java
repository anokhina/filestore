/*
 * Copyright 2019 Veronica Anokhina.
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
package ru.org.sevn.filestore.resource;

import io.milton.http.Auth;
import io.milton.http.Request;
import io.milton.http.Request.Method;
import io.milton.http.http11.auth.DigestGenerator;
import io.milton.http.http11.auth.DigestResponse;
import io.milton.resource.DigestResource;
import io.milton.resource.PropFindableResource;
import java.util.Date;

public abstract class AbstractResource implements DigestResource, PropFindableResource {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AbstractResource.class);

    //TODO
    @Override
    public Object authenticate(final String user, final String requestedPassword) {
        if( user.equals("user") && requestedPassword.equals("password")) {
            return user;
        }
        return null;
    }

    //TODO
    @Override
    public Object authenticate(final DigestResponse digestRequest) {
        if (digestRequest.getUser().equals("user")) {
            final DigestGenerator gen = new DigestGenerator();
            final String actual = gen.generateDigest(digestRequest, "password");
            if (actual.equals(digestRequest.getResponseDigest())) {
                return digestRequest.getUser();
            } else {
                log.warn("that password is incorrect");
            }
        } else {
            log.warn("user not found: " + digestRequest.getUser() );
        }
        return null;
    }

    @Override
    public String getUniqueId() {
        return null;
    }

    @Override
    public String checkRedirect(final Request request) {
        return null;
    }

    @Override
    public boolean authorise(final Request request, final Method method, final Auth auth) {
        log.debug("authorise");
        return auth != null;
    }

    @Override
    public String getRealm() {
        return "testrealm@host.com"; //TODO
    }

    @Override
    public Date getModifiedDate() {
        return null;
    }

    @Override
    public Date getCreateDate() {
        return null;
    }
       
    @Override
    public boolean isDigestAllowed() {
        return true;
    }
}
