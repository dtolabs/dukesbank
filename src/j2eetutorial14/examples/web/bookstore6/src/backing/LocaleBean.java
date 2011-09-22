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


package backing;

import listeners.AreaSelectedEvent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


/**
 * <p>This is the main bean for the application.  It maintains a
 * <code>Map</code> of {@link CarBean} instances, keyed by model name,
 * and a <code>Map</code> of {@link CarCustomizer} instances, keyed by
 * package name.  The <code>CarBean</code> instances in the model
 * <code>Map</code> are accessed from several pages, as described
 * below.</p>
 *
 * <p>Several pages in the application use this bean as the target of
 * method reference and value reference expressions.</p>
 *
 * <ul>
 *
 * <li><p>The "chooseLocale" page uses <code>actionListener</code>
 * attributes to point to the {@link #chooseLocaleFromMap} and {@link
 * #chooseLocaleFromLink} methods.</p></li>
 *
 * <li><p>The "storeFront" page uses value binding expressions to pull
 * information about four of the known car models in the store.</p></li>
 *
 * <li><p>The "carDetail" page uses value binding expressions to pull
 * information about the currently chosen model.  It also uses the
 * <code>action</code> attribute to convey the user's package
 * choices.</p></li>
 *
 * <li><p>The "confirmChoices" page uses value binding expressions to
 * pull the user's choices from the currently chosen model.</p></li>
 *
 * </ul>
 */
public class LocaleBean extends AbstractBean {
    /**
     * <p>The locales to be selected for each hotspot, keyed by the
     * alternate text for that area.</p>
     */
    private Map locales = null;

    public LocaleBean() {
        locales = new HashMap();
        locales.put("NAmerica", new Locale("en", "US"));
        locales.put("SAmerica", new Locale("es", "MX"));
        locales.put("Germany", new Locale("de", "DE"));
        locales.put("France", new Locale("fr", "FR"));
    }

    // 
    // ActionListener handlers
    //
    public void chooseLocaleFromMap(ActionEvent actionEvent) {
        AreaSelectedEvent event = (AreaSelectedEvent) actionEvent;
        String current = event.getMapComponent()
                              .getCurrent();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getViewRoot()
               .setLocale((Locale) locales.get(current));
    }

    public void chooseLocaleFromLink(ActionEvent event) {
        String current = event.getComponent()
                              .getId();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getViewRoot()
               .setLocale((Locale) locales.get(current));
    }
}
