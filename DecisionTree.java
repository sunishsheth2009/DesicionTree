/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/**
 *
 * @author Sunish
 */
public class DecisionTree {

    /**
     * @param args the command line arguments
     */
    Object dataSet[][];
    Object testSet[][];
    Object featureSet[];
    Object validationSet[][];

    public static void main(String[] args) throws Exception {
        TextIo t = new TextIo();
        Object dataSet[][] = t.readDataSet(args[2]); // Array for Training Data
        Object featureSet[] = t.readFeatures(args[2]); // Array of feature list
        Object testSet[][] = t.readTestSet(args[4]); // Array for Test Data
        Object validationSet[][] = t.readValidationSet(args[3]); // Array for Validation Data
        MainExecution m = new MainExecution();
        TestSet test = new TestSet();
        Cloning c = new Cloning();
        Prunning p = new Prunning();
        int l = Integer.parseInt(args[0]), k = Integer.parseInt(args[1]);

        // Tree Formed using Information Gain Heuristic :
        Node root1 = new Node();
        m.executeHeuristicOne(root1, dataSet, "root", featureSet, 0);
        System.out.println("Decision Tree using Information Gain Heuristic :");
        //Check whether the tree is displayed or not
        if (args[5].equalsIgnoreCase("Yes")) {
            System.out.println("Tree Formed before pruning :");
            root1.printTree(root1, "");
        }
        test.accCount = 0;
        test.testSet(testSet, featureSet, root1);
        double accuracy1 = test.checkAccuracy(testSet.length);
        Node root1Copy1 = new Node();
        Node root1Copy2 = new Node();
        root1Copy1 = c.clonning(root1);
        root1Copy2 = c.clonning(root1);
        p.prun(root1Copy1, root1Copy2, l, k, validationSet, featureSet, testSet, args[5]);

        System.out.println();


        // Tree Formed using Variance Heuristic :
        Node root2 = new Node();
        m.executeHeuristicTwo(root2, dataSet, "root", featureSet, 0);
        //Check whether the tree is displayed or not
        System.out.println("Decision Tree using Variance Impurity Heuristic :");
        if (args[5].equalsIgnoreCase("Yes")) {
            System.out.println("Tree Formed before pruning :");
            root2.printTree(root2, "");
        }
        test.accCount = 0;
        test.testSet(testSet, featureSet, root2);
        double accuracy2 = test.checkAccuracy(testSet.length);
        Node root2Copy1 = new Node();
        Node root2Copy2 = new Node();
        root2Copy1 = c.clonning(root2);
        root2Copy2 = c.clonning(root2);
        p.prun(root2Copy1, root2Copy2, l, k, validationSet, featureSet, testSet, args[5]);
    }
}

class MainExecution {

