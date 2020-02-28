package cn.bload.share.service;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import cn.bload.share.constant.Const;
import cn.bload.share.exception.MyRuntimeException;
import cn.bload.share.model.PanTree;
import cn.bload.share.model.WorkMessage;
import cn.bload.share.pan.AbstractPan;
import cn.bload.share.pan.PanFactory;
import cn.bload.share.utils.RedisOperator;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/23 下午 9:35
 * @describe 类描述:
 */
@Component
public class ShareService {
    @Resource
    RedisOperator redisOperator;
    @Resource
    RabbitTemplate rabbitTemplate;

    private AtomicInteger count = new AtomicInteger(0);

    public int getCount(){
        return Integer.valueOf(redisOperator.get(Const.CACHE_WORK_NUM).toString());
    }
//
//    @Async("taskExecutor")
//    public void doTree(String key, String url, List<HttpCookie> cookies) {
//        BaiduPan baiduPan = new BaiduPan(url, null);
//        baiduPan.setCookies(cookies);
//        List<PanTree> tree = baiduPan.getTree();
//
//        redisOperator.set(Const.CACHE_URL_RESULT + key,tree,Const.CACHE_URL_EXPIRE);
//
//        //执行完毕数量减少
//        count.decrementAndGet();
//    }


    public List<PanTree> getKey(String key) {
        return (List<PanTree>) redisOperator.get(Const.CACHE_URL_RESULT+ key);
    }

    public String checkKeyErr(String key) {
        if (redisOperator.exists(Const.CACHE_URL_RESULT_ERR  + key)){
            return redisOperator.get(Const.CACHE_URL_RESULT_ERR  + key).toString();
        }
        return null;
    }

    public boolean checkKey(String key) {
        return redisOperator.exists(Const.CACHE_URL_RESULT  + key);
    }

//    @Cache(key = "'" + Const.CACHE_URL + "'" + "+ #url",expire = Const.CACHE_URL_EXPIRE)
//    @Limit(key = Const.CACHE_QUERY_LIMIT,period = Const.CACHE_QUERY_LIMIT_PERIOD,count = Const.CACHE_QUERY_LIMIT_COUNT)
    public String add(String url, String password) {
        AbstractPan pan = PanFactory.getPan(url, password);
        if (pan == null){
            throw new MyRuntimeException("无法识别此类型链接");
        }
        try {
            if (!pan.init()){
                throw new MyRuntimeException("解析链接失败");
            }
        }catch (MyRuntimeException e){
            throw e;
        }catch (Exception e){
            throw new MyRuntimeException("解析链接失败");
        }

        redisOperator.increment(Const.CACHE_WORK_NUM);

        //随机生成key，用于排队等待
        String key = UUID.randomUUID().toString();

        WorkMessage workMessage = pan.getWorkMessage(key);

        //投递任务
        // 消息唯一ID
        CorrelationData correlationData = new CorrelationData(key);
        rabbitTemplate.convertAndSend("",Const.QUEUE_WORK,workMessage,correlationData);

        return key;
    }

}
