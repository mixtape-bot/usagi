package codegen

import com.squareup.kotlinpoet.*

fun FileSpec.Builder.addAutogeneratedWarning(): FileSpec.Builder {
    return addFileComment("DO NOT EDIT THIS FILE! This was generated by the `./gradlew :generateAmqpClasses` task.`")
}

/* code gen related to creating objects used to represent different AMQP classes. */
fun generateAMQP(name: String, classes: List<AMQP.Class>): FileSpec {
    return FileSpec.builder(protocolPackage, name)
        .addType(generateAMQPObject(name, classes))
        .addAutogeneratedWarning()
        .build()
}

fun generateAMQPObject(name: String, amqpClasses: List<AMQP.Class>): TypeSpec {
    val classHolder = TypeSpec.objectBuilder(name)
        .addModifiers(KModifier.PUBLIC)
        //.addKdoc("Container class for auto-generated AMQP classes & their methods.")

    for (amqpClass in amqpClasses) {
        val spec = generateAMQPClass(amqpClass)
        classHolder.addType(spec)
    }

    return classHolder
        .amqpReadMethodFromFunction(amqpClasses)
        .amqpReadPropertiesFromFunction(amqpClasses)
        .build()
}

fun TypeSpec.Builder.amqpReadMethodFromFunction(amqpClasses: List<AMQP.Class>): TypeSpec.Builder {
    val spec = FunSpec.builder("readMethodFrom")
        .addModifiers(KModifier.PUBLIC, KModifier.SUSPEND)
        .addParameter("reader", METHOD_PROTOCOL_READER)
        .returns(METHOD)

    spec.addCode(
        """
        |val classId  = reader.readShortUnsigned().toInt()
        |val methodId = reader.readShortUnsigned().toInt()
        |return when (classId) {
        |""".trimMargin()
    )

    for (amqpClass in amqpClasses) {
        spec.addCode("$INDENT${amqpClass.id} -> ${amqpClass.createWhenMethodId()}\n")
    }

    spec.addCode("""${INDENT}else -> error("Invalid class id: ${"$"}classId")""")
    spec.addCode("\n}")

    return addFunction(spec.build())
}

fun AMQP.Class.createWhenMethodId(): String =
    """when (methodId) {
    ${methods.joinToString("\n") { "|$INDENT${it.id} -> ${normalizedName}.${it.normalizedName}(reader)" }}
    |${INDENT}else -> error("Invalid method id ${"$"}methodId for class ${"$"}classId") 
    |}
    """.replaceIndentByMargin(INDENT)

fun generateAMQPClass(amqpClass: AMQP.Class): TypeSpec {
    val spec = TypeSpec.objectBuilder(amqpClass.normalizedName)
        .addModifiers(KModifier.PUBLIC)
        //.addKdoc("Represents the `${amqpClass.name}` (${amqpClass.id}) AMQP class.")

    for (amqpMethod in amqpClass.methods) {
        val method = generateMethodClass(amqpClass, amqpMethod)
        spec.addType(method)
    }

    if (amqpClass.properties.isNotEmpty()) {
        spec.amqpClassProperties(amqpClass)
    }

    return spec.build()
}
