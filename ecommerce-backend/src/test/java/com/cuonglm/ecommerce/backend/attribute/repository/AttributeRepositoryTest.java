package com.cuonglm.ecommerce.backend.attribute.repository;

import com.cuonglm.ecommerce.backend.attribute.projection.AttributeOptionInfoView;
import com.cuonglm.ecommerce.backend.attribute.entity.Attribute;
import com.cuonglm.ecommerce.backend.attribute.entity.AttributeOption;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeScope;
import com.cuonglm.ecommerce.backend.attribute.enums.AttributeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Sử dụng DB thực tế (PostgreSQL) hoặc H2 tùy cấu hình
class AttributeOptionRepositoryTest {

    @Autowired
    private AttributeRepository attributeRepository;

    @Autowired
    private AttributeOptionRepository attributeOptionRepository;

    @Test
    @DisplayName("Test Interface Projection lấy thành công attributeName từ Attribute cha")
    void testFindAllByIdIn_ShouldReturnAttributeNameCorrectly() {
        // 1. ARRANGE: Tạo mẫu Attribute cha (Màu sắc)
        Attribute attribute = new Attribute();
        attribute.setName("Màu sắc");
        attribute.setCode("COLOR");
        attribute.setScope(AttributeScope.GLOBAL);
        attribute.setStatus(AttributeStatus.ACTIVE);
        Attribute savedAttribute = attributeRepository.save(attribute);

        // 2. ARRANGE: Tạo mẫu AttributeOption con (Đỏ)
        UUID optionId = UUID.randomUUID();
        AttributeOption option = new AttributeOption();
        option.setId(optionId);
        option.setAttribute(savedAttribute);
        option.setValue("Đỏ");
        option.setScope(AttributeScope.GLOBAL);
        option.setStatus(AttributeStatus.ACTIVE);
        attributeOptionRepository.save(option);

        // Flush dữ liệu xuống DB trước khi test query projection
        attributeOptionRepository.flush();

        // 3. ACT: Chạy hàm projection
        List<AttributeOptionInfoView> views = attributeOptionRepository.findAllByIdIn(List.of(optionId));

        // 4. ASSERT: Kiểm tra kết quả trả về
        assertThat(views).isNotEmpty();

        AttributeOptionInfoView result = views.get(0);

        // In trực tiếp ra console xem thử
        System.out.println("=========================================");
        System.out.println("Option ID      : " + result.getId());
        System.out.println("Option Value   : " + result.getValue());
        System.out.println("Attribute ID   : " + result.getAttributeId());
        System.out.println("Attribute Name : " + result.getAttributeName());
        System.out.println("=========================================");

        // Verify kết quả đúng như kỳ vọng
        assertThat(result.getValue()).isEqualTo("Đỏ");
        assertThat(result.getAttributeName()).isEqualTo("Màu sắc");
    }
}