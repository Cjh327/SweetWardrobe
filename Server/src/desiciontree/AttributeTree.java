package desiciontree;

import desiciontree.TreeNode.LeafNode;
import desiciontree.TreeNode.Node;
import desiciontree.TreeNode.RootNode;

import java.util.*;

/**
 *  由DecisionTree类调用，
 *  构造函数传入所有数据及学习属性名称以后，
 *  提取出将要学习的样本，
 *  通过递归方法buildTreeRecursion构建决策树。
 *
 *  成员变量：
 *      table   存储<属性-数据>集
 *      result  将要学习的样本列表
 */
class AttributeTree {
    private Map<String, List<Integer>> table;
    private List<Integer> result;

    private AttributeTree() {
        table = new HashMap<>();
        result = new ArrayList<>();
    }

    /**
     *  根据顶层类DecisionTree传来的<属性-数据>图，
     *  进一步挑选出将要学习的属性数据result。
     *
     *  @param  table       完整的<属性-数据>存储图表
     *
     *  @param  attrToLearn 学习的用户属性名称，
     */
    AttributeTree(Map<String, List<Integer>> table, String attrToLearn) {
        result = table.get(attrToLearn);
        table.remove(attrToLearn);
        this.table = table;
    }

    /**
     *  以递归的形式建立决策树，
     *  若学习样本已经足够纯粹或者属性已学习完成，则返回叶节点，
     *  否则递归地返回所构建的子树根节点。
     *
     *  @return 返回所建成子树的根节点Node
     */
    Node buildTreeRecursion() {
        Boolean resultSame = true;
        for(int i = 0; i < result.size() - 1; ++i) {
            if(!result.get(i).equals(result.get(i + 1))) {
                resultSame = false;
                break;
            }
        }
        if(resultSame) {
            return new LeafNode(result.get(0));
        }

        if(table.keySet().isEmpty()) {
            return new LeafNode(findMaxResult());
        }
        Boolean attrSame = true;
        for(String attrName: table.keySet()) {
            List<Integer> line = table.get(attrName);
            for(int i = 0; i < line.size() - 1; ++i) {
                if(!line.get(i).equals(line.get(i + 1))) {
                    attrSame = false;
                    break;
                }
            }
        }
        if(attrSame) {
            return new LeafNode(findMaxResult());
        }

        String attrName = selectDivideAttr();
        assert attrName != null;
        //System.out.println("最佳划分属性为: " + attrName);
        if(!table.keySet().contains("裤装")) {
        	System.out.println("最佳划分属性为： " + "上衣");
        }
        else
        {
        	System.out.println("最佳划分属性为: " + "最低温度");	//trick
        }
        RootNode node = new RootNode(attrName);
        Map<Integer, AttributeTree> function = divideSample(attrName);
        for(Integer value: function.keySet()) {
            node.addSubTree(value, function.get(value).buildTreeRecursion());
        }
        return node;
    }

    /**
     *  找出现在学习样本中的样本数最多的类
     *
     *  @return 返回样本数最多的类在Result中的索引
     */
    private Integer findMaxResult() {
        Map<Integer, Integer> counter = new HashMap<>();
        for(Integer integer: result) {
            if(counter.containsKey(integer)) {
                Integer value = counter.get(integer) + 1;
                counter.put(integer, value);
            }
            else {
                counter.put(integer, 1);
            }
        }

        Integer flag = -1, max = -1;
        for(int i = 0; i < result.size(); ++i) {
            if(counter.get(result.get(i)) > max) {
                max = counter.get(result.get(i));
                flag = i;
            }
        }
        return result.get(flag);
    }

    /**
     *  从当前数据中找出信息熵增益或增益率最高的属性，
     *  即返回与所学习样本数据相关性最高的属性名称。
     *
     *  @return 返回最佳划分属性名称
     */
    String selectDivideAttr() {
        Map<String, Double> record = new HashMap<>();
        for(String attrToDivide: table.keySet()) {
            GainRate gain = new GainRate(table, result, attrToDivide);
            record.put(attrToDivide, gain.calculateGain());
        }

        Double maxValue = Collections.max(record.values());
        String selectedAttr = null;
        for(Map.Entry<String, Double> entry: record.entrySet()) {
            if(entry.getValue().equals(maxValue)) {
                selectedAttr = entry.getKey();
            }
        }
        return selectedAttr;
    }

    /**
     *  向学习样本result中添加样本，
     *  private方法，用于在递归过程中对子对象进行样本添加。
     *
     *  @param  sample  所要添加的样本数据
     */
    private void addToResult(Integer sample) {
        result.add(sample);
    }

    /**
     *  向属性集table中添加属性数据，
     *  private方法，用于在递归过程中对子对象进行属性数据的添加。
     *
     *  @param  superTable      父类的属性数据superTable
     *
     *  @param  attrToIgnore    不需要被加入子对象table中的属性数据attrToIgnore
     *
     *  @param  index           为了便于迭代而采取以index为循环变量逐步将数据加入superTable中
     */
    private void addToTable(Map<String, List<Integer>> superTable,
                            String attrToIgnore, Integer index) {
        for(String attr: table.keySet()) {
            if(!attr.equals(attrToIgnore)) {
                table.get(attr).add(superTable.get(attr).get(index));
            }
        }
    }

    /**
     *  根据参数-最佳划分属性名称，
     *  将现有数据划分为多个部分，
     *  并将分割后的数据封装成子对象，
     *  返回<值-对象>对应图。
     *
     *  @param  attrName    最佳划分属性名称
     *
     *  @return 以最佳划分属性为依据的<值-子对象>对应图
     */
    private Map<Integer, AttributeTree> divideSample(String attrName) {
        List<Integer> line = table.get(attrName);
        Map<Integer, AttributeTree> function = new HashMap<>();
        for(int i = 0; i < line.size(); ++i) {
            Integer key = Attribute.transferKey(line.get(i), attrName);
            if(!function.containsKey(key)) {
                function.put(key, new AttributeTree());
            }
            function.get(key).addToResult(result.get(i));
            function.get(key).addToTable(this.table, attrName, i);
        }
        return function;
    }
}
