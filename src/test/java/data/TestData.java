package data;

public class TestData {

    public static String supplierRequest(String supplierName) {
        return """
        {
          "name": "%s",
          "contact_name": "Test Contact",
          "contact_email": "supplier@example.com",
          "phone_number": "+37360000000",
          "address": "Test Address",
          "country": "Moldova",
          "city": "Chisinau",
          "website": "https://example.com/"
        }
        """.formatted(supplierName);
    }

    public static String productRequest(
            String productName,
            String supplierId
    ) {
        return """
        {
          "name": "%s",
          "description": "Product created by automated test",
          "category": "TestCategory",
          "price": 100,
          "stock_quantity": 10,
          "supplier_id": "%s",
          "image_url": "https://example.com/product.jpg",
          "weight": 1.5,
          "dimensions": "10x10x10",
          "manufacturer": "TestManufacturer"
        }
        """.formatted(productName, supplierId);
    }

    public static String productRequestWithoutName(String supplierId) {
        return """
    {
      "description": "Product without name",
      "category": "TestCategory",
      "price": 100,
      "stock_quantity": 10,
      "supplier_id": "%s",
      "image_url": "https://example.com/product.jpg",
      "weight": 1.5,
      "dimensions": "10x10x10",
      "manufacturer": "TestManufacturer"
    }
    """.formatted(supplierId);
    }

    public static String updatedProductRequest(
            String productName,
            String supplierId
    ) {
        return """
    {
      "name": "%s",
      "description": "Updated product description",
      "category": "UpdatedCategory",
      "price": 200,
      "stock_quantity": 25,
      "supplier_id": "%s",
      "image_url": "https://example.com/updated-product.jpg",
      "weight": 2.5,
      "dimensions": "20x20x20",
      "manufacturer": "UpdatedManufacturer"
    }
    """.formatted(productName, supplierId);
    }
}