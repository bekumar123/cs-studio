
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

import java.security.Key;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;

/**
 * @author mmoeller
 * @since 23.01.2014
 */
public class CipherMessages {

    public CipherMessages() {
    }

    public String encodeBase64(String text) {
        if (text == null) {
            return "";
        }
        return Base64.encodeBase64String(text.getBytes());
    }

    public String decodeBase64(String text) {
        if (text == null) {
            return "";
        }
        return new String(Base64.decodeBase64(text));
    }

    /**
     * Encrypts only the values associated to the key names in the collection <i>encryptedNames</i>
     *
     * @param msg
     * @param encryptedNames
     * @param key
     * @return A Map object that contains the encrypted content OR en empty Map object in case
     *         of an error.
     */
    public Map<String, String> encryptMessage(Map<String, String> msg,
                                              Collection<String> encryptedNames,
                                              Key key) {
        Map<String, String> result = new HashMap<String, String>();
        if (key != null) {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                Iterator<String> names = msg.keySet().iterator();
                while (names.hasNext()) {
                    String name = names.next();
                    String value = msg.get(name);
                    if (encryptedNames.contains(name) || encryptedNames.isEmpty()) {
                        value = Base64.encodeBase64String(cipher.doFinal(value.getBytes()));
                    }
                    result.put(name, value);
                }
            } catch (Exception e) {
                result.clear();
            }
        }
        return result;
    }

    /**
     * Every value in the map will be encrypted.
     *
     * @param msg
     * @param key
     * @return
     */
    public Map<String, String> encryptMessage(Map<String, String> msg, Key key) {
        return encryptMessage(msg, new Vector<String>(), key);
    }

    public Map<String, String> decryptMessage(Map<String, String> msg,
                                              Collection<String> encryptedNames,
                                              Key key) {
        Map<String, String> result = new HashMap<String, String>();
        if (key != null) {
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, key);
                Iterator<String> names = msg.keySet().iterator();
                while (names.hasNext()) {
                    String name = names.next();
                    String value = msg.get(name);
                    if (encryptedNames.contains(name) || encryptedNames.isEmpty()) {
                        value = new String(cipher.doFinal(Base64.decodeBase64(value)));
                    }
                    result.put(name, value);
                }
            } catch (Exception e) {
                result.clear();
            }
        }
        return result;
    }

    public Map<String, String> decryptMessage(Map<String, String> msg, Key key) {
        return decryptMessage(msg, new Vector<String>(), key);
    }

    public String encryptString(String text, Key key) {
        if (text == null) {
            return "";
        }
        if (text.trim().isEmpty()) {
            return "";
        }
        String result = null;
        if (key != null) {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.ENCRYPT_MODE, key);
                byte[] encrypted = cipher.doFinal(text.trim().getBytes());
                result = Base64.encodeBase64String(encrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String decryptString(String text, Key key) {
        if (text == null) {
            return "";
        }
        if (text.trim().isEmpty()) {
            return "";
        }
        String result = null;
        if (key != null) {
            byte[] code = Base64.decodeBase64(text.trim());
            try {
                Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] decrypt = cipher.doFinal(code);
                result = new String(decrypt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
