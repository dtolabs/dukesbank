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

import javax.faces.webapp.ValidatorTag;
import javax.faces.validator.Validator;
import javax.servlet.jsp.JspException;
import validators.FormatValidator;


/**
 * FormatValidatorTag is the tag handler class for FormatValidator tag,
 * <code>format_validator</code>.
 *
 */
public class FormatValidatorTag extends ValidatorTag {
    //
    // Protected Constants
    //
    protected String formatPatterns = null;

    // Relationship Instance Variables

    //
    // Constructors and Initializers    
    //
    public FormatValidatorTag() {
        super();
        super.setValidatorId("FormatValidator");
    }

    //
    // Class methods
    //
    public String getFormatPatterns() {
        return formatPatterns;
    }

    public void setFormatPatterns(String fmtPatterns) {
        formatPatterns = fmtPatterns;
    }

    // 
    // Methods from ValidatorTag
    // 
    protected Validator createValidator() throws JspException {
        FormatValidator result = null;
        result = (FormatValidator) super.createValidator();

        result.setFormatPatterns(formatPatterns);

        return result;
    }
} // end of class FormatValidatorTag
