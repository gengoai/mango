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

package com.davidbracewell.utils;

import com.davidbracewell.application.CommandLineApplication;
import com.davidbracewell.cli.Option;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.CSVReader;
import com.davidbracewell.io.CSVWriter;
import com.davidbracewell.io.resource.Resource;

import java.util.List;

public class Csv2TsvConverter extends CommandLineApplication {

  private static final long serialVersionUID = 7922861124680149233L;
  @Option(description = "The input file.", required = true)
  Resource input;

  @Option(description = "The input file.", required = true)
  Resource output;


  public Csv2TsvConverter() {
    super("Csv2TsvConverter");
  }

  /**
   * @param args Command line args
   */
  public static void main(String[] args) {
    new Csv2TsvConverter().run(args);
  }

  @Override
  protected void programLogic() throws Exception {
    try (CSVReader reader = CSV.builder().reader(input); CSVWriter writer = CSV.builder().delimiter('\t').writer(output)) {
      List<String> row;
      while ((row = reader.nextRow()) != null) {
        writer.write(row);
      }
    }
  }

}
