package gayfi;

import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.CommandListener;
import gayfi.MIDlet;

public abstract class Service
{
  protected MIDlet midlet;
  protected Display display;
  protected Form main = new Form("GayFi");

  public void setMIDlet(MIDlet midlet) {
    this.midlet = midlet;
    display = this.midlet.getDisplay();
    //System.out.println(getClass().getName());
    midlet.setCommands(main);
    //System.out.println("End");
  }

  public void draw() { };

  public void start() {
    draw();
    display.setCurrent(main);
  }

  abstract public String calculate();
}
