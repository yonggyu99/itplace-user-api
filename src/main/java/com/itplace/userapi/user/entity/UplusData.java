package com.itplace.userapi.user.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.Getter;

@Getter
@Entity
@Table(name = "uplusData")
public class UplusData {

    @Id
    private Long id;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "phoneNumber", length = 11)
    private String phoneNumber;

    @Column(name = "gender", length = 5)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birthday", length = 8)
    private LocalDate birthday;

    @Column(name = "membershipId", length = 16)
    private String membershipId;
}
