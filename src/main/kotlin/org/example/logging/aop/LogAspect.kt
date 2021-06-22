package org.example.logging.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Aspect
@Component
class LogAspect {

    @Pointcut("execution(* org.example..*(..))")
    fun pointcut() {
    }

    @Before("pointcut()")
    fun before(joinPoint: JoinPoint) {
        val logger: Logger = LoggerFactory.getLogger(joinPoint.target.javaClass.name)
        logger.info("${joinPoint.signature.name} start")
    }

    @After("pointcut()")
    fun after(joinPoint: JoinPoint) {
        val logger: Logger = LoggerFactory.getLogger(joinPoint.target.javaClass.name)
        logger.info("${joinPoint.signature.name} end")
    }
}