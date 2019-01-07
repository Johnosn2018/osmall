package com.osmall.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Johnson on 2018/4/14.
 */
public interface IFileService {

    String upload(MultipartFile file, String path);


}
