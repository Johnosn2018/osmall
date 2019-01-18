package com.osmall.web.controller.elasticsearch;

import com.osmall.common.ServiceResponse;
import com.osmall.elasticsearch.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 */
@Controller
@RequestMapping("/es/")
public class ElasticsearchController {

    @Autowired
    private SearchService searchService;


    @RequestMapping(value="test.do",method= RequestMethod.GET)
    @ResponseBody
    public void test(){
        System.out.println("************************ test **********************");
    }

    @RequestMapping(value="importAll.do",method= RequestMethod.POST)
    @ResponseBody
    public void importAllList(){
        int count = searchService.importAll();
        System.out.println("*************count="+count);
    }
}
