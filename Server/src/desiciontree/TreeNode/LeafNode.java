package desiciontree.TreeNode;

public class LeafNode extends Node {
    private Integer clothesId;

    public LeafNode() {
        end = true;
        clothesId = -1;
    }

    public LeafNode(Integer clothesId) {
        end = true;
        this.clothesId = clothesId;
    }

    public void setClothesId(Integer clothesId) {
        this.clothesId = clothesId;
    }

    public Integer getClothesId() {
        return clothesId;
    }
}