    /**
     *
     * @param root
     * @param dataSet
     * @param side
     * @param featureSet
     * @param level The method that creates Decision tree using Heuristic one
     * i.e. Maximum Information Gain.
     */
    public void executeHeuristicOne(Node root, Object dataSet[][], String side, Object featureSet[], int level) {
        int countCheckZero = 0;
        int countCheckOne = 0;
        String feature;

        // Calculate Purity of the Dataset.
        for (int s = 0; s < dataSet.length; s++) {
            if (dataSet[s][dataSet[0].length - 1].equals("0")) {
                countCheckZero++;
            } else {
                countCheckOne++;
            }
        }
        root.setZero(countCheckZero);
        root.setOne(countCheckOne);

        // Enters if case if the Dataset is not completely pure, i.e. the data is still impure.
        if ((countCheckZero < (dataSet.length)) && (countCheckOne < (dataSet.length)) && featureSet.length > 1) {
            GainCalculation e = new GainCalculation();
            int choosenFeature = e.completeGainCalucation(dataSet); // The function returns back the feature with maximum Information Gain
            feature = featureSet[choosenFeature].toString();
            root.setData(feature);

            // Check how many datapoint will form the left node and how many will form the right node.
            int nextDataSetCountLeft = 0;
            int nextDataSetCountRight = 0;
            for (int i = 0; i < dataSet.length; i++) {
                if (dataSet[i][choosenFeature].equals("0")) {
                    nextDataSetCountLeft++;
                } else if (dataSet[i][choosenFeature].equals("1")) {
                    nextDataSetCountRight++;
                }
            }
            Object newFeatureSet[] = new Object[dataSet[0].length - 1];
            Object LeftDataSet[][] = new Object[nextDataSetCountLeft][dataSet[0].length - 1];
            Object RightDataSet[][] = new Object[nextDataSetCountRight][dataSet[0].length - 1];

            // Form a new featureSet removing he choosenFeature from the old
            int xyz = 0;
            for (int l = 0; l < featureSet.length; l++) {
                if (l != choosenFeature) {
                    newFeatureSet[xyz] = featureSet[l];
                    xyz++;
                }
            }

            // Creating two diffirent branch
            int l = 0;
            int m = 0;
            for (int i = 0; i < dataSet.length; i++) {
                if (dataSet[i][choosenFeature].equals("0")) {
                    int j = 0;
                    int k = 0;
                    while (j < (dataSet[0].length - 1)) {
                        if (k != choosenFeature) {
                            LeftDataSet[l][j] = dataSet[i][k];
                            k++;
                            j++;
                        } else {
                            k++;
                        }
                    }
                    l++;
                } else if (dataSet[i][choosenFeature].equals("1")) {
                    int j = 0;
                    int k = 0;
                    while (j < (dataSet[0].length - 1)) {
                        if (k != choosenFeature) {
                            RightDataSet[m][j] = dataSet[i][k];
                            k++;
                            j++;
                        } else {
                            k++;
                        }
                    }
                    m++;
                }
            }

            // Setting the level of the root and then incrementing for next level.
            root.setLevel(level);
            level++;
            countCheckZero = 0;
            countCheckOne = 0;
            for (int s = 0; s < LeftDataSet.length; s++) {
                if (LeftDataSet[s][LeftDataSet[0].length - 1].equals("0")) {
                    countCheckZero++;
                } else {
                    countCheckOne++;
                }
            }

            //  Checking for boundary condition for left branch else sovling for left branch
            if ((countCheckZero == (LeftDataSet.length)) && (LeftDataSet.length) > 0 || ((countCheckZero >= countCheckOne) && newFeatureSet.length <= 2) || ((countCheckZero >= countCheckOne) && LeftDataSet.length <= 1)) {
                root.setLeftLeaf(0);
            } else if ((countCheckOne == (LeftDataSet.length)) && (LeftDataSet.length) > 0 || ((countCheckOne >= countCheckZero) && newFeatureSet.length <= 2) || ((countCheckOne >= countCheckZero) && LeftDataSet.length <= 1)) {
                root.setLeftLeaf(1);
            } else {
                Node leftNode = new Node();
                leftNode.setLevel(level);
                root.setLeft(leftNode);
                executeHeuristicOne(leftNode, LeftDataSet, "Left", newFeatureSet, level);
            }

            countCheckZero = 0;
            countCheckOne = 0;
            //  Checking for boundary condition for left branch else sovling for left branch
            for (int s = 0; s < RightDataSet.length; s++) {
                if (RightDataSet[s][RightDataSet[0].length - 1].equals("0")) {
                    countCheckZero++;
                } else {
                    countCheckOne++;
                }
            }

            if (((countCheckZero == (RightDataSet.length)) && (RightDataSet.length) > 0) || ((countCheckZero >= countCheckOne) && newFeatureSet.length <= 2) || ((countCheckZero >= countCheckOne) && (RightDataSet.length <= 1))) {
                root.setRightLeaf(0);
            } else if (((countCheckOne == (RightDataSet.length)) && (RightDataSet.length) > 0) || ((countCheckOne >= countCheckZero) && newFeatureSet.length <= 2) || ((countCheckOne >= countCheckZero) && (RightDataSet.length <= 1))) {
                root.setRightLeaf(1);
            } else {
                Node rightNode = new Node();
                root.setRight(rightNode);
                rightNode.setLevel(level);
                executeHeuristicOne(rightNode, RightDataSet, "Right", newFeatureSet, level);
            }
        }
    }

