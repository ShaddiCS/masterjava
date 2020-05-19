package ru.javaops.masterjava.matrix;

import java.util.Random;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    // TODO implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws InterruptedException, ExecutionException {
        int size = matrixA.length;
        CompletionService<Void> service = new ExecutorCompletionService<>(executor);
        CountDownLatch latch = new CountDownLatch(size);

        int[][] matrixC = new int[size][size];

        int[][] transposeB = new int[size][size];

        for (int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                transposeB[i][j] = matrixB[j][i];
            }
        }

        for (int i = 0; i < size; i++) {
            int I = i;
            service.submit(() -> {
                calcRow(I, matrixA, transposeB, matrixC);
                latch.countDown();
            }, null);
        }
        latch.await();
        return matrixC;
    }

     static void calcRow(int row, int[][] matrixA, int[][] matrixB, int[][] matrixC) {
        int size = matrixA.length;
        for(int i = 0; i < size; i++) {
            int sum = 0;
            int[] rowA = matrixA[row];
            int[] colB = matrixB[i];
            for(int j = 0; j < size; j++) {
                sum += rowA[j] * colB[j];
            }
            matrixC[row][i] = sum;
        }
    }

    // TODO optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final int[] transposeColumn = new int[matrixSize];

        for (int i = 0; i < matrixSize; i++) {
            for(int k = 0; k < matrixSize; k++) {
                transposeColumn[k] = matrixB[k][i];
            }

            for (int j = 0; j < matrixSize; j++) {
                int sum = 0;
                for (int k = 0; k < matrixSize; k++) {
                    sum += matrixA[j][k] * transposeColumn[k];
                }
                matrixC[j][i] = sum;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
