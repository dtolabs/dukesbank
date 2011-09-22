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

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import java.util.Locale;


/**
 * <B>Util</B> is a class which houses common functionality used by
 * other classes.
 *
   version $Id: Util.java,v 1.3.2.1 2005/11/23 07:20:28 jenball Exp $
 */
public class Util extends Object {
    //
    // Protected Constants
    //

    //
    // Class Variables
    //

    /**
     * This array contains attributes that have a boolean value in JSP,
     * but have have no value in HTML.  For example "disabled" or
     * "readonly". <P>
     *
       see renderBooleanPassthruAttributes
     */
    private static String[] booleanPassthruAttributes =
        { "disabled", "readonly", "ismap" };

    /**
     * This array contains attributes whose value is just rendered
     * straight to the content.  This array should only contain
     * attributes that require no interpretation by the Renderer.  If an
     * attribute requires interpretation by a Renderer, it should be
     * removed from this array.<P>
     *
       see renderPassthruAttributes
     */
    private static String[] passthruAttributes =
        {
            "accesskey", "alt", "cols", "height", "lang", "longdesc",
            "maxlength", "onblur", "onchange", "onclick", "ondblclick",
            "onfocus", "onkeydown", "onkeypress", "onkeyup", "onload",
            "onmousedown", "onmousemove", "onmouseout", "onmouseover",
            "onmouseup", "onreset", "onselect", "onsubmit", "onunload", "rows",
            "size", "tabindex", //"class",   PENDING(rlubke)  revisit this for JSFA105
            "title", "style", "width", "dir", "rules", "frame", "border",
            "cellspacing", "cellpadding", "summary", "bgcolor", "usemap",
            "enctype", "accept-charset", "accept", "target", "onsubmit",
            "onreset"
        };
    private static long id = 0;

    //
    // Instance Variables
    //

    // Attribute Instance Variables

    // Relationship Instance Variables

    //
    // Constructors and Initializers    
    //
    private Util() {
        throw new IllegalStateException();
    }

    //
    // Class methods
    //
    public static Class loadClass(String name) throws ClassNotFoundException {
        ClassLoader loader = Thread.currentThread()
                                   .getContextClassLoader();

        if (loader == null) {
            return Class.forName(name);
        } else {
            return loader.loadClass(name);
        }
    }

    /**
     * Generate a new identifier currently used to uniquely identify
     * components.
     */
    public static synchronized String generateId() {
        if (id == Long.MAX_VALUE) {
            id = 0;
        } else {
            id++;
        }

        return Long.toHexString(id);
    }

    /**
     * Return a Locale instance using the following algorithm: <P>
     *
     * <UL>
     *
     * <LI>
     *
     * If this component instance has an attribute named "bundle",
     * interpret it as a model reference to a LocalizationContext
     * instance accessible via FacesContext.getModelValue().
     *
     * </LI>
     *
     * <LI>
     *
     * If FacesContext.getModelValue() returns a LocalizationContext
     * instance, return its Locale.
     *
     * </LI>
     *
     * <LI>
     *
     * If FacesContext.getModelValue() doesn't return a
     * LocalizationContext, return the FacesContext's Locale.
     *
     * </LI>
     *
     * </UL>
     */
    public static Locale getLocaleFromContextOrComponent(FacesContext context,
        UIComponent component) {
        Locale result = null;
        String bundleName = null;
        String bundleAttr = "bundle";

        //	ParameterCheck.nonNull(context);
        //	ParameterCheck.nonNull(component);

        // verify our component has the proper attributes for bundle.
        if (null != (bundleName = (String) component.getAttributes()
                                                        .get(bundleAttr))) {
            // verify there is a Locale for this modelReference
            javax.servlet.jsp.jstl.fmt.LocalizationContext locCtx = null;

            if (null != (locCtx = (javax.servlet.jsp.jstl.fmt.LocalizationContext) (Util.getValueBinding(
                            bundleName)).getValue(context))) {
                result = locCtx.getLocale();

                //		Assert.assert_it(null != result);
            }
        }

        if (null == result) {
            result = context.getViewRoot()
                            .getLocale();
        }

        return result;
    }

    /**
     * Render any boolean "passthru" attributes.
     * <P>
     *
       see passthruAttributes
     */
    public static String renderBooleanPassthruAttributes(FacesContext context,
        UIComponent component) {
        int i = 0;
        int len = booleanPassthruAttributes.length;
        String value;
        boolean thisIsTheFirstAppend = true;
        StringBuffer renderedText = new StringBuffer();

        for (i = 0; i < len; i++) {
            if (null != (value = (String) component.getAttributes()
                                                       .get(booleanPassthruAttributes[i]))) {
                if (thisIsTheFirstAppend) {
                    // prepend ' '
                    renderedText.append(' ');
                    thisIsTheFirstAppend = false;
                }

                if (Boolean.valueOf(value)
                               .booleanValue()) {
                    renderedText.append(booleanPassthruAttributes[i] + ' ');
                }
            }
        }

        return renderedText.toString();
    }

    /**
     * Render any "passthru" attributes, where we simply just output the
     * raw name and value of the attribute.  This method is aware of the
     * set of HTML4 attributes that fall into this bucket.  Examples are
     * all the javascript attributes, alt, rows, cols, etc.  <P>
     *
       return the rendererd attributes as specified in the component.
     *         Padded with leading and trailing ' '.  If there are no passthru
     *         attributes in the component, return the empty String.
     *
       see passthruAttributes
     */
    public static String renderPassthruAttributes(FacesContext context,
        UIComponent component) {
        int i = 0;
        int len = passthruAttributes.length;
        String value;
        boolean thisIsTheFirstAppend = true;
        StringBuffer renderedText = new StringBuffer();

        for (i = 0; i < len; i++) {
            if (null != (value = (String) component.getAttributes()
                                                       .get(passthruAttributes[i]))) {
                if (thisIsTheFirstAppend) {
                    // prepend ' '
                    renderedText.append(' ');
                    thisIsTheFirstAppend = false;
                }

                renderedText.append(passthruAttributes[i] + "=\"" + value +
                    "\" ");
            }
        }

        return renderedText.toString();
    }

    public static ValueBinding getValueBinding(String valueRef) {
        ApplicationFactory af =
            (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        Application a = af.getApplication();

        return (a.createValueBinding(valueRef));
    }

    public static MethodBinding createConstantMethodBinding(String outcome) {
        return new ConstantMethodBinding(outcome);
    }

    //
    // General Methods
    //
} // end of class Util
