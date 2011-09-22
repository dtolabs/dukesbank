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


package renderers;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * <p>Convenient base class for <code>Renderer</code> implementations.</p>
 */
public abstract class BaseRenderer extends Renderer {
    public static final String BUNDLE_ATTR = "com.sun.faces.bundle";

    public String convertClientId(FacesContext context, String clientId) {
        return clientId;
    }

    protected String getKeyAndLookupInBundle(FacesContext context,
        UIComponent component, String keyAttr) throws MissingResourceException {
        String key = null;
        String bundleName = null;
        ResourceBundle bundle = null;

        key = (String) component.getAttributes()
                                .get(keyAttr);
        bundleName = (String) component.getAttributes()
                                       .get(BUNDLE_ATTR);

        // if the bundleName is null for this component, it might have
        // been set on the root component.
        if (bundleName == null) {
            UIComponent root = context.getViewRoot();

            bundleName = (String) root.getAttributes()
                                      .get(BUNDLE_ATTR);
        }

        // verify our component has the proper attributes for key and bundle.
        if ((null == key) || (null == bundleName)) {
            throw new MissingResourceException("Can't load JSTL classes",
                bundleName, key);
        }

        // verify the required Class is loadable
        // PENDING(edburns): Find a way to do this once per ServletContext.
        if (null == Thread.currentThread()
                              .getContextClassLoader()
                              .getResource("javax.servlet.jsp.jstl.fmt.LocalizationContext")) {
            Object[] params =
                { "javax.servlet.jsp.jstl.fmt.LocalizationContext" };
            throw new MissingResourceException("Can't load JSTL classes",
                bundleName, key);
        }

        // verify there is a ResourceBundle in scoped namescape.
        javax.servlet.jsp.jstl.fmt.LocalizationContext locCtx = null;

        if ((null == (locCtx = (javax.servlet.jsp.jstl.fmt.LocalizationContext) (Util.getValueBinding(
                        bundleName)).getValue(context))) ||
                (null == (bundle = locCtx.getResourceBundle()))) {
            throw new MissingResourceException("Can't load ResourceBundle ",
                bundleName, key);
        }

        return bundle.getString(key);
    }

    protected void encodeRecursive(FacesContext context, UIComponent component)
        throws IOException {
        component.encodeBegin(context);

        if (component.getRendersChildren()) {
            component.encodeChildren(context);
        } else {
            Iterator kids = component.getChildren()
                                     .iterator();

            while (kids.hasNext()) {
                UIComponent kid = (UIComponent) kids.next();
                encodeRecursive(context, kid);
            }
        }

        component.encodeEnd(context);
    }
}
