package com.shadow.jse_middleware_service.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "symbol")
@Where(clause = "del_flg = 0")
public class Symbol {

    @Id
    private String id;

    @Column(name = "company_name")
    private String companyName;
}
