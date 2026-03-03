package com.example.provider.orders;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class OrderEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Integer productId;

  @Column(nullable = false)
  private Integer quantity;

  protected OrderEntity() {}

  public OrderEntity(Integer productId, Integer quantity) {
    this.productId = productId;
    this.quantity = quantity;
  }

  public Long getId() { return id; }
  public Integer getProductId() { return productId; }
  public Integer getQuantity() { return quantity; }
}
