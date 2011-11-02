package crypt;

public class MD5 extends GeneralDigest
{
  protected org.bouncycastle.crypto.digests.MD5Digest md5;

  public MD5() {
    md5 = new org.bouncycastle.crypto.digests.MD5Digest();
  }

  public MD5(String s) {
    this();
    update(s);
  }

  public MD5(byte[] buffer) {
    this();
    update(buffer);
  }

  public MD5(byte[] buffer, int offset, int length) {
    this();
    update(buffer, offset, length);
  }

  public MD5(byte b) {
    this();
    update(b);
  }

  public void update(byte buffer[], int offset, int length) {
    md5.update(buffer, offset, length);
  }

  public void update(byte buffer[]) {
    md5.update(buffer, 0, buffer.length);
  }

  public void update(byte b) {
    md5.update(b);
  }

  public void update(String s) {
    update(s.getBytes());
  }

  public byte[] digest() {
    byte[] res = new byte[16];
    md5.doFinal(res, 0);
    return res;
  }

  public void reset() {
    md5.reset();
  }
}
