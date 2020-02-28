package cn.bload.share.pan.git;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/02/28 下午 3:07
 * @describe 类描述:
 */
public class Gitee extends AbstractGit {
    @Override
    protected String getApi() {
        return "https://gitee.com/api/v5/";
    }
}
