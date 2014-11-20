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
name|plugins
operator|.
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|Map
operator|.
name|Entry
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
name|CompositeDataSupport
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
name|CompositeType
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
name|OpenType
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
name|SimpleType
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
name|TabularData
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
name|javax
operator|.
name|management
operator|.
name|openmbean
operator|.
name|TabularType
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
name|api
operator|.
name|jmx
operator|.
name|CheckpointMBean
import|;
end_import

begin_comment
comment|/**  * {@code CheckpointMBean} implementation for the {@code DocumentNodeStore}.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentCheckpointMBean
implements|implements
name|CheckpointMBean
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|FIELD_NAMES
init|=
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"created"
block|,
literal|"expires"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|FIELD_DESCRIPTIONS
init|=
name|FIELD_NAMES
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
specifier|private
specifier|static
specifier|final
name|OpenType
index|[]
name|FIELD_TYPES
init|=
operator|new
name|OpenType
index|[]
block|{
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|,
name|SimpleType
operator|.
name|STRING
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|CompositeType
name|TYPE
init|=
name|createCompositeType
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|CompositeType
name|createCompositeType
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|CompositeType
argument_list|(
name|DocumentCheckpointMBean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Checkpoints"
argument_list|,
name|FIELD_NAMES
argument_list|,
name|FIELD_DESCRIPTIONS
argument_list|,
name|FIELD_TYPES
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|OpenDataException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|final
name|DocumentNodeStore
name|store
decl_stmt|;
specifier|public
name|DocumentCheckpointMBean
parameter_list|(
name|DocumentNodeStore
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
name|TabularData
name|listCheckpoints
parameter_list|()
block|{
name|Map
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|checkpoints
init|=
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getCheckpoints
argument_list|()
decl_stmt|;
if|if
condition|(
name|checkpoints
operator|==
literal|null
condition|)
block|{
name|checkpoints
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|TabularDataSupport
name|tab
init|=
operator|new
name|TabularDataSupport
argument_list|(
operator|new
name|TabularType
argument_list|(
name|DocumentCheckpointMBean
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"Checkpoints"
argument_list|,
name|TYPE
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"id"
block|}
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|checkpoint
range|:
name|checkpoints
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|id
init|=
name|checkpoint
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Date
name|created
init|=
operator|new
name|Date
argument_list|(
name|checkpoint
operator|.
name|getKey
argument_list|()
operator|.
name|getTimestamp
argument_list|()
argument_list|)
decl_stmt|;
name|Date
name|expires
init|=
operator|new
name|Date
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|checkpoint
operator|.
name|getValue
argument_list|()
argument_list|)
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
name|created
operator|.
name|toString
argument_list|()
argument_list|,
name|expires
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|tab
return|;
block|}
catch|catch
parameter_list|(
name|OpenDataException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|CompositeDataSupport
name|toCompositeData
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|created
parameter_list|,
name|String
name|expires
parameter_list|)
throws|throws
name|OpenDataException
block|{
return|return
operator|new
name|CompositeDataSupport
argument_list|(
name|TYPE
argument_list|,
name|FIELD_NAMES
argument_list|,
operator|new
name|String
index|[]
block|{
name|id
block|,
name|created
block|,
name|expires
block|}
argument_list|)
return|;
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
name|checkpoint
parameter_list|)
block|{
return|return
name|store
operator|.
name|release
argument_list|(
name|checkpoint
argument_list|)
return|;
block|}
block|}
end_class

end_unit

