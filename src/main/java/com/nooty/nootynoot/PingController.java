package com.nooty.nootynoot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("/noot")
public class PingController {

    @GetMapping(path = "/ping", produces = "application/json")
    public ResponseEntity get() {
        return ResponseEntity.ok("Pong");
    }
}
