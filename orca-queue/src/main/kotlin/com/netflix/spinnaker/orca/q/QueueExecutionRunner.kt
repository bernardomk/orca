/*
 * Copyright 2017 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.q

import com.netflix.spinnaker.orca.pipeline.ExecutionRunner
import com.netflix.spinnaker.orca.pipeline.model.Execution
import com.netflix.spinnaker.q.Queue
import com.netflix.spinnaker.security.AuthenticatedRequest
import org.springframework.stereotype.Component

@Component
class QueueExecutionRunner(
  private val queue: Queue
) : ExecutionRunner {

  override fun start(execution: Execution) =
    queue.push(StartExecution(execution))

  override fun reschedule(execution: Execution) {
    queue.push(RescheduleExecution(execution))
  }

  override fun restart(execution: Execution, stageId: String) {
    queue.push(RestartStage(execution, stageId, AuthenticatedRequest.getSpinnakerUser().orElse(null)))
  }

  override fun unpause(execution: Execution) {
    queue.push(ResumeExecution(execution))
  }

  override fun cancel(execution: Execution, user: String, reason: String?) {
    queue.push(CancelExecution(execution, user, reason))
  }
}
