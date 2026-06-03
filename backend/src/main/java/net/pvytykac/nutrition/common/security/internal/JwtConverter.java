package net.pvytykac.nutrition.common.security.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class JwtConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter defaultConverter;

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);

        List<String> roles = Optional.ofNullable(jwt.getClaimAsMap("realm_access"))
                .map(realmAccess -> (List<String>) realmAccess.get("roles"))
                .orElse(Collections.emptyList());
        List<SimpleGrantedAuthority> realmRoles = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        authorities = Stream.concat(authorities.stream(), realmRoles.stream())
                .collect(Collectors.toList());

        return authorities;
    }
}
