package com.jc.research.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @program: composite-indicators
 * @description:
 * @author: SunChao
 * @create: 2021-08-11 14:09
 **/
public class DataCleaning {
    public static void main(String[] args) throws IOException {
        new DataCleaning().getPyCsv();
    }

    public void getPyCsv() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("F:/Java/workSpace/composite-indicator-construct/db/raw_data.txt")));
        int count = 0;
        String numberStr;
        while ((numberStr = reader.readLine()) != null) {
            count++;
            System.out.print(numberStr);
            if (count % 8 == 0) {
                System.out.println();
            } else {
                System.out.print(",");
            }
        }
    }
    public void getPyArr() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream("F:/Java/workSpace/composite-indicator-construct/db/raw_data.txt")));
        int count = 0;
        String numberStr;
        while ((numberStr = reader.readLine()) != null) {
            count++;
            System.out.print(numberStr);
            if (count % 8 == 0) {
                System.out.print("],");
                System.out.println();
                System.out.print("[");
            } else {
                System.out.print(", ");
            }
        }
    }
}
