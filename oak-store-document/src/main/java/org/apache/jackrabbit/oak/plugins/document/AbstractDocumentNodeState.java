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
name|EmptyNodeState
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
name|ModifiedNodeState
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
name|AbstractNodeState
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
name|EqualsDiff
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
name|NodeStateDiff
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
name|commons
operator|.
name|PerfLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractDocumentNodeState
extends|extends
name|AbstractNodeState
block|{
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|perfLogger
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractDocumentNodeState
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|abstract
name|Path
name|getPath
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|RevisionVector
name|getLastRevision
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|RevisionVector
name|getRootRevision
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isFromExternalChange
parameter_list|()
function_decl|;
comment|/**      * Creates a copy of this {@code DocumentNodeState} with the      * {@link #getRootRevision()} set to the given {@code root} revision. This method      * returns {@code this} instance if the given {@code root} revision is      * the same as the one in this instance and the {@link #isFromExternalChange()}      * flags are equal.      *      * @param root the root revision for the copy of this node state.      * @param externalChange if the {@link #isFromExternalChange()} flag must be      *                       set on the returned node state.      * @return a copy of this node state with the given root revision and      *          external change flag.      */
specifier|public
specifier|abstract
name|AbstractDocumentNodeState
name|withRootRevision
parameter_list|(
annotation|@
name|NotNull
name|RevisionVector
name|root
parameter_list|,
name|boolean
name|externalChange
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|hasNoChildren
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|NodeStateDiffer
name|getNodeStateDiffer
parameter_list|()
function_decl|;
comment|//--------------------------< NodeState>-----------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|that
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|that
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|that
operator|instanceof
name|AbstractDocumentNodeState
condition|)
block|{
name|AbstractDocumentNodeState
name|other
init|=
operator|(
name|AbstractDocumentNodeState
operator|)
name|that
decl_stmt|;
if|if
condition|(
operator|!
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
comment|// path does not match: not equals
comment|// (even if the properties are equal)
return|return
literal|false
return|;
block|}
if|if
condition|(
name|revisionEquals
argument_list|(
name|other
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// revision does not match: might still be equals
block|}
elseif|else
if|if
condition|(
name|that
operator|instanceof
name|ModifiedNodeState
condition|)
block|{
name|ModifiedNodeState
name|modified
init|=
operator|(
name|ModifiedNodeState
operator|)
name|that
decl_stmt|;
if|if
condition|(
name|modified
operator|.
name|getBaseState
argument_list|()
operator|==
name|this
condition|)
block|{
return|return
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|this
argument_list|,
name|modified
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|that
operator|instanceof
name|NodeState
condition|)
block|{
return|return
name|AbstractNodeState
operator|.
name|equals
argument_list|(
name|this
argument_list|,
operator|(
name|NodeState
operator|)
name|that
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|compareAgainstBaseState
parameter_list|(
name|NodeState
name|base
parameter_list|,
name|NodeStateDiff
name|diff
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|base
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|base
operator|==
name|EMPTY_NODE
operator|||
operator|!
name|base
operator|.
name|exists
argument_list|()
condition|)
block|{
comment|// special case
return|return
name|EmptyNodeState
operator|.
name|compareAgainstEmptyState
argument_list|(
name|this
argument_list|,
name|diff
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|base
operator|instanceof
name|AbstractDocumentNodeState
condition|)
block|{
name|AbstractDocumentNodeState
name|mBase
init|=
operator|(
name|AbstractDocumentNodeState
operator|)
name|base
decl_stmt|;
if|if
condition|(
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|mBase
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|revisionEquals
argument_list|(
name|mBase
argument_list|)
condition|)
block|{
comment|// no differences
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// use DocumentNodeStore compare
specifier|final
name|long
name|start
init|=
name|perfLogger
operator|.
name|start
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|getNodeStateDiffer
argument_list|()
operator|.
name|compare
argument_list|(
name|this
argument_list|,
name|mBase
argument_list|,
name|diff
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|perfLogger
operator|.
name|end
argument_list|(
name|start
argument_list|,
literal|1
argument_list|,
literal|"compareAgainstBaseState, path={}, lastRevision={}, base.path={}, base.lastRevision={}"
argument_list|,
name|getPath
argument_list|()
argument_list|,
name|getLastRevision
argument_list|()
argument_list|,
name|mBase
operator|.
name|getPath
argument_list|()
argument_list|,
name|mBase
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|// fall back to the generic node state diff algorithm
return|return
name|super
operator|.
name|compareAgainstBaseState
argument_list|(
name|base
argument_list|,
name|diff
argument_list|)
return|;
block|}
comment|//------------------------------< internal>--------------------------------
comment|/**      * Returns {@code true} if this state is equal to the {@code other} state      * by inspecting the root and last revision. Two node states are guaranteed      * to be equal if their root revisions are equal (even if the two revisions      * have different branch flags) or their last revisions are equal. This      * method may return {@code false} even if the actual states are in fact      * equal!      *      * @param other the other state to compare with.      * @return {@code true} if this state is equal to the {@code other} state      *      based on the root and last revisions.      */
specifier|private
name|boolean
name|revisionEquals
parameter_list|(
name|AbstractDocumentNodeState
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|getRootRevision
argument_list|()
operator|.
name|asTrunkRevision
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getRootRevision
argument_list|()
operator|.
name|asTrunkRevision
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|this
operator|.
name|getLastRevision
argument_list|()
operator|!=
literal|null
operator|&&
name|this
operator|.
name|getLastRevision
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getLastRevision
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

