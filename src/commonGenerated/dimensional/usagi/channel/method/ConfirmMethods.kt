// DO NOT EDIT THIS FILE! This was generated by the `./gradlew :generateAmqpClasses` task.`
package dimensional.usagi.channel.method

import dimensional.usagi.channel.Channel
import dimensional.usagi.protocol.AMQP
import kotlin.Unit
import kotlin.jvm.JvmInline

@JvmInline
public value class ConfirmMethods(
  public val channel: Channel,
) {
  public suspend fun select(block: AMQP.Confirm.Select.Builder.() -> Unit): AMQP.Confirm.SelectOk? =
      select(AMQP.Confirm.Select.Builder().apply(block).build())

  public suspend fun select(method: AMQP.Confirm.Select): AMQP.Confirm.SelectOk? {
    if (method.nowait) {
      channel.send(method)
      return null
    }
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Confirm.SelectOk) { 
      "Expected 'confirm.select-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }
}

public val Channel.confirm: ConfirmMethods
  get() = ConfirmMethods(this)
