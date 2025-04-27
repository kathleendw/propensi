package b02Propensi.siladu.user.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    public BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(Customizer.withDefaults())
            .authorizeHttpRequests((authorize) -> authorize
                    .requestMatchers("/registration/**").permitAll()
                    .requestMatchers("/pilih-member").permitAll()
                    .requestMatchers("/login").permitAll()
                    .requestMatchers("/tentang-kami").permitAll()
                    .requestMatchers("/js/**").permitAll()
                    .requestMatchers("/css/**").permitAll()
                    .requestMatchers("/images/**").permitAll()
                    .requestMatchers("/news/{idNews}").permitAll()
                    .requestMatchers("/").permitAll()
                    .anyRequest().authenticated()
            ).formLogin((form) -> form
                    .loginPage("/login")
                    .successHandler(customAuthenticationSuccessHandler)
                    .usernameParameter("username")
                    .permitAll()
            ).logout(
                logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login?logout")   
                    .permitAll()
            );
        return http.build();
    
    }

    @Autowired
    public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

}
