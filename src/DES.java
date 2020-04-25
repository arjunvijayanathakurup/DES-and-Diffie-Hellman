import java.io.*;
import java.nio.charset.Charset;
import java.util.Scanner;
class Encryption{
    int ip[] = {                            //  INITIAL PERMUTATION
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };
    int pc_1[] = {                         //  PERMUTATION CHOICE 1
            57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4
    };

    int[] ipArray = new int[ip.length];     //  PLAIN TEXT INITIAL PERMUTATION VALUE
    int[] left = new int[32];               //  LEFT HALF OF PLAIN TEXT AFTER INITIAL PERMUTATION
    int[] right = new int[32];              //  RIGHT HALF OF PLAIN TEXT AFTER INITIAL PERMUTATION
    protected int[] SecPc1 = new int[56];   //  SECRET KEY AFTER PERMUTATION CHOICE 1
    int[] lf_pc1 = new int[28];
    int[] rg_pc1 = new int[28];


    //        Permutation choice 2
    int pc_2[] = {
            14, 17, 11, 24, 1, 5,
            3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8,
            16,  7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48,
            44, 49, 39, 56, 34, 53,
            46, 42, 50, 36, 29, 32

    };

    //        Final Permutation
    int pc_fin_[] = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25

    };

    //        Substitution tables
    int s_table[][][] = {
            {
                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}

            },
            {
                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9}
            },
            {
                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12}
            },
            {
                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14}
            },
            {
                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3}
            },
            {
                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13}
            },
            {
                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12}
            },
            {
                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
            }
    };
    int e_table[] = {
            32, 1,  2,  3,  4,  5,
            4,  5,  6,  7,  8,  9,
            8,  9, 10, 11, 12, 13,
            12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25,
            24, 25, 26, 27, 28, 29,
            28, 29, 30, 31, 32,  1
    };
    int shift[] = {
            1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };


//    INITIAL PERMUTATION ON 64 BIT BINARY VALUES OF THE PLAIN TEXT
    public void initPermu(int[] num){
        for(int i = 0; i < num.length; i++) {
            ipArray[i] = num[ip[i] - 1];
        }
        IpSplit(ipArray);
    }

//    PERMUTATION CODE
    public int[] permutation(int[] num, int[] num2){
        int[] result = {};
        for(int i = 0; i < num.length; i++) {
            result[i] = num[num2[i] - 1];
        }
        return result;
    }

//    SPLITTING THE INITIAL PERMUTATION VALUE INTO TWO HALVES LEFT AND RIGHT BLOCK CONTAINING 32 BITS EACH
    public void IpSplit(int[] initpermu){
        int length = initpermu.length;
        int split_num = length / 2;

        for (int j = 0; j < split_num; j++) {
            left[j] = initpermu[j];
        }

//        FOR PRINTING UN-COMMENT THIS PART TO VIEW THE LEFT BLOCK
//        System.out.println("LEFT");
//        for (int i: left) {
//            System.out.print(i);
//        }
        int m = 0;
        for (int j = split_num; j < length; j++){
                right[m] = initpermu[j];
                m++;
        }
//        FOR PRINTING UN-COMMENT THIS PART TO VIEW THE LEFT BLOCK
//        System.out.println("\nRIGHT");
//        for (int i: right) {
//            System.out.print(i);
//        }
    }

//    KEY PERMUTATION CHOICE 1
    protected void DesKeyGen(int[] secretkey){
        for(int i = 0; i < SecPc1.length; i++) {
            SecPc1[i] = secretkey[pc_1[i] - 1];
        }
        PcSplit(SecPc1);
//        FOR PRINTING UN-COMMENT THIS PART TO VIEW THE LEFT BLOCK
//        System.out.println("\nDESKEYGEN");
//        for (int i: SecPc1) {
//            System.out.print(i);
//        }
    }

    protected void PcSplit(int[] key){
        int length = key.length;
        int split_num = length / 2;

        for (int j = 0; j < split_num; j++) {
            lf_pc1[j] = key[j];
        }

//        FOR PRINTING UN-COMMENT THIS PART TO VIEW THE LEFT BLOCK
//        System.out.println("LEFT");
//        for (int i: lf_pc1) {
//            System.out.print(i);
//        }

        int m = 0;
        for (int j = split_num; j < length; j++){
            rg_pc1[m] = key[j];
            m++;
        }
//        FOR PRINTING UN-COMMENT THIS PART TO VIEW THE LEFT BLOCK
//        System.out.println("\nRIGHT");
//        for (int i: rg_pc1) {
//            System.out.print(i);
//        }
    }