    /**
     *
     * @param root
     * @param dataSet
     * @param side
     * @param featureSet
     * @param level The method that creates Decision tree using Heuristic two
     * i.e. Variance Impurity.
     */
    public void executeHeuristicTwo(Node root, Object dataSet[][], String side, Object featureSet[], int level) {
        int countCheckZero = 0;
        int countCheckOne = 0;
        String feature;
        for (int s = 0; s < dataSet.length; s++) {
            if (dataSet[s][dataSet[0].length - 1].equals("0")) {
                countCheckZero++;
            } else {
                countCheckOne++;
            }
        }
        root.setZero(countCheckZero);
        root.setOne(countCheckOne);
        if ((countCheckZero < (dataSet.length)) && (countCheckOne < (dataSet.length))) {
            GainCalculation e = new GainCalculation();
            int choosenFeature = e.completeGainCalucationHeuristicTwo(dataSet);
            feature = featureSet[choosenFeature].toString();
            root.setData(feature);
            int nextDataSetCountLeft = 0;
            int nextDataSetCountRight = 0;
            for (int i = 0; i < dataSet.length; i++) {
                if (dataSet[i][choosenFeature].equals("0")) {
                    nextDataSetCountLeft++;
                } else if (dataSet[i][choosenFeature].equals("1")) {
                    nextDataSetCountRight++;
                }
            }
            Object newFeatureSet[] = new Object[dataSet[0].length - 1];
            Object LeftDataSet[][] = new Object[nextDataSetCountLeft][dataSet[0].length - 1];
            Object RightDataSet[][] = new Object[nextDataSetCountRight][dataSet[0].length - 1];

            int xyz = 0;
            for (int l = 0; l < featureSet.length; l++) {
                if (l != choosenFeature) {
                    newFeatureSet[xyz] = featureSet[l];
                    xyz++;
                }
            }

            int l = 0;
            int m = 0;
            for (int i = 0; i < dataSet.length; i++) {
                if (dataSet[i][choosenFeature].equals("0")) {
                    int j = 0;
                    int k = 0;
                    while (j < (dataSet[0].length - 1)) {
                        if (k != choosenFeature) {
                            LeftDataSet[l][j] = dataSet[i][k];
                            k++;
                            j++;
                        } else {
                            k++;
                        }
                    }
                    l++;
                } else if (dataSet[i][choosenFeature].equals("1")) {
                    int j = 0;
                    int k = 0;
                    while (j < (dataSet[0].length - 1)) {
                        if (k != choosenFeature) {
                            RightDataSet[m][j] = dataSet[i][k];
                            k++;
                            j++;
                        } else {
                            k++;
                        }
                    }
                    m++;
                }
            }
            root.setLevel(level);
            level++;
            countCheckZero = 0;
            countCheckOne = 0;
            for (int s = 0; s < LeftDataSet.length; s++) {
                if (LeftDataSet[s][LeftDataSet[0].length - 1].equals("0")) {
                    countCheckZero++;
                } else {
                    countCheckOne++;
                }
            }
            if (((countCheckZero == (LeftDataSet.length)) && (LeftDataSet.length) > 0) || ((countCheckZero >= countCheckOne) && newFeatureSet.length <= 2) || ((countCheckZero >= countCheckOne) && LeftDataSet.length <= 1)) {
                root.setLeftLeaf(0);
            } else if ((countCheckOne == (LeftDataSet.length)) && (LeftDataSet.length) > 0 || ((countCheckOne >= countCheckZero) && newFeatureSet.length <= 2) || ((countCheckOne >= countCheckZero) && LeftDataSet.length <= 1)) {
                root.setLeftLeaf(1);
            } else {
                Node leftNode = new Node();
                leftNode.setLevel(level);
                root.setLeft(leftNode);
                executeHeuristicTwo(leftNode, LeftDataSet, "Left", newFeatureSet, level);
            }

            countCheckZero = 0;
            countCheckOne = 0;
            for (int s = 0; s < RightDataSet.length; s++) {
                if (RightDataSet[s][RightDataSet[0].length - 1].equals("0")) {
                    countCheckZero++;
                } else {
                    countCheckOne++;
                }
            }
            if (((countCheckZero == (RightDataSet.length)) && (RightDataSet.length) > 0) || ((countCheckZero >= countCheckOne) && newFeatureSet.length <= 2) || ((countCheckZero >= countCheckOne) && RightDataSet.length <= 1)) {
                root.setRightLeaf(0);
            } else if (((countCheckOne == (RightDataSet.length)) && (RightDataSet.length) > 0) || ((countCheckOne >= countCheckZero) && newFeatureSet.length <= 2) || ((countCheckOne >= countCheckZero) && RightDataSet.length <= 1)) {
                root.setRightLeaf(1);
            } else {
                Node rightNode = new Node();
                root.setRight(rightNode);
                rightNode.setLevel(level);
                executeHeuristicTwo(rightNode, RightDataSet, "Right", newFeatureSet, level);
            }

        } else {
            if (featureSet.length > 1) {
                for (int s = 0; s < dataSet.length; s++) {
                    if (dataSet[s][dataSet[0].length - 1].equals("0")) {
                        countCheckZero++;
                    } else {
                        countCheckOne++;
                    }
                }
                if (countCheckZero > countCheckOne) {
                    root.setLeftLeaf(0);
                    root.setRightLeaf(0);
                } else {
                    root.setLeftLeaf(1);
                    root.setRightLeaf(1);
                }

            }
        }
    }
}

