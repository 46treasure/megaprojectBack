package okten.megaproject.Configurations;

import okten.megaproject.Service.UserDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;



@Configuration
public class Security extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailServiceImpl userDetailServiceImpl;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("pass")).roles("ADMIN");
        auth.userDetailsService(userDetailServiceImpl);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/topTen").permitAll()
                .antMatchers(HttpMethod.POST,"/addfilm").permitAll()
                .antMatchers(HttpMethod.GET,"/getAllUsers").permitAll()
                .antMatchers(HttpMethod.POST,"/getbyid").permitAll()
                .antMatchers(HttpMethod.POST,"/delfilm").permitAll()
                .antMatchers(HttpMethod.POST,"/findByGenre").permitAll()
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers(HttpMethod.POST, "/reg").permitAll()
                .antMatchers(HttpMethod.POST, "/search").permitAll()
                .antMatchers(HttpMethod.POST, "/getUserById").permitAll()
                .antMatchers(HttpMethod.POST, "/findSearchingUser").permitAll()
                .antMatchers(HttpMethod.POST, "/currentPage").permitAll()
                .antMatchers(HttpMethod.POST, "/subscribe").permitAll()
                .antMatchers(HttpMethod.POST, "/unSubscribe").permitAll()
                .antMatchers(HttpMethod.POST, "/exist").permitAll()
                .antMatchers(HttpMethod.POST, "/getSubscribers").permitAll()
                .antMatchers(HttpMethod.POST, "/getFolowing").permitAll()
                .antMatchers(HttpMethod.POST, "/rating").permitAll()
                .antMatchers("/get").authenticated()
                .antMatchers(HttpMethod.GET,"/close").permitAll()
                .antMatchers(HttpMethod.POST,"/adduserfilm").authenticated()
                .antMatchers(HttpMethod.POST,"/userpage-userfilms").authenticated()
                .antMatchers(HttpMethod.POST,"/deluserfilms").permitAll()
                .antMatchers(HttpMethod.POST,"/setAvatar").permitAll()
                .antMatchers(HttpMethod.POST,"/setStatus").permitAll()
                .antMatchers(HttpMethod.GET,"/finishReg/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(new RequestProcessingJWTFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class);

    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.HEAD.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()));
        corsConfiguration.addExposedHeader("Authorization");
        corsConfiguration.addExposedHeader("CurrentUser");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();

    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailServiceImpl);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}

