package com.fly.web.restcontroller;

import com.fly.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/person")
public class RestPersonController {

    @Autowired
    PersonService personService;

    @PostMapping(value = "/all")
    public Map<String, Object> personAll(String reqObj) throws Exception {
        return personService.findAll(reqObj);
    }


}
