package gift.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

public record OrderResponse(
    long id,
    long optionId,
    int quantity,
    LocalDateTime orderDateTime,
    String message
) {
}
