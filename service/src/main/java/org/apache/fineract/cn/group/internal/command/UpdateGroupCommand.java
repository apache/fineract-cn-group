package org.apache.fineract.cn.group.internal.command;

import org.apache.fineract.cn.group.api.v1.domain.Group;

public class UpdateGroupCommand {

    private final Group group;

    public UpdateGroupCommand(final Group group) {
        super();
        this.group = group;
    }

    public Group group() {
        return this.group;
    }
}