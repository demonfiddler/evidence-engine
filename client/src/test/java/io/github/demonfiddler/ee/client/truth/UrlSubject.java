/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

package io.github.demonfiddler.ee.client.truth;

import static com.google.common.truth.Truth.assertAbout;

import java.net.URL;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

public class UrlSubject extends Subject {

    public static UrlSubject assertThat(/*@Nullable*/ URL url) {
        return assertAbout(urls()).that(url);
    }

    public static Subject.Factory<UrlSubject, URL> urls() {
        return UrlSubject::new;
    }

    final URL actual;

    UrlSubject(FailureMetadata metadata, URL actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void hasExternalForm(String externalForm) {
        externalForm().isEqualTo(externalForm);
    }

    public StringSubject externalForm() {
        return check("externalForm()").that(actual.toExternalForm());
    }

    public void hasProtocol(String protocol) {
        protocol().isEqualTo(protocol);
    }

    public StringSubject protocol() {
        return check("protocol()").that(actual.getProtocol());
    }

    public void hasAuthority(String authority) {
        authority().isEqualTo(authority);
    }

    public StringSubject authority() {
        return check("authority()").that(actual.getAuthority());
    }

    public void hasUserInfo(String userInfo) {
        userInfo().isEqualTo(userInfo);
    }

    public StringSubject userInfo() {
        return check("userInfo()").that(actual.getUserInfo());
    }

    public void hasHost(String host) {
        host().isEqualTo(host);
    }

    public StringSubject host() {
        return check("host()").that(actual.getHost());
    }

    public void hasPort(int port) {
        port().isEqualTo(port);
    }

    public IntegerSubject port() {
        return check("port()").that(actual.getPort());
    }

    public void hasPath(String path) {
        path().isEqualTo(path);
    }

    public StringSubject path() {
        return check("path()").that(actual.getPath());
    }

    public void hasQuery(String query) {
        query().isEqualTo(query);
    }

    public StringSubject query() {
        return check("query()").that(actual.getQuery());
    }

    public void hasRef(String ref) {
        ref().isEqualTo(ref);
    }

    public StringSubject ref() {
        return check("ref()").that(actual.getRef());
    }

}
