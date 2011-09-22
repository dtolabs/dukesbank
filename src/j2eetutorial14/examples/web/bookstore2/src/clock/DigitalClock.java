/*
 * Copyright (c) 2006 Sun Microsystems, Inc.  All rights reserved.  U.S.
 * Government Rights - Commercial software.  Government users are subject
 * to the Sun Microsystems, Inc. standard license agreement and
 * applicable provisions of the FAR and its supplements.  Use is subject
 * to license terms.
 *
 * This distribution may include materials developed by third parties.
 * Sun, Sun Microsystems, the Sun logo, Java and J2EE are trademarks
 * or registered trademarks of Sun Microsystems, Inc. in the U.S. and
 * other countries.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. Tous droits reserves.
 *
 * Droits du gouvernement americain, utilisateurs gouvernementaux - logiciel
 * commercial. Les utilisateurs gouvernementaux sont soumis au contrat de
 * licence standard de Sun Microsystems, Inc., ainsi qu'aux dispositions
 * en vigueur de la FAR (Federal Acquisition Regulations) et des
 * supplements a celles-ci.  Distribue par des licences qui en
 * restreignent l'utilisation.
 *
 * Cette distribution peut comprendre des composants developpes par des
 * tierces parties. Sun, Sun Microsystems, le logo Sun, Java et J2EE
 * sont des marques de fabrique ou des marques deposees de Sun
 * Microsystems, Inc. aux Etats-Unis et dans d'autres pays.
 */


package clock;

import java.util.*;
import java.awt.*;
import java.applet.*;
import java.text.*;


public class DigitalClock extends Applet implements Runnable {
    Thread timer; // The thread that displays clock
    DateFormat formatter; // Formats the date displayed
    String lastdate; // String to hold date displayed
    Date currentDate; // Used to get date to display
    Color numberColor; // Color of numbers
    Font clockFaceFont;
    Locale locale;
    String language;
    String country;

    public void init() {
        numberColor = Color.black;

        try {
            language = getParameter("language");
        } catch (Exception E) {
        }

        try {
            country = getParameter("country");
        } catch (Exception E) {
        }

        if (country == null) {
            country = "";
        } else {
            System.err.println(country);
        }

        if (language == null) {
            locale = Locale.getDefault();
        } else {
            System.err.println(language);
            locale = new Locale(language, country);
        }

        System.err.println(locale.getDisplayName());

        try {
            setBackground(new Color(Integer.parseInt(getParameter("bgcolor"), 16)));
        } catch (Exception E) {
        }

        try {
            numberColor = new Color(Integer.parseInt(getParameter("fgcolor"), 16));
        } catch (Exception E) {
        }

        formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.MEDIUM, locale);
        currentDate = new Date();
        lastdate = formatter.format(currentDate);
        clockFaceFont = new Font("Sans-Serif", Font.PLAIN, 14);
        resize(275, 25); // Set clock window size
    }

    // Paint is the main part of the program
    public void paint(Graphics g) {
        String today;
        currentDate = new Date();
        formatter = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.MEDIUM, locale);
        today = formatter.format(currentDate);
        g.setFont(clockFaceFont);

        // Erase and redraw  
        g.setColor(getBackground());
        g.drawString(lastdate, 0, 12);

        g.setColor(numberColor);
        g.drawString(today, 0, 12);
        lastdate = today;
        currentDate = null;
    }

    public void start() {
        timer = new Thread(this);
        timer.start();
    }

    public void stop() {
        timer = null;
    }

    public void run() {
        Thread me = Thread.currentThread();

        while (timer == me) {
            try {
                Thread.currentThread()
                      .sleep(100);
            } catch (InterruptedException e) {
            }

            repaint();
        }
    }

    public void update(Graphics g) {
        paint(g);
    }

    public String getAppletInfo() {
        return "Digital Clock.";
    }

    public String[][] getParameterInfo() {
        String[][] info =
            {
                {
                    "bgcolor", "hexadecimal RGB number",
                    "The background color. Default is the color of your browser."
                },
                { "fgcolor", "hexadecimal RGB number", "The color of the date." }
            };

        return info;
    }
}
