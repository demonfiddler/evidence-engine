/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-26 Adrian Price. All rights reserved.
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

import java.net.URI;

import com.google.common.truth.BooleanSubject;
import com.google.common.truth.ComparableSubject;
import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IntegerSubject;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;

public class UriSubject extends ComparableSubject<URI> {

    public static UriSubject assertThat(/*@Nullable*/ URI uri) {
        return assertAbout(uris()).that(uri);
    }

    public static Subject.Factory<UriSubject, URI> uris() {
        return UriSubject::new;
    }

    final URI actual;

    UriSubject(FailureMetadata metadata, URI actual) {
        super(metadata, actual);
        this.actual = actual;
    }

    public void hasAsciiString(String asciiString) {
        asciiString().isEqualTo(asciiString);
    }

    public StringSubject asciiString() {
        return check("asciiString()").that(actual.toASCIIString());
    }

    public void hasAuthority(String authority) {
        authority().isEqualTo(authority);
    }

    public StringSubject authority() {
        return check("authority()").that(actual.getAuthority());
    }

    public void hasScheme(String scheme) {
        scheme().isEqualTo(scheme);
    }

    public StringSubject scheme() {
        return check("scheme()").that(actual.getScheme());
    }

    public void hasSchemeSpecificPart(String schemeSpecificPart) {
        schemeSpecificPart().isEqualTo(schemeSpecificPart);
    }

    public StringSubject schemeSpecificPart() {
        return check("schemeSpecificPart()").that(actual.getSchemeSpecificPart());
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

    public void hasFragment(String fragment) {
        fragment().isEqualTo(fragment);
    }

    public StringSubject fragment() {
        return check("fragment()").that(actual.getFragment());
    }

    public void hasAbsolute(Boolean absolute) {
        absolute().isEqualTo(absolute);
    }

    public BooleanSubject absolute() {
        return check("absolute()").that(actual.isAbsolute());
    }

    public void hasOpaque(Boolean opaque) {
        opaque().isEqualTo(opaque);
    }

    public BooleanSubject opaque() {
        return check("opaque()").that(actual.isOpaque());
    }

}
