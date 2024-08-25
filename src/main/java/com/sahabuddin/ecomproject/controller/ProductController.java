package com.sahabuddin.ecomproject.controller;

import com.sahabuddin.ecomproject.entity.Product;
import com.sahabuddin.ecomproject.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
@Slf4j
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping("/products")
    public ResponseEntity< List<Product> >getProducts() {

        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById( @PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>( product, HttpStatus.OK);
    }

    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile imageFile) {
        log.info("Adding product {}", product);
        log.info("Image name {}", imageFile.getOriginalFilename());
        try{
            Product product1 = productService.addProduct(product, imageFile);
            return new ResponseEntity<>( product1, HttpStatus.CREATED);
        }catch (Exception e) {
            return new ResponseEntity<>( e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/product/{id}/image")
    public ResponseEntity<byte[]> getProductImageById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        byte[] imageFile = product.getImageData();
        return ResponseEntity.ok().contentType(MediaType.valueOf(product.getImageType())).body(imageFile);
    }

    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestPart Product product,
                                                @RequestPart MultipartFile imageFile) {
        try{
            Product product1 = productService.updateProduct(id, product, imageFile);
            if (product1 == null) {
                return new ResponseEntity<>("Failed to update", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Product updated", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
        }

        productService.deleteProduct(product);
        return new ResponseEntity<>("Product deleted", HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProduct(@RequestParam String keyword) {
        log.info("Searching for {}", keyword);
        List<Product> products = productService.searchProduct(keyword);
        log.info("Found {} products", products.size());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
