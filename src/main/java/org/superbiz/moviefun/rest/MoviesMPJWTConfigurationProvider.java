/*
 *     Licensed to the Apache Software Foundation (ASF) under one or more
 *     contributor license agreements.  See the NOTICE file distributed with
 *     this work for additional information regarding copyright ownership.
 *     The ASF licenses this file to You under the Apache License, Version 2.0
 *     (the "License"); you may not use this file except in compliance with
 *     the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package org.superbiz.moviefun.rest;

import org.apache.tomee.microprofile.jwt.config.JWTAuthContextInfo;
import org.superbiz.moviefun.utils.TokenUtil;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Optional;

@Dependent
public class MoviesMPJWTConfigurationProvider {

    public static final String ISSUED_BY = "/oauth2/token";

    @Produces
    Optional<JWTAuthContextInfo> getOptionalContextInfo() throws Exception {
        JWTAuthContextInfo contextInfo = new JWTAuthContextInfo();

        // todo use MP Config to load the configuration
        contextInfo.setIssuedBy(ISSUED_BY);

        byte[] encodedBytes = TokenUtil.readPublicKey("/publicKey.pem").getEncoded();

        final X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedBytes);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final RSAPublicKey pk = (RSAPublicKey) kf.generatePublic(spec);

        contextInfo.setSignerKey(pk);
        contextInfo.setExpGracePeriodSecs(10);

        return Optional.of(contextInfo);
    }

    @Produces
    JWTAuthContextInfo getContextInfo() throws Exception {
        return getOptionalContextInfo().get();
    }
}
