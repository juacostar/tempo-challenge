package com.tenpo.challenge.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "call")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column
    private LocalDateTime timestamp;

    @Column
    private String endpoint;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> params;

    @Column
    private String response;

    @Column
    private Boolean success;

}
