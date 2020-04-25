import java.awt.*;
import java.io.File;
import java.math.BigInteger;
import java.util.Scanner;

public class DiffieHellman {
    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);

        BigInteger p,g, x, y, k1, k2, alice, bob;

        System.out.println("Enter two prime numbers\n");
        p = scan.nextBigInteger();
        g = scan.nextBigInteger();

//         Bob A = ga mod p
        System.out.println("Enter secret key Alice: ");
        x = scan.nextBigInteger();
        alice = g.modPow(x, p);

//         Alice B = gb mod p
        System.out.println("Enter secret key BOB:");
        y = scan.nextBigInteger();
        bob = g.modPow(y, p);

//        Alice computes s = Ba mod p
        k1 = bob.modPow(x, p);
//        Bob computes s = Ab mod p
        k2 = alice.modPow(y, p);

        System.out.println("Alice's secret key is : " + k1 + "\n");
        System.out.println("Bob's secret key is : " + k2 + "\n");
    }
}
