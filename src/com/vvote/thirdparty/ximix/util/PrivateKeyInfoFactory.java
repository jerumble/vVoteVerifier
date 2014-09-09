package com.vvote.thirdparty.ximix.util;

import it.unisa.dia.gas.crypto.jpbc.signature.bls01.params.BLS01Parameters;

import java.io.IOException;
import java.math.BigInteger;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Factory to create PrivateKeyInfo objects from private keys.
 */
public class PrivateKeyInfoFactory
{
    /**
     * Return a PrivateKeyInfo object containing an encoding of BLS private key.
     *
     * @param value the private value associated with the private key.
     * @param parameters the parameters associated with the private key.
     * @return a PrivateKeyInfo object containing the value and parameters.
     * @throws java.io.IOException if the private key cannot be encoded.
     */
    public static PrivateKeyInfo createPrivateKeyInfo(BigInteger value, BLS01Parameters parameters)
        throws IOException
    {
        return new PrivateKeyInfo(new AlgorithmIdentifier(XimixObjectIdentifiers.ximixAlgorithmsExperimental, new DERSequence(
                    new ASN1Encodable[]
                        {
                            new DERUTF8String(parameters.getCurveParameters().toString()),
                            new DEROctetString(parameters.getG().toBytes())
                        })), new ASN1Integer(value));
    }
}
