package cn.bload.share.utils;

import java.util.List;

import cn.bload.share.model.PanTree;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/28 下午 2:21
 * @describe 类描述:
 */
public class PanUtil {

    //获取解析树文本
    public static String getTreeStr(List<PanTree> tree){
        return doTreeStr(tree,1);
    }

    private static String doTreeStr(List<PanTree> panTrees, int level){
        String meet = "┣━━";
        String str = "";
        for (PanTree panTree : panTrees) {
            for (int i = 0; i < level - 1; i++) {
                str += "\t";
            }
            str += meet + panTree.getName() + "\n";
            if (panTree.getChildrens() != null){
                str += doTreeStr(panTree.getChildrens(),level + 1);
            }
        }
        return str;
    }
}
