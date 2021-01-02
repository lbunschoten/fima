@file:Suppress("unused")

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder

object KoTestConfig : AbstractProjectConfig() {

    override val parallelism: Int = 4

    override val testCaseOrder: TestCaseOrder = TestCaseOrder.Random

    override val isolationMode: IsolationMode = IsolationMode.InstancePerLeaf

    override val globalAssertSoftly: Boolean = true

}
