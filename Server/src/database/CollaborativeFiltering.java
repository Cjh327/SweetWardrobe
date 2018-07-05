package database;


import Jama.Matrix;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class CollaborativeFiltering {

    private Double learningRate, lambda;

    private Integer F, U, I;

    private Integer numIterations;

    private Matrix R, R_predict;

    private Matrix P, Q;

    private void readFile(double[][] arr, String pathname, int r, int c) {
        try {
            File file = new File(pathname);
            Scanner s = new Scanner(file);
            for (int i = 0; i < r; i++) {
                for (int j = 0; j < c; j++) {
                    arr[i][j] = s.nextDouble();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    CollaborativeFiltering(Double learningRate, Double lambda, Integer F, Integer numIterations, List<UserInfo> userInfoList) {
        this.learningRate = learningRate;
        this.lambda = lambda;
        this.numIterations = numIterations;
        this.F = F;
        this.U = userInfoList.size();
        this.I = 42;
        this.Q = Matrix.random(U, F);
        this.P = Matrix.random(I, F);
        this.R = new Matrix(U, I);
        for (Integer u = 0; u < userInfoList.size(); u++) {
            for (Clothes clothes : userInfoList.get(u).getClothesInfo().getClothesList()) {
                Integer feat1 = clothes.getAbsoluteFeats().get(0);
                Integer feat2 = clothes.getAbsoluteFeats().get(1);
                R.set(u, feat1, feat1 + 1);
                R.set(u, feat2, feat2 + 1);
            }
        
            //可加入历史数据
        }
        
    }

    public CollaborativeFiltering(Double learningRate, Double lambda, Integer F, Integer numIterations) {
        this.learningRate = learningRate;
        this.lambda = lambda;
        this.numIterations = numIterations;
        this.F = F;
        this.U = 8;
        this.I = 28;

    /*    double[][] arr = {{5, 0, 0, 0, 0, -5, 0, 0, 5, 3, 0, 1, 5},     //典型休闲风：夹克，风衣，T恤，卫衣，休闲短裤，休闲长裤，牛仔裤，休闲鞋，白色，粉色，卡其色，棕色，蓝色
                {0, 0, 0, 3, 0, 0, 0, 0, 3, 0, 0, 0, 4},                //典型运动风：大衣，卫衣，T恤，运动长裤，运动短裤，运动鞋，蓝色，灰色，黑色，棕色
                {0, 0, 1, 0, 2, -5, 4, 0, 0, -2, -2, 0, -2},            //典型商务风：西装，西裤，马甲，POLO，衬衫，西裤，皮鞋，褐色，黑色，青色，白色
                {0, 4, 4, 3, 0, 0, -2, 0, -5, 0, 0, 3, 0},              //典型程序员：冲锋衣，衬衫，POLO，T恤，休闲长裤，运动鞋，凉鞋，灰色，白色，黑色
                {0, 5, -5, 0, -5, 0, 4, 3, 0, 0, 4, 0, 0},              //潮流街头风：皮衣，卫衣，T恤，牛仔裤，休闲长裤，休闲鞋，白色，黄色，粉色，蓝色，黑色，紫色
                {0, 0, 4, 0, 0, 3, 0, 0, 4, 0, 0, 0, 0},                //复古工装风：夹克，衬衫，T恤，工装裤，靴子，白色，青色，绿色，黑色
                {0, -2, 0, 0, 0, 5, 0, 0, 0, 4, 0, 4, -2},              //日系风：大衣，风衣，毛衣，针织衫，休闲短裤，休闲长裤，休闲鞋，绿色，白色，黑色，青色
                {0, -2, 0, 0, 0, 5, 0, 5, 0, 4, 0, 0, -2}};*/
        double[][] arr = new double[this.U][this.I];
        readFile(arr, "D:\\userdata.txt", this.U, this.I);
        System.out.printf("原始user-feature矩阵R\n");
        for (int i = 0; i < this.U; i++) {
            System.out.print(i + ":   ");
            for (int j = 0; j < this.I; j++) {
                System.out.print((int)arr[i][j] + "  ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
        this.Q = Matrix.random(U, F);
        this.P = Matrix.random(I, F);
        //  this.Q = new Matrix(U, F, 0.1);
        //  this.P = new Matrix(I, F, 0.1);
        this.R = new Matrix(arr);
        assert this.R.getRowDimension() == this.U;
        assert this.R.getColumnDimension() == this.I;
    }


    public void optimize() {
        for (Integer ni = 0; ni < numIterations; ni++) {
            for (Integer u = 0; u < U; u++) {
                for (Integer i = 0; i < I; i++) {

                    Matrix Qu = Q.getMatrix(u, u, 0, F - 1);
                    Matrix Pi = P.getMatrix(i, i, 0, F - 1);

                    Matrix dQ = Pi.times(R.get(u, i) - (Qu.times(Pi.transpose())).get(0, 0)).minus(Qu.times(lambda));
                    Matrix dP = Qu.times(R.get(u, i) - (Qu.times(Pi.transpose())).get(0, 0)).minus(Pi.times(lambda));
                    assert dQ.getRowDimension() == 1 && dQ.getColumnDimension() == F;
                    assert dP.getRowDimension() == 1 && dP.getColumnDimension() == F;

                    Qu.plusEquals(dQ.times(learningRate));
                    Pi.plusEquals(dP.times(learningRate));

                    Q.setMatrix(u, u, 0, F - 1, Qu);
                    P.setMatrix(i, i, 0, F - 1, Pi);

                }
            }
        }
    }

    private Double euclideanDistance(Integer user1, Integer user2) {

        Matrix user1Matrix = R_predict.getMatrix(user1, user1, 0, I - 1);
        Matrix user2Matrix = R_predict.getMatrix(user2, user2, 0, I - 1);

        assert user1Matrix.getRowDimension() == 1 && user2Matrix.getRowDimension() == 1;
        Matrix dif = user1Matrix.minus(user2Matrix);
        dif.arrayTimesEquals(dif);
        Double dist = 0.0;
        for (Integer i = 0; i < dif.getColumnDimension(); i++) {
            dist += dif.get(0, i);
        }
        dist = Math.sqrt(dist);

        return dist;

    }

    public Integer recommendUser(Integer userId) {

        optimize();
        R_predict = Q.times(P.transpose());
        printResult();
        Integer recommendedUserId = -1;
        Double minDist = 99999.9;
        for (Integer u = 0; u < R_predict.getRowDimension(); u++) {
            if (!u.equals(userId)) {
                Double dist = euclideanDistance(userId, u);
                System.out.printf("用户%d与用户%d的欧几里得距离为%f\n", userId, u, dist);
                if (dist < minDist) {
                    minDist = dist;
                    recommendedUserId = u;
                }
            }
        }

        System.out.printf("向用户%d推荐用户%d\n", userId, recommendedUserId);

        return recommendedUserId;

    }

    public void printResult() {
    	System.out.printf("user-style矩阵Q\n");
        Q.print(F, 2);
        System.out.printf("style-feature矩阵P\n");
        P.print(F, 2);
        System.out.printf("预测user-feature矩阵R_predict\n");
        R_predict.print(I, 2);
    }


    public static void main(String[] args) {
        double learningRate = 0.1, lambda = 0.7;
        Integer F = 5, numIteration = 1000;
/*
        Clothes clothes1 = new Clothes(0, 0, 0, 25, 0); //夹克
        Clothes clothes2 = new Clothes(1, 0, 0, 26, 0); //夹克
        Clothes clothes3 = new Clothes(2, 0, 0, 27, 0); //夹克
        Clothes clothes4 = new Clothes(3, 0, 0, 28, 0); //夹克
        List<Clothes> clothesList1 = new ArrayList<>();
        clothesList1.add(clothes1);
        clothesList1.add(clothes2);
        clothesList1.add(clothes3);
        clothesList1.add(clothes4);
        ClothesInfo clothesInfo1 = new ClothesInfo(clothesList1);
        UserInfo userInfo1 = new UserInfo(110, "cjh", "123456", clothesInfo1);
        Clothes clothes5 = new Clothes(4, 0, 0, 29, 0); //夹克
        Clothes clothes6 = new Clothes(5, 0, 0, 30, 0); //夹克
        Clothes clothes7 = new Clothes(6, 0, 0, 31, 0); //夹克
        Clothes clothes8 = new Clothes(7, 0, 1, 28, 0); //
        List<Clothes> clothesList2 = new ArrayList<>();
        clothesList2.add(clothes5);
        clothesList2.add(clothes6);
        clothesList2.add(clothes7);
        clothesList2.add(clothes8);
        ClothesInfo clothesInfo2 = new ClothesInfo(clothesList2);
        UserInfo userInfo2 = new UserInfo(110, "cjh", "123456", clothesInfo2);
        Clothes clothes9 = new Clothes(8, 0, 2, 30, 0); //
        Clothes clothes10 = new Clothes(9, 0, 3, 31, 0); //
        Clothes clothes11 = new Clothes(10, 0, 4, 32, 0); //
        Clothes clothes12 = new Clothes(11, 0, 5, 32, 0); //
        List<Clothes> clothesList3 = new ArrayList<>();
        clothesList3.add(clothes9);
        clothesList3.add(clothes10);
        clothesList3.add(clothes11);
        clothesList3.add(clothes12);
        ClothesInfo clothesInfo3 = new ClothesInfo(clothesList3);
        UserInfo userInfo3 = new UserInfo(110, "cjh", "123456", clothesInfo3);
        List<UserInfo> userInfoList = new ArrayList<>();
        userInfoList.add(userInfo1);
        userInfoList.add(userInfo2);
        userInfoList.add(userInfo3);
*/
        //特征数F不要太大，学习率learningRate不要太大，正则项参数lamda不要太大
        CollaborativeFiltering LFM = new CollaborativeFiltering(learningRate, lambda, F, numIteration);
        LFM.recommendUser(0);
    }

}