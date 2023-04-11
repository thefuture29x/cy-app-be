package cy.configs.jwt;

import cy.dtos.common.CustomHandleException;
import cy.services.common.CustomUserDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;

//Authentication with JWT Token
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationProvider.class);
    protected final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //Get user of authentication request
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        //Check password of request with user's password (Decrypted)
        if (!BCrypt.checkpw(authentication.getCredentials().toString(), userDetail.getPassword()))
            throw new CustomHandleException(9);
        //Check user's account status
        this.check(userDetail);
        //Return detail and token
        return authentication;
    }

    public void check(UserDetails user) {
        //Check if account is disabled
        if (!user.isEnabled()) {
            JwtAuthenticationProvider.this.logger.debug("Tài khoản chưa được kích hoạt!");
            throw new CustomHandleException(10);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        //Check if username and password authenticate can support the class
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
