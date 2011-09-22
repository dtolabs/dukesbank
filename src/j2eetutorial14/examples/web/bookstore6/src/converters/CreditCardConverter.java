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


package converters;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import util.MessageFactory;


/**
 * CreditCardConverter Class accepts a Credit Card Number of type String
 * and strips blanks and <oode>"-"</code> if any from it. It also formats the
 * CreditCardNumber such a blank space separates every four characters.
 * Blanks and <oode>"-"</code> characters are the expected demiliters
 * that could be used as part of a CreditCardNumber.
 */
public class CreditCardConverter implements Converter {
    /**
     * <p>The message identifier of the Message to be created if
     * the conversion fails.  The message format string for this
     * message may optionally include a <code>{0}</code> and
     * <code>{1}</code> placeholders, which
     * will be replaced by the object and value.</p>
     */
    public static final String CONVERSION_ERROR_MESSAGE_ID = "ConversionError";

    /**
     * Parses the CreditCardNumber and strips any blanks or <oode>"-"</code>
     * characters from it.
     */
    public Object getAsObject(FacesContext context, UIComponent component,
        String newValue) throws ConverterException {
        String convertedValue = null;

        if (newValue == null) {
            return newValue;
        }

        // Since this is only a String to String conversion, this conversion 
        // does not throw ConverterException.
        convertedValue = newValue.trim();

        if (((convertedValue.indexOf("-")) != -1) ||
                ((convertedValue.indexOf(" ")) != -1)) {
            char[] input = convertedValue.toCharArray();
            StringBuffer buffer = new StringBuffer(50);

            for (int i = 0; i < input.length; ++i) {
                if ((input[i] == '-') || (input[i] == ' ')) {
                    continue;
                } else {
                    buffer.append(input[i]);
                }
            }

            convertedValue = buffer.toString();
        }

        // System.out.println("Converted value " + convertedValue);
        return convertedValue;
    }

    /**
     * Formats the value by inserting space after every four characters
     * for better readability if they don't already exist. In the process
     * converts any <oode>"-"</code> characters into blanks for consistency.
     */
    public String getAsString(FacesContext context, UIComponent component,
        Object value) throws ConverterException {
        String inputVal = null;

        if (value == null) {
            return null;
        }

        // value must be of the type that can be cast to a String.
        try {
            inputVal = (String) value;
        } catch (ClassCastException ce) {
            FacesMessage errMsg =
                MessageFactory.getMessage(CONVERSION_ERROR_MESSAGE_ID,
                    (new Object[] { value, inputVal }));
            throw new ConverterException(errMsg.getSummary());
        }

        // insert spaces after every four characters for better    
        // readability if it doesn't already exist.   
        char[] input = inputVal.toCharArray();
        StringBuffer buffer = new StringBuffer(50);

        for (int i = 0; i < input.length; ++i) {
            if (((i % 4) == 0) && (i != 0)) {
                if ((input[i] != ' ') || (input[i] != '-')) {
                    buffer.append(" ");

                    // if there any "-"'s convert them to blanks.    
                } else if (input[i] == '-') {
                    buffer.append(" ");
                }
            }

            buffer.append(input[i]);
        }

        String convertedValue = buffer.toString();

        // System.out.println("Formatted value " + convertedValue);
        return convertedValue;
    }
}
