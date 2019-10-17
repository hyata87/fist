package info.gravitypianist.fist.ktx.model

import info.gravitypianist.fist.base.Model
import info.gravitypianist.fist.base.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class DefaultKtModelTest {

    data class Sample(
            val title: String
    )

    sealed class SampleAction {
        object Action1 : SampleAction()
        object Action2 : SampleAction()
    }

    sealed class SampleError : Throwable() {
        object NoError : SampleError()
    }

    abstract class ActionMapping : Model.ActionMapping<SampleAction, Sample>

    abstract class ErrorMapping : Resource.ErrorMapping<Sample, SampleError>

    abstract class Collector : FlowCollector<Resource<Sample, SampleError>>

    @Test
    @ExperimentalCoroutinesApi
    fun test() {
        val actionMapping = mock(ActionMapping::class.java)
        val errorMapping = mock(ErrorMapping::class.java)

        `when`(actionMapping.invoke(ArgumentMatchers.any())).thenReturn(Sample(""))
        `when`(actionMapping.invoke(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Sample(""))
        `when`(errorMapping.invoke(ArgumentMatchers.any())).thenReturn(Resource.failure("", SampleError.NoError))

        runBlocking {

            val ktModel = DefaultKtModel.create(
                    this,
                    actionMapping,
                    errorMapping
            )

            val resources = async {
                ktModel.resource.toList()
            }

            val actions = async {
                ktModel.dispatch(SampleAction.Action1)
                delay(10)
                ktModel.dispatch(SampleAction.Action2)
                delay(10)
                ktModel.dispatch(SampleAction.Action1)
                delay(10)
                ktModel.dispose()
            }

            actions.await()
            val list = resources.await()
            assertTrue(list.count() == 6)
        }


    }
}