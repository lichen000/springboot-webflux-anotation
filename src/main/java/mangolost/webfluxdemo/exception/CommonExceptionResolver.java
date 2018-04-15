package mangolost.webfluxdemo.exception;

import mangolost.webfluxdemo.common.CommonMessage;
import mangolost.webfluxdemo.common.CommonResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebInputException;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by mangolost on 2017-04-13
 */
@ControllerAdvice
//public class CommonExceptionResolver implements HandlerExceptionResolver {
public class CommonExceptionResolver {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommonExceptionResolver.class);

    @Value("${system.runmode}")
    private String runMode;

    /**
     * @param ex
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public CommonResult resolveException(Exception ex) {

        int code = 200;
        String message = "OK";

        try {

            String exMsg = "";
            if ("dev".equals(runMode) || "uat".equals(runMode) || "stg".equals(runMode)) {
                exMsg = ": " + ex.getMessage();
            }

            //根据异常类型返回对应的code与message
            if (ex instanceof ServerWebInputException
                    ||ex instanceof MethodArgumentTypeMismatchException
                    || ex instanceof NumberFormatException
                    || ex instanceof BindException
                    || ex instanceof ConstraintViolationException) {
                code = 430;
                message = CommonMessage.PARAM_ERROR + exMsg;
            } else if (ex instanceof ServiceException) {
                // 自定义异常
                ServiceException se = (ServiceException) ex;
                if (se.getCode() != 0) {
                    code = se.getCode();
                }
                if (se.getMessage() != null) {
                    message = se.getMessage();
                }
            } else {

                code = 500;
                if ("dev".equals(runMode) || "uat".equals(runMode) || "stg".equals(runMode)) {
                    exMsg += "\r\n" + printStackTraceToString(ex);
                }
                message = CommonMessage.INTERNAL_SERVER_ERROR + exMsg;
                LOGGER.error("服务器内部异常,api:{}.{}", ex);
            }
        } catch (Exception e) {
            LOGGER.warn("Handling of [" + ex.getClass().getName() + "] resulted in Exception", e);
        }

        return buildCommonResult(code, message);

    }

    /**
     * @param code
     * @param message
     * @return
     */
    // 返回结果处理, JSON字符串化的CommonResult对象
    private CommonResult buildCommonResult(Integer code, String message) {
        CommonResult commonResult = new CommonResult();
        commonResult.setCode(code);
        commonResult.setMessage(message);
        commonResult.setTs(System.currentTimeMillis());
        commonResult.setData(null);
        return commonResult;
    }

    /**
     *
     * @param t
     * @return
     */
    private static String printStackTraceToString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return sw.getBuffer().toString();
    }

}