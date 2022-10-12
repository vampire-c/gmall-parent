package com.atguigu.gmall.cache.aspect;


import com.atguigu.gmall.cache.annotation.MallCache;
import com.atguigu.gmall.cache.constant.Constant;
import com.atguigu.gmall.cache.service.CacheOpsService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Component
@Aspect // 声明切面
public class MallCacheAspect {

    @Autowired
    private CacheOpsService cacheOpsService;

    @Autowired
    private RedissonClient redissonClient;

    SpelExpressionParser parser = new SpelExpressionParser();

    StandardEvaluationContext context = new StandardEvaluationContext();


    // @Before(value = "execution(public com.atguigu.gmall.web.SkuDetailVo com.atguigu.gmall.item.service.impl.SkuDetailServiceImpl.getSkuDetail(Long))")
    // @Before(value = "execution(public * *.*(..))")

    // @Before("@annotation(com.atguigu.gmall.cache.annotation.MallCache)")
    // public void before() {
    //     System.out.println("前置通知");
    // }
    //
    // @After("@annotation(com.atguigu.gmall.cache.annotation.MallCache)")
    // public void after() {
    //     System.out.println("后置通知");
    // }

    @Around("@annotation(com.atguigu.gmall.cache.annotation.MallCache)")
    public Object cacheIntercept(ProceedingJoinPoint pjp) throws Throwable {
        //  获取注解
        MallCache mallCache = getAnnotationFromMethod(pjp, MallCache.class);
        String cacheKey = "";
        Object result;
        boolean lockStatus = false;

        try {
            // 获取缓存
            cacheKey = determinCacheKey(pjp);
            // 方法返回值类型
            Type methodReturnType = getMethodReturnType(pjp);
            // 查询缓存
            Object data = cacheOpsService.getCacheData(cacheKey, new TypeReference<Object>() {
                @Override
                public Type getType() {
                    return methodReturnType;
                }
            });
            // 存在缓存直接返回
            if (!StringUtils.isEmpty(data)) {
                return data;
            }

            // 缓存没有, 需要回源

            // 获取注解中的布隆key
            String bloomKey = getAnnotationFromMethod(pjp, MallCache.class).bloomKey();

            // 如果布隆过滤器存在
            if (!StringUtils.isEmpty(bloomKey)) {
                // 获取布隆过滤器
                RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(bloomKey);
                // 获取该数据在布隆过滤器的值
                Object bloomValue = calculateExpression(mallCache.bloomValue(), pjp, Object.class);
                // 问布隆过滤器是否存在该该数据
                if (!bloomFilter.contains(bloomValue)) {
                    // 布隆返回不包含,返回空
                    return null;
                }
            }

            // 是否启用分布式锁
            if (mallCache.enableLock()) {
                // 获取分布式锁
                RLock lock = redissonClient.getLock(Constant.REDIS_LOCK_PREFIX + cacheKey);
                // 加锁
                boolean b = lock.tryLock();
                if (b) {
                    lockStatus = b;
                    // 加锁成功回源
                    // 目标方法执行, 有异常抛出, 否则多切面下可能会引起切面逻辑失效
                    result = pjp.proceed(pjp.getArgs());
                    cacheOpsService.saveCacheData(cacheKey, result, mallCache.ttl(), mallCache.unit());
                    return result;
                } else {
                    // 加锁失败, 等待后再次查询缓存
                    Thread.sleep(500);
                    return cacheOpsService.getCacheData(cacheKey, new TypeReference<Object>() {
                        @Override
                        public Type getType() {
                            return methodReturnType;
                        }
                    });
                }
            } else {
                // 未启用分布式锁
                result = pjp.proceed(pjp.getArgs());
                cacheOpsService.saveCacheData(cacheKey, result, mallCache.ttl(), mallCache.unit());
                return result;
            }
        } finally {
            // 开启锁功能且加锁时才需要解锁
            if (mallCache.enableLock() && lockStatus) {
                RLock lock = redissonClient.getLock(Constant.REDIS_LOCK_PREFIX + cacheKey);
                lock.unlock();
            }
        }
    }

    private Type getMethodReturnType(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Type genericReturnType = signature.getMethod().getGenericReturnType();
        return genericReturnType;
    }

    /**
     * 决定哪个key去缓存中查询
     *
     * @param pjp
     * @return
     */
    private String determinCacheKey(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        MallCache annotation = getAnnotationFromMethod(pjp, MallCache.class);
        // 获取注解信息
        String expr = annotation.cacheKey();
        // 计算出cacheKey
        String cacheKey = calculateExpression(expr, pjp, String.class);
        return cacheKey;
    }

    /**
     * 获取方法的注解
     *
     * @param pjp
     * @return
     */
    private <T extends Annotation> T getAnnotationFromMethod(ProceedingJoinPoint pjp, Class<T> t) {
        // 获取方法上标注的完整签名
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        // 获取方法
        Method method = signature.getMethod();
        // 获取方法上标注的注解
        T annotation = method.getAnnotation(t);
        return annotation;
    }


    /**
     * 根据表达式语法动态计算出表达式的值
     *
     * @param expr
     * @param pjp
     * @param returnType
     * @param <T>
     * @return
     */
    private <T> T calculateExpression(String expr, ProceedingJoinPoint pjp, Class<T> returnType) {
        //
        Expression expression = parser.parseExpression(expr, ParserContext.TEMPLATE_EXPRESSION);
        // 计算上下文
        context.setVariable("args", pjp.getArgs());
        // 计算值
        T value = expression.getValue(context, returnType);

        return value;
    }
}
