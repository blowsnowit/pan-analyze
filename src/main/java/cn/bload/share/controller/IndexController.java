package cn.bload.share.controller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bload.share.exception.MyRuntimeException;
import cn.bload.share.model.PanTree;
import cn.bload.share.utils.BaiduPan;
import cn.bload.share.utils.SpringUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/2/19 10:11
 * @describe 类描述:
 */
@Controller
public class IndexController {
    private Map<String, List<PanTree>> workMap = new HashMap<>();

    private AtomicInteger count = new AtomicInteger(0);

    @RequestMapping({"index",""})
    public String index(){
        return "index";
    }

    @RequestMapping("tree")
    public String tree(String key, Model model){
        if (!workMap.containsKey(key)){
            model.addAttribute("tip","解析的链接不存在/未完成");
            return "index";
        }
        BaiduPan baiduPan = new BaiduPan(null, null);
        baiduPan.setTrees(workMap.get(key));

        model.addAttribute("treeStr",baiduPan.getTreeStr());
        model.addAttribute("treeJson", JSONUtil.toJsonStr(baiduPan.getTree()));

        return "tree";
    }

    @Async("taskExecutor")
    void doTree(String key, String url, List<HttpCookie> cookies){
        BaiduPan baiduPan = new BaiduPan(url, null);
        baiduPan.setCookies(cookies);
        List<PanTree> tree = baiduPan.getTree();

        workMap.put(key,tree);

        count.decrementAndGet();
    }

    //TODO 考虑限制同ip提交次数/频率
    //或者同 url 直接返回缓存的等等...
    @RequestMapping("add")
    @ResponseBody
    public Map add (String url, String password){
        Map<String,Object> map = new HashMap<>();

        BaiduPan baiduPan = null;
        try {
            baiduPan = new BaiduPan(url, password);
            baiduPan.init();
        }catch (MyRuntimeException e){
            map.put("code",0);
            map.put("message",e.getMessage());
            return map;
        }catch (Exception e){
            map.put("code",0);
            map.put("message","解析链接失败");
            return map;
        }

        //随机生成key，用于排队等待
        String key = UUID.randomUUID().toString();

        //加入执行队列
        IndexController bean = SpringUtil.getBean(IndexController.class);
        bean.doTree(key,url,baiduPan.getCookies());

        map.put("num",count.getAndIncrement());
        map.put("code",1);
        map.put("key",key);
        return map;
    }

    @RequestMapping("check")
    @ResponseBody
    public Map check(String key){
        Map<String,Object> map = new HashMap<>();
        if (workMap.containsKey(key)){
            map.put("code",1);
        }else{
            map.put("code",0);
        }
        return map;
    }


}
