package net.pvytykac.nutrition.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * Annotation to enforce user or admin realm role authorization on controller methods or classes.
 * Requires the user to have either ROLE_user or ROLE_admin authority.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyAuthority('ROLE_user', 'ROLE_admin')")
public @interface HasUserOrAdminRole {
}
