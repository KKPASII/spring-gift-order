package gift.order.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long optionId;

    private int quantity;

    private LocalDateTime orderDateTime;
    
    private String message;

    protected Order() {}

    public Order(Long optionId, int quantity, LocalDateTime orderDateTime, String message) {
        this.optionId = optionId;
        this.quantity = quantity;
        this.orderDateTime = orderDateTime;
        this.message = message;
    }

    public Order(Long id, Long optionId, int quantity, LocalDateTime orderDateTime, String message) {
        this.id = id;
        this.optionId = optionId;
        this.quantity = quantity;
        this.orderDateTime = orderDateTime;
        this.message = message;
    }

    public Long getId() {
        return this.id;
    }

    public Long getOptionId() {
        return this.optionId;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public LocalDateTime getOrderDateTime() {
        return this.orderDateTime;
    }

    public String getMessage() {
        return this.message;
    }
}
