package xyz.liangwh.headwaters.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.liangwh.headwaters.core.interfaces.Monitor;

import java.util.Map;


@Controller
@RequestMapping("manger")
public class HwMangerController {

    @Autowired
    Monitor headwaters;

    @RequestMapping(value = "cache")
    public String cache(Model model){
        Map map = headwaters.getInfo();
        System.out.println(map);
        model.addAttribute("data",map);
        return "headwaters";
    }


}
