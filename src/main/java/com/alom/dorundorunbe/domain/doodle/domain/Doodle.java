package com.alom.dorundorunbe.domain.doodle.domain;

import com.alom.dorundorunbe.global.util.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity @Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Doodle extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 32)
    private String name;

    private long goal;
}
