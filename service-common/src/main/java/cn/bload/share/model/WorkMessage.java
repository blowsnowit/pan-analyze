package cn.bload.share.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/27 下午 2:12
 * @describe 类描述:
 */
@Data
public class WorkMessage implements Serializable {

    private String key;

    private String url;

    private String password;

    private List<String> cookies;

    private Map<String,Object> params;

}
