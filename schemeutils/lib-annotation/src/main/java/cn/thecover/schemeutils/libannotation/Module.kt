package cn.thecover.lib.schemeutils.lib_annotation

/**
 * @author:likewen
 * @date:2022/9/22
 * desc:
 */
@Target(AnnotationTarget.CLASS)
annotation class Module(val name: String)

@Target(AnnotationTarget.CLASS)
annotation class Root(val name: String, val value: String)

@Target(AnnotationTarget.FUNCTION)
annotation class Action(val name: String)

@Repeatable
@Target(AnnotationTarget.FUNCTION)
annotation class Param(val name: String,val type: String)
