package com.itplace.userapi;

import com.itplace.userapi.map.entity.Store;
import com.itplace.userapi.map.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class TestController {

    private final StoreRepository storeRepository;

    @GetMapping("/test")
    public ResponseEntity<Store> test() {
        Store result = storeRepository.findByStoreName("거창점굽네치킨");
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
