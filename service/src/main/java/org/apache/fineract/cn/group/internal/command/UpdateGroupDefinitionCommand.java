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

import org.apache.fineract.cn.group.api.v1.domain.GroupDefinition;

public class UpdateGroupDefinitionCommand {

    private final String identifier;
    private final GroupDefinition groupDefinition;

    public UpdateGroupDefinitionCommand(final String identifier, final GroupDefinition groupDefinition) {
        super();
        this.identifier = identifier;
        this.groupDefinition = groupDefinition;
    }

    public String identifier() {
        return this.identifier;
    }

    public GroupDefinition groupDefinition() {
        return this.groupDefinition;
    }
}