//    LEFT SHIFT OPERATION
//    TODO: LEFT SHIFT NEEDS TO BE CHECKED
    protected void LeftShift(int[] key, int count){
        int temp = 0;
        for (int i = 0; i < count; i++)
        {
            temp = key[0];
            for (int j = 0; j < count; j++){
                key[i - 1] = key[i];
            }
            key[key.length - 1] = temp;
        }
//        for (int i: key) {
//            System.out.print(i);
//        }
    }
//    XOR EACH ARRAY ELEMENTS
    public int[] xor(int[] a, int[] b)    {
        int[] r = new int[a.length];

        for(int i = 0; i < r.length; i++)   {
            r[i] = xorBitwise(a[i], b[i]);
        }

        return r;
    }

//    XOR BITWISE
    private int xorBitwise(int a, int b){

        if((a == 0) && (b == 0))    {
            return 0;
        }
        else if((a == 0) && (b == 1))   {
            return 1;
        }
        else if((a == 1) && (b == 0))   {
            return 1;
        }
        else if((a == 1) && (b == 1))   {
            return 0;
        }
        else    {
            return -1;
        }
    }
    public int[] encryptFunct(int[] right_arr, int[] key){
        int[] result = {};
        int[] expansion = permutation(right_arr, key);
        int[] xoring = xor(key, expansion);
//        System.out.println(xoring);

        return result;
    }

}
class DES {
    //    MAIN FUNCTION
    public static void main(String[] args) {

        try {
//            Scanner scan = new Scanner(System.in);
//            System.out.println("Enter the Plain Text \n");
//            String plain_text = scan.next();
//            System.out.println("Plain Text : " + plain_text); TODO: LAST IMPLEMENTATION

//            Block 64 bit
//            String plaintext_block = new String();
//            if (plain_text.length() > 8) {
//                plaintext_block = plain_text.substring(0, 8);
//            }
//            System.out.println("plain text block of 8 " + plaintext_block);  TODO: LAST IMPLEMENTATION

//            Conversion from string to binary
//            byte[] bytes = plaintext_block.getBytes();
//            StringBuilder binary = new StringBuilder();
//            for (byte b : bytes) {
//                int val = b;
//                for (int i = 0; i < 8; i++) {
//                    binary.append((val & 128) == 0 ? 0 : 1);
//                    val <<= 1;
//                }
//                binary.append(' ');
//            }
//
//            String bin = binary.toString(); // Conversion from StringBuilder to strings
//            System.out.println("Binary value: " + bin);   TODO: LAST IMPLEMENTATION

//            Binary string conversion to integer array
//            String[] bin_string = new String[8];
//            bin_string = bin.split(" ");
//
////            for (String a: bin_string) {
////                System.out.print(a + "\n");
////            }
//
//            int[] num = new int[8];
//            int index = 0;
//            for (int i = 0; i < bin_string.length; i++) {
//                try {
//                    num[index] = Integer.parseInt(bin_string[i]);
//                    index++;
//                }
//                catch (NumberFormatException nfe) {
//
//                }
//            }
//            for (int i: num) {
//                System.out.println("Binary Array value: " + i);
//            }

            Encryption encrypt = new Encryption();
            int[] a = {0, 0, 0, 0, 0, 0, 0, 1,
                    0, 0, 1, 0, 0, 0, 1, 1,
                    0, 1, 0, 0, 0, 1, 0, 1,
                    0, 1, 1, 0, 0, 1, 1, 1,
                    1, 0, 0, 0, 1, 0, 0, 1,
                    1, 0, 1, 0, 1, 0, 1, 1,
                    1, 1, 0, 0, 1, 1, 0, 1,
                    1, 1, 1, 0, 1, 1, 1, 1};

            int[] b = {0, 1, 1, 0, 0, 0, 1, 0,
                    0, 1, 1, 0, 1, 0, 0, 1, 0,
                    1, 1, 0, 1, 1, 1, 0, 0, 1,
                    1, 1, 0, 0, 1, 1, 0, 1, 1,
                    0, 0, 1, 0, 1, 0, 1, 1, 0,
                    0, 0, 1, 1, 0, 1, 1, 0, 1,
                    0, 1, 1, 0, 1, 1, 0, 0, 1,
                    0, 1
            };
//            Encryption
            encrypt.initPermu(a);   // Initial Permutation
            encrypt.DesKeyGen(b);   //  Key-Generation
            for (int i = 0; i < 16; i++) {
                encrypt.LeftShift(encrypt.lf_pc1, encrypt.shift.length);
                encrypt.LeftShift(encrypt.rg_pc1, encrypt.shift.length);
            }
//            for (int i = 1; i <= 16; i++ ){
//                encrypt.left[i] = encrypt.right[i-1];
//                encrypt.right[i] = encrypt.xor(encrypt.left[i - 1], encrypt.encryptFunct(encrypt.right, encrypt.SecPc1));
//            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
}

