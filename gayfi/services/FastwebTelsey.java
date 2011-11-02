package gayfi.services;

import javax.microedition.lcdui.TextField;
import gayfi.Service;
import java.lang.Integer;
import crypt.Hashword;

public class FastwebTelsey extends Service
{
  private TextField essidBox = new TextField("FASTWEB-1-", null, 12, TextField.ANY);

  public FastwebTelsey() {
    main.append("Fastweb Telsey");
    main.append(essidBox);
  }

  public static int byteToInt(byte b) {
    int res = 0;
    for (int i = 0; i < 8; i++) {
      res |= b & (1 << (i));
    }
    return res;
  }

  public static int merge(byte one, byte two, byte three, byte four) {
    return (byteToInt(one) << 24) | (byteToInt(two) << 16) | (byteToInt(three) << 8) | byteToInt(four);
  }

  public String calculate() {
    String essid = essidBox.getString().toUpperCase();

    if (essid.length() != 12)
      return "Invalid ESSID.";

    byte[] mac = new byte[6];
    for (int i = 0; i < 12; i += 2) {
      mac[i / 2] = (byte) Integer.parseInt(essid.substring(i, i + 2), 16);
    }

    int[] table = new int[64];
    table[0]  = merge(mac[5], mac[1], mac[0], mac[5]);
    table[1]  = merge(mac[1], mac[0], mac[1], mac[5]);
    table[2]  = merge(mac[4], mac[2], mac[3], mac[2]);
    table[3]  = merge(mac[4], mac[3], mac[2], mac[2]);
    table[4]  = merge(mac[2], mac[4], mac[2], mac[0]);
    table[5]  = merge(mac[2], mac[5], mac[3], mac[1]);
    table[6]  = merge(mac[0], mac[4], mac[0], mac[1]);
    table[7]  = merge(mac[1], mac[4], mac[1], mac[0]);
    table[8]  = merge(mac[2], mac[4], mac[2], mac[2]);
    table[9]  = merge(mac[3], mac[1], mac[3], mac[4]);
    table[10] = merge(mac[4], mac[1], mac[4], mac[3]);
    table[11] = merge(mac[5], mac[1], mac[5], mac[5]);
    table[12] = merge(mac[2], mac[1], mac[0], mac[5]);
    table[13] = merge(mac[1], mac[0], mac[1], mac[1]);
    table[14] = merge(mac[4], mac[2], mac[1], mac[3]);
    table[15] = merge(mac[3], mac[3], mac[5], mac[2]);
    table[16] = merge(mac[4], mac[4], mac[5], mac[4]);
    table[17] = merge(mac[5], mac[1], mac[4], mac[0]);
    table[18] = merge(mac[2], mac[5], mac[0], mac[5]);
    table[19] = merge(mac[2], mac[1], mac[3], mac[5]);
    table[20] = merge(mac[5], mac[2], mac[2], mac[4]);
    table[21] = merge(mac[2], mac[3], mac[1], mac[4]);
    table[22] = merge(mac[0], mac[4], mac[4], mac[3]);
    table[23] = merge(mac[3], mac[0], mac[5], mac[3]);
    table[24] = merge(mac[4], mac[3], mac[0], mac[0]);
    table[25] = merge(mac[3], mac[2], mac[1], mac[1]);
    table[26] = merge(mac[2], mac[1], mac[2], mac[5]);
    table[27] = merge(mac[1], mac[3], mac[4], mac[3]);
    table[28] = merge(mac[0], mac[2], mac[3], mac[4]);
    table[29] = merge(mac[0], mac[0], mac[2], mac[2]);
    table[30] = merge(mac[0], mac[0], mac[0], mac[5]);
    table[31] = merge(mac[1], mac[1], mac[1], mac[4]);
    table[32] = merge(mac[4], mac[0], mac[2], mac[2]);
    table[33] = merge(mac[3], mac[3], mac[3], mac[0]);
    table[34] = merge(mac[0], mac[2], mac[4], mac[1]);
    table[35] = merge(mac[5], mac[5], mac[5], mac[0]);
    table[36] = merge(mac[0], mac[4], mac[5], mac[0]);
    table[37] = merge(mac[1], mac[1], mac[5], mac[2]);
    table[38] = merge(mac[2], mac[2], mac[5], mac[1]);
    table[39] = merge(mac[3], mac[3], mac[2], mac[3]);
    table[40] = merge(mac[1], mac[0], mac[2], mac[4]);
    table[41] = merge(mac[1], mac[5], mac[2], mac[5]);
    table[42] = merge(mac[0], mac[1], mac[4], mac[0]);
    table[43] = merge(mac[1], mac[1], mac[1], mac[4]);
    table[44] = merge(mac[2], mac[2], mac[2], mac[2]);
    table[45] = merge(mac[3], mac[3], mac[3], mac[3]);
    table[46] = merge(mac[5], mac[4], mac[0], mac[1]);
    table[47] = merge(mac[4], mac[0], mac[5], mac[5]);
    table[48] = merge(mac[1], mac[0], mac[5], mac[0]);
    table[49] = merge(mac[0], mac[1], mac[5], mac[1]);
    table[50] = merge(mac[2], mac[2], mac[4], mac[2]);
    table[51] = merge(mac[3], mac[4], mac[4], mac[3]);
    table[52] = merge(mac[4], mac[3], mac[1], mac[5]);
    table[53] = merge(mac[5], mac[5], mac[1], mac[4]);
    table[54] = merge(mac[3], mac[0], mac[1], mac[5]);
    table[55] = merge(mac[3], mac[1], mac[0], mac[4]);
    table[56] = merge(mac[4], mac[2], mac[2], mac[5]);
    table[57] = merge(mac[4], mac[3], mac[3], mac[1]);
    table[58] = merge(mac[2], mac[4], mac[3], mac[0]);
    table[59] = merge(mac[2], mac[3], mac[5], mac[1]);
    table[60] = merge(mac[3], mac[1], mac[2], mac[3]);
    table[61] = merge(mac[5], mac[0], mac[1], mac[2]);
    table[62] = merge(mac[5], mac[3], mac[4], mac[1]);
    table[63] = merge(mac[0], mac[2], mac[3], mac[0]);

    String v1, v2;

    {
      Hashword hash = new Hashword();
      for (int i = 0; i < 64; i++) {
        hash.update(table, 0, i);
      }
      v1 = hash.hexdigest();
    }

    for (int i = 0; i < 64; i++) {
      if (i < 8)
        table[i] <<= 3;
      else if (i < 16)
        table[i] >>>= 5;
      else if (i < 32)
        table[i] >>>= 2;
      else
        table[i] <<= 7;
    }

    {
      Hashword hash = new Hashword();
      for (int i = 0; i < 64; i++) {
        hash.update(table, 0, i);
      }
      v2 = hash.hexdigest();
    }

    return v1.substring(3, 8) + v2.substring(0, 5);
  }
}
