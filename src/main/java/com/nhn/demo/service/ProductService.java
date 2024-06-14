package com.nhn.demo.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.nhn.demo.dto.request.product.ProductCreateRequest;
import com.nhn.demo.dto.request.product.ProductUpdateRequest;
import com.nhn.demo.dto.response.CloudinaryResponse;
import com.nhn.demo.dto.response.category.CategoryDetailResponse;
import com.nhn.demo.dto.response.product.ProductDetailResponse;
import com.nhn.demo.entity.Category;
import com.nhn.demo.entity.Product;
import com.nhn.demo.exception.NotFoundException;
import com.nhn.demo.repository.CategoryRepository;
import com.nhn.demo.repository.ProductRepository;
import com.nhn.demo.util.FileUploadUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductService {
    @Autowired
    private ProductRepository repository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private MessageSource messageSource;

    private final static String PRODUCT_NOT_FOUND_CODE = "product.not.found";

    private String getMessage(final String code, final Object... args) {
        return this.messageSource.getMessage(code, args,
                                             LocaleContextHolder.getLocale());
    }

    public List<ProductDetailResponse> findAll() {
        final List<Product> products = this.repository.findAll();
        return products.stream().map(this::buildProductDetailResponse).toList();
    }

    @Transactional
    public ProductDetailResponse findById(final Integer id) {
        final Product product = this.repository.findById(id)
                                               .orElseThrow(() -> new NotFoundException(this.getMessage(PRODUCT_NOT_FOUND_CODE)));
        return this.buildProductDetailResponse(product);
    }

    public ProductDetailResponse createProduct(final ProductCreateRequest request) {
        final Integer  categoryId = request.getCategoryId();
        final Category category   = this.categoryRepository.findById(categoryId)
                                                           .orElseThrow(() -> new NotFoundException("Category not found"));
        final Product  product    = Product.builder().build();
        BeanUtils.copyProperties(request, product);
        product.setCategory(category);
        final Product savedProduct = this.repository.save(product);
        return this.buildProductDetailResponse(savedProduct);
    }

    @Transactional
    public void uploadImage(final Integer id, final MultipartFile file) {
        final Product product = this.repository.findById(id)
                                               .orElseThrow(() -> new NotFoundException("Product not found"));
        FileUploadUtil.assertAllowed(file, FileUploadUtil.IMAGE_PATTERN);
        final String fileName = FileUploadUtil.getFileName(file.getOriginalFilename());
        final CloudinaryResponse response = this.cloudinaryService.uploadFile(file, fileName);
        product.setImageUrl(response.getUrl());
        product.setCloudinaryImageId(response.getPublicId());
        this.repository.save(product);
    }

    public ProductDetailResponse updateProduct(final Integer id,
                                               final ProductUpdateRequest request) {
        final Product product = this.repository.findById(id)
                                               .orElseThrow(() -> new NotFoundException("Product not found"));
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setDiscount(request.getDiscount());
        final Product savedProduct = this.repository.save(product);
        return this.buildProductDetailResponse(savedProduct);
    }

    public void deleteProduct(final Integer id) {
        if (this.repository.existsById(id)) {
            this.repository.deleteById(id);
        } else {
            throw new NotFoundException("Product not found");
        }
    }

    private ProductDetailResponse buildProductDetailResponse(final Product product) {
        final ProductDetailResponse response = ProductDetailResponse.builder()
                                                                    .build();
        BeanUtils.copyProperties(product, response);

        final Category               category         = product.getCategory();
        final CategoryDetailResponse categoryResponse = CategoryDetailResponse.builder()
                                                                              .build();
        BeanUtils.copyProperties(category, categoryResponse);
        response.setCategory(categoryResponse);
        return response;
    }
}
