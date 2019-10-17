package info.gravitypianist.fist.ktx.model

import info.gravitypianist.fist.base.Model
import info.gravitypianist.fist.base.Resource
import info.gravitypianist.fist.base.model.ModelLogic
import info.gravitypianist.fist.ktx.KtModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class DefaultKtModel<Action, Value, Error : Throwable> private constructor(
        private val scope: CoroutineScope,
        private val logic: ModelLogic<Action, Value, Error>
) : KtModel<Action, Value, Error> {

    private val channel = BroadcastChannel<Resource<Value, Error>>(10)

    @FlowPreview
    override val resource: Flow<Resource<Value, Error>>
        get() = channel.asFlow()

    init {
        scope.launch {
            logic.setDispatcherObserver {
                logic.update(it)
            }
            logic.setResourceObserver {
                channel.offer(it)
            }
        }
    }

    override fun dispatch(action: Action) {
        scope.launch {
            logic.dispatch(action)
        }
    }

    override fun dispose() {
        scope.launch {
            channel.cancel()
        }
    }

    companion object {
        fun <Action, Value, Error : Throwable> create(
                scope: CoroutineScope,
                actionMapping: Model.ActionMapping<Action, Value>,
                errorMapping: Resource.ErrorMapping<Value, Error>
        ): KtModel<Action, Value, Error> =
                DefaultKtModel(
                        scope,
                        ModelLogic.create(
                                actionMapping,
                                errorMapping
                        )
                )

        fun <Action, Value, Error : Throwable> create(
                scope: CoroutineScope,
                actionMapping: Model.ActionMapping<Action, Value>,
                errorMapping: Resource.ErrorMapping<Value, Error>,
                value: Value
        ): KtModel<Action, Value, Error> =
                DefaultKtModel(
                        scope,
                        ModelLogic.create(
                                actionMapping,
                                errorMapping,
                                value
                        )
                )
    }

}