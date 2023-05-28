package netty.zhyf.domain;

import java.util.Set;

import lombok.Data;

@Data
public class Group {

    private String groupName;

    private Set<String> members;

    public Group(String groupName, Set<String> members) {
        this.groupName = groupName;
        this.members = members;
    }

}
