package com.sivalabs.techtube.config

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    val publicResources =
        arrayOf(
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**",
            "/favicon.ico",
            "/actuator/health/**",
            "/actuator/info/**",
            "/error",
            "/login",
            "/register",
        )

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { requests ->
            requests
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .permitAll()
                .requestMatchers(*publicResources)
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/posts/**")
                .permitAll()
                .anyRequest()
                .authenticated()
        }
        http.csrf { c -> c.disable() }
        http.formLogin { formLogin -> formLogin.loginPage("/login").permitAll().defaultSuccessUrl("/") }
        http.logout { logout ->
            logout
                .logoutRequestMatcher(AntPathRequestMatcher("/logout"))
                .permitAll()
                .logoutSuccessUrl("/")
        }
        return http.build()
    }
}
