/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell;

import lombok.NonNull;
import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * <p>Convenience methods for encryption with common algorithms.</p>
 *
 * @author David B. Bracewell
 */
public enum EncryptionMethod {
  AES("AES", 16),
  DES("DES", 16) {
    @SneakyThrows
    protected Cipher constructCipher(byte[] key, int mode) {
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(name);
      KeySpec keySpec = new DESKeySpec(ensureKeyLength(key));
      SecretKey secretkey = keyFactory.generateSecret(keySpec);
      Cipher cipher = Cipher.getInstance(name);
      cipher.init(mode, secretkey);
      return cipher;
    }
  },
  TRIPLE_DES("DESede", 24),
  BLOWFISH("Blowfish", 16);

  protected final String name;
  protected final int keyLength;

  /**
   * Private Constructor
   *
   * @param name The name used for Java internals
   */
  EncryptionMethod(String name, int keyLength) {
    this.name = name;
    this.keyLength = keyLength;
  }

  /**
   * Parses a String to find the correct EncryptionMethod.
   *
   * @param name The name;
   * @return An EncryptionMethod
   */
  public static EncryptionMethod fromName(@NonNull String name) {
    for (EncryptionMethod en : EncryptionMethod.values()) {
      if (en.name.equals(name)) {
        return en;
      }
    }
    return EncryptionMethod.valueOf(name);
  }

  @SneakyThrows
  protected final byte[] ensureKeyLength(byte[] key) {
    if (key.length == keyLength) {
      return key;
    }
    MessageDigest digest;
    digest = MessageDigest.getInstance("MD5");
    byte[] keyBytes = Arrays.copyOf(digest.digest(key), keyLength);
    for (int j = 0, k = 16; j < (keyLength - 16); ) {
      keyBytes[k++] = keyBytes[j++];
    }
    return keyBytes;
  }

  @SneakyThrows
  protected Cipher constructCipher(byte[] key, int mode) {
    SecretKeySpec keySpec = new SecretKeySpec(ensureKeyLength(key), name);
    Cipher cipher = Cipher.getInstance(name);
    cipher.init(mode, keySpec);
    return cipher;
  }

  /**
   * Encrypts content into a Base64 encoded string.
   *
   * @param content The content
   * @param key     The password
   * @return A Base64 encoded version of the encrypted content
   */
  public final String encrypt(String content, String key) {
    return encrypt(content.getBytes(), key.getBytes());
  }

  /**
   * Encrypts content into a Base64 encoded string.
   *
   * @param content The content
   * @param key     The password
   * @return A Base64 encoded version of the encrypted content
   */
  @SneakyThrows
  public String encrypt(byte[] content, byte[] key) {
    Cipher cipher = constructCipher(key, Cipher.ENCRYPT_MODE);
    byte[] encryptedText = cipher.doFinal(content);
    return new String(Base64.getEncoder().withoutPadding().encode(encryptedText));
  }

  /**
   * Decrypts encrypted content in a Base64 encoded string into a string.
   *
   * @param content The encrypted content
   * @param key     The password
   * @return An unencrypted version of the content
   */
  public final String decryptToString(String content, String key) {
    return new String(decrypt(content, key.getBytes()), StandardCharsets.UTF_8);
  }

  /**
   * Decrypts encrypted content in a Base64 encoded string into a byte array.
   *
   * @param content The encrypted content
   * @param key     The password
   * @return An unencrypted version of the content
   */
  public final byte[] decrypt(String content, String key) {
    return decrypt(content, key.getBytes());
  }

  /**
   * Decrypts encrypted content in a Base64 encoded byte array into a byte array.
   *
   * @param content The encrypted content
   * @param key     The password
   * @return An unencrypted version of the content
   */
  @SneakyThrows
  public byte[] decrypt(String content, byte[] key) {
    Cipher cipher = constructCipher(key, Cipher.DECRYPT_MODE);
    return cipher.doFinal(Base64.getDecoder().decode(content.trim()));
  }

}// END OF EncryptionMethod