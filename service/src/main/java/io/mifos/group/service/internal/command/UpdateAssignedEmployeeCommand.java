/*
 * Copyright 2016 The Mifos Initiative.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.group.service.internal.command;

public class UpdateAssignedEmployeeCommand {

  private final String identifier;
  private final String employeeIdentifier;

  public UpdateAssignedEmployeeCommand(final String identifier, final String employeeIdentifier) {
    super();
    this.identifier = identifier;
    this.employeeIdentifier = employeeIdentifier;
  }

  public String identifier() {
    return this.identifier;
  }

  public String employeeIdentifier() {
    return this.employeeIdentifier;
  }
}
