package com.jc.research.indicatorAl.algorithm;

import com.jc.research.indicatorAl.algorithmAnnotation.ContainProcessResult;
import com.jc.research.indicatorAl.entity.AlgorithmProcessResult.FactorAnalysisPR;
import com.jc.research.indicatorAl.entity.AlgorithmProcessResult.ProcessResult;
import com.jc.research.util.AlgorithmUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @program: constructing-composite-indicators
 * @description: 基于因子分析的权重聚合算法
 * @author: SunChao
 * @create: 2021-08-17 16:04
 **/
@Data
@Getter
@Setter
@ContainProcessResult
public class FactorAnalysis extends Algorithm {

    private int execOrder = 2;

    private String stepName = "weightingAndAggregation";

    @Transient
    private String fullClassName = "";

    @Override
    public ProcessResult exec() {
//        String doublesStr = Arrays.deepToString(matrix);

        String[] pyArgs = {"F:/Python/workspace/py_run_on_java/venv/Scripts/python", "F:/Python/workspace/py_run_on_java/FactorAnalysis.py"};
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
        //设置旋转因子载荷矩阵
        factorAnalysisPR.setRotatedFactorLoadingsMatrix(AlgorithmUtil.toDoubleArray(resultStrArr[0]));
        //设置特征值，累积方差等
//        System.out.println(resultStrArr[1]);
        factorAnalysisPR.setEigenvalues(AlgorithmUtil.toDoubleArray(resultStrArr[1]));
        //设置权重
        double[][] weight = AlgorithmUtil.toDoubleArray(resultStrArr[2]);
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
        String[] split = originStr.replaceAll("\\s+", "").split("]],");
        split[0] = split[0].substring(1) + "]]";
        split[1] += "]]";
        split[2] = "[" + split[2];
        return split;
    }

    @Override
    public int getExecOrder() {
        return this.execOrder;
    }

    @Override
    public String getStepName() {
        return stepName;
    }
}
