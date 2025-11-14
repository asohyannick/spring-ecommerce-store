package com.mercado.mercadoSpring.entity.auth;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
}
