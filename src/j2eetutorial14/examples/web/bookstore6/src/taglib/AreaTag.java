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

import components.AreaComponent;
import renderers.Util;
import javax.faces.component.UIComponent;
import javax.faces.component.ValueHolder;
import javax.faces.webapp.UIComponentTag;


/**
 * <p>{@link UIComponentTag} for an image map hotspot.</p>
 */
public class AreaTag extends UIComponentTag {
    private String alt = null;
    private String targetImage = null;
    private String coords = null;
    private String onmouseout = null;
    private String onmouseover = null;
    private String shape = null;
    private String styleClass = null;
    private String value = null;

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setTargetImage(String targetImage) {
        this.targetImage = targetImage;
    }

    public void setCoords(String coords) {
        this.coords = coords;
    }

    public void setOnmouseout(String newonmouseout) {
        onmouseout = newonmouseout;
    }

    public void setOnmouseover(String newonmouseover) {
        onmouseover = newonmouseover;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setValue(String newValue) {
        value = newValue;
    }

    public String getComponentType() {
        return ("DemoArea");
    }

    public String getRendererType() {
        return ("DemoArea");
    }

    public void release() {
        super.release();
        this.alt = null;
        this.coords = null;
        this.onmouseout = null;
        this.onmouseover = null;
        this.shape = null;
        this.styleClass = null;
        this.value = null;
    }

    protected void setProperties(UIComponent component) {
        super.setProperties(component);

        AreaComponent area = (AreaComponent) component;

        if (alt != null) {
            if (isValueReference(alt)) {
                area.setValueBinding("alt", Util.getValueBinding(alt));
            } else {
                area.getAttributes()
                    .put("alt", alt);
            }
        }

        if (coords != null) {
            if (isValueReference(coords)) {
                area.setValueBinding("coords", Util.getValueBinding(coords));
            } else {
                area.getAttributes()
                    .put("coords", coords);
            }
        }

        if (onmouseout != null) {
            if (isValueReference(onmouseout)) {
                area.setValueBinding("onmouseout",
                    Util.getValueBinding(onmouseout));
            } else {
                area.getAttributes()
                    .put("onmouseout", onmouseout);
            }
        }

        if (onmouseover != null) {
            if (isValueReference(onmouseover)) {
                area.setValueBinding("onmouseover",
                    Util.getValueBinding(onmouseover));
            } else {
                area.getAttributes()
                    .put("onmouseover", onmouseover);
            }
        }

        if (shape != null) {
            if (isValueReference(shape)) {
                area.setValueBinding("shape", Util.getValueBinding(shape));
            } else {
                area.getAttributes()
                    .put("shape", shape);
            }
        }

        if (styleClass != null) {
            if (isValueReference(styleClass)) {
                area.setValueBinding("styleClass",
                    Util.getValueBinding(styleClass));
            } else {
                area.getAttributes()
                    .put("styleClass", styleClass);
            }
        }

        if (area instanceof ValueHolder) {
            ValueHolder valueHolder = (ValueHolder) component;

            if (value != null) {
                if (isValueReference(value)) {
                    area.setValueBinding("value", Util.getValueBinding(value));
                } else {
                    valueHolder.setValue(value);
                }
            }
        }

        // target image is required
        area.setTargetImage(targetImage);
    }
}
