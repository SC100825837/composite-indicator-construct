package com.cvicse.cic.module.algorithm.bean;

import com.cvicse.cic.util.AlgorithmConstants;
import com.cvicse.cic.util.AlgorithmUtil;
import lombok.Data;

/**
 * @program: composite-indicator-construct
 * @description: z-scores
 * @author: SunChao
 * @create: 2021-08-17 16:03
 **/
@Data
public class ZScores extends Algorithm {

    private int execOrder = AlgorithmConstants.NORMALISATION_ORDER;

    private String stepName = "normalisation";

    @Override
    public Double[][] exec(Double[][] matrix) {
        Double[][] transposeMatrix = AlgorithmUtil.transposeMatrix(matrix);
        Double[][] normalizationMatrix = new Double[matrix[0].length][matrix.length];
        for (int i = 0; i < transposeMatrix.length; i++) {
            //计算平均值
            Double average = AlgorithmUtil.getAverage(AlgorithmUtil.getSum(transposeMatrix[i]), transposeMatrix[i].length);
            //计算标准差
            Double standardDeviation = AlgorithmUtil.getStandardDeviation(AlgorithmUtil.getVariance(transposeMatrix[i], average, transposeMatrix[i].length));
            for (int j = 0; j < transposeMatrix[i].length; j++) {
                Double standardisation = (transposeMatrix[i][j] - average) / standardDeviation;
                normalizationMatrix[i][j] = AlgorithmUtil.handleFractional(3, standardisation);
            }
        }
        return AlgorithmUtil.transposeMatrix(normalizationMatrix);
    }

}
