begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|segment
operator|.
name|osgi
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
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
name|scr
operator|.
name|annotations
operator|.
name|Component
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
name|scr
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This component is activated when a configuration for the deprecated {@code  * SegmentNodeStoreService} from {@code oak-segment} is detected. When this  * component is activated, it prints a detailed error message describing the  * detected problem and hinting at a possible solution.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|,
name|configurationPid
operator|=
literal|"org.apache.jackrabbit.oak.plugins.segment.SegmentNodeStoreService"
argument_list|)
specifier|public
class|class
name|SegmentNodeStoreServiceDeprecationError
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SegmentNodeStoreServiceDeprecationError
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|OLD_PID
init|=
literal|"org.apache.jackrabbit.oak.plugins.segment.SegmentNodeStoreService"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NEW_PID
init|=
literal|"org.apache.jackrabbit.oak.segment.SegmentNodeStoreService"
decl_stmt|;
annotation|@
name|Activate
specifier|public
name|void
name|activate
parameter_list|()
block|{
name|logger
operator|.
name|error
argument_list|(
name|DeprecationMessage
operator|.
name|movedPid
argument_list|(
name|OLD_PID
argument_list|,
name|NEW_PID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

