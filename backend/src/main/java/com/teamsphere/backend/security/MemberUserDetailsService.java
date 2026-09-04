package com.teamsphere.backend.security;

import com.teamsphere.backend.entity.Member;
import com.teamsphere.backend.repository.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MemberUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public MemberUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Member not found: " + email));

        UserDetails userDetails = User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole())
                .disabled(!member.getActive())
                .build();

        System.out.println("MEMBER ROLE: " + member.getRole());
        System.out.println("AUTHORITIES: " + userDetails.getAuthorities());

        return userDetails;
    }
}