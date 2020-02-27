package cn.bload.share.constant;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/27 下午 1:52
 * @describe 类描述:
 */
public interface Const {
    /**
     * 工作队列数量
     */
    String CACHE_WORK_NUM = "cache_work_num";


    /**
     * 查询限制时间
     */
    String CACHE_QUERY_LIMIT = "cache_query_limit";
    //查询间隔次数
    long CACHE_QUERY_LIMIT_PERIOD = 60;
    int CACHE_QUERY_LIMIT_COUNT = 1;

    /**
     * 缓存查询地址，value为 随机生成的一个key
     */
    String CACHE_URL = "cache_url_";
    long CACHE_URL_EXPIRE = 259200;

    /**
     * 缓存查询的结果
     * 需要一个key
     */
    String CACHE_URL_RESULT = "cache_url_result_";
    String CACHE_URL_RESULT_ERR = "cache_url_result_err_";

    String QUEUE_WORK = "panwork";
}
