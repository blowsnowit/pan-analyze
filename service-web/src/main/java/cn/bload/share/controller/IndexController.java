package cn.bload.share.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bload.share.model.PanTree;
import cn.bload.share.service.ShareService;
import cn.bload.share.utils.PanUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/2/19 10:11
 * @describe 类描述:
 */
@Controller
public class IndexController {

    @Autowired
    ShareService shareService;

    @RequestMapping({"index",""})
    public String index(){
        return "index";
    }

    @RequestMapping("tree")
    public String tree(String key, Model model){
        if (!shareService.checkKey(key)){
            model.addAttribute("tip","解析的链接不存在/未完成");
            return "index";
        }

        List<PanTree> tree = shareService.getTrees(key);
        model.addAttribute("treeStr", PanUtil.getTreeStr(tree));
        model.addAttribute("treeJson", JSONUtil.toJsonStr(tree));

        return "tree";
    }


    @RequestMapping("add")
    @ResponseBody
    public Map add(String url, String password){
        //检查一下是否存在
        Map<String,Object> map = new HashMap<>();
        if (shareService.checkKey(shareService.getKey(url))){
            map.put("code",2);
            map.put("key",shareService.getKey(url));
            return map;
        }

        String key = shareService.add(url, password);

        map.put("num",shareService.getCount());
        map.put("code",1);
        map.put("key",key);
        return map;
    }

    @RequestMapping("check")
    @ResponseBody
    public Map check(String key){
        Map<String,Object> map = new HashMap<>();
        String err;
        if ((err = shareService.checkKeyErr(key)) != null){
            map.put("code",-1);
            map.put("message",err);
        }
        if (shareService.checkKey(key)){
            map.put("code",1);
        }else{
            map.put("code",0);
        }
        return map;
    }
}
