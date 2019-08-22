package ff.projects.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by F on 2017/6/23.
 */
@Data
public class TreeNode implements Serializable {

    private String id;

    private String text;

    private String state = "open"; // 节点状态,'open'或者'closed',默认是'open'

    private String htmlUrl; //pages/table 目标页面

    private String dataUrl; //目标数据

    private String iconCls;

    private Set<TreeNode>  children;

    public TreeNode() {
    }

    public TreeNode(String text, String htmlUrl, String dataUrl) {
        this.text = text;
        this.htmlUrl = htmlUrl;
        this.dataUrl = dataUrl;
    }

    public TreeNode(String id,String text, String state, String htmlUrl, String dataUrl, Set<TreeNode> children) {
        this.id = id;
        this.text = text;
        this.state = state;
        this.htmlUrl = htmlUrl;
        this.dataUrl = dataUrl;
        this.children = children;
    }

    public TreeNode(String id, String text, String state, String htmlUrl, String dataUrl) {
        this.id = id;
        this.text = text;
        this.state = state;
        this.htmlUrl = htmlUrl;
        this.dataUrl = dataUrl;
    }
}
