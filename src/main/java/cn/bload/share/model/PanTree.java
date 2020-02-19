package cn.bload.share.model;

import java.util.List;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/2/19 13:01
 * @describe 类描述:
 */
@Data
public class PanTree{
    private String name;
    private String size;
    private String md5;

    private String localCtime;
    private String localMtime;

    private String serverCtime;
    private String serverMtime;

    private List<PanTree> childrens;

    public PanTree(JSONObject o) {
        this.setName(o.get("server_filename").toString());
        this.setSize(o.get("size").toString());
        if (o.get("md5") != null){
            this.setMd5(o.get("md5").toString());
        }
        this.setLocalCtime(o.get("local_ctime").toString());
        this.setLocalMtime(o.get("local_mtime").toString());
        this.setServerCtime(o.get("server_ctime").toString());
        this.setServerMtime(o.get("server_mtime").toString());
    }
}
