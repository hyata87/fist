package info.gravitypianist.fist.ktx

import info.gravitypianist.fist.base.Resource
import kotlinx.coroutines.flow.Flow

interface KtModel<Action, Value, Error : Throwable> {
    val resource: Flow<Resource<Value, Error>>
    fun dispatch(action: Action)
    fun dispose()
}