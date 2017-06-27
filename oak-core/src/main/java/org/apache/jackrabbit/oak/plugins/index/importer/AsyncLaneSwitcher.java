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
name|index
operator|.
name|importer
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
name|api
operator|.
name|PropertyState
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
name|Type
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
name|index
operator|.
name|IndexConstants
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
name|memory
operator|.
name|PropertyStates
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
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
name|index
operator|.
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
import|;
end_import

begin_comment
comment|/**  * Coordinates the switching of indexing lane for indexes which are  * to be imported. Its support idempotent operation i.e. if an  * indexer is switched to temp lane then a repeat of same  * operation would be no op.  */
end_comment

begin_class
specifier|public
class|class
name|AsyncLaneSwitcher
block|{
comment|/**      * Property name where previous value of 'async' is stored      */
specifier|static
specifier|final
name|String
name|ASYNC_PREVIOUS
init|=
literal|"async-previous"
decl_stmt|;
comment|/**      * Value stored in previous async property if the index is not async      * i.e. when a sync index is reindexed in out of band mode      */
specifier|static
specifier|final
name|String
name|ASYNC_PREVIOUS_NONE
init|=
literal|"none"
decl_stmt|;
comment|/**      * Index lane name which is used for indexing      */
specifier|private
specifier|static
specifier|final
name|String
name|TEMP_LANE_PREFIX
init|=
literal|"temp-"
decl_stmt|;
comment|/**      * Make a copy of current async value and replace it with one required for offline reindexing      * The switch lane operation can be safely repeated and if the index      * lane is found to be switched already it would not be modified      */
specifier|public
specifier|static
name|void
name|switchLane
parameter_list|(
name|NodeBuilder
name|idxBuilder
parameter_list|,
name|String
name|laneName
parameter_list|)
block|{
name|PropertyState
name|currentAsyncState
init|=
name|idxBuilder
operator|.
name|getProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|)
decl_stmt|;
name|PropertyState
name|newAsyncState
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|,
name|laneName
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxBuilder
operator|.
name|hasProperty
argument_list|(
name|ASYNC_PREVIOUS
argument_list|)
condition|)
block|{
comment|//Lane already switched
return|return;
block|}
name|PropertyState
name|previousAsyncState
decl_stmt|;
if|if
condition|(
name|currentAsyncState
operator|==
literal|null
condition|)
block|{
name|previousAsyncState
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|ASYNC_PREVIOUS
argument_list|,
name|ASYNC_PREVIOUS_NONE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//Ensure that previous state is copied with correct type
name|previousAsyncState
operator|=
name|clone
argument_list|(
name|ASYNC_PREVIOUS
argument_list|,
name|currentAsyncState
argument_list|)
expr_stmt|;
block|}
name|idxBuilder
operator|.
name|setProperty
argument_list|(
name|previousAsyncState
argument_list|)
expr_stmt|;
name|idxBuilder
operator|.
name|setProperty
argument_list|(
name|newAsyncState
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getTempLaneName
parameter_list|(
name|String
name|laneName
parameter_list|)
block|{
return|return
name|TEMP_LANE_PREFIX
operator|+
name|laneName
return|;
block|}
specifier|public
specifier|static
name|void
name|revertSwitch
parameter_list|(
name|NodeBuilder
name|idxBuilder
parameter_list|,
name|String
name|indexPath
parameter_list|)
block|{
name|PropertyState
name|previousAsync
init|=
name|idxBuilder
operator|.
name|getProperty
argument_list|(
name|ASYNC_PREVIOUS
argument_list|)
decl_stmt|;
name|checkState
argument_list|(
name|previousAsync
operator|!=
literal|null
argument_list|,
literal|"No previous async state property found for index [%s]"
argument_list|,
name|indexPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|ASYNC_PREVIOUS_NONE
operator|.
name|equals
argument_list|(
name|previousAsync
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
condition|)
block|{
name|idxBuilder
operator|.
name|removeProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|idxBuilder
operator|.
name|setProperty
argument_list|(
name|clone
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
name|previousAsync
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|idxBuilder
operator|.
name|removeProperty
argument_list|(
name|ASYNC_PREVIOUS
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|PropertyState
name|clone
parameter_list|(
name|String
name|newName
parameter_list|,
name|PropertyState
name|currentAsyncState
parameter_list|)
block|{
name|PropertyState
name|clonedState
decl_stmt|;
if|if
condition|(
name|currentAsyncState
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|clonedState
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|newName
argument_list|,
name|currentAsyncState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|clonedState
operator|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
name|newName
argument_list|,
name|currentAsyncState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
return|return
name|clonedState
return|;
block|}
block|}
end_class

end_unit

