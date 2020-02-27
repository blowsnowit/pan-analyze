package cn.bload.share.exception;

/**
 * @author 作者 : blownsow
 * @version 版本: 1.0
 * @date 创建时间 : 2020/2/19 12:27
 * @describe 类描述:
 */
public class MyRuntimeException extends RuntimeException {

    public MyRuntimeException(String message) {
        super(message);
    }
}
