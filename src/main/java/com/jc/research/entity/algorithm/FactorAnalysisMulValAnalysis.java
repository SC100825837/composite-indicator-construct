package com.jc.research.entity.algorithm;

import com.jc.research.entity.algorithm.result.FAMulValAnalysisPR;
import com.jc.research.entity.algorithm.result.ProcessResult;
import com.jc.research.util.AlgorithmConstants;
import com.jc.research.util.AlgorithmUtil;
import com.jc.research.util.ContainProcessResult;
import lombok.Data;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * @program: composite-indicator-construct
 * @description: 基于因子分析的权重聚合算法
 * @author: SunChao
 * @create: 2021-08-17 16:04
 **/
@Data
@ContainProcessResult
public class FactorAnalysisMulValAnalysis extends Algorithm {

    private int execOrder = AlgorithmConstants.MULTI_VARIATE_ANALYSIS_ORDER;

    private String stepName = "multivariateAnalysis";

    private String fullClassName = "";

    @Override
    public ProcessResult exec(Double[][] matrix) {
        String doublesStr = Arrays.deepToString(matrix);
        String columnCounts = String.valueOf(matrix[0].length);
        String[] pyArgs = {AlgorithmUtil.getPythonEnvPath(), AlgorithmUtil.getPythonAlgorithmPath() + "FactorAnalysisMulValAnalysis.py", doublesStr, columnCounts};
        String calcResultLine = "";
        StringBuilder copyResultStr = new StringBuilder();
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
//        System.out.println(copyResultStr.toString());
        String[] resultStrArr = normalizeStrAndToArr(copyResultStr.toString());
        //拿到相关性矩阵和旋转因子负荷矩阵的二维数组
        List<Double[][]> doubleArray = AlgorithmUtil.toDoubleArray(resultStrArr);

        FAMulValAnalysisPR faMulValAnalysisPR = new FAMulValAnalysisPR();
        if (doubleArray == null || doubleArray.isEmpty()) {
            return faMulValAnalysisPR;
        }
        faMulValAnalysisPR.setCorrelationMatrix(doubleArray.get(0));
        faMulValAnalysisPR.setRotatedFactorLoadingsMatrix(doubleArray.get(1));
        faMulValAnalysisPR.setFinalResult(matrix);
        return faMulValAnalysisPR;
    }

    /**
     * 将返回来的结果字符串去除空格，并将每个结果切分开，去除多余字符，并以字符串数组的形式返回
     * @param originStr
     * @return
     */
    private String[] normalizeStrAndToArr(String originStr) {
        String[] split = originStr.replaceAll("\\s+", "").split("]\\[");
        split[0] += "]";
        split[1] = "[" + split[1];
        return split;
    }
}
