package crypt;

abstract public class GeneralDigest
{
  abstract public byte[] digest();

  public String bindigest() {
    return asBin(digest());
  }

  public String hexdigest() {
    return asHex(digest());
  }

  public static String asBin(byte[] digest) {
    char[] buffer = new char[digest.length * 8];

    for (int j = 0, x = 0; j < digest.length; j++) {
      for (int i = 0; i < 8; i++, x++) {
        buffer[x] = (((digest[j] >>> (7 - i)) & 1) == 0) ? '0' : '1';
      }
    }

    return new String(buffer);
  }

  private static final char[] HEX_CHARS = {'0', '1', '2', '3',
                                           '4', '5', '6', '7',
                                           '8', '9', 'a', 'b',
                                           'c', 'd', 'e', 'f'};

  public static String asHex(byte[] hash) {
    char buf[] = new char[hash.length * 2];
    for (int i = 0, x = 0; i < hash.length; i++) {
      buf[x++] = HEX_CHARS[(hash[i] >>> 4) & 0xf];
      buf[x++] = HEX_CHARS[hash[i] & 0xf];
    }
    return new String(buf);
  }
}
