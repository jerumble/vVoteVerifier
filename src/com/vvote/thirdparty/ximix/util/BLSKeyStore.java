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

import it.unisa.dia.gas.crypto.jpbc.signature.bls01.params.BLS01Parameters;
import it.unisa.dia.gas.crypto.jpbc.signature.bls01.params.BLS01PublicKeyParameters;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.EllipticCurve;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBMPString;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OutputEncryptor;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS12PfxPdu;
import org.bouncycastle.pkcs.PKCS12PfxPduBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCS12SafeBagBuilder;
import org.bouncycastle.pkcs.PKCS12SafeBagFactory;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.bouncycastle.pkcs.jcajce.JcePKCS12MacCalculatorBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder;
import org.bouncycastle.pkcs.jcajce.JcePKCSPBEOutputEncryptorBuilder;
import org.bouncycastle.util.encoders.Hex;

/**
 * A class for loading a PKCS#12 key store holding BLS partial keys.
 */
public class BLSKeyStore
{
    private final Map<String, BLS01Parameters> paramsMap = new HashMap<String, BLS01Parameters>();
    private final Set<String> signingKeys = new HashSet<String>();
    private final Map<String, BigInteger> sharedPrivateKeyMap = new HashMap<String, BigInteger>();
    private final Map<String, Element> sharedPublicKeyMap = new HashMap<String, Element>();
    private final Map<String, Integer> sequenceNoMap = new HashMap<String, Integer>();

    
    /**
     * Base constructor.
     */
    public BLSKeyStore()
    {
    }

    /**
     * Return true if the store contains a key with the name keyID
     *
     * @param keyID the ID of the key been passed in.
     * @return true if a key matching keyID is present, false otherwise.
     */
    public boolean hasPrivateKey(String keyID)
    {
        return sharedPrivateKeyMap.containsKey(keyID);
    }

    /**
     * Return the BLS01Parameters associated with the key identified by keyID.
     *
     * @param keyID the ID of the key of interest.
     * @return the parameters associated with keyID if present.
     */
    public BLS01Parameters getParams(String keyID)
    {
        return paramsMap.get(keyID);
    }

    /**
     * Return the full public key associated with keyID.
     *
     * @param keyID the ID of the key of interest.
     * @return a SubjectPublicKeyInfo object representing the public key.
     * @throws IOException if there is an issue extracting the key information.
     */
    public SubjectPublicKeyInfo fetchPublicKey(String keyID)
        throws IOException
    {
        if (sharedPublicKeyMap.containsKey(keyID))
        {
            Element share = sharedPublicKeyMap.get(keyID);

            if (share != null)
            {
                Element pK = share;
                BLS01Parameters params = paramsMap.get(keyID);

                return SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(new BLS01PublicKeyParameters(params, pK));
            }
        }

        return null;
    }

    /**
     * Return the partial public key associated with keyID.
     *
     * @param keyID the ID of the key of interest.
     * @return a PartialPublicKeyInfo object representing the partial public key.
     * @throws IOException if there is an issue extracting the key information.
     */
    public PartialPublicKeyInfo fetchPartialPublicKey(String keyID)
        throws IOException
    {
        if (sharedPrivateKeyMap.containsKey(keyID))
        {
            BLS01Parameters params = paramsMap.get(keyID);
            BigInteger share = sharedPrivateKeyMap.get(keyID);
            Pairing pairing = PairingFactory.getPairing(params.getCurveParameters());
            Element g = params.getG();

            // calculate the public key
            Element sk = pairing.getZr().newElement(share);

            Element pk = g.powZn(sk);

            return new PartialPublicKeyInfo(sequenceNoMap.get(keyID), SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(new BLS01PublicKeyParameters(params, pk.getImmutable())));
        }

        return null;
    }

    /**
     * Return the partial private key associated with keyID.
     *
     * @param keyID the ID of the key of interest.
     * @return an Element representing the private key value.
     */
    public Element getPartialPrivateKey(String keyID)
    {
        BLS01Parameters params = paramsMap.get(keyID);
        BigInteger share = sharedPrivateKeyMap.get(keyID);
        Pairing pairing = PairingFactory.getPairing(params.getCurveParameters());

        Element sk = pairing.getZr().newElement(share);

        return sk;
    }

