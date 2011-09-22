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


package taglib;

import components.MapComponent;
import renderers.Util;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.webapp.UIComponentTag;


/**
 * <p>{@link UIComponentTag} for an image map.</p>
 */
public class MapTag extends UIComponentTag {
    private String current = null;
    private String actionListener = null;
    private String action = null;
    private String immediate = null;
    private String styleClass = null;

    public void setCurrent(String current) {
        this.current = current;
    }

    public void setActionListener(String actionListener) {
        this.actionListener = actionListener;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setImmediate(String immediate) {
        this.immediate = immediate;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getComponentType() {
        return ("DemoMap");
    }

    public String getRendererType() {
        return ("DemoMap");
    }

    public void release() {
        super.release();
        current = null;
        styleClass = null;
        actionListener = null;
        action = null;
        immediate = null;
        styleClass = null;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        MapComponent map = (MapComponent) component;

        if (styleClass != null) {
            if (isValueReference(styleClass)) {
                ValueBinding vb =
                    FacesContext.getCurrentInstance()
                                .getApplication()
                                .createValueBinding(styleClass);
                map.setValueBinding("styleClass", vb);
            } else {
                map.getAttributes()
                   .put("styleClass", styleClass);
            }
        }

        if (actionListener != null) {
            if (isValueReference(actionListener)) {
                Class[] args = { ActionEvent.class };
                MethodBinding mb =
                    FacesContext.getCurrentInstance()
                                .getApplication()
                                .createMethodBinding(actionListener, args);
                map.setActionListener(mb);
            } else {
                Object[] params = { actionListener };
                throw new javax.faces.FacesException();
            }
        }

        if (action != null) {
            if (isValueReference(action)) {
                MethodBinding vb =
                    FacesContext.getCurrentInstance()
                                .getApplication()
                                .createMethodBinding(action, null);
                map.setAction(vb);
            } else {
                map.setAction(Util.createConstantMethodBinding(action));
            }
        }

        if (immediate != null) {
            if (isValueReference(immediate)) {
                ValueBinding vb =
                    FacesContext.getCurrentInstance()
                                .getApplication()
                                .createValueBinding(immediate);
                map.setValueBinding("immediate", vb);
            } else {
                boolean _immediate = new Boolean(immediate).booleanValue();
                map.setImmediate(_immediate);
            }
        }
    }
}
