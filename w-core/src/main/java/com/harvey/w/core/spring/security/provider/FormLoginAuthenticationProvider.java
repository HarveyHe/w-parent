package com.harvey.w.core.spring.security.provider;

import com.harvey.w.core.model.UserBaseModel;
import com.harvey.w.core.service.UserBaseService;
import com.harvey.w.core.service.UserDetailsService;
import com.harvey.w.core.spring.security.EafAuthenticationDetails;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.harvey.w.core.service.BaseUserService;
import com.harvey.w.core.utils.CommonUtils;

public class FormLoginAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private UserDetailsServiceWrapper userDetailsService;
    private PasswordEncoder passwordEncoder;
    /**
     * The password used to perform
     * {@link PasswordEncoder#isPasswordValid(String, String, Object)} on when
     * the user is not found to avoid SEC-2056. This is necessary, because some
     * {@link PasswordEncoder} implementations will short circuit if the
     * password is not in a valid format.
     */
    private String userNotFoundEncodedPassword;

    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }
        if (passwordEncoder != null) {
            String presentedPassword = authentication.getCredentials().toString();

            if (!passwordEncoder.matches(presentedPassword,userDetails.getPassword())) {
                logger.debug("Authentication failed: password does not match stored value");

                throw new BadCredentialsException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
            }
        }
        UserBaseModel user = (UserBaseModel) userDetails;
        try {
            this.userDetailsService.authenticate(user, authentication);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AuthenticationServiceException(ex.getMessage());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        UserDetails loadedUser = null;
        try {
            loadedUser = this.userDetailsService.retrieveUser(username, authentication);
        } catch (UsernameNotFoundException notFound) {
            if (authentication.getCredentials() != null && passwordEncoder != null) {
                String presentedPassword = authentication.getCredentials().toString();
                passwordEncoder.matches(presentedPassword,userNotFoundEncodedPassword);
            }
            throw notFound;
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    public UserDetailsService getUserDetailsService() {
        if (this.userDetailsService instanceof UserDetailsServiceWrapper) {
            return ((UserDetailsServiceWrapper) this.userDetailsService).getUserDetailsService();
        }
        return this.userDetailsService;
    }

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        if (userDetailsService instanceof UserBaseService) {
            this.userDetailsService = new UserBaseServiceWrapper((UserBaseService) userDetailsService);
        } else if (userDetailsService instanceof BaseUserService) {
            this.userDetailsService = new BaseUserServiceWrapper((BaseUserService) userDetailsService);
        } else {
            throw new InternalAuthenticationServiceException("unsupportted UserDetailsService");
        }
    }

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        if (userDetailsService == null) {
            this.setUserDetailsService(this.applicationContext.getBean(UserDetailsService.class));
        }
        if (this.passwordEncoder != null) {
            userNotFoundEncodedPassword = this.passwordEncoder.encode("userNotFoundEncodedPassword");
        }
    }

    /*public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }*/

    public void setPasswordEncoder(Object passwordEncoder) {
        if(passwordEncoder instanceof PasswordEncoder){
            this.passwordEncoder = (PasswordEncoder)passwordEncoder;
        }else if(passwordEncoder instanceof org.springframework.security.authentication.encoding.PasswordEncoder){
            final org.springframework.security.authentication.encoding.PasswordEncoder encoder = (org.springframework.security.authentication.encoding.PasswordEncoder)passwordEncoder;
            this.passwordEncoder  = new PasswordEncoder() {
                
                @Override
                public boolean matches(CharSequence rawPassword, String encodedPassword) {
                    return encoder.isPasswordValid(encodedPassword, (String) rawPassword, null);
                }
                
                @Override
                public String encode(CharSequence rawPassword) {
                    return encoder.encodePassword((String)rawPassword, null);
                }
            };
        }
    }

    private interface UserDetailsServiceWrapper extends UserDetailsService {
        UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws UsernameNotFoundException;

        UserDetailsService getUserDetailsService();
    }

    private class UserBaseServiceWrapper implements UserDetailsServiceWrapper {
        private UserBaseService userBaseService;

        public UserBaseServiceWrapper(UserBaseService userBaseService) {
            this.userBaseService = userBaseService;
        }

        @Override
        public UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws UsernameNotFoundException {
            return userBaseService.loadUserByUsername(username);
        }

        public UserDetailsService getUserDetailsService() {
            return userBaseService;
        }

        @Override
        public void authenticate(UserBaseModel user, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
            this.userBaseService.authenticate(user, usernamePasswordAuthenticationToken);
        }

    }

    private class BaseUserServiceWrapper implements UserDetailsServiceWrapper {
        private BaseUserService baseUserService;

        public BaseUserServiceWrapper(BaseUserService baseUserService) {
            this.baseUserService = baseUserService;
        }

        @Override
        public UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws UsernameNotFoundException {
            EafAuthenticationDetails details = CommonUtils.of(authentication.getDetails(), EafAuthenticationDetails.class);
            if (details != null) {
                return baseUserService.loadUserDetials(username, details.getParameters());
            } else {
                return baseUserService.loadUserDetials(username, null);
            }
        }

        @Override
        public UserDetailsService getUserDetailsService() {
            return baseUserService;
        }

        @Override
        public void authenticate(UserBaseModel user, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
            this.baseUserService.authenticate(user, usernamePasswordAuthenticationToken);
        }
    }
}