    /**
     * Return the key store object as a PKCS#12 byte array.
     *
     * @param password the password to use to encrypt the key data.
     * @return an array of bytes representing the encoding.
     * @throws IOException on a conversion to ASN.1 encoding error.
     * @throws GeneralSecurityException if there is an issue encrypting the key data.
     */
    public synchronized byte[] getEncoded(char[] password)
        throws IOException, GeneralSecurityException
    {
        KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");

        EllipticCurve curve = new EllipticCurve(
            new ECFieldFp(new BigInteger("883423532389192164791648750360308885314476597252960362792450860609699839")), // q
            new BigInteger("7fffffffffffffffffffffff7fffffffffff8000000000007ffffffffffc", 16), // a
            new BigInteger("6b016c3bdcf18941d0d654921475ca71a9db2fb27d1d37796185c2942c0a", 16)); // b

        ECParameterSpec spec = new ECParameterSpec(
            curve,
            ECPointUtil.decodePoint(curve, Hex.decode("020ffa963cdca8816ccc33b8642bedf905c3d358573d3f27fbbd3b3cb9aaaf")), // G
            new BigInteger("883423532389192164791648750360308884807550341691627752275345424702807307"), // n
            1); // h

       // TODO: neeed an EC key for the node
        ECPrivateKeySpec priKeySpec = new ECPrivateKeySpec(
            new BigInteger("876300101507107567501066130761671078357010671067781776716671676178726717"), // d
            spec);

        try
        {
            OutputEncryptor encOut = new JcePKCSPBEOutputEncryptorBuilder(NISTObjectIdentifiers.id_aes256_CBC).setProvider("BC").build(password);

            JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
            PKCS12PfxPduBuilder builder = new PKCS12PfxPduBuilder();

            for (String keyID : sharedPrivateKeyMap.keySet())
            {
                PrivateKey sigKey = fact.generatePrivate(priKeySpec);
                SubjectPublicKeyInfo pubKey = this.fetchPublicKey(keyID);

                PKCS12SafeBagBuilder eeCertBagBuilder = new PKCS12SafeBagBuilder(createCertificate(
                    keyID, sequenceNoMap.get(keyID), sigKey));

                eeCertBagBuilder.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute, new DERBMPString(keyID));

                SubjectKeyIdentifier pubKeyId = extUtils.createSubjectKeyIdentifier(pubKey);

                eeCertBagBuilder.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, pubKeyId);

                PKCS12SafeBagBuilder keyBagBuilder = new PKCS12SafeBagBuilder(PrivateKeyInfoFactory.createPrivateKeyInfo(sharedPrivateKeyMap.get(keyID), paramsMap.get(keyID)), encOut);

                keyBagBuilder.addBagAttribute(PKCS12SafeBag.friendlyNameAttribute, new DERBMPString(keyID));
                keyBagBuilder.addBagAttribute(PKCS12SafeBag.localKeyIdAttribute, pubKeyId);

                builder.addEncryptedData(new JcePKCSPBEOutputEncryptorBuilder(PKCSObjectIdentifiers.pbeWithSHAAnd128BitRC2_CBC).setProvider("BC").build(password), new PKCS12SafeBag[] { eeCertBagBuilder.build() });

                builder.addData(keyBagBuilder.build());
            }

            PKCS12PfxPdu pfx = builder.build(new JcePKCS12MacCalculatorBuilder(NISTObjectIdentifiers.id_sha256), password);

