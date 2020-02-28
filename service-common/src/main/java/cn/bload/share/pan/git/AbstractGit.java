package cn.bload.share.pan.git;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bload.share.exception.MyRuntimeException;
import cn.bload.share.model.PanTree;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/28 下午 3:07
 * @describe 类描述:
 */
public abstract class AbstractGit {
    /**
     * 必须覆盖接口地址
     * @return
     */
    protected abstract String getApi();

    public static AbstractGit getGit(String url){
        if (url.indexOf("gitee.com") != -1){
            return new Gitee();
        }
        if (url.indexOf("github.com") != -1){
            return new Github();
        }
        return null;
    }




    //获取trees 列表
    public List<PanTree> getTree(String url){
        // https://gitee.com/yks/Newbe.Pct
        //  https://gitee.com/yks/Newbe.Pct/tree/fork/
        // https://github.com/zxlie/FeHelper/tree/master/output

        //需要 用户名 ， 库名 ， 分支名
        Git git = new Git(url);

        //拼凑 获取目录Tree 接口地址
        String api = String.format("%s/repos/%s/%s/git/trees/%s?recursive=1", getApi(), git.owner, git.repo, git.branch);
        String body = HttpUtil.createGet(api).execute().body();
        JSONObject jsonObject = JSONUtil.parseObj(body);

        if (!jsonObject.containsKey("tree")){
            throw new MyRuntimeException("无法访问此仓库");
        }
        JSONArray tree = jsonObject.getJSONArray("tree");

        List<PanTree> panTrees = new ArrayList<>();
        Map<String,PanTree> pathPanTree = new HashMap<>();
        for (Object o : tree) {
            JSONObject object = (JSONObject)o;

            boolean isdir = "040000".equals(object.getStr("mode"));
            //解析文件名和文件目录
            String filePath = object.getStr("path");
            int lastIndexOf = filePath.lastIndexOf("/");
            if (lastIndexOf == -1) lastIndexOf = 0;
            String path = filePath.substring(0, lastIndexOf);
            String name = filePath.substring(lastIndexOf == 0 ? lastIndexOf : lastIndexOf + 1, filePath.length());

            PanTree panTree = new PanTree();
            panTree.setIsdir(isdir);
            panTree.setName(name);
            if (!isdir){
                panTree.setSize(object.getStr("size"));
            }else{
                panTree.setSize("" + 0);
            }
            panTree.setMd5(object.getStr("sha"));

            //说明是顶级目录
            if (isdir){
                pathPanTree.put(filePath,panTree);
            }
            if (path.equals("")){
                panTrees.add(panTree);
            }else{
                //找到上级目录
                PanTree parentPanTree = pathPanTree.get(path);
                if (parentPanTree.getChildrens() == null){
                    parentPanTree.setChildrens(new ArrayList<>());
                }
                parentPanTree.getChildrens().add(panTree);
            }
        }

        //排序 目录在前
        panTrees.sort((a,b)->{
            if (a.isIsdir() && b.isIsdir()){
                return 0;
            }else if (a.isIsdir()){
                return -1;
            }else if (b.isIsdir()){
                return 1;
            }
            return 0;
        });
        return panTrees;
    }


    @Data
    class Git{
        //用户名
        private String owner;
        //库名
        private String repo;
        //分支名
        private String branch;

        public Git(String url) {
            String pattern = "https?://[\\s\\S]*?/(.*?)/([a-zA-Z0-9\\._-]*)/?(tree/([a-zA-Z0-9\\._-]*)/?)?";
            Pattern r = Pattern.compile(pattern);
            // 现在创建 matcher 对象
            Matcher m = r.matcher(url);
            if (m.groupCount() == 4 && m.find()){
                owner = m.group(1);
                repo = m.group(2);
                branch = m.group(4);
                if ("".equals(branch) || branch == null){
                    branch = "master";
                }
            }else{
                throw new RuntimeException("解析失败");
            }
        }
    }

    public static void main(String[] args){
        AbstractGit gitee = new Gitee();
        List<PanTree> tree = gitee.getTree("https://gitee.com/yks/Newbe.Pct");
        System.out.println(tree);
    }
}