class GainCalculation {

    // Calulating complete gain for heuristic one
    public int completeGainCalucation(Object dataSet[][]) {
        double max = 0.0, a = 0, b = 0, c = 0;
        int choosenFeature = 0;
        a = calculateEntropyInitial(dataSet);
        for (int i = 0; i < dataSet[0].length - 1; i++) {
            b = calculateEntropy(dataSet, i, "0");
            c = calculateEntropy(dataSet, i, "1");

            double gain = a - b - c;
            if (max < gain) {
                max = gain;
                choosenFeature = i;
            }
        }
        if (max == -1000) {

            System.out.println("System Error " + a + " " + b + " " + c);
        }
        return choosenFeature;

    }
    // Calulating entropy of root (Information Gain)

    public double calculateEntropyInitial(Object dataSet[][]) {
        double countOnes = 0;
        double countZeros = 0;
        double count = 0;
        for (int i = 0; i < dataSet.length; i++) {
            if ((dataSet[i][dataSet[i].length - 1]).equals("1")) {
                countOnes++;
            } else {
                countZeros++;
            }
            count++;
        }
        double entropy1, entropy2, entropy;
        entropy1 = ((countOnes / (double) dataSet.length) * ((Math.log10((countOnes / (double) dataSet.length))) / Math.log10(2)));
        entropy2 = ((countZeros / (double) dataSet.length) * ((Math.log10((countZeros / (double) dataSet.length))) / Math.log10(2)));
        entropy = -entropy1 - entropy2;
        return entropy;
    }
    // Calulating inner node entropy (Information Gain)

