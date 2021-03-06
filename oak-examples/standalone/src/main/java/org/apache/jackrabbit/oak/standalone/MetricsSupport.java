begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|standalone
package|;
end_package

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|connect
operator|.
name|launch
operator|.
name|PojoServiceRegistry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|framework
operator|.
name|ServiceReference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|beans
operator|.
name|factory
operator|.
name|annotation
operator|.
name|Autowired
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|boot
operator|.
name|autoconfigure
operator|.
name|condition
operator|.
name|ConditionalOnClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|annotation
operator|.
name|Bean
import|;
end_import

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|annotation
operator|.
name|Configuration
import|;
end_import

begin_class
annotation|@
name|Configuration
annotation|@
name|ConditionalOnClass
argument_list|(
name|MetricRegistry
operator|.
name|class
argument_list|)
specifier|public
class|class
name|MetricsSupport
block|{
annotation|@
name|Autowired
specifier|private
name|PojoServiceRegistry
name|serviceRegistry
decl_stmt|;
annotation|@
name|Bean
specifier|public
name|MetricRegistry
name|getMetricsRegistry
parameter_list|()
block|{
name|ServiceReference
argument_list|<
name|?
argument_list|>
name|metricRegistry
init|=
name|serviceRegistry
operator|.
name|getServiceReference
argument_list|(
name|MetricRegistry
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|MetricRegistry
operator|)
name|serviceRegistry
operator|.
name|getService
argument_list|(
name|metricRegistry
argument_list|)
return|;
block|}
block|}
end_class

end_unit

