/**
 * Copyright 2013 Crypto Workshop Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vvote.thirdparty.ximix.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/**
 * ASN.1 object identifiers associated with Ximix.
 */
public interface XimixObjectIdentifiers
{
    //
    // {iso(1) member-body(2) au(36) cryptoworkshop(50159983187) ximix(1)
    //
    static final ASN1ObjectIdentifier ximix = new ASN1ObjectIdentifier("1.2.36.50159983187.1");

    static final ASN1ObjectIdentifier ximixAlgorithms = ximix.branch("1");
    static final ASN1ObjectIdentifier ximixAlgorithmsExperimental = ximixAlgorithms.branch("0");

    static final ASN1ObjectIdentifier ximixCertExtension = ximix.branch("2");
    static final ASN1ObjectIdentifier ximixShareIdExtension = ximixCertExtension.branch("1");
}