    public double calculateEntropy(Object dataSet[][], int feature, String value) {
        double countOnes = 0;
        double countZeros = 0;
        double count = 0;
        double setCount = 0;
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i][feature].equals(value)) {
                if ((dataSet[i][dataSet[i].length - 1]).equals("1")) {
                    countOnes++;
                } else {
                    countZeros++;
                }
                count++;
            }
            setCount++;
        }

        double checkOne = (double) countOnes / count;
        double checkZero = (double) countZeros / count;
        double entropy1 = 0, entropy2 = 0, entropy = 0;
        if (checkOne > 0 && checkZero > 0) {
            entropy1 = (checkOne * ((Math.log10(checkOne)) / Math.log10(2)));
            entropy2 = (checkZero * ((Math.log10(checkZero)) / Math.log10(2)));
            entropy = -entropy1 - entropy2;
        }
        double inter = ((double) count / setCount) * entropy;
        return inter;
    }
    // Calulating complete gain for heuristic one

    public int completeGainCalucationHeuristicTwo(Object dataSet[][]) {
        double max = 0.0, a = 0, b = 0, c = 0;
        int choosenFeature = 0;
        a = calculateEntropyInitialHeuristicTwo(dataSet);
        for (int i = 0; i < dataSet[0].length - 1; i++) {
            b = calculateEntropyHeuristicTwo(dataSet, i, "0");
            c = calculateEntropyHeuristicTwo(dataSet, i, "1");

            double gain = a - b - c;
            if (max < gain) {
                max = gain;
                choosenFeature = i;
            }
        }
        if (max == -1000) {

            System.out.println("System Error " + a + " " + b + " " + c);
        }
        return choosenFeature;

    }
    // Calulating entropy of root (Variance Impurity)

    public double calculateEntropyInitialHeuristicTwo(Object dataSet[][]) {
        double countOnes = 0;
        double countZeros = 0;
        double count = 0;
        for (int i = 0; i < dataSet.length; i++) {
            if ((dataSet[i][dataSet[i].length - 1]).equals("1")) {
                countOnes++;
            } else {
                countZeros++;
            }
            count++;
        }
        double entropy1, entropy2, entropy;
        entropy1 = ((countOnes / (double) dataSet.length));
        entropy2 = ((countZeros / (double) dataSet.length));
        entropy = entropy1 * entropy2;
        return entropy;
    }
    // Calulating inner node entropy (Variance Impurity)

    public double calculateEntropyHeuristicTwo(Object dataSet[][], int feature, String value) {
        double countOnes = 0;
        double countZeros = 0;
        double count = 0;
        double setCount = 0;
        for (int i = 0; i < dataSet.length; i++) {
            if (dataSet[i][feature].equals(value)) {
                if ((dataSet[i][dataSet[i].length - 1]).equals("1")) {
                    countOnes++;
                } else {
                    countZeros++;
                }
                count++;
            }
            setCount++;
        }
        double checkOne = (double) countOnes / count;
        double checkZero = (double) countZeros / count;
        double entropy1 = 0, entropy2 = 0, entropy = 0;
        entropy1 = (checkOne);
        entropy2 = (checkZero);
        entropy = entropy1 * entropy2;
        double inter = ((double) count / setCount) * entropy;
        return inter;
    }
}

class TextIo {

    Object dataSet[][];
    Object featureSet[];
    Object testSet[][];
    Object validationSet[][];
    int i = 0, j;

