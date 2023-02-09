package dimensional.usagi.channel.method

import dimensional.usagi.channel.Channel
import dimensional.usagi.protocol.AMQP
import kotlin.Unit
import kotlin.jvm.JvmInline

@JvmInline
public value class ExchangeMethods(
  public val channel: Channel,
) {
  public suspend fun declare(block: AMQP.Exchange.Declare.Builder.() -> Unit):
      AMQP.Exchange.DeclareOk = declare(AMQP.Exchange.Declare.Builder().apply(block).build())

  public suspend fun declare(method: AMQP.Exchange.Declare): AMQP.Exchange.DeclareOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Exchange.DeclareOk) { 
      "Expected 'exchange.declare-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun delete(block: AMQP.Exchange.Delete.Builder.() -> Unit): AMQP.Exchange.DeleteOk
      = delete(AMQP.Exchange.Delete.Builder().apply(block).build())

  public suspend fun delete(method: AMQP.Exchange.Delete): AMQP.Exchange.DeleteOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Exchange.DeleteOk) { 
      "Expected 'exchange.delete-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun bind(block: AMQP.Exchange.Bind.Builder.() -> Unit): AMQP.Exchange.BindOk =
      bind(AMQP.Exchange.Bind.Builder().apply(block).build())

  public suspend fun bind(method: AMQP.Exchange.Bind): AMQP.Exchange.BindOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Exchange.BindOk) { 
      "Expected 'exchange.bind-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun unbind(block: AMQP.Exchange.Unbind.Builder.() -> Unit): AMQP.Exchange.UnbindOk
      = unbind(AMQP.Exchange.Unbind.Builder().apply(block).build())

  public suspend fun unbind(method: AMQP.Exchange.Unbind): AMQP.Exchange.UnbindOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Exchange.UnbindOk) { 
      "Expected 'exchange.unbind-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }
}

public val Channel.exchange: ExchangeMethods
  get() = ExchangeMethods(this)
