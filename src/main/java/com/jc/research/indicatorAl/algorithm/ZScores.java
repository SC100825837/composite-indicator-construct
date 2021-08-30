package com.jc.research.indicatorAl.algorithm;

import com.jc.research.util.AlgorithmUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @program: constructing-composite-indicators
 * @description: z-scores
 * @author: SunChao
 * @create: 2021-08-17 16:03
 **/
@Data
@Getter
@Setter
public class ZScores extends Algorithm {

    private int execOrder = 1;

    private String stepName = "normalisation";

    @Override
    public double[][] exec() {
//        String doublesStr = Arrays.deepToString(matrix);

        String[] pyArgs = {"python", "F:/Python/workspace/py_run_on_java/ZScores.py"};
        String calcResultLine = "";
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(pyArgs);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                while ((calcResultLine = reader.readLine()) != null) {
                    if (calcResultLine == null) {
                        continue;
                    }
                    result = calcResultLine;
                    System.out.println("ZScores: " + result);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return AlgorithmUtil.toDoubleArray(result);
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
