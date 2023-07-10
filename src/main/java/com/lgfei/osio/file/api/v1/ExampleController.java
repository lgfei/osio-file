package com.lgfei.osio.file.api.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/example")
public class ExampleController {

    @GetMapping("/get")
    public String get(){
        return "get request";
    }
}
