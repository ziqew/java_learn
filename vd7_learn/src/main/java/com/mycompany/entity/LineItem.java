package com.mycompany.entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by ziqew on 12/5/13.
 */
@Entity
@Table(name = "line_items")
public class LineItem {
    private int id;
    private Integer productId;
    private Integer cartId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer quantity;
    private Integer orderId;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "product_id")
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Basic
    @Column(name = "cart_id")
    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    @Basic
    @Column(name = "created_at")
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Basic
    @Column(name = "updated_at")
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Basic
    @Column(name = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Basic
    @Column(name = "order_id")
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LineItem lineItem = (LineItem) o;

        if (id != lineItem.id) return false;
        if (cartId != null ? !cartId.equals(lineItem.cartId) : lineItem.cartId != null) return false;
        if (createdAt != null ? !createdAt.equals(lineItem.createdAt) : lineItem.createdAt != null) return false;
        if (orderId != null ? !orderId.equals(lineItem.orderId) : lineItem.orderId != null) return false;
        if (productId != null ? !productId.equals(lineItem.productId) : lineItem.productId != null) return false;
        if (quantity != null ? !quantity.equals(lineItem.quantity) : lineItem.quantity != null) return false;
        if (updatedAt != null ? !updatedAt.equals(lineItem.updatedAt) : lineItem.updatedAt != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (productId != null ? productId.hashCode() : 0);
        result = 31 * result + (cartId != null ? cartId.hashCode() : 0);
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0);
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (orderId != null ? orderId.hashCode() : 0);
        return result;
    }
}
