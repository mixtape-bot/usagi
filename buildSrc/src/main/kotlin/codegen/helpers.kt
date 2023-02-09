package codegen

import com.squareup.kotlinpoet.*

val AMQP_OBJECT = ClassName(protocolPackage, "AMQP")

/**
 * Map of class-id to method-ids to ignore because they
 * are not to be used by the client.
 *
 * 60-71 is documented as MAY be used by the client but
 * for right now it will be ignored.
 *
 * 20-
 */
val METHODS_TO_IGNORE = mapOf(
    10 to intArrayOf(10, 20, 30, 70),
    60 to intArrayOf(
        // special implementations
        20,
        40,

        //
        50,
        60,
        71,
        72
    )
)

fun generateChannelMethodHelpers(name: String, amqpClass: AMQP.Class): FileSpec? {
    if (amqpClass.id == 10) {
        return null
    }

    val methodIgnoreList = METHODS_TO_IGNORE[amqpClass.id] ?: intArrayOf()

    val methods = amqpClass.methods.filterNot {
        it.id in methodIgnoreList
    }

    if (methods.isEmpty()) {
        return null
    }

    return FileSpec.builder("$channelPackage.method", amqpClass.normalizedName + "Methods")
        .generateClassHelper(amqpClass, methods)
        .generateClassHelperConvenienceProperty(amqpClass)
        .addImport(protocolPackage, name)
        .build()
}

fun FileSpec.Builder.generateClassHelper(amqpClass: AMQP.Class, methods: List<AMQP.Method>): FileSpec.Builder {
    val spec = TypeSpec.valueClassBuilder("${amqpClass.normalizedName}Methods")
        .addAnnotation(ClassName("kotlin.jvm", "JvmInline"))
        .addModifiers(KModifier.PUBLIC)
        .generateClassHelperMethods(amqpClass, methods)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("channel", CHANNEL)
                .build()
        )
        .addProperty(
            PropertySpec.builder("channel", CHANNEL)
                .initializer("channel")
                .build()
        )
        .build()

    return addType(spec)
}

fun TypeSpec.Builder.generateClassHelperMethods(amqpClass: AMQP.Class, methods: List<AMQP.Method>): TypeSpec.Builder {
    val classContainer = AMQP_OBJECT.nestedClass(amqpClass.normalizedName)
    
    for (method in methods) {
        if (method.name.endsWith("-ok")) {
            /* methods ending with -ok are sent by the client as responses. */
            continue
        }

        var builderMethodSpec: FunSpec.Builder? = null

        val name = method.normalizedName.decapitalize()
        val methodClass = classContainer.nestedClass(method.normalizedName)
        if (method.arguments.isNotEmpty()) {
            /* e.g. publish(block: AMQP.Basic.Publish.() -> Unit) */
            val builder = methodClass.nestedClass("Builder")
            builderMethodSpec = FunSpec.builder(name)
                .addModifiers(KModifier.PUBLIC, KModifier.SUSPEND)
                .addParameter("block", LambdaTypeName.get(builder, returnType = UNIT))
                .addCode("""|return $name(%T().apply(block).build())""".trimMargin(), builder)
        }

        /* e.g. publish(method: AMQP.Basic.Publish) */
        val rawMethodSpec = FunSpec.builder(name)
            .addModifiers(KModifier.PUBLIC, KModifier.SUSPEND)

        if (method.arguments.isNotEmpty()) {
            rawMethodSpec.addParameter("method", methodClass)
        } else {
            rawMethodSpec.addCode("val method = %T\n", methodClass)
        }

        if (method.synchronous) {
            val response = classContainer.nestedClass("${method.normalizedName}Ok")

            if (method.arguments.any { it.name == "no-wait" }) {
                rawMethodSpec.addCode(
                    """|if (method.nowait) {
                       |  channel.send(method)
                       |  return null
                       |}\n""".trimIndent()
                )

                val returnType = response.copy(nullable = true)
                rawMethodSpec.returns(returnType)
                builderMethodSpec?.returns(returnType)
            } else {
                rawMethodSpec.returns(response)
                builderMethodSpec?.returns(response)
            }

            rawMethodSpec.addCode(
                """|val ok = channel.rpc(method)
                   |require(ok.method is %T) { 
                   |  "Expected '${amqpClass.name}.${method.name}-ok', not ${"$"}{ok.method.methodName()}"
                   |}
                   |
                   |return ok.method""".trimMargin(),
                response
            )
        } else {
            builderMethodSpec?.returns(UNIT)
            rawMethodSpec.returns(UNIT)
            rawMethodSpec.addCode("channel.send(method)")
        }

        builderMethodSpec?.build()?.let(::addFunction)
        addFunction(rawMethodSpec.build())
    }

    return this
}

fun FileSpec.Builder.generateClassHelperConvenienceProperty(amqpClass: AMQP.Class): FileSpec.Builder {
    val spec = PropertySpec.builder(amqpClass.normalizedName.decapitalize(), ClassName(packageName, name))
        .addModifiers(KModifier.PUBLIC)
        .receiver(CHANNEL)
        .getter(
            FunSpec.getterBuilder()
                .addStatement("return %L(this)", ClassName("", name))
                .build()
        )
        .build()

    return addProperty(spec)
}
