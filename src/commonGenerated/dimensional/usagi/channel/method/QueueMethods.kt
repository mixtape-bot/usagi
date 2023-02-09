package dimensional.usagi.channel.method

import dimensional.usagi.channel.Channel
import dimensional.usagi.protocol.AMQP
import kotlin.Unit
import kotlin.jvm.JvmInline

@JvmInline
public value class QueueMethods(
  public val channel: Channel,
) {
  public suspend fun declare(block: AMQP.Queue.Declare.Builder.() -> Unit): AMQP.Queue.DeclareOk =
      declare(AMQP.Queue.Declare.Builder().apply(block).build())

  public suspend fun declare(method: AMQP.Queue.Declare): AMQP.Queue.DeclareOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Queue.DeclareOk) { 
      "Expected 'queue.declare-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun bind(block: AMQP.Queue.Bind.Builder.() -> Unit): AMQP.Queue.BindOk =
      bind(AMQP.Queue.Bind.Builder().apply(block).build())

  public suspend fun bind(method: AMQP.Queue.Bind): AMQP.Queue.BindOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Queue.BindOk) { 
      "Expected 'queue.bind-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun purge(block: AMQP.Queue.Purge.Builder.() -> Unit): AMQP.Queue.PurgeOk =
      purge(AMQP.Queue.Purge.Builder().apply(block).build())

  public suspend fun purge(method: AMQP.Queue.Purge): AMQP.Queue.PurgeOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Queue.PurgeOk) { 
      "Expected 'queue.purge-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun delete(block: AMQP.Queue.Delete.Builder.() -> Unit): AMQP.Queue.DeleteOk =
      delete(AMQP.Queue.Delete.Builder().apply(block).build())

  public suspend fun delete(method: AMQP.Queue.Delete): AMQP.Queue.DeleteOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Queue.DeleteOk) { 
      "Expected 'queue.delete-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }

  public suspend fun unbind(block: AMQP.Queue.Unbind.Builder.() -> Unit): AMQP.Queue.UnbindOk =
      unbind(AMQP.Queue.Unbind.Builder().apply(block).build())

  public suspend fun unbind(method: AMQP.Queue.Unbind): AMQP.Queue.UnbindOk {
    val ok = channel.rpc(method)
    require(ok.method is AMQP.Queue.UnbindOk) { 
      "Expected 'queue.unbind-ok', not ${ok.method.methodName()}"
    }

    return ok.method
  }
}

public val Channel.queue: QueueMethods
  get() = QueueMethods(this)
