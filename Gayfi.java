import javax.microedition.lcdui.Screen;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Alert;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.midlet.MIDlet;

import java.util.Hashtable;
import java.util.Enumeration;

public class Gayfi extends MIDlet implements gayfi.MIDlet, CommandListener
{
    private Display display;
    private Alert main;
    private Alert alert;
    private Hashtable services;
    private String currentService = null;

    public Gayfi()
    {
        super();
        display = Display.getDisplay(this);

        services = new Hashtable(5);
        services.put("Alice AGPF", new gayfi.services.AliceAGPF());
        services.put("Fastweb Pirelli", new gayfi.services.FastwebPirelli());
        services.put("Fastweb Telsey", new gayfi.services.FastwebTelsey());

        alert = new Alert("GayFi");
        alert.addCommand(new Command("OK", Command.EXIT, 1));
        alert.addCommand(new Command("Esci", Command.OK, 1));

        main = new Alert("GayFi");
        main.setString("GayFi v0.0.1");
        try {
          main.setImage(Image.createImage(getClass().getResourceAsStream("/res/logo.png")));
        } catch(java.io.IOException e) {
        }

        setCommands(main);
        for (Enumeration e = services.elements(); e.hasMoreElements(); ) {
          gayfi.Service srv = (gayfi.Service)(e.nextElement());
          if (srv != null) srv.setMIDlet(this);
        }
    }

    public void setCommands(Screen screen) {
      if (screen == null) {
        //System.out.println("screen null");
        return;
      }

      screen.addCommand(new Command("Esci", Command.EXIT, 2));
      for (Enumeration e = services.keys(); e.hasMoreElements(); ) {
        String cmd = (String) e.nextElement();
        if (cmd != null) screen.addCommand(new Command(cmd, Command.OK, 2));
      }
      screen.addCommand(new Command("Calcola", Command.EXIT, 1));
      screen.setCommandListener(this);
    }

    protected void startApp() throws MIDletStateChangeException
    {
        display.setCurrent(main);
    }

    protected void destroyApp(boolean arg0) throws MIDletStateChangeException { }
    protected void pauseApp() { }

    public void commandAction(Command c, Displayable arg1)
    {
      String label = c.getLabel();
      if ("Esci".equals(label))
      {
        notifyDestroyed();
      }
      else if ("OK".equals(label))
      {
        if (currentService != null)
        {
          getCurrentService().start();
        }
      }
      else if ("Calcola".equals(label))
      {
        if (currentService != null)
        {
          alert.setString(getCurrentService().calculate());
          display.setCurrent(alert);
        }
      }
      else if (services.containsKey(label))
      {
        currentService = label;
        getCurrentService().start();
      }
    }

    protected gayfi.Service getCurrentService() {
      return (gayfi.Service)services.get(currentService);
    }

    public Display getDisplay() { return display; }
}
