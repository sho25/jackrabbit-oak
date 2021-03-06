begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|composite
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
name|jmx
operator|.
name|AbstractCheckpointMBean
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|OpenDataException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularDataSupport
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|StreamSupport
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|composite
operator|.
name|CompositeNodeStore
operator|.
name|CHECKPOINT_METADATA
import|;
end_import

begin_class
specifier|public
class|class
name|CompositeCheckpointMBean
extends|extends
name|AbstractCheckpointMBean
block|{
specifier|private
specifier|final
name|CompositeNodeStore
name|store
decl_stmt|;
specifier|public
name|CompositeCheckpointMBean
parameter_list|(
name|CompositeNodeStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|createCheckpoint
parameter_list|(
name|long
name|lifetime
parameter_list|)
block|{
return|return
name|store
operator|.
name|checkpoint
argument_list|(
name|lifetime
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|releaseCheckpoint
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
name|store
operator|.
name|release
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|collectCheckpoints
parameter_list|(
name|TabularDataSupport
name|tab
parameter_list|)
throws|throws
name|OpenDataException
block|{
for|for
control|(
name|String
name|id
range|:
name|store
operator|.
name|checkpoints
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
init|=
name|store
operator|.
name|allCheckpointInfo
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|tab
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|toCompositeData
argument_list|(
name|id
argument_list|,
name|getDate
argument_list|(
name|info
argument_list|,
name|CHECKPOINT_METADATA
operator|+
literal|"created"
argument_list|)
argument_list|,
name|getDate
argument_list|(
name|info
argument_list|,
name|CHECKPOINT_METADATA
operator|+
literal|"expires"
argument_list|)
argument_list|,
name|store
operator|.
name|checkpointInfo
argument_list|(
name|id
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|getOldestCheckpointCreationTimestamp
parameter_list|()
block|{
return|return
name|StreamSupport
operator|.
name|stream
argument_list|(
name|store
operator|.
name|checkpoints
argument_list|()
operator|.
name|spliterator
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|map
argument_list|(
name|store
operator|::
name|allCheckpointInfo
argument_list|)
operator|.
name|map
argument_list|(
name|i
lambda|->
name|i
operator|.
name|get
argument_list|(
name|CHECKPOINT_METADATA
operator|+
literal|"created"
argument_list|)
argument_list|)
operator|.
name|mapToLong
argument_list|(
name|l
lambda|->
name|l
operator|==
literal|null
condition|?
literal|0
else|:
name|Long
operator|.
name|valueOf
argument_list|(
name|l
argument_list|)
argument_list|)
operator|.
name|sorted
argument_list|()
operator|.
name|findFirst
argument_list|()
operator|.
name|orElse
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|getDate
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|String
name|p
init|=
name|info
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|"NA"
return|;
block|}
try|try
block|{
return|return
operator|new
name|Date
argument_list|(
name|Long
operator|.
name|valueOf
argument_list|(
name|p
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
return|return
literal|"NA"
return|;
block|}
block|}
block|}
end_class

end_unit

