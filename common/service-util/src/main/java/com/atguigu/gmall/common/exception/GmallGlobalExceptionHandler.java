package com.atguigu.gmall.common.exception;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @ResponseBody
// @ControllerAdvice // 异常处理器
@RestControllerAdvice //
public class GmallGlobalExceptionHandler {


    /**
     * 处理业务异常
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(GmallException.class)
    public Result handleBizException(GmallException exception) {

        Result<Object> fail = Result.fail();
        fail.setCode(exception.getCode());
        fail.setMessage(exception.getMessage());
        return fail;
    }

    /**
     *
     * @param throwable
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public Result exception(Throwable throwable) {
        Result<Object> fail = Result.fail();
        fail.setMessage(throwable.getMessage());
        return fail;
    }
}
