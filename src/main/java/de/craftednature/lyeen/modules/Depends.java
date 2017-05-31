package de.craftednature.lyeen.modules;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * This annotation marks a field of a {@link Module} on which this {@link Module} depends.<br>
 * The {@link Module} will then <i>(if not changed using the annotations values)</i> only be initialized, <b>after</b> all its dependencies are initialized and loaded.<br>
 * The instance of the dependency will also be injected after the {@link Module}s construction.<br>
 * <br>
 * If the dependency can't be initialized, loaded or does not exist, this module will not be initialized.
 */
@Target(ElementType.FIELD)
public @interface Depends {}
