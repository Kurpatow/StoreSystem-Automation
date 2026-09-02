package model;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonProperty;

public class ProductRequest {

    private String name;
    private String description;
    private String category;
    private double price;

    @JsonProperty("stock_quantity")
    private int stockQuantity;

    @JsonProperty("supplier_id")
    private String supplierId;

    @JsonProperty("image_url")
    private String imageUrl;

    private double weight;
    private String dimensions;
    private String manufacturer;

public ProductRequest(
        String name,
        String description,
        String category,
        double price,
        int stockQuantity,
        String supplierId,
        String imageUrl,
        double weight,
        String dimensions,
        String manufacturer
) {
    this.name = name;
    this.description = description;
    this.category = category;
    this.price = price;
    this.stockQuantity = stockQuantity;
    this.supplierId = supplierId;
    this.imageUrl = imageUrl;
    this.weight = weight;
    this.dimensions = dimensions;
    this.manufacturer = manufacturer;
}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getWeight() {
        return weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getManufacturer() {
        return manufacturer;
    }
}
