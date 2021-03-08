package com.fly.web.restcontroller;

import com.fly.common.base.pojo.ResultBean;
import com.fly.entity.Media;
import com.fly.entity.Person;
import com.fly.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
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


    @PostMapping(value = "/get")
    private Person getPerson(String id) {
        return personService.findOne(Long.parseLong(id));
    }



    /**
     *
     * @param person
     * @return
     */
    @PostMapping(value = "/save")
    private ResultBean<Person> savePerson(Person person) {

        Long id = person.getId();
        if (id == null ){
            person.setCreateDate(new Date());
        }else{
            Person p = personService.findOne(id);
            person.setFaceLogo(p.getFaceLogo());
            person.setUpdateDate(new Date());
        }

        personService.save(person);

        return new ResultBean<>(person);
    }

    /**
     *
     * @param
     * @return
     */
    @PostMapping(value = "/upload/{personId}")
    private ResultBean<Person> uploadFaceLogo(@PathVariable(name = "personId", required = true) Long personId, @RequestParam(name="faceLogo",required=false) MultipartFile faceLogo) throws IOException {
        Person person ;
        if (personId !=0){
            person = personService.findOne(personId);
            person.setUpdateDate(new Date());
        }else{
            person = new Person();
            person.setCreateDate(new Date());
            person.setUpdateDate(new Date());
        }

        if (faceLogo == null){
            person.setFaceLogo(null);
        }else {
            person.setFaceLogo(faceLogo.getBytes());
        }

        personService.save(person);

        return new ResultBean<>(person);

    }



    @PostMapping(value = "/names")
    public Map getByPersonNos(String personDoubanNos){
        return personService.getPersonNamesByDoubanNos(personDoubanNos);
    }


    @PostMapping(value = "/name")
    public Map getByPersonIds(String personIds){
        return personService.getPersonNamesByPersonIds(personIds);
    }

}
