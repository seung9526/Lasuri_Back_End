package com.suutich.systems.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.*;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        })
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일을 입력하세요.")
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    @Size(max = 20)
    private String password;

    @NotBlank(message = "이름을 입력하세요.")
    @Size(max = 20)
    private String name;

    @Size(max = 120)
    private String address;

    @Enumerated(EnumType.STRING)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Builder
    public User(String email, String password, String name, String address){
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
    }


    // 사용자 id 반환
    @Override
    public String getUsername() {
        return email;
    }

    // 사용자 패스워드 반환
    @Override
    public String getPassword(){
        return password;
    }

    // 계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true; // 만료 되지 않음
    }

    // 계정 잠금 여부
    @Override
    public boolean isAccountNonLocked() {
        return true; // 잠금 되지 않음
    }

    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 만료 되지 않음
    }

    // 계정 사용 가능지 확인
    @Override
    public boolean isEnabled() {
        return true; // 사용 가능
    }
}