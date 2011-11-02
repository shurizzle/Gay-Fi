package crypt;

public class Hashword extends GeneralDigest
{
  protected int lastVal;

  public Hashword(int initval) {
    lastVal = initval;
  }

  public Hashword() {
    this(0);
  }

  public Hashword(int[] k, int offset, int length, int initval) {
    this(initval);
    update(k, offset, length);
  }

  public Hashword(int[] k, int offset, int length) {
    this(k, offset, length, 0);
  }

  public Hashword(int[]k , int initval) {
    this(k, 0, k.length, initval);
  }

  public Hashword(int[] k) {
    this(k, 0);
  }

  public void reset() {
    lastVal = 0;
  }

  public void update(int[] k, int offset, int length) {
    int a, b, c;
    a = b = c = 0xdeadbeef + (length << 2) + lastVal;

    int i = offset;
    while (length > 3)
    {
      a += k[i];
      b += k[i + 1];
      c += k[i + 2];

      a -= c; a ^= (c <<  4) | (c >>>  -4); c += b;
      b -= a; b ^= (a <<  6) | (a >>>  -6); a += c;
      c -= b; c ^= (b <<  8) | (b >>>  -8); b += a;
      a -= c; a ^= (c << 16) | (c >>> -16); c += b;
      b -= a; b ^= (a << 19) | (a >>> -19); a += c;
      c -= b; c ^= (b <<  4) | (b >>>  -4); b += a;

      length -= 3;
      i += 3;
    }

    switch(length) {
      case 3: c += k[i + 2];
      case 2: b += k[i + 1];
      case 1: a += k[i];
        c ^= b; c -= (b << 14) | (b >>> -14);
        a ^= c; a -= (c << 11) | (c >>> -11);
        b ^= a; b -= (a << 25) | (a >>> -25);
        c ^= b; c -= (b << 16) | (b >>> -16);
        a ^= c; a -= (c <<  4) | (c >>>  -4);
        b ^= a; b -= (a << 14) | (a >>> -14);
        c ^= b; c -= (b << 24) | (b >>> -24);
      case 0:
        break;
    }

    lastVal = c;
  }

  public void update(int[] k) {
    update(k, 0, k.length);
  }

  public byte[] digest() {
    byte[] res = new byte[4];
    res[0] = (byte) ((lastVal >>> 24));
    res[1] = (byte) ((lastVal >>> 16) & 0xFF);
    res[2] = (byte) ((lastVal >>>  8) & 0xFF);
    res[3] = (byte) ((lastVal) & 0xFF);
    reset();

    return res;
  }
}
