package dimensional.usagi.channel.method

import dimensional.usagi.channel.Channel
import dimensional.usagi.protocol.AMQP
import kotlin.Unit
import kotlin.jvm.JvmInline

@JvmInline
public value class BasicMethods(
  public val channel: Channel,
) {
  public suspend fun qos(block: AMQP.Basic.Qos.Builder.() -> Unit): AMQP.Basic.QosOk =
      qos(AMQP.Basic.Qos.Builder().apply(block).build())

  public suspend fun qos(method: AMQP.Basic.Qos): AMQP.Basic.QosOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Basic.QosOk) { 
      "Expected 'basic.qos-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun cancel(block: AMQP.Basic.Cancel.Builder.() -> Unit): AMQP.Basic.CancelOk =
      cancel(AMQP.Basic.Cancel.Builder().apply(block).build())

  public suspend fun cancel(method: AMQP.Basic.Cancel): AMQP.Basic.CancelOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Basic.CancelOk) { 
      "Expected 'basic.cancel-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun `get`(block: AMQP.Basic.Get.Builder.() -> Unit): AMQP.Basic.GetOk =
      get(AMQP.Basic.Get.Builder().apply(block).build())

  public suspend fun `get`(method: AMQP.Basic.Get): AMQP.Basic.GetOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Basic.GetOk) { 
      "Expected 'basic.get-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun ack(block: AMQP.Basic.Ack.Builder.() -> Unit): Unit =
      ack(AMQP.Basic.Ack.Builder().apply(block).build())

  public suspend fun ack(method: AMQP.Basic.Ack): Unit {
    channel.send(method)
  }

  public suspend fun reject(block: AMQP.Basic.Reject.Builder.() -> Unit): Unit =
      reject(AMQP.Basic.Reject.Builder().apply(block).build())

  public suspend fun reject(method: AMQP.Basic.Reject): Unit {
    channel.send(method)
  }

  public suspend fun recoverAsync(block: AMQP.Basic.RecoverAsync.Builder.() -> Unit): Unit =
      recoverAsync(AMQP.Basic.RecoverAsync.Builder().apply(block).build())

  public suspend fun recoverAsync(method: AMQP.Basic.RecoverAsync): Unit {
    channel.send(method)
  }

  public suspend fun recover(block: AMQP.Basic.Recover.Builder.() -> Unit): AMQP.Basic.RecoverOk =
      recover(AMQP.Basic.Recover.Builder().apply(block).build())

  public suspend fun recover(method: AMQP.Basic.Recover): AMQP.Basic.RecoverOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Basic.RecoverOk) { 
      "Expected 'basic.recover-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun nack(block: AMQP.Basic.Nack.Builder.() -> Unit): Unit =
      nack(AMQP.Basic.Nack.Builder().apply(block).build())

  public suspend fun nack(method: AMQP.Basic.Nack): Unit {
    channel.send(method)
  }
}

public val Channel.basic: BasicMethods
  get() = BasicMethods(this)
