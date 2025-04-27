package b02Propensi.siladu.user.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import b02Propensi.siladu.user.model.UserModel;
import b02Propensi.siladu.user.repository.UserDb;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
    @Autowired
    private UserDb userDb;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> userOptional = userDb.findByEmail(username);
        
        if(!userOptional.isPresent())
            throw new UsernameNotFoundException("Email " + username + " tidak ditemukan!");
        UserModel user = userOptional.get();
        
        if (!user.getIsActive()){
            throw new UnsupportedOperationException("Pengguna dengan email " + username + " tidak aktif!");
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), grantedAuthorities);

    }

}
    
