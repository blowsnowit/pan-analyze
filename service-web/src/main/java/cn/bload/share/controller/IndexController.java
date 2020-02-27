package cn.bload.share.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import cn.bload.share.service.ShareService;
import cn.bload.share.utils.BaiduPan;
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
        BaiduPan baiduPan = new BaiduPan(null, null);
        baiduPan.setTrees(shareService.getKey(key));

        model.addAttribute("treeStr",baiduPan.getTreeStr());
        model.addAttribute("treeJson", JSONUtil.toJsonStr(baiduPan.getTree()));

        return "tree";
    }


    @RequestMapping("add")
    @ResponseBody
    public Map add(String url, String password){
        String key = shareService.add(url, password);

        Map<String,Object> map = new HashMap<>();
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
