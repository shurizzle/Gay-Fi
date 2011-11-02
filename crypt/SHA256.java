package crypt;

public class SHA256 extends GeneralDigest
{
  protected org.bouncycastle.crypto.digests.SHA256Digest sha256;

  public SHA256() {
    sha256 = new org.bouncycastle.crypto.digests.SHA256Digest();
  }

  public SHA256(String s) {
    this();
    update(s);
  }

  public SHA256(byte[] buffer) {
    this();
    update(buffer);
  }

  public SHA256(byte[] buffer, int offset, int length) {
    this();
    update(buffer, offset, length);
  }

  public SHA256(byte b) {
    this();
    update(b);
  }

  public void update(byte buffer[], int offset, int length) {
    sha256.update(buffer, offset, length);
  }

  public void update(byte buffer[]) {
    sha256.update(buffer, 0, buffer.length);
  }

  public void update(byte b) {
    sha256.update(b);
  }

  public void update(String s) {
    update(s.getBytes());
  }

  public byte[] digest() {
    byte[] res = new byte[32];
    sha256.doFinal(res, 0);
    return res;
  }

  public void reset() {
    sha256.reset();
  }
}
