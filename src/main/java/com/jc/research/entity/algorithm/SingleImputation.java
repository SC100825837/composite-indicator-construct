package com.jc.research.entity.algorithm;

import com.jc.research.util.AlgorithmConstants;
import lombok.Data;
import static com.jc.research.util.AlgorithmUtil.*;

@Data
public class SingleImputation extends Algorithm {

    private int execOrder = AlgorithmConstants.MISS_DATA_IMPUTATION_ORDER;

    private String stepName = "missDataImputation";

    @Override
    public Double[][] exec(Double[][] matrix) {
        for (int col = 0; col < matrix[0].length; col++) {
            Double sum = (double) 0;
            for (int row = 0; row < matrix.length; row++) {
                if (matrix[row][col] == null) {
                    sum += matrix[row][col];
                }
            }
            Double average = getAverage(sum, matrix.length);
            for (int row = 0; row < matrix.length; row++) {
                if (matrix[row][col] == null) {
                    matrix[row][col] = average;
                }
            }
        }
        return matrix;
    }

}
