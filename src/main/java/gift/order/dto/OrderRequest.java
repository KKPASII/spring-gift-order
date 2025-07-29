package gift.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

public record OrderRequest(
    long optionId,
    
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    @Max(value = 99_999_999, message = "수량은 1억개 미만이어야 합니다")
    int quantity,

    @Length(min = 1, max = 50)
    String message
) {
}
