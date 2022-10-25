package com.atguigu.gmall.common.exception;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// @ResponseBody
// @ControllerAdvice // 异常处理器
@RestControllerAdvice //
@Slf4j
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
     * @param throwable
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public Result exception(Throwable throwable) {
        log.info("全局异常捕获...{}", throwable);
        Result<Object> fail = Result.fail();
        fail.setMessage(throwable.getMessage());
        return fail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        // 获取所有校验异常
        BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
        // 将异常存到一个map中
        Map<String, String> args = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String field = fieldError.getField();
            String message = fieldError.getDefaultMessage();
            args.put(field, message);
        }

        return Result.build(args, ResultCodeEnum.ARGS_INVALIDE);
    }
}
