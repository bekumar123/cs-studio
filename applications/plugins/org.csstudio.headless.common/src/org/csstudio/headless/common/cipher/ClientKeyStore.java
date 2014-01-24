
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mmoeller
 * @since 23.01.2014
 */
public class ClientKeyStore implements FilenameFilter {

    private String keyStorePath;

    private Map<String, PublicKey> clientKeys;

    public ClientKeyStore(String storePath) {
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
        clientKeys = new HashMap<String, PublicKey>();
        readClientKeys();
    }

    public PublicKey getPublicKey(String clientName) {
        PublicKey key = null;
        if (clientKeys.containsKey(clientName)) {
            key = clientKeys.get(clientName);
        }
        return key;
    }

    private void readClientKeys() {
        File clientDir = new File(keyStorePath);
        if (!clientDir.exists()) {
            // No client keys yet
            clientDir.mkdirs();
            return;
        }
        File[] fileList = clientDir.listFiles(this);
        for (File file : fileList) {
            String fileName = file.getName();
            if (!fileName.isEmpty()) {
                fileName = fileName.replace(".key", "");
                PublicKey key = readKeyFile(file);
                if (key != null) {
                    clientKeys.put(fileName, key);
                }
            }
        }
    }

    private PublicKey readKeyFile(File file) {

        PublicKey publicKey = null;
        FileInputStream fis = null;

        // generiere Key
        try {

            // Public key lesen
            fis = new FileInputStream(file);
            byte[] encodedPublicKey = new byte[(int) file.length()];
            fis.read(encodedPublicKey);

            // generiere Key
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
            publicKey = keyFactory.generatePublic(publicKeySpec);

        } catch (NoSuchAlgorithmException e) {
            // Ignore it
        } catch (InvalidKeySpecException e) {
            // Ignore it
        } catch (FileNotFoundException e) {
            // Ignore it
        } catch (IOException e) {
            // Ignore it
        } finally {
            if (fis != null) {
                try {fis.close();} catch (Exception e) {/**/}
            }
        }

        return publicKey;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(".key");
    }
}
