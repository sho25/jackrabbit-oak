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
name|segment
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PropertiesUtil
import|;
end_import

begin_import
import|import
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
name|SegmentNodeStoreMonitorService
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
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
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|AttributeDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|Designate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|metatype
operator|.
name|annotations
operator|.
name|ObjectClassDefinition
import|;
end_import

begin_comment
comment|/**  * An OSGi wrapper for segment node store monitoring configurations.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|configurationPolicy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|)
annotation|@
name|Designate
argument_list|(
name|ocd
operator|=
name|Configuration
operator|.
name|class
argument_list|)
specifier|public
class|class
name|SegmentNodeStoreMonitorService
block|{
annotation|@
name|ObjectClassDefinition
argument_list|(
name|name
operator|=
literal|"Oak Segment Tar Monitoring service"
argument_list|,
name|description
operator|=
literal|"This service is responsible for different configurations related to "
operator|+
literal|"Oak Segment Tar read/write monitoring."
argument_list|)
annotation_defn|@interface
name|Configuration
block|{
annotation|@
name|AttributeDefinition
argument_list|(
name|name
operator|=
literal|"Writer groups"
argument_list|,
name|description
operator|=
literal|"Writer groups for which commits are tracked individually"
argument_list|)
name|String
index|[]
name|commitsTrackerWriterGroups
argument_list|()
expr|default
block|{}
expr_stmt|;
block|}
annotation|@
name|Reference
specifier|private
name|SegmentNodeStoreStatsMBean
name|snsStatsMBean
decl_stmt|;
annotation|@
name|Activate
specifier|public
name|void
name|activate
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|snsStatsMBean
operator|.
name|setWriterGroupsForLastMinuteCounts
argument_list|(
name|PropertiesUtil
operator|.
name|toStringArray
argument_list|(
name|config
operator|.
name|commitsTrackerWriterGroups
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

