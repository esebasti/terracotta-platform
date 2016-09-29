/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.toolkit;

/**
 *
 */
public class ToolkitReconnectData {
  private final String type;
  private final String name;
  private final byte[] payload;

  public ToolkitReconnectData(String type, String name, byte[] payload) {
    this.type = type;
    this.name = name;
    this.payload = payload;
  }

  public String getType() {
    return type;
  }

  public String getName() {
    return name;
  }

  public byte[] getPayload() {
    return payload;
  }
  
  
}
