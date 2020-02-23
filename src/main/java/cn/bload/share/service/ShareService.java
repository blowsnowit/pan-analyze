package cn.bload.share.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.net.HttpCookie;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import cn.bload.share.annotation.Cache;
import cn.bload.share.annotation.Limit;
import cn.bload.share.exception.MyRuntimeException;
import cn.bload.share.model.PanTree;
import cn.bload.share.utils.BaiduPan;
import cn.bload.share.utils.RedisOperator;
import cn.bload.share.utils.SpringUtil;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/23 下午 9:35
 * @describe 类描述:
 */
@Component
public class ShareService {
    @Autowired
    RedisOperator redisOperator;

    private AtomicInteger count = new AtomicInteger(0);

    public int getCount(){
        return count.get();
    }

    @Async("taskExecutor")
    public void doTree(String key, String url, List<HttpCookie> cookies) {
        BaiduPan baiduPan = new BaiduPan(url, null);
        baiduPan.setCookies(cookies);
        List<PanTree> tree = baiduPan.getTree();

        redisOperator.set("cache_tree_" + key,tree,259200L);

        //执行完毕数量减少
        count.decrementAndGet();
    }


    public List<PanTree> getKey(String key) {
        return (List<PanTree>) redisOperator.get("cache_tree_" + key);
    }

    public boolean checkKey(String key) {
        return redisOperator.exists("cache_tree_" + key);
    }

    @Cache(key = "'cache_url_' + #url + '_' + #password",expire = 259200)
    @Limit(key = "cache_url_limit",period = 60,count = 1)
    public String add(String url, String password) {
        BaiduPan baiduPan = null;
        try {
            baiduPan = new BaiduPan(url, password);
            if (!baiduPan.init()){
                throw new MyRuntimeException("解析链接失败");
            }
        }catch (MyRuntimeException e){
            throw e;
        }catch (Exception e){
            throw new MyRuntimeException("解析链接失败");
        }

        //随机生成key，用于排队等待
        String key = UUID.randomUUID().toString();

        //加入执行队列
        ShareService bean = SpringUtil.getBean(ShareService.class);
        bean.doTree(key,url,baiduPan.getCookies());

        count.incrementAndGet();
        return key;
    }
}
