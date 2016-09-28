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

import java.nio.ByteBuffer;

/**
 *
 */
public class GetToolkitObject implements ToolkitMessage {
  private final String type;
  private final String name;

  public GetToolkitObject(String type, String name) {
    this.type = type;
    this.name = name;
  }

  @Override
  public ToolkitCommand command() {
    return ToolkitCommand.GET;
  }

  @Override
  public String type() {
    return type;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public byte[] payload() {
    return null;
  }
  
}
