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

package com.gengoai.mango.utils;


import com.gengoai.mango.EncryptionMethod;
import com.gengoai.mango.application.CommandLineApplication;
import com.gengoai.mango.cli.Option;
import com.gengoai.mango.io.resource.Resource;
import com.gengoai.mango.EncryptionMethod;
import com.gengoai.mango.cli.Option;
import com.gengoai.mango.io.resource.Resource;

public class Encrypt extends CommandLineApplication {


  private static final long serialVersionUID = 6088352105748946270L;
  @Option(description = "The input file.")
  Resource input;

  @Option(description = "The input file.")
  Resource output;

  @Option(description = "The key.")
  String key;

  @Option(description = "The key.", defaultValue = "false")
  boolean decrypt;


  public Encrypt() {
    super("Encrypt");
  }

  @Override
  protected void programLogic() throws Exception {
    System.out.println("-i = " + input);
    System.out.println("-o = " + output);
    System.out.println("-k = " + key);

    if (decrypt && (key == null || key.length() == 0)) {
      System.err.println("Encrypt --input <input> --output <output> -key <encryption key> [--decrypt, --base64]");
      System.exit(-1);
    }

    String content = input.readToString();
    if (decrypt) {
      output.write(EncryptionMethod.DES.decrypt(content, key.getBytes()));
    } else {
      output.write(EncryptionMethod.DES.encrypt(content, key));
    }
  }

  /**
   * @param args Command Line Args
   */
  public static void main(String[] args) {
    new Encrypt().run(args);
  }

}
