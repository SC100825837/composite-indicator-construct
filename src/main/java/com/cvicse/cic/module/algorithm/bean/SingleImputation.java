package com.cvicse.cic.module.algorithm.bean;

import com.cvicse.cic.util.AlgorithmConstants;
import lombok.Data;
import static com.cvicse.cic.util.AlgorithmUtil.*;

@Data
public class SingleImputation extends Algorithm {

    private int execOrder = AlgorithmConstants.MISS_DATA_IMPUTATION_ORDER;

    private String stepName = "missDataImputation";

    @Override
    public Double[][] exec(Double[][] matrix) {
        //先按列遍历
        for (int col = 0; col < matrix[0].length; col++) {
            //定义列的和
            Double sum = (double) 0;
            //定义标识符，来判断是否需要进行插补操作
            boolean ifImputation = false;
            //再按行遍历，进行加和操作
            for (int row = 0; row < matrix.length; row++) {
                //不为空的话就加和,空就跳过
                if (matrix[row][col] == null) {
                    ifImputation = true;
                    continue;
                }
                sum += matrix[row][col];
            }
            if (!ifImputation) {
                continue;
            }
            //如果中间有空值，就说明需要进行数据插补
            Double average = handleFractional(2, getAverage(sum, matrix.length));
            for (int row = 0; row < matrix.length; row++) {
                if (matrix[row][col] == null) {
                    matrix[row][col] = average;
                }
            }
        }
        return matrix;
    }

}
