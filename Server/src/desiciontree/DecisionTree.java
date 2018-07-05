package desiciontree;

import database.*;
import desiciontree.TreeNode.*;

import java.io.*;
import java.util.*;

/**
 *  决策树模块的顶层类，
 *  构造函数以userId为索引从database中读取用户信息，
 *  通过方法studyAttribute传入需要学习的属性并构建决策树，
 *  ......
 *
 *  成员变量：
 *      attrSelMode     最佳分裂属性选择模式(暂未实现)
 *      table           <属性-列表>形式存储数据的
 *      root            所构建决策树的根节点
 *      attrList        静态的属性列表
 */
public class DecisionTree {
    private Integer attrSelMode;    //最佳分裂属性选择模式，0表示以信息增益度衡量，1表示以信息增益率衡量
    private Map<String, List<Integer>> table;
    private Node root;

    private DecisionTree() {
        new Attribute();
        attrSelMode = 0;
    }

    /**
     *  以userId为参数，从数据库中读取相关数据，
     *  并以<属性-列表>的形式存储
     *
     *  @param userId   用户Id，作为索引从数据库中读取相关数据
     *
     */
    public DecisionTree(Integer userId) {
        this();
        Main userMain = new Main();
        UserInfo userData = userMain.getUserInfoById(userId);

        List<List<Integer>> weatherData = new ArrayList<>();
        List<List<Integer>> clothesData = new ArrayList<>();
        for(Weather weather: userData.getClothesInfo().getWeatherHistory()) {
            weatherData.add(weather.formatWeather());
        }

        for(Suit suit: userData.getClothesInfo().getSuitHistory()) {
            clothesData.add(suit.getClothesIdList());
        }

        List<List<Integer>> data = null;
        try {
            List<List<Integer>> data1 = deepCopy(weatherData);
            List<List<Integer>> data2 = deepCopy(clothesData);
            data = data1;
            assert data1.size() == data2.size();
            for(int i = 0; i < data.size(); ++i) {
                data.get(i).addAll(data2.get(i));
            }

        } catch(Exception e) {
            e.printStackTrace();
            assert false;
        }

        table = listToMap(data);
    }

    public DecisionTree(Integer userId, Integer attrSelMode) {
        this(userId);
        this.attrSelMode = attrSelMode;
    }

    public void setAttrSelMode(Integer attrSelMode) {
        this.attrSelMode = attrSelMode;
    }

    public Integer recommandation(String attrToLearn, Map<String, Integer> params) {
        root = buildTree(attrToLearn);
        Node p = root;
        if(p==null)
        	return 0;
        while(!p.isLeaf() ) {
            RootNode pr = (RootNode)p;
            String divideAttr = pr.getDivideAttr();
            Integer data = params.get(divideAttr);
            Integer key = Attribute.transferKey(data, divideAttr);
            p = pr.accessSubTree(key);
            if(p==null) {
            	return 0;
            }
        }
        
        return ((LeafNode)p).getClothesId(); 
    }

    /**
     * 构建决策树，对root进行赋值
     *
     * @param   attrName   将要学习的属性名称
     *
     * @return  所建成的决策树根节点
     *
     */
    private Node buildTree(String attrName) {
        assert Attribute.isClothes(attrName);
        AttributeTree attrTree = new AttributeTree(table, attrName);
        return attrTree.buildTreeRecursion();
    }

    /**
     *  用ByteArray的方法实现Java的深度拷贝
     *  需要用try...catch...处理可能出现的异常
     *
     *  @param  src     需要被拷贝的List
     *
     *  @return 拷贝后得到新的List的引用
     *
     */
    private static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }


    /**
     *  将weather和clothes有关的数据改变组织形式，
     *  以<属性-列表>的对应表存储起来
     *
     *  @param  data    与天气、衣物搭配有关的数据列表
     *
     *  @return <属性-列表>的对应图结构
     *
     */
    private static Map<String, List<Integer>> listToMap(List<List<Integer>> data) {
        Map<String, List<Integer>> table = new HashMap<>();
        for(String name: Attribute.attrList) {
            table.put(name, new ArrayList<>());
        }

        for(List<Integer> line: data) {
            for(int i = 0; i < line.size(); ++i) {
                table.get(Attribute.attrList.get(i)).add(line.get(i));
            }
        }
        return table;
    }

    /**
     *  手动输入weather和clothes中的数据，用于测试
     */
    private void setTable() {
        table = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter lines of data: ");
        Integer lines = sc.nextInt();
        for(String attrName: Attribute.attrList) {
            System.out.print("Enter " + attrName + ": ");
            table.put(attrName, new ArrayList<>());
            for(int i = 0; i < lines; ++i) {
                table.get(attrName).add(sc.nextInt());
            }
        }
    }

    /**
     *  输入用户属性名称，匹配后输出与其相关性最大的属性，
     *  用于对attrTree中方法selectDivideAttr的测试
     *
     *  @param   attrName    用于匹配的用户属性名称
     *
     *  @return  返回与attrName相关性最大的属性
     */
    private String experiment(String attrName) {
        AttributeTree attrTree = new AttributeTree(table, attrName);
        return attrTree.selectDivideAttr();
    }

    public static void main(String[] args) {
        DecisionTree tree = new DecisionTree();
        System.out.print("请输入需要学习的属性: ");
        String attrToLearn = new Scanner(System.in).next();
        tree.setTable();
        String result = tree.experiment(attrToLearn);
        System.out.println("选择属性: " + result);
    }
}