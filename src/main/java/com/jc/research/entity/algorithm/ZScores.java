package com.jc.research.entity.algorithm;

import com.jc.research.util.AlgorithmExecOrder;
import lombok.Data;
import static com.jc.research.util.AlgorithmUtil.*;

/**
 * @program: constructing-composite-indicators
 * @description: z-scores
 * @author: SunChao
 * @create: 2021-08-17 16:03
 **/
@Data
public class ZScores extends Algorithm {

    private int execOrder = AlgorithmExecOrder.NORMALISATION;

    private String stepName = "normalisation";

    @Override
    public double[][] exec(double[][] matrix) {
        double[][] transposeMatrix = transposeMatrix(matrix);
        double[][] normalizationMatrix = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < transposeMatrix.length; i++) {
            //计算平均值
            double average = getAverage(getSum(transposeMatrix[i]), transposeMatrix[i].length);
            //计算标准差
            double standardDeviation = getStandardDeviation(getVariance(transposeMatrix[i], average, transposeMatrix[i].length));
            for (int j = 0; j < transposeMatrix[i].length; j++) {
                double standardisation = (transposeMatrix[i][j] - average) / standardDeviation;
                normalizationMatrix[i][j] = standardisation;
            }
        }
        return transposeMatrix(normalizationMatrix);
    }

    @Override
    public int getExecOrder() {
        return execOrder;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

}
