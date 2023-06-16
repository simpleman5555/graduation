package com.erettsegi;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.core.Instances;
import weka.core.Range;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GépiTanulás2CrossValidation {

    static double sumOfCorrectlyClassifiedInstances;
    static String algoritmusNév;
    GépiTanulás2CrossValidation(String fájlNév, int classIndex, Classifier classifier, PrintWriter kiir, String algoritmusNév) throws FileNotFoundException {
        try {
            this.algoritmusNév = algoritmusNév;
            PlainText plainText = new PlainText();
            StringBuffer stringBuffer = new StringBuffer();
            plainText.setBuffer(stringBuffer);
            kiir.println("\nclassifier.getClass():" + classifier.getClass());
            BufferedReader bufferedReader = new BufferedReader(new FileReader(fájlNév));
            Instances instances = new Instances(bufferedReader);
            instances.setClassIndex(classIndex);
            if(false) instances.randomize(new Random());
            Evaluation evaluation = new Evaluation(instances);
            evaluation.crossValidateModel( classifier, instances, 10, new Random(1), plainText, new Range("first,last"), false);
            List<Double> eredmeny = new ArrayList<Double>();
            String[] lines = stringBuffer.toString().split("\n");
            int index = 0;
            for(String line : lines) {
                //System.out.println(line); --- ezt kiíratva láthatjuk többek között a kiértékelő halmaz minden egyedére a valós és predikált értékeket
                if(index > 0) {
                    String[] data = line.split("[: +]");
                    // A data[9] és data[11] tömbelemek vagy számokat (1 vagy 2), vagy szavakat (democrat vagy republican) tartalmaznak, amelyek
                    // valójában a valós és megjósolt értékeknek feleltethetők meg - a democrat 1-re, a republican 2-re képezhető -,
                    // még pontosabban a data[9] a valós, a data[11] a jósolt eredmény. És ezen értékek összehasonlításából (8 eset lehetséges) megállapítható,
                    // hogy az algoritmus "jóslása" helyes volt-e vagy nem, így adva hozzá az eredmeny nevű tömbhöz 1.0 -t, ha helyes volt, 0.0 -t, ha nem:
                    if(data[9].startsWith("1") && data[11].startsWith("1")) eredmeny.add(1.0); // 1. eset
                    if(data[9].startsWith("2") && data[11].startsWith("2")) eredmeny.add(1.0); // 2. eset
                    if(data[9].startsWith("d") && data[11].startsWith("d")) eredmeny.add(1.0); // 3. eset
                    if(data[9].startsWith("r") && data[11].startsWith("r")) eredmeny.add(1.0); // 4. eset
                    if((data[9].startsWith("1") && data[11].startsWith("2")) ||
                            data[9].startsWith("2") && data[11].startsWith("1")) eredmeny.add(0.0); // 5. és 6. eset
                    if((data[9].startsWith("d") && data[11].startsWith("r")) ||
                            data[9].startsWith("r") && data[11].startsWith("d")) eredmeny.add(0.0); // 7. és 8. eset
                }
                index++;
            }

            sumOfCorrectlyClassifiedInstances = 100*evaluation.correct()/instances.size();
            kiir.println("\nCorrectly Classified Instances:"+(int)evaluation.correct()+"\t"+100*evaluation.correct()/instances.size()+"%");
            kiir.println("\nIncorrectly Classified Instances:"+(instances.size()-(int)evaluation.correct())+"\t"+100*(instances.size()-evaluation.correct())/instances.size()+"%");

            int TP=0, TN=0, FP=0, FN=0;
            //  TP:TP, TN:trueNegative, FP:falsePositive, FN:falseNegative
            for(int i=0;i<instances.size();i++){
                if((((Instances)instances).get(i)).classValue()==1 && eredmeny.get(i)==1)
                    TP++;
                if((((Instances)instances).get(i)).classValue()==1 && eredmeny.get(i)==0)
                    FN++;
                if((((Instances)instances).get(i)).classValue()==0 && eredmeny.get(i)==1)
                    FP++;
                if((((Instances)instances).get(i)).classValue()==0 && eredmeny.get(i)==0)
                    TN++;
            }
            kiir.println("\nTP="+TP+", "+"TN="+TN+", "+"FP="+FP+", "+"FN="+FN);
            kiir.println("--------------------------------------------");
        }
        catch (Exception e) {
            System.out.println("Error Occurred!!!! \n" + e.getMessage());
        }
    }

    public static String[] getImportantData() {
        String[] arr = new String[2];
        arr[0] = sumOfCorrectlyClassifiedInstances + "";
        arr[1] = algoritmusNév;
        return arr;
    };
}
