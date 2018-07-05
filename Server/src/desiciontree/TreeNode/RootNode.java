package desiciontree.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RootNode extends Node {
    private String attrName;
    private Map<Integer, Node> subTree;
    /*
     *  attrName为该子树结点所对应的最佳分裂属性名称
     *  subTree存储该结点所有该属性的值与其对应的分支
     */

    private RootNode() {
        end = false;
        subTree = new HashMap<>();
    }

    public RootNode(String attrName) {
        this();
        this.attrName = attrName;
    }

    public void addSubTree(Integer key, Node value) {
        if(!subTree.containsKey(key)) {
            subTree.put(key, value);
        }
        else {
            System.out.println("该键值已被加入图中");
            assert false;
        }
    }

    public String getDivideAttr() {
        return attrName;
    }

    public Node accessSubTree(Integer key) {
        assert subTree.containsKey(key);
        return subTree.get(key);
    }
}
