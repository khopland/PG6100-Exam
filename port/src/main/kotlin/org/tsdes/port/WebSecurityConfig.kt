package org.tsdes.port

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy

@Configuration
@EnableWebSecurity
class WebSecurityConfig : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.exceptionHandling().authenticationEntryPoint { _, response, _ ->
            response.setHeader("WWW-Authenticate", "cookie")
            response.sendError(401)
        }.and()
            .authorizeRequests()
            .antMatchers("/swagger*/**", "/v3/api-docs", "/actuator/**").permitAll()
            .antMatchers(HttpMethod.GET, "/api/port*/**").permitAll()
            .antMatchers(HttpMethod.HEAD, "/api/port*/**").permitAll()
            .antMatchers("/api/port*/**").hasRole("ADMIN")
            .anyRequest().denyAll()
            .and()
            .csrf().disable()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.NEVER)
    }
}

