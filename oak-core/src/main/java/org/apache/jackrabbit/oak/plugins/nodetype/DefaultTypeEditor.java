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
name|plugins
operator|.
name|nodetype
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
name|CommitFailedException
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
name|commit
operator|.
name|CommitHook
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
name|ChildNodeEntry
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
name|DefaultNodeStateDiff
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
name|NodeState
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
name|NodeStateUtils
import|;
end_import

begin_comment
comment|/**  * This class updates a Lucene index when node content is changed.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultTypeEditor
implements|implements
name|CommitHook
block|{
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// TODO: Calculate default type from the node definition
name|NodeBuilder
name|builder
init|=
name|after
operator|.
name|builder
argument_list|()
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
operator|new
name|DefaultTypeDiff
argument_list|(
name|builder
argument_list|,
literal|"nt:unstructured"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|DefaultTypeDiff
extends|extends
name|DefaultNodeStateDiff
block|{
specifier|private
specifier|final
name|NodeBuilder
name|builder
decl_stmt|;
specifier|private
specifier|final
name|String
name|defaultType
decl_stmt|;
specifier|public
name|DefaultTypeDiff
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|,
name|String
name|defaultType
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|defaultType
operator|=
name|defaultType
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeBuilder
name|childBuilder
init|=
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|after
operator|.
name|getProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|)
operator|==
literal|null
condition|)
block|{
name|childBuilder
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
name|defaultType
argument_list|)
expr_stmt|;
block|}
name|DefaultTypeDiff
name|childDiff
init|=
operator|new
name|DefaultTypeDiff
argument_list|(
name|childBuilder
argument_list|,
name|defaultType
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|after
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|childDiff
operator|.
name|childNodeAdded
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|NodeBuilder
name|childBuilder
init|=
name|builder
operator|.
name|child
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|DefaultTypeDiff
name|childDiff
init|=
operator|new
name|DefaultTypeDiff
argument_list|(
name|childBuilder
argument_list|,
name|defaultType
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|childDiff
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

