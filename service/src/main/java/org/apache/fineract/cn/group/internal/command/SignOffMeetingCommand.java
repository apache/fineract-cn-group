/*
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
package org.apache.fineract.cn.group.internal.command;

import org.apache.fineract.cn.group.api.v1.domain.SignOffMeeting;

public class SignOffMeetingCommand {

  private final String groupIdentifier;
  private final SignOffMeeting signOffMeeting;

  public SignOffMeetingCommand(final String groupIdentifier, final SignOffMeeting signOffMeeting) {
    super();
    this.groupIdentifier = groupIdentifier;
    this.signOffMeeting = signOffMeeting;
  }

  public String groupIdentifier() {
    return this.groupIdentifier;
  }

  public SignOffMeeting signOffMeeting() {
    return this.signOffMeeting;
  }
}
