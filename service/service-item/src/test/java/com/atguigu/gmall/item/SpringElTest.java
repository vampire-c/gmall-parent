package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.UUID;

// @SpringBootTest
public class SpringElTest {

    @Test
    public void test() {
        SpelExpressionParser parser = new SpelExpressionParser();
        String uuid = UUID.randomUUID().toString();
        String person = "new com.atguigu.gmall.product.entity.SkuInfo()";
        Expression expression = parser.parseExpression(person);
        System.out.println(expression.getValue());
    }


    @Test
    public void test2() {
        String ex = "sku:info:#{1+1}:#{T(java.util.UUID).randomUUID().toString()}";
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(ex, ParserContext.TEMPLATE_EXPRESSION);
        System.out.println(expression.getValue());
    }

    @Test
    public void test3() {
        String ex = "sku:info:#{#params[2]}";
        SpelExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression(ex, ParserContext.TEMPLATE_EXPRESSION);
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("params", new Long[]{1L, 2L, 3L});
        System.out.println(expression.getValue(context, String.class));
    }

    @Test
    public void test4() {

    }
}
