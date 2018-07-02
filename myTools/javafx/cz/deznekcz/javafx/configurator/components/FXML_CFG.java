package cz.deznekcz.javafx.configurator.components;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cz.deznekcz.javafx.configurator.ASetup;
import cz.deznekcz.javafx.configurator.Configurator;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;

/**
 * Mean private fields or methods which is loaded by 
 * {@link ASetup#format(StringProperty, String, ReadOnlyValue...) format} 
 * function in {@link ASetup}
 * @author Zdenek Novotny (DeznekCZ)
 * @see Configurator
 * @see ASetup#format(StringProperty)
 * @see ASetup#format(StringProperty, String, ReadOnlyValue...)
 * @see ASetup#formatEach(StringProperty...)
 * @see FXML
 */
@Target(value={ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FXML_CFG {

}