    public Object[] readFeatures(String training) throws Exception {
        try {
            BufferedReader r = new BufferedReader(new FileReader(training));
            String line = r.readLine();
            if (line != null) {
                Object rowData[] = line.split(",");
                j = rowData.length;
            }
            featureSet = new Object[j];
            r = new BufferedReader(new FileReader(training));
            line = r.readLine();
            Object rowData1[] = line.split(",");
            for (int l = 0; l < j; l++) {
                featureSet[l] = rowData1[l];
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return (featureSet);
    }

    public Object[][] readTestSet(String test) throws Exception {
        i = 0;
        try {
            BufferedReader r = new BufferedReader(new FileReader(test));
            String line = r.readLine();
            if (line != null) {
                do {
                    Object rowData[] = line.split(",");
                    j = rowData.length;
                    i++;
                    line = r.readLine();
                } while (line != null);
            }
            testSet = new Object[i - 1][j];
            r = new BufferedReader(new FileReader(test));
            line = r.readLine();
            line = r.readLine();
            int k = 0;
            if (line != null) {
                do {
                    Object rowData[] = line.split(",");
                    for (int l = 0; l < j; l++) {
                        testSet[k][l] = rowData[l];
                    }
                    k++;
                    line = r.readLine();
                } while (line != null);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return (testSet);
    }

    public Object[][] readValidationSet(String validation) throws Exception {
        i = 0;
        try {
            BufferedReader r = new BufferedReader(new FileReader(validation));
            String line = r.readLine();
            if (line != null) {
                do {
                    Object rowData[] = line.split(",");
                    j = rowData.length;
                    i++;
                    line = r.readLine();
                } while (line != null);
            }
            validationSet = new Object[i - 1][j];
            r = new BufferedReader(new FileReader(validation));
            line = r.readLine();
            line = r.readLine();
            int k = 0;
            if (line != null) {
                do {
                    Object rowData[] = line.split(",");
                    for (int l = 0; l < j; l++) {
                        validationSet[k][l] = rowData[l];
                    }
                    k++;
                    line = r.readLine();
                } while (line != null);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return (validationSet);
    }

    public Object[][] readDataSet(String training) throws Exception {
        i = 0;
        try {
            BufferedReader r = new BufferedReader(new FileReader(training));
            String line = r.readLine();
            if (line != null) {
                do {
                    Object rowData[] = line.split(",");
                    j = rowData.length;
                    i++;
                    line = r.readLine();
                } while (line != null);
            }
            dataSet = new Object[i - 1][j];
            r = new BufferedReader(new FileReader(training));
            line = r.readLine();
            line = r.readLine();
            int k = 0;
            if (line != null) {
                do {
                    Object rowData[] = line.split(",");
                    for (int l = 0; l < j; l++) {
                        dataSet[k][l] = rowData[l];
                    }
                    k++;
                    line = r.readLine();
                } while (line != null);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
        return (dataSet);
    }
}

class Node {

    private String data;
    private Node left;
    private Node right;
    private int level;
    private Integer leftleaf;
    private Integer rightleaf;
    private Integer Zeros;
    private Integer Ones;
    int countLeaf = 0;

    public Node() {
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setLeftLeaf(int leaf) {
        this.leftleaf = leaf;
    }

    public void setRightLeaf(int leaf) {
        this.rightleaf = leaf;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setZero(Integer zero) {
        this.Zeros = zero;
    }

    public void setOne(Integer one) {
        this.Ones = one;
    }

    public Node getLeft() {
        return this.left;
    }

    public Node getRight() {
        return this.right;
    }

    public Integer getLeftLeaf() {
        return this.leftleaf;
    }

    public Integer getRightLeaf() {
        return this.rightleaf;
    }

    public Integer getOne() {
        return this.Ones;
    }

    public Integer getZero() {
        return this.Zeros;
    }

    public String getData() {
        return this.data;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public void printTree(Node root, String side) {
        int flag = 0;
        if (root == null) {
            return;
        }
        int level = root.getLevel();
        String indent = "";
        for (int i = 0; i < level; i++) {
            indent = indent + "|";
        }
        String leafleftvalue = "", leafrightvalue = "";
        if (root.getLeftLeaf() != null) {
            leafleftvalue = " : " + root.getLeftLeaf();
            System.out.println(indent + root.getData() + " = 0" + leafleftvalue);
            flag = 1;
        }
        if (flag != 1) {
            System.out.println(indent + root.getData() + " = 0 :");
        }

        printTree(root.getLeft(), "Left");
        flag = 0;
        level = root.getLevel();
        indent = "";
        for (int i = 0; i < level; i++) {
            indent = indent + "|";
        }
        if (root.getRightLeaf() != null) {
            leafrightvalue = " : " + root.getRightLeaf();
            System.out.println(indent + root.getData() + " = 1" + leafrightvalue);
            flag = 1;
        }
        if (flag != 1) {
            System.out.println(indent + root.getData() + " = 1 : ");
        }
        printTree(root.getRight(), "Right");

    }

    public void NonLeafCount(Node root) {
        if (root == null) {
            return;
        }
        countLeaf++;
        if (root.getLeft() != null) {
            NonLeafCount(root.getLeft());
        }
        if (root.getRight() != null) {
            NonLeafCount(root.getRight());
        }
    }

    public int displayNonLeafCount() {
        return countLeaf;
    }
}

class TestSet {

    int accCount = 0;

    public void accuracy(int acc) {
        accCount = accCount + acc;
    }

    public double checkAccuracy(int len) {
        double accPercent = (double) accCount / len;
        accPercent = accPercent * 100;
        return accPercent;
    }

    public void testSet(Object testSet[][], Object features[], Node root) {
        Object eachRow[] = new Object[testSet[0].length];
        for (int k = 0; k < testSet.length; k++) {
            for (int l = 0; l < testSet[k].length; l++) {
                eachRow[l] = testSet[k][l];
            }
            testingEachRow(eachRow, features, root);
        }
    }

    public void testingEachRow(Object eachRow[], Object features[], Node root) {
        int index = 0;
        if (root != null) {
            String data = root.getData();
            for (int i = 0; i < features.length - 1; i++) {
                if (features[i].equals(data)) {
                    index = i;
                }
            }
            String value = eachRow[index].toString();
            if (value.equals("0")) {
                String check = "";
                if (root.getLeftLeaf() != null) {
                    check = root.getLeftLeaf().toString();
                    if (check.equals(eachRow[(eachRow.length) - 1])) {
                        accuracy(1);
                    } else {
                        accuracy(0);
                    }
                } else {
                    testingEachRow(eachRow, features, root.getLeft());
                }
            } else if (value.equals("1")) {
                String check = "";
                if (root.getRightLeaf() != null) {
                    check = root.getRightLeaf().toString();
                    if (check.equals(eachRow[eachRow.length - 1])) {
                        accuracy(1);
                    } else {
                        accuracy(0);
                    }
                } else {
                    testingEachRow(eachRow, features, root.getRight());
                }
            } else {
                System.out.println("Failure");
            }
        }
    }
}

class Cloning {

    public Node clonning(Node root1) {
        if (root1 == null) {
            return null;
        }
        Node root2 = new Node();
        root2.setData(root1.getData());
        root2.setLevel(root1.getLevel());
        root2.setZero(root1.getZero());
        root2.setOne(root1.getOne());
        if (root1.getLeftLeaf() != null) {
            root2.setLeftLeaf(root1.getLeftLeaf());
        } else {
            root2.setLeft(clonning(root1.getLeft()));
        }
        if (root1.getRightLeaf() != null) {
            root2.setRightLeaf(root1.getRightLeaf());
        } else {
            root2.setRight(clonning(root1.getRight()));
        }
        return root2;
    }
}

class Prunning {

    public void prun(Node dBest, Node d, int l, int k, Object validationSet[][], Object featureSet[], Object testSet[][], String print) {

        TestSet test = new TestSet();
        test.accCount = 0;
        test.testSet(testSet, featureSet, dBest);
        double accurancyBefore = test.checkAccuracy(testSet.length);
        System.out.println("Accuracy Before Prunning " + accurancyBefore);

        for (int i = 1; i <= l; i++) {
            Node dDash = new Node();
            Cloning c = new Cloning();
            dDash = c.clonning(d);
            int range = (k - 1) + 1;
            int m = (int) (Math.random() * range) + 1;
            for (int j = 1; j <= m; j++) {
                dDash.countLeaf = 0;
                dDash.NonLeafCount(dDash);
                int n = dDash.displayNonLeafCount();
                range = (n - 1) + 1;
                int p = (int) (Math.random() * range) + 1;
                trimTree(dDash, p);
            }
            test.accCount = 0;
            test.testSet(validationSet, featureSet, dBest);
            double accurancyDBest = test.checkAccuracy(validationSet.length);
            test.accCount = 0;
            test.testSet(validationSet, featureSet, dDash);
            double accurancyDDash = test.checkAccuracy(validationSet.length);
            if (accurancyDDash > accurancyDBest) {
                Node dDashTemp = new Node();
                dDashTemp = c.clonning(dDash);
                dBest = dDashTemp;
            }
        }
        test.accCount = 0;
        test.testSet(testSet, featureSet, dBest);
        double accurancyPrunning = test.checkAccuracy(testSet.length);
        if (print.equalsIgnoreCase("Yes")) {
            System.out.println("Decision tree formed after pruning :");
            dBest.printTree(dBest, "");
        }
        System.out.println("Accuracy After Prunning " + accurancyPrunning);

    }

    public void trimTree(Node root, int p) {
        if (root == null) {
            return;
        }
        p--;
        if (p == 1) {
            int countZero = root.getZero();
            int countOne = root.getOne();
            root.setLeft(null);
            root.setRight(null);
            if (countZero >= countOne) {
                root.setLeftLeaf(0);
                root.setRightLeaf(0);
            } else {
                root.setLeftLeaf(1);
                root.setRightLeaf(1);
            }
            return;
        } else {
            if (root.getLeft() != null) {
                trimTree(root.getLeft(), p);
            }
            if (root.getRight() != null) {
                trimTree(root.getRight(), p);
            }
        }
    }
}