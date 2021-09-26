package com.jc.research.entity.algorithm;

import com.jc.research.util.AlgorithmConstants;
import com.jc.research.util.ContainProcessResult;
import com.jc.research.entity.algorithm.result.FactorAnalysisPR;
import com.jc.research.entity.algorithm.result.ProcessResult;
import com.jc.research.util.AlgorithmUtil;
import lombok.Data;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @program: composite-indicator-construct
 * @description: 基于因子分析的权重聚合算法
 * @author: SunChao
 * @create: 2021-08-17 16:04
 **/
@Data
@ContainProcessResult
public class FactorAnalysis extends Algorithm {

    private int execOrder = AlgorithmConstants.WEIGHTING_AND_AGGREGATION_ORDER;

    private String stepName = "weightingAndAggregation";

    private String fullClassName = "";

    /*private Map<String, Double> weightMap;

    public FactorAnalysis(Map<String, Double> indicatorMap) {
        this.weightMap = indicatorMap;
    }*/

    @Override
    public ProcessResult exec(Double[][] matrix) {
        String doublesStr = Arrays.deepToString(matrix);
        String columnCounts = String.valueOf(matrix[0].length);

        String[] pyArgs = {AlgorithmUtil.getPythonEnvPath(), AlgorithmUtil.getPythonAlgorithmPath() + "FactorAnalysis.py", doublesStr, columnCounts};
        String calcResultLine = "";
        StringBuilder copyResultStr = new StringBuilder();
        FactorAnalysisPR factorAnalysisPR = new FactorAnalysisPR();
        try {
            Process process = Runtime.getRuntime().exec(pyArgs);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
            while ((calcResultLine = reader.readLine()) != null) {
                if (calcResultLine == null || calcResultLine.equals("")) {
                    continue;
                }
//                System.out.println("FactorAnalysis: " + calcResultLine);
                copyResultStr.append(String.copyValueOf(calcResultLine.toCharArray()));
            }
            process.waitFor();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] resultStrArr = normalizeStrAndToArr(copyResultStr.toString());
        if (resultStrArr == null) {
            return factorAnalysisPR;
        }
        //设置旋转因子载荷矩阵
        factorAnalysisPR.setRotatedFactorLoadingsMatrix(AlgorithmUtil.toDoubleArray(resultStrArr[0]));
        //设置平方因子负荷矩阵
        //先取出旋转因子负载矩阵
        Double[][] rotatedFactorLoadingsMatrix = factorAnalysisPR.getRotatedFactorLoadingsMatrix();
        //创建平方因子负荷矩阵
        Double[][] squaredFactorLoadingMatrix = new Double[rotatedFactorLoadingsMatrix.length][rotatedFactorLoadingsMatrix[0].length];
        //缩放
        for (int i = 0; i < rotatedFactorLoadingsMatrix.length; i++) {
            Double oneFactorSum = (double) 0;
            for (int j = 0; j < rotatedFactorLoadingsMatrix[i].length; j++) {
                oneFactorSum += rotatedFactorLoadingsMatrix[i][j];
            }
            for (int j = 0; j < rotatedFactorLoadingsMatrix[i].length; j++) {
                squaredFactorLoadingMatrix[i][j] = rotatedFactorLoadingsMatrix[i][j] / oneFactorSum;
            }
        }
        //设置平方因子负荷矩阵
        factorAnalysisPR.setSquaredFactorLoadingMatrix(squaredFactorLoadingMatrix);

        //设置特征值，累积方差等
//        System.out.println(resultStrArr[1]);
        factorAnalysisPR.setEigenvalues(AlgorithmUtil.toDoubleArray(resultStrArr[1]));
        //设置权重
        Double[][] weight = AlgorithmUtil.toDoubleArray(resultStrArr[2]);
        factorAnalysisPR.setIndicatorWeight(weight);
        //设置最终结果
        factorAnalysisPR.setFinalResult(weight);
        return factorAnalysisPR;
    }

    /**
     * 将返回来的结果字符串去除空格，并将每个结果切分开，去除多余字符，并以字符串数组的形式返回
     * @param originStr
     * @return
     */
    private String[] normalizeStrAndToArr(String originStr) {
        if ("".equals(originStr)) {
            return null;
        }
        String[] split = originStr.replaceAll("\\s+", "").split("]],");
        split[0] = split[0].substring(1) + "]]";
        split[1] += "]]";
        split[2] = "[" + split[2];
        return split;
    }

}
