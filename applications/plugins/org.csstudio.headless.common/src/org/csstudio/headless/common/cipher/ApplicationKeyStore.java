
/*
 * Copyright (c) 2014 Stiftung Deutsches Elektronen-Synchrotron,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS.
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS,
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION,
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

package org.csstudio.headless.common.cipher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 * @author mmoeller
 * @since 24.01.2014
 */
public class ApplicationKeyStore {

    private String keyStorePath;

    private KeyPair appKeyPair;

    private String bas64PublicKey;

    public ApplicationKeyStore(String storePath) {
        if (storePath == null) {
            keyStorePath = "./";
        } else {
            if (storePath.trim().isEmpty()) {
                keyStorePath = "./";
            } else {
                keyStorePath = storePath.trim();
            }
        }
        if (!keyStorePath.endsWith("\\") && !keyStorePath.endsWith("/")) {
            keyStorePath += "/";
        }
        appKeyPair = null;
        if (existServerKeyFiles()) {
            readKeyPair();
        } else {
            createServerKeyPair();
        }
        createPublicKeyAsBase64();

    }

    public static PublicKey getPublicKeyFromBase64(String base64) {
        PublicKey publicKey = null;
        ObjectInputStream ois = null;
        try {
            byte[] objectByte = Base64.decodeBase64(base64);
            ByteArrayInputStream bais = new ByteArrayInputStream(objectByte);
            ois = new ObjectInputStream(bais);
            Object object = ois.readObject();
            if (object instanceof PublicKey) {
                publicKey = (PublicKey) object;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {ois.close();} catch (Exception e) {/**/}
            }
        }
        return publicKey;
    }

    public PublicKey getPublicKey() {
        return appKeyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return appKeyPair.getPrivate();
    }

    public String getBase64PublicKey() {
        return bas64PublicKey;
    }

    private void createPublicKeyAsBase64() {
        PublicKey publicKey = appKeyPair.getPublic();
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(publicKey);
            data = baos.toByteArray();
            bas64PublicKey = Base64.encodeBase64String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {oos.close();} catch (Exception e) {/**/}
            }
        }
    }

    private void readKeyPair() {

        File dateiPriK = new File(keyStorePath + "private.key");

        // Private Key lesen
        FileInputStream fis = null;
        byte[] encodedPrivateKey = null;

        // generiere Key
        try {

            fis = new FileInputStream(dateiPriK);
            encodedPrivateKey = new byte[(int) dateiPriK.length()];
            fis.read(encodedPrivateKey);

            try {fis.close();} catch (Exception e) {/**/}
            fis = null;

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
            PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

            //+++++++++++++++++++++++++++++++++
            // Public key
            //+++++++++++++++++++++++++++++++++
            // Datei
            File dateiPubK = new File(keyStorePath + "public.key");

            // Public key lesen
            fis = new FileInputStream(dateiPubK);
            byte[] encodedPublicKey = new byte[(int) dateiPubK.length()];
            fis.read(encodedPublicKey);

            try {fis.close();} catch (Exception e) {/**/}

            // generiere Key
            keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

            appKeyPair = new KeyPair(publicKey, privateKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean existServerKeyFiles() {
        File privateFile = new File(keyStorePath + "private.key");
        File publicFile = new File(keyStorePath + "public.key");
        return privateFile.exists() && publicFile.exists();
    }

    private void createServerKeyPair() {

        File dir = new File(keyStorePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        PrivateKey privateKey = null;
        PublicKey publicKey = null;

        try {

            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            keygen.initialize(1024);
            appKeyPair = keygen.genKeyPair();

            // schluessel lesen
            privateKey = appKeyPair.getPrivate();
            publicKey = appKeyPair.getPublic();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (privateKey != null && publicKey != null) {
            // Public Key sichern
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKey.getEncoded());
            FileOutputStream fos = null;

            try {
                fos = new FileOutputStream(dir.getAbsoluteFile() + "/public.key");
                fos.write(x509EncodedKeySpec.getEncoded());
                fos.close();

                // Private Key sichern
                PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKey.getEncoded());
                fos = new FileOutputStream(dir.getAbsoluteFile() + "/private.key");
                fos.write(pkcs8EncodedKeySpec.getEncoded());
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
