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
name|checkpoint
package|;
end_package

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
name|plugins
operator|.
name|document
operator|.
name|CheckpointsHelper
operator|.
name|getCheckpoints
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
name|plugins
operator|.
name|document
operator|.
name|CheckpointsHelper
operator|.
name|min
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
name|plugins
operator|.
name|document
operator|.
name|CheckpointsHelper
operator|.
name|removeOlderThan
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|plugins
operator|.
name|document
operator|.
name|CheckpointsHelper
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
name|plugins
operator|.
name|document
operator|.
name|DocumentNodeStore
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
name|plugins
operator|.
name|document
operator|.
name|Revision
import|;
end_import

begin_class
class|class
name|DocumentCheckpoints
extends|extends
name|Checkpoints
block|{
specifier|private
specifier|final
name|DocumentNodeStore
name|store
decl_stmt|;
name|DocumentCheckpoints
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
name|List
argument_list|<
name|CP
argument_list|>
name|list
parameter_list|()
block|{
name|List
argument_list|<
name|CP
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|Long
argument_list|>
name|entry
range|:
name|getCheckpoints
argument_list|(
name|store
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|CP
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|removeAll
parameter_list|()
block|{
return|return
name|CheckpointsHelper
operator|.
name|removeAll
argument_list|(
name|store
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|removeUnreferenced
parameter_list|()
block|{
name|Revision
name|ref
init|=
name|min
argument_list|(
name|getReferencedCheckpoints
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
name|removeOlderThan
argument_list|(
name|store
argument_list|,
name|ref
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|remove
parameter_list|(
name|String
name|cp
parameter_list|)
block|{
name|Revision
name|r
decl_stmt|;
try|try
block|{
name|r
operator|=
name|Revision
operator|.
name|fromString
argument_list|(
name|cp
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|CheckpointsHelper
operator|.
name|remove
argument_list|(
name|store
argument_list|,
name|r
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getInfo
parameter_list|(
name|String
name|cp
parameter_list|)
block|{
return|return
name|store
operator|.
name|checkpointInfo
argument_list|(
name|cp
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|setInfoProperty
parameter_list|(
name|String
name|cp
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
return|return
name|CheckpointsHelper
operator|.
name|setInfoProperty
argument_list|(
name|store
argument_list|,
name|cp
argument_list|,
name|name
argument_list|,
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

