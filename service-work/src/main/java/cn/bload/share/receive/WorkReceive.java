package cn.bload.share.receive;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.Resource;

import cn.bload.share.constant.Const;
import cn.bload.share.model.PanTree;
import cn.bload.share.model.WorkMessage;
import cn.bload.share.utils.BaiduPan;
import cn.bload.share.utils.RedisOperator;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/27 下午 4:39
 * @describe 类描述:
 */

@Component
public class WorkReceive {
    @Resource
    RedisOperator redisOperator;

    @RabbitListener(queues = Const.QUEUE_WORK)
    @RabbitHandler
    public void process(WorkMessage workMessage) {
        System.out.println("开始处理：" + workMessage.getUrl());

        List<PanTree> tree = null;

        String key = workMessage.getKey();
        try {
            BaiduPan baiduPan = new BaiduPan(workMessage.getUrl(), workMessage.getPassword());
            baiduPan.setCookies(workMessage.getCookies());
            tree = baiduPan.getTree();
        }catch (Exception e){
            //添加错误任务提示
            redisOperator.set(Const.CACHE_URL_RESULT_ERR + key,"查询失败",5 * 60L);
            //移除任务key
            redisOperator.remove(Const.CACHE_URL + workMessage.getUrl());
            e.printStackTrace();
            return;
        }


        redisOperator.set(Const.CACHE_URL_RESULT + key,tree,Const.CACHE_URL_EXPIRE);
        //执行完毕数量减少
        redisOperator.decrement(Const.CACHE_WORK_NUM);
    }
}
