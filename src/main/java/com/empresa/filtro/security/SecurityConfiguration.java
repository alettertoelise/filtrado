package com.empresa.filtro.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration

@ConditionalOnClass(name="org.springframework.security.config.annotation.web.configuration.EnableWebSecurity")
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	public static final String WS_URI = "/api/";
	
	 @Override
	    public void configure(WebSecurity web) throws Exception {
//			if (securityEnabled) {
//				web.ignoring()
//						.antMatchers(new String[] { "/js/**", "/css/**", "/img/**", "/fonts/**","/api/**"});
//			} else {
				web.ignoring().antMatchers(new String[] { "/**" });
//			}
	      }

	    
	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
//	    	if (securityEnabled) {
//		        http
//		               .cors().and().csrf().disable()
//		               .authorizeRequests()
//		               .antMatchers(SecurityConfiguration.WS_URI + "**").permitAll()
//		               .antMatchers("/js/**","/css/**","/img/**","/fonts/**").permitAll()
//		               .anyRequest().authenticated()                                  
//		               .and().exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
//		        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//		                    .and().addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class)
//		                    .addFilterBefore(exceptionHandlerFilter, LogoutFilter.class);
//	    	}
//	    	else {
	    		http
	            .authorizeRequests()
	            .antMatchers("/**").permitAll();                                  
//	    	}
	    	
	    }
}
