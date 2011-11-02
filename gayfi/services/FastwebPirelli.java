package gayfi.services;

import javax.microedition.lcdui.TextField;
import gayfi.Service;
import java.lang.Integer;
import crypt.MD5;

public class FastwebPirelli extends Service
{
  private TextField essidBox = new TextField("FASTWEB-1-", null, 12, TextField.ANY);

  public FastwebPirelli() {
    main.append("Fastweb Pirelli");
    main.append(essidBox);
  }

  public static int byteToInt(byte b) {
    int res = 0;

    for (int i = 0; i < 8; i++) {
      res |= b & (1 << (i));
    }

    return res;
  }

  public String calculate() {
    String essid = essidBox.getString().toUpperCase();

    if (essid.length() != 12)
      return "Invalid ESSID.";

    byte[] hash = {0, 0, 0, 0, 0, 0, 0x22, 0x33, 0x11, 0x34, 0x02, -127, -6,
      0x22, 0x11, 0x41, 0x68, 0x11, 0x12, 0x01, 0x05, 0x22, 0x71, 0x42, 0x10, 0x66};
    for (int i = 0; i < 12; i += 2) {
      hash[i / 2] = (byte) Integer.parseInt(essid.substring(i, i + 2), 16);
    }

    byte[] digest = (new MD5(hash)).digest();
    String bindigest = new String();

    for (int i = 0; i < digest.length; i++) {
      String tmp = Integer.toBinaryString(byteToInt(digest[i]));

      for (int j = 0; j < (8 - tmp.length()); j++) {
        bindigest += "0";
      }
      bindigest += tmp;
    }

    String res = new String();
    for (int i = 0; i < 25; i += 5) {
      int tmp = Integer.parseInt(bindigest.substring(i, i + 5), 2);
      if (tmp > 0x0a)
        tmp += 0x57;
      String htmp = Integer.toHexString(tmp);
      if (htmp.length() < 2) res += "0";
      res += htmp;
    }

    return res;
  }
}
