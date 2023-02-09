package dimensional.usagi.channel.method

import dimensional.usagi.channel.Channel
import dimensional.usagi.protocol.AMQP
import kotlin.Unit
import kotlin.jvm.JvmInline

@JvmInline
public value class AccessMethods(
  public val channel: Channel,
) {
  public suspend fun request(block: AMQP.Access.Request.Builder.() -> Unit): AMQP.Access.RequestOk =
      request(AMQP.Access.Request.Builder().apply(block).build())

  public suspend fun request(method: AMQP.Access.Request): AMQP.Access.RequestOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Access.RequestOk) { 
      "Expected 'access.request-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }
}

public val Channel.access: AccessMethods
  get() = AccessMethods(this)
