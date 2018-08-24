/* JEWEL, Java Environment With Enhanced Linkage
 * Copyright (c) 1998-2004, Rodrigo Augusto Barbato Ferreira
 */
package jewel.core;

import java.util.Arrays;
import java.util.zip.Checksum;

/**
 * This class implements the Secure Hash Algorithm 1 (SHA-1),
 * as described by the Secure Hash Standard, used to compute
 * the message digest for the Digital Signature Algorithm (DSA).
 * The SHA-1 is called secure because it is computationally
 * infeasible to find a message which corresponds to a given
 * message digest, or to find two different messages which
 * produce the same message digest. Any change to a message,
 * with very high probability, result in a different message
 * digest.
 * @author Rodrigo Ferreira
 */
public class SHA1 implements Checksum {

  public static String toString(SHA1 sha1) {
    int[] values = sha1.getPreciseValue();
    StringBuffer sb = new StringBuffer(9*values.length-1);
    for (int i = 0; i < values.length; i++) {
      String s = Integer.toHexString(values[i]);
      for (int j = s.length(); j < 8; j++)
        sb.append('0');
      sb.append(s);
      if (i+1 < values.length)
        sb.append(' ');
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    SHA1 sha1 = new SHA1();
    sha1.update(0x61);
    sha1.update(0x62);
    sha1.update(0x63);
    System.out.println("Example 1: "+toString(sha1));
    sha1.update(new byte[]{ 
                        0x64,
      0x62, 0x63, 0x64, 0x65,
      0x63, 0x64, 0x65, 0x66,
      0x64, 0x65, 0x66, 0x67,
      0x65, 0x66, 0x67, 0x68,
      0x66, 0x67, 0x68, 0x69,
      0x67, 0x68, 0x69, 0x6A,
      0x68, 0x69, 0x6A, 0x6B,
      0x69, 0x6A, 0x6B, 0x6C,
      0x6A, 0x6B, 0x6C, 0x6D,
      0x6B, 0x6C, 0x6D, 0x6E,
      0x6C, 0x6D, 0x6E, 0x6F,
      0x6D, 0x6E, 0x6F, 0x70,
      0x6E, 0x6F, 0x70, 0x71,
    });
    System.out.println("Example 2: "+toString(sha1));
    sha1.reset();
    byte[] data = new byte[]{ 0x61, 0x61, 0x61, 0x61, 0x61, 0x61, 0x61, 0x61, 0x61, 0x61, };
    for (int i = 0; i < 100000; i++)
      sha1.update(data);
    System.out.println("Example 3: "+toString(sha1));
  }

  private int H0 = 0x67452301;
  private int H1 = 0xEFCDAB89;
  private int H2 = 0x98BADCFE;
  private int H3 = 0x10325476;
  private int H4 = 0xC3D2E1F0;

  private long total;
  private byte count;
  private byte[] bytes = new byte[64];

  private transient boolean padded;

  private transient int C0;
  private transient int C1;
  private transient int C2;
  private transient int C3;
  private transient int C4;

  public SHA1() { }

  public void reset() {
    H0 = 0x67452301;
    H1 = 0xEFCDAB89;
    H2 = 0x98BADCFE;
    H3 = 0x10325476;
    H4 = 0xC3D2E1F0;
    total = 0;
    count = 0;
    padded = false;
  }

  private void update(int W00, int W01, int W02, int W03, int W04, int W05, int W06, int W07,
                      int W08, int W09, int W10, int W11, int W12, int W13, int W14, int W15) {
    int F0 = (H0<<5|H0>>>27)+(H1&H2|~H1&H3)+H4+W00+0x5A827999; 
    int C0 = H1<<30|H1>>>2;
    int F1 = (F0<<5|F0>>>27)+(H0&C0|~H0&H2)+H3+W01+0x5A827999;
    int C1 = H0<<30|H0>>>2;
    int F2 = (F1<<5|F1>>>27)+(F0&C1|~F0&C0)+H2+W02+0x5A827999;
    int C2 = F0<<30|F0>>>2;
    F0 = (F2<<5|F2>>>27)+(F1&C2|~F1&C1)+C0+W03+0x5A827999;
    C0 = F1<<30|F1>>>2;
    F1 = (F0<<5|F0>>>27)+(F2&C0|~F2&C2)+C1+W04+0x5A827999;
    C1 = F2<<30|F2>>>2;
    F2 = (F1<<5|F1>>>27)+(F0&C1|~F0&C0)+C2+W05+0x5A827999;
    C2 = F0<<30|F0>>>2;
    F0 = (F2<<5|F2>>>27)+(F1&C2|~F1&C1)+C0+W06+0x5A827999;
    C0 = F1<<30|F1>>>2;
    F1 = (F0<<5|F0>>>27)+(F2&C0|~F2&C2)+C1+W07+0x5A827999;
    C1 = F2<<30|F2>>>2;
    F2 = (F1<<5|F1>>>27)+(F0&C1|~F0&C0)+C2+W08+0x5A827999;
    C2 = F0<<30|F0>>>2;
    F0 = (F2<<5|F2>>>27)+(F1&C2|~F1&C1)+C0+W09+0x5A827999;
    C0 = F1<<30|F1>>>2;
    F1 = (F0<<5|F0>>>27)+(F2&C0|~F2&C2)+C1+W10+0x5A827999;
    C1 = F2<<30|F2>>>2;
    F2 = (F1<<5|F1>>>27)+(F0&C1|~F0&C0)+C2+W11+0x5A827999;
    C2 = F0<<30|F0>>>2; 
    F0 = (F2<<5|F2>>>27)+(F1&C2|~F1&C1)+C0+W12+0x5A827999;
    C0 = F1<<30|F1>>>2;
    F1 = (F0<<5|F0>>>27)+(F2&C0|~F2&C2)+C1+W13+0x5A827999;
    C1 = F2<<30|F2>>>2;
    F2 = (F1<<5|F1>>>27)+(F0&C1|~F0&C0)+C2+W14+0x5A827999;
    C2 = F0<<30|F0>>>2;
    F0 = (F2<<5|F2>>>27)+(F1&C2|~F1&C1)+C0+W15+0x5A827999;
    C0 = F1<<30|F1>>>2;
    int T = W13^W08^W02^W00;
    W00 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|~F2&C2)+C1+W00+0x5A827999;
    C1 = F2<<30|F2>>>2;
    T = W14^W09^W03^W01;
    W01 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0&C1|~F0&C0)+C2+W01+0x5A827999;
    C2 = F0<<30|F0>>>2;
    T = W15^W10^W04^W02;
    W02 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1&C2|~F1&C1)+C0+W02+0x5A827999;
    C0 = F1<<30|F1>>>2;
    T = W00^W11^W05^W03;
    W03 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|~F2&C2)+C1+W03+0x5A827999;
    C1 = F2<<30|F2>>>2;
    T = W01^W12^W06^W04;
    W04 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W04+0x6ED9EBA1;
    C2 = F0<<30|F0>>>2; 
    T = W02^W13^W07^W05;
    W05 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W05+0x6ED9EBA1;
    C0 = F1<<30|F1>>>2; 
    T = W03^W14^W08^W06;
    W06 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W06+0x6ED9EBA1;
    C1 = F2<<30|F2>>>2; 
    T = W04^W15^W09^W07;
    W07 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W07+0x6ED9EBA1;
    C2 = F0<<30|F0>>>2; 
    T = W05^W00^W10^W08;
    W08 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W08+0x6ED9EBA1;
    C0 = F1<<30|F1>>>2; 
    T = W06^W01^W11^W09;
    W09 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W09+0x6ED9EBA1;
    C1 = F2<<30|F2>>>2; 
    T = W07^W02^W12^W10;
    W10 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W10+0x6ED9EBA1;
    C2 = F0<<30|F0>>>2; 
    T = W08^W03^W13^W11;
    W11 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W11+0x6ED9EBA1;
    C0 = F1<<30|F1>>>2; 
    T = W09^W04^W14^W12;
    W12 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W12+0x6ED9EBA1;
    C1 = F2<<30|F2>>>2; 
    T = W10^W05^W15^W13;
    W13 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W13+0x6ED9EBA1;
    C2 = F0<<30|F0>>>2; 
    T = W11^W06^W00^W14;
    W14 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W14+0x6ED9EBA1;
    C0 = F1<<30|F1>>>2; 
    T = W12^W07^W01^W15;
    W15 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W15+0x6ED9EBA1;
    C1 = F2<<30|F2>>>2;
    T = W13^W08^W02^W00;
    W00 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W00+0x6ED9EBA1;
    C2 = F0<<30|F0>>>2;
    T = W14^W09^W03^W01;
    W01 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W01+0x6ED9EBA1;
    C0 = (F1 << 30)|(F1 >>> 2);
    T = W15^W10^W04^W02;
    W02 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W02+0x6ED9EBA1;
    C1 = F2<<30|F2>>>2;
    T = W00^W11^W05^W03;
    W03 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W03+0x6ED9EBA1;
    C2 = F0<<30|F0>>>2;
    T = W01^W12^W06^W04;
    W04 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W04+0x6ED9EBA1;
    C0 = F1<<30|F1>>>2;
    T = W02^W13^W07^W05;
    W05 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W05+0x6ED9EBA1;
    C1 = F2<<30|F2>>>2;
    T = W03^W14^W08^W06;
    W06 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W06+0x6ED9EBA1;
    C2 = F0<<30|F0>>>2;
    T = W04^W15^W09^W07;
    W07 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W07+0x6ED9EBA1;
    C0 = F1<<30|F1>>>2;
    T = W05^W00^W10^W08;
    W08 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|F2&C2|C0&C2)+C1+W08+0x8F1BBCDC;
    C1 = F2<<30|F2>>>2;
    T = W06^W01^W11^W09;
    W09 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0&C1|F0&C0|C1&C0)+C2+W09+0x8F1BBCDC;
    C2 = F0<<30|F0>>>2;
    T = W07^W02^W12^W10;
    W10 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1&C2|F1&C1|C2&C1)+C0+W10+0x8F1BBCDC;
    C0 = F1<<30|F1>>>2;
    T = W08^W03^W13^W11;
    W11 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|F2&C2|C0&C2)+C1+W11+0x8F1BBCDC;
    C1 = F2<<30|F2>>>2;
    T = W09^W04^W14^W12;
    W12 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0&C1|F0&C0|C1&C0)+C2+W12+0x8F1BBCDC;
    C2 = F0<<30|F0>>>2;
    T = W10^W05^W15^W13;
    W13 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1&C2|F1&C1|C2&C1)+C0+W13+0x8F1BBCDC;
    C0 = F1<<30|F1>>>2;
    T = W11^W06^W00^W14;
    W14 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|F2&C2|C0&C2)+C1+W14+0x8F1BBCDC;
    C1 = F2<<30|F2>>>2;
    T = W12^W07^W01^W15;
    W15 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0&C1|F0&C0|C1&C0)+C2+W15+0x8F1BBCDC;
    C2 = F0<<30|F0>>>2;
    T = W13^W08^W02^W00;
    W00 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1&C2|F1&C1|C2&C1)+C0+W00+0x8F1BBCDC;
    C0 = F1<<30|F1>>>2;
    T = W14^W09^W03^W01;
    W01 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|F2&C2|C0&C2)+C1+W01+0x8F1BBCDC;
    C1 = F2<<30|F2>>>2;
    T = W15^W10^W04^W02;
    W02 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0&C1|F0&C0|C1&C0)+C2+W02+0x8F1BBCDC;
    C2 = F0<<30|F0>>>2;
    T = W00^W11^W05^W03;
    W03 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1&C2|F1&C1|C2&C1)+C0+W03+0x8F1BBCDC;
    C0 = F1<<30|F1>>>2;
    T = W01^W12^W06^W04;
    W04 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|F2&C2|C0&C2)+C1+W04+0x8F1BBCDC;
    C1 = F2<<30|F2>>>2;
    T = W02^W13^W07^W05;
    W05 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0&C1|F0&C0|C1&C0)+C2+W05+0x8F1BBCDC;
    C2 = F0<<30|F0>>>2;
    T = W03^W14^W08^W06;
    W06 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1&C2|F1&C1|C2&C1)+C0+W06+0x8F1BBCDC;
    C0 = F1<<30|F1>>>2;
    T = W04^W15^W09^W07;
    W07 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|F2&C2|C0&C2)+C1+W07+0x8F1BBCDC;
    C1 = F2<<30|F2>>>2;
    T = W05^W00^W10^W08;
    W08 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0&C1|F0&C0|C1&C0)+C2+W08+0x8F1BBCDC;
    C2 = F0<<30|F0>>>2;
    T = W06^W01^W11^W09;
    W09 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1&C2|F1&C1|C2&C1)+C0+W09+0x8F1BBCDC;
    C0 = F1<<30|F1>>>2;
    T = W07^W02^W12^W10;
    W10 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2&C0|F2&C2|C0&C2)+C1+W10+0x8F1BBCDC;
    C1 = F2<<30|F2>>>2;
    T = W08^W03^W13^W11;
    W11 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0&C1|F0&C0|C1&C0)+C2+W11+0x8F1BBCDC;
    C2 = F0<<30|F0>>>2;
    T = W09^W04^W14^W12;
    W12 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W12+0xCA62C1D6;
    C0 = F1<<30|F1>>>2;
    T = W10^W05^W15^W13;
    W13 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W13+0xCA62C1D6;
    C1 = F2<<30|F2>>>2;
    T = W11^W06^W00^W14;
    W14 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W14+0xCA62C1D6;
    C2 = F0<<30|F0>>>2;
    T = W12^W07^W01^W15;
    W15 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W15+0xCA62C1D6;
    C0 = F1<<30|F1>>>2;
    T = W13^W08^W02^W00;
    W00 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W00+0xCA62C1D6;
    C1 = F2<<30|F2>>>2;
    T = W14^W09^W03^W01;
    W01 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W01+0xCA62C1D6;
    C2 = F0<<30|F0>>>2;
    T = W15^W10^W04^W02;
    W02 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W02+0xCA62C1D6;
    C0 = F1<<30|F1>>>2;
    T = W00^W11^W05^W03;
    W03 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W03+0xCA62C1D6;
    C1 = F2<<30|F2>>>2;
    T = W01^W12^W06^W04;
    W04 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W04+0xCA62C1D6;
    C2 = F0<<30|F0>>>2;
    T = W02^W13^W07^W05;
    W05 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W05+0xCA62C1D6;
    C0 = F1<<30|F1>>>2;
    T = W03^W14^W08^W06;
    W06 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W06+0xCA62C1D6;
    C1 = F2<<30|F2>>>2;
    T = W04^W15^W09^W07;
    W07 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W07+0xCA62C1D6;
    C2 = F0<<30|F0>>>2;
    T = W05^W00^W10^W08;
    W08 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W08+0xCA62C1D6;
    C0 = F1<<30|F1>>>2;
    T = W06^W01^W11^W09;
    W09 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W09+0xCA62C1D6;
    C1 = F2<<30|F2>>>2;
    T = W07^W02^W12^W10;
    W10 = T<<1|T>>>31;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+W10+0xCA62C1D6;
    C2 = F0<<30|F0>>>2;
    T = W08^W03^W13^W11;
    W11 = T<<1|T>>>31;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+W11+0xCA62C1D6;
    C0 = F1<<30|F1>>>2;
    T = W09^W04^W14^W12;
    W12 = T<<1|T>>>31;
    F1 = (F0<<5|F0>>>27)+(F2^C0^C2)+C1+W12+0xCA62C1D6;
    C1 = F2<<30|F2>>>2;
    T = W10^W05^W15^W13;
    F2 = (F1<<5|F1>>>27)+(F0^C1^C0)+C2+(T<<1|T>>>31)+0xCA62C1D6;
    C2 = F0<<30|F0>>>2;
    T = W11^W06^W00^W14;
    F0 = (F2<<5|F2>>>27)+(F1^C2^C1)+C0+(T<<1|T>>>31)+0xCA62C1D6;
    C0 = F1<<30|F1>>>2;
    T = W12^W07^W01^W15;
    H0 += (F0<<5|F0>>>27)+(F2^C0^C2)+C1+(T<<1|T>>>31)+0xCA62C1D6;
    H1 += F0;
    H2 += F2<<30|F2>>>2;
    H3 += C0;
    H4 += C2;
  }

  private static int intAt(byte[] buffer, int start) {
    return (buffer[start+0]&0xFF)<<24
          |(buffer[start+1]&0xFF)<<16
          |(buffer[start+2]&0xFF)<<8
          |(buffer[start+3]&0xFF);
  }

  private void update(byte[] buffer, int start) {
    update(intAt(buffer, start+ 0), intAt(buffer, start+ 4), intAt(buffer, start+ 8), intAt(buffer, start+12),
           intAt(buffer, start+16), intAt(buffer, start+20), intAt(buffer, start+24), intAt(buffer, start+28),
           intAt(buffer, start+32), intAt(buffer, start+36), intAt(buffer, start+40), intAt(buffer, start+44), 
           intAt(buffer, start+48), intAt(buffer, start+52), intAt(buffer, start+56), intAt(buffer, start+60));
  }

  public void update(int bite) {
    total += 8;
    bytes[count] = (byte)bite;
    count = (byte)((count+1)%64);
    if (count == 0)
      update(bytes, 0);
    padded = false;
  }

  public void update(byte[] buffer) {
    update(buffer, 0, buffer.length);
  }

  public void update(byte[] buffer, int start, int length) {
    if (start < 0 || start > buffer.length)
      throw new ArrayIndexOutOfBoundsException(start);
    int end = start+length;
    if (end < start || end > buffer.length)
      throw new ArrayIndexOutOfBoundsException(end);
    total += 8*(long)length;
    if (count+length < 64) {
      System.arraycopy(buffer, start, bytes, count, length);
      count += length;
    } else {
      System.arraycopy(buffer, start, bytes, count, 64-count);
      update(bytes, 0);
      start += 64-count;
      length -= 64-count;
      count = 0;
      while (length >= 64) {
        update(buffer, start);
        start += 64;
        length -= 64;
      }
      System.arraycopy(buffer, start, bytes, 0, length);
      count = (byte)length;
    }
    padded = false;
  }

  private void ensurePadded() {
    if (!padded) {
      int T0 = H0, T1 = H1, T2 = H2, T3 = H3, T4 = H4;
      if (count < 56) {
        bytes[count] = (byte)0x80;
        Arrays.fill(bytes, count+1, 56, (byte)0);
        bytes[56] = (byte)(total>>56);
        bytes[57] = (byte)(total>>48);
        bytes[58] = (byte)(total>>40);
        bytes[59] = (byte)(total>>32);
        bytes[60] = (byte)(total>>24);
        bytes[61] = (byte)(total>>16);
        bytes[62] = (byte)(total>>8);
        bytes[63] = (byte)total;
        update(bytes, 0);
      } else {
        bytes[count] = (byte)0x80;
        Arrays.fill(bytes, count+1, 64, (byte)0);
        update(bytes, 0);
        update(0,0,0,0,0,0,0,0,0,0,0,0,0,0,(int)(total>>32),(int)total);
      }
      C0 = H0; C1 = H1; C2 = H2; C3 = H3; C4 = H4;
      H0 = T0; H1 = T1; H2 = T2; H3 = T3; H4 = T4;
      padded = true;
    }
  }

  public long getValue() {
    ensurePadded();
    return (((long)C4 & 0xFFFFFFFFL))
         ^ (((long)C3 & 0xFFFFFFFFL) << 8)
         ^ (((long)C2 & 0xFFFFFFFFL) << 16)
         ^ (((long)C1 & 0xFFFFFFFFL) << 24)
         ^ (((long)C0 & 0xFFFFFFFFL) << 32);
  }

  public int[] getPreciseValue() {
    ensurePadded();
    return new int[]{ C0, C1, C2, C3, C4 };
  }

}

