package exercise3.impl;

import java.util.ArrayList;
import java.util.List;

public class Encoding {
    /**
     * Compresses the passed values using Differential Encoding.
     */
    public static int[] encodeDiff(int[] numbers) {
        int[] encodedNumbers = new int[numbers.length];
        encodedNumbers[0] = numbers[0];

        for (int i = 1; i < numbers.length; i++) {
            encodedNumbers[i] = numbers[i] - numbers[i-1];
        }

        return encodedNumbers;
    }

    /**
     * Decompresses values previously compressed via Differential Encoding.
     */
    public static int[] decodeDiff(int[] numbers) {
        int[] decodedNumbers = new int[numbers.length];
        decodedNumbers[0] = numbers[0];

        for (int i = 1; i < numbers.length; i++) {
            decodedNumbers[i] = numbers[i] + decodedNumbers[i-1];
        }

        return decodedNumbers;
    }

    /**
     * Compresses the passed values using Variable Byte Encoding.
     */
    public static byte[] encodeVB(int[] numbers) {
        List<Byte> byteStream = new ArrayList<>();

        for (int number : numbers) {
            List<Byte> bytes = new ArrayList<>();
            while (true) {
                bytes.addFirst((byte) (number % 128));
                if (number < 128) {
                    break;
                }
                number = number / 128;
            }
            bytes.set(bytes.size() - 1, (byte) (bytes.getLast() + 128));
            byteStream.addAll(bytes);
        }

        byte[] result = new byte[byteStream.size()];
        for (int i = 0; i < byteStream.size(); i++) {
            result[i] = byteStream.get(i);
        }

        return result;
    }

    /**
     * Decompresses values previously compressed via Variable Byte Encoding.
     */
    public static int[] decodeVB(byte[] vbs) {
        List<Integer> numbers = new ArrayList<>();
        int n = 0;

        for (int i = 1; i < vbs.length; i++ ) {
            if ((vbs[i] & 128) == 0) {
                n = 128 * n + vbs[i];
            } else {
                n = 128 *n + (vbs[i] - 128);
                numbers.add(n);
            }
        }


        int[] result = new int[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            result[i] = numbers.get(i);
        }

        return result;
    }

    public static void main(String[] args) {
        int[] seq = {1, 7, 56, 134, 256, 268, 384, 472, 512, 648};

        int[] diffEncoded = encodeDiff(seq);
        System.out.println("Size of Differential Encoding: " + diffEncoded.length * Integer.BYTES + " bytes");

        byte[] vbEncoded = encodeVB(seq);
        System.out.println("Size of VB Compression: " + vbEncoded.length + " bytes");

        byte[] vbDiffEncoded = encodeVB(diffEncoded);
        System.out.print("Size of Differential Encoding and subsequence VB compression: " + vbDiffEncoded.length + " bytes");
    }
}