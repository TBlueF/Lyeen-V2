package de.craftednature.lyeen.modules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation marks a field of a {@link Module} which this {@link Module} uses.<br>
 * The instance of the used {@link Module} will be injected <b>after</b> this {@link Module} is <b>initiailzed and loaded</b>.<br>
 * <br>
 * If the used {@link Module} can't be initialized, loaded or does not exist, the field will be <code>null</code>.<br>
 * <br>
 * <b>Fields with this annotation can always be set to <code>null</code>!
 */
@Target(ElementType.FIELD)
public @interface Uses {}
