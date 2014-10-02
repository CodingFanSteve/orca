/*
 * Copyright 2014 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.spinnaker.orca.kato.tasks

import spock.lang.Specification
import spock.lang.Subject
import com.netflix.spinnaker.orca.SimpleTaskContext
import com.netflix.spinnaker.orca.mort.MortService

class SecurityGroupForceCacheRefreshTaskSpec extends Specification {
  @Subject task = new SecurityGroupForceCacheRefreshTask()
  def context = new SimpleTaskContext()

  def config = [
    name   : "sg-12345a",
    region : "us-west-1",
    account: "fzlem"
  ]

  def setup() {
    config.each {
      context."upsert.${it.key}" = it.value
    }
  }

  void "should force cache refresh security groups via mort"() {
    setup:
    task.mort = Mock(MortService)

    when:
    task.execute(context)

    then:
    1 * task.mort.forceCacheUpdate(SecurityGroupForceCacheRefreshTask.REFRESH_TYPE, _) >> { String type, Map<String, ? extends Object> body ->
      assert body.securityGroupName == config.name
      assert body.account == config.account
      assert body.region == "us-west-1"
    }
  }
}
