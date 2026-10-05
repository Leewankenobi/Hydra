package com.example.hydra.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "plant_image")
public class PlantImageEntity { 
  // UUID
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "image_id")
  private UUID imageId;

  // PlantID
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "plant_id", nullable = false, unique = true)
  private PlantEntity plant;
  
  @Lob
  @Column(name = "image_data", nullable = false)
  private byte[] imageData;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "content_size", nullable = false)
  private Integer contentSize;

  public UUID getImageId() {
    return imageId;
  }

  public void setImageId(UUID imageId) {
    this.imageId = imageId;
  }

  public PlantEntity getPlant() {
    return plant;
  }

  public void setPlant(PlantEntity plant) {
    this.plant = plant;
  }

  public byte[] getImageData() {
    return imageData;
  }

  public void setImageData(byte[] imageData) {
    this.imageData = imageData;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Integer getContentSize() {
    return contentSize;
  }

  public void setContentSize(Integer contentSize) {
    this.contentSize = contentSize;
  }



  


}