            return pfx.getEncoded(ASN1Encoding.DL);
        }
        catch (PKCSException e)
        {
            throw new GeneralSecurityException("Unable to create key store: " + e.getMessage(), e);
        }
        catch (OperatorCreationException e)
        {
            throw new GeneralSecurityException("Unable to create operator: " + e.getMessage(), e);
        }
    }

    /**
     * Load the key store object from the passed in PKCS#12 encoding, using the passed in password.
     *
     * @param password the password to unlock the key store.
     * @param encoding the ASN.1 encoded bytes representing the PKCS#12 store.
     * @throws IOException on a parsing error.
     * @throws GeneralSecurityException if there's an exception decrypting the store.
     */
    public synchronized void load(char[] password, byte[] encoding)
        throws IOException, GeneralSecurityException
    {
        try
        {
            PKCS12PfxPdu pfx = new PKCS12PfxPdu(encoding);
            InputDecryptorProvider inputDecryptorProvider = new JcePKCSPBEInputDecryptorProviderBuilder()
                .setProvider("BC").build(password);
            ContentInfo[] infos = pfx.getContentInfos();

            for (int i = 0; i != infos.length; i++)
            {
                if (infos[i].getContentType().equals(PKCSObjectIdentifiers.encryptedData))
                {
                    PKCS12SafeBagFactory dataFact = new PKCS12SafeBagFactory(infos[i], inputDecryptorProvider);

                    PKCS12SafeBag[] bags = dataFact.getSafeBags();

                    Attribute[] attributes = bags[0].getAttributes();

                    X509CertificateHolder cert = (X509CertificateHolder)bags[0].getBagValue();

                    String keyID = getKeyID(attributes);
                    BLS01PublicKeyParameters publicKeyParameters = BLSPublicKeyFactory.createKey(cert.getSubjectPublicKeyInfo());

                    paramsMap.put(keyID, publicKeyParameters.getParameters());
                    sequenceNoMap.put(keyID, ASN1Integer.getInstance(cert.getExtension(XimixObjectIdentifiers.ximixShareIdExtension).getParsedValue()).getValue().intValue());
                    sharedPublicKeyMap.put(keyID, publicKeyParameters.getPk());

                    if (KeyUsage.fromExtensions(cert.getExtensions()).hasUsages(KeyUsage.digitalSignature))
                    {
                        signingKeys.add(keyID);
                    }
                }
                else
                {
                    PKCS12SafeBagFactory dataFact = new PKCS12SafeBagFactory(infos[i]);

                    PKCS12SafeBag[] bags = dataFact.getSafeBags();
                    String keyID = getKeyID(bags[0].getAttributes());

                    PKCS8EncryptedPrivateKeyInfo encInfo = (PKCS8EncryptedPrivateKeyInfo)bags[0].getBagValue();
                    PrivateKeyInfo info = encInfo.decryptPrivateKeyInfo(inputDecryptorProvider);

                    sharedPrivateKeyMap.put(keyID, ASN1Integer.getInstance(info.parsePrivateKey()).getValue());
                }
            }
        }
        catch (PKCSException e)
        {
            throw new GeneralSecurityException("Unable to load key store: " + e.getMessage(), e);
        }
    }

    // TODO: in this case we should get the private key from somewhere else - probably node config
    private X509CertificateHolder createCertificate(
        String keyID,
        int sequenceNo,
        PrivateKey privKey)
        throws GeneralSecurityException, OperatorCreationException, IOException
    {
        String name = "C=AU, O=Ximix Network Node, OU=" + "Util";

        //
        // create the certificate - version 3
        //
        X509v3CertificateBuilder v3CertBuilder = new X509v3CertificateBuilder(
            new X500Name(name),
            BigInteger.valueOf(1),
            new Date(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30),
            new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365)),
            new X500Name(name),
            this.fetchPublicKey(keyID));

        // we use keyUsage extension to distinguish between signing and encryption keys

        if (signingKeys.contains(keyID))
        {
            v3CertBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature));
        }
        else
        {
            v3CertBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.dataEncipherment));
        }

        v3CertBuilder.addExtension(XimixObjectIdentifiers.ximixShareIdExtension, true, new ASN1Integer(sequenceNo));

        return v3CertBuilder.build(new JcaContentSignerBuilder("SHA1withECDSA").setProvider("BC").build(privKey));
    }

    private String getKeyID(Attribute[] attributes)
    {
        for (Attribute attr : attributes)
        {
            if (PKCS12SafeBag.friendlyNameAttribute.equals(attr.getAttrType()))
            {
                return DERBMPString.getInstance(attr.getAttrValues().getObjectAt(0)).getString();
            }
        }

        throw new IllegalStateException("No friendlyNameAttribute found.");
    }
